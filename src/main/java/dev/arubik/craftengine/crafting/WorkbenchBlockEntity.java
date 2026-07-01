package dev.arubik.craftengine.crafting;

import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import dev.arubik.craftengine.conveyor.ConveyorItemDisplay;
import dev.arubik.craftengine.multiblock.HorizontalDoubleGeometry;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker;
import net.momirealms.craftengine.core.util.Direction;
import net.momirealms.craftengine.core.world.BlockPos;
import net.momirealms.craftengine.core.world.CEWorld;
import net.momirealms.craftengine.core.world.ChunkPos;
import org.bukkit.NamespacedKey;
import dev.arubik.craftengine.util.NbtType;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * Master (LEFT-half) controller for the engineer's workbench. Persists the BLUEPRINT (tool) item in
 * the block — it survives closing the menu and is dropped on break — while the input grid stays
 * transient (returned to the player on close). Also renders the blueprint flat on the RIGHT half's
 * tabletop square and the matched recipe output(s) flat on the LEFT half's square, via fake
 * item-display entities.
 */
public class WorkbenchBlockEntity extends PersistentBlockEntity {

    /** Recipe output preview pushed by the open menu (transient — not persisted). */
    private final java.util.List<org.bukkit.inventory.ItemStack> outputs = new java.util.ArrayList<>(2);

    private ConveyorItemDisplay bpDisplay;   // blueprint, on the RIGHT half
    private ConveyorItemDisplay out0Display;  // primary output, on the LEFT half
    private ConveyorItemDisplay out1Display;  // secondary output, tilted to the right
    private int lastBpHash, lastOut0Hash, lastOut1Hash;

    private WorkbenchBehavior cfgBehavior; // render config, injected by createMasterController

    public WorkbenchBlockEntity(BlockEntity blockEntity) {
        super(blockEntity);
    }

    public void setConfig(WorkbenchBehavior b) {
        this.cfgBehavior = b;
    }

    // ---- shared menu: ONE inventory per block for all viewers (prevents blueprint duplication) ----
    private WorkbenchMenu sharedMenu;

    public void openMenu(org.bukkit.entity.Player p, String title, StationRecipeRegistry reg) {
        if (sharedMenu == null)
            sharedMenu = new WorkbenchMenu(title, reg, this);
        sharedMenu.open(p); // multiple players view the SAME inventory -> the tool slot can't be duped
    }

    /** Called when the last viewer closes — drop the cached menu so the next open re-syncs. */
    public void clearSharedMenu() {
        sharedMenu = null;
    }

    // ---------------- blueprint persistence ----------------

    // Persisted on this block entity's own CE tag, alongside every other TypedKey-backed field.
    private static final dev.arubik.craftengine.util.TypedKey<byte[]> BP_KEY =
            dev.arubik.craftengine.util.TypedKey.of("craftengine", "wb_blueprint",
                    dev.arubik.craftengine.util.NbtType.BYTE_ARRAY);

    private org.bukkit.inventory.ItemStack cachedBlueprint; // last value seen (block may be air in onRemove)

    public org.bukkit.inventory.ItemStack getBlueprint() {
        byte[] bytes = get(BP_KEY);
        if (bytes == null || bytes.length == 0) {
            cachedBlueprint = null;
            return null;
        }
        try {
            cachedBlueprint = org.bukkit.inventory.ItemStack.deserializeBytes(bytes);
            return cachedBlueprint;
        } catch (Throwable t) {
            return null;
        }
    }

    public void setBlueprint(org.bukkit.inventory.ItemStack stack) {
        cachedBlueprint = (stack == null || stack.getType().isAir()) ? null : stack.clone();
        if (stack == null || stack.getType().isAir())
            remove(BP_KEY.getKey());
        else
            set(BP_KEY, stack.serializeAsBytes());
    }

    /** The open menu pushes the current matched output(s) here so they render on the table. */
    public void setRenderOutputs(java.util.List<org.bukkit.inventory.ItemStack> outs) {
        outputs.clear();
        if (outs != null)
            for (org.bukkit.inventory.ItemStack s : outs)
                if (s != null && !s.getType().isAir())
                    outputs.add(s.clone());
    }

    // ---------------- ticking / render ----------------

    @Override
    @SuppressWarnings("unchecked")
    public <C extends BlockEntityController> BlockEntityTicker<C> createBlockEntityTicker(
            CEWorld world, ImmutableBlockState state) {
        return BlockEntityController.createTickerHelper(
                (BlockEntityTicker<WorkbenchBlockEntity>) WorkbenchBlockEntity::tick);
    }

    public static void tick(CEWorld world, BlockPos pos, ImmutableBlockState state, WorkbenchBlockEntity self) {
        self.render(world, pos);
    }

    private Direction facing() {
        try {
            net.momirealms.craftengine.core.block.property.Property<?> p = blockState().getProperty("facing");
            if (p != null) {
                Object v = blockState().get(p);
                if (v != null)
                    return Direction.valueOf(v.toString().toUpperCase());
            }
        } catch (Throwable ignored) {
        }
        return Direction.NORTH;
    }

    /** Y rotation (radians) applied to the model per facing (matches the entity-renderer mapping). */
    private static float facingYawRad(Direction facing) {
        switch (facing) {
            case SOUTH: return 0f;
            case WEST:  return (float) Math.toRadians(90);
            case NORTH: return (float) Math.toRadians(180);
            case EAST:  return (float) Math.toRadians(270);
            default:    return 0f;
        }
    }

    /** Rotate a pixel offset (authored for facing=south) around the cell centre by the facing yaw. */
    private static Vector3f localOffset(float px, float py, float pz, Direction facing) {
        float x = px / 16f - 0.5f;
        float z = pz / 16f - 0.5f;
        float yaw = facingYawRad(facing);
        float cos = (float) Math.cos(yaw);
        float sin = (float) Math.sin(yaw);
        float rx = x * cos - z * sin;
        float rz = x * sin + z * cos;
        return new Vector3f(rx + 0.5f, py / 16f, rz + 0.5f);
    }

    /** Item laid FLAT (face-up) on the table, oriented by facing yaw + an extra X/Y/Z rotation. */
    private static Quaternionf flatRotation(Direction facing, float[] rot) {
        Quaternionf q = new Quaternionf().rotationY(facingYawRad(facing));
        q.rotateX((float) Math.toRadians(-90)); // lay flat
        if (rot != null) {
            if (rot[0] != 0f) q.rotateX((float) Math.toRadians(rot[0]));
            if (rot[1] != 0f) q.rotateY((float) Math.toRadians(rot[1]));
            if (rot[2] != 0f) q.rotateZ((float) Math.toRadians(rot[2]));
        }
        return q;
    }

    private void render(CEWorld world, BlockPos masterPos) {
        Direction facing = facing();
        WorkbenchBehavior cfg = cfgBehavior != null ? cfgBehavior : getBlockBehavior(WorkbenchBehavior.class);
        float[] bpPos = cfg != null ? cfg.blueprintPos : new float[] { 4f, 13.5f, 4f };
        float bpScale = cfg != null ? cfg.blueprintScale : 0.5f;
        float[] outPos = cfg != null ? cfg.outputPos : new float[] { 4f, 13.5f, 4f };
        float outScale = cfg != null ? cfg.outputScale : 0.375f;
        float[] out2Off = cfg != null ? cfg.output2Offset : new float[] { 3f, 1f, 0f };
        float[] bpRot = cfg != null ? cfg.blueprintRotation : new float[] { 0f, 0f, 0f };
        float[] outRot = cfg != null ? cfg.outputRotation : new float[] { 0f, 0f, 0f };
        float[] out2Rot = cfg != null ? cfg.output2Rotation : new float[] { 0f, 0f, 22f };
        java.util.List<net.momirealms.craftengine.core.entity.player.Player> viewers =
                world.world().getTrackedBy(new ChunkPos(masterPos));

        // Blueprint -> RIGHT half tabletop square, flat.
        org.bukkit.inventory.ItemStack bp = getBlueprint();
        if (bp != null && !bp.getType().isAir()) {
            WorkbenchBehavior.RenderOverride ov = override(cfg, bp);
            float[] pos = addPos(bpPos, ov);
            BlockPos right = HorizontalDoubleGeometry.rightCell(masterPos, facing);
            Vector3f o = localOffset(pos[0], pos[1], pos[2], facing);
            if (bpDisplay == null)
                bpDisplay = new ConveyorItemDisplay();
            bpDisplay.setScale(bpScale + (ov != null ? ov.scale : 0f));
            bpDisplay.setRotation(flatRotation(facing, addRot(bpRot, ov)));
            int h = bp.hashCode();
            bpDisplay.setNmsItem(org.bukkit.craftbukkit.inventory.CraftItemStack.asNMSCopy(bp));
            bpDisplay.render(viewers, right.x() + o.x, right.y() + o.y, right.z() + o.z, h != lastBpHash);
            bpDisplay.consumeRotationDirty();
            lastBpHash = h;
        } else {
            despawn(world, masterPos, bpDisplay);
            bpDisplay = null;
            lastBpHash = 0;
        }

        // Output(s) -> LEFT (master) half square, flat, 6x6 px (scale 0.375). Derived from the
        // BLUEPRINT's recipe (not the input grid), so it shows what this blueprint produces.
        java.util.List<org.bukkit.inventory.ItemStack> recipeOuts = blueprintRecipeOutputs(bp);
        org.bukkit.inventory.ItemStack o0 = recipeOuts.size() > 0 ? recipeOuts.get(0) : null;
        org.bukkit.inventory.ItemStack o1 = recipeOuts.size() > 1 ? recipeOuts.get(1) : null;
        if (o0 != null) {
            WorkbenchBehavior.RenderOverride ov = override(cfg, o0);
            float[] pos = addPos(outPos, ov);
            Vector3f o = localOffset(pos[0], pos[1], pos[2], facing);
            if (out0Display == null)
                out0Display = new ConveyorItemDisplay();
            out0Display.setScale(outScale + (ov != null ? ov.scale : 0f));
            out0Display.setRotation(flatRotation(facing, addRot(outRot, ov)));
            int h = o0.hashCode();
            out0Display.setNmsItem(org.bukkit.craftbukkit.inventory.CraftItemStack.asNMSCopy(o0));
            out0Display.render(viewers, masterPos.x() + o.x, masterPos.y() + o.y, masterPos.z() + o.z, h != lastOut0Hash);
            out0Display.consumeRotationDirty();
            lastOut0Hash = h;
        } else {
            despawn(world, masterPos, out0Display);
            out0Display = null;
            lastOut0Hash = 0;
        }
        if (o1 != null) {
            WorkbenchBehavior.RenderOverride ov = override(cfg, o1);
            float[] base2 = { outPos[0] + out2Off[0], outPos[1] + out2Off[1], outPos[2] + out2Off[2] };
            float[] pos = addPos(base2, ov);
            Vector3f o = localOffset(pos[0], pos[1], pos[2], facing);
            if (out1Display == null)
                out1Display = new ConveyorItemDisplay();
            out1Display.setScale(outScale + (ov != null ? ov.scale : 0f));
            out1Display.setRotation(flatRotation(facing, addRot(out2Rot, ov)));
            int h = o1.hashCode();
            out1Display.setNmsItem(org.bukkit.craftbukkit.inventory.CraftItemStack.asNMSCopy(o1));
            out1Display.render(viewers, masterPos.x() + o.x, masterPos.y() + o.y, masterPos.z() + o.z, h != lastOut1Hash);
            out1Display.consumeRotationDirty();
            lastOut1Hash = h;
        } else {
            despawn(world, masterPos, out1Display);
            out1Display = null;
            lastOut1Hash = 0;
        }
    }

    /** Output item(s) the stored blueprint's recipe produces (static result, independent of inputs). */
    private java.util.List<org.bukkit.inventory.ItemStack> blueprintRecipeOutputs(org.bukkit.inventory.ItemStack bp) {
        java.util.List<org.bukkit.inventory.ItemStack> out = new java.util.ArrayList<>(2);
        if (bp == null || bp.getType().isAir())
            return out;
        net.momirealms.craftengine.core.util.Key tool = toolItemId(bp);
        if (tool == null)
            return out;
        StationRecipe r = StationRecipeRegistry.global().byTool(tool).orElse(null);
        if (r == null)
            return out;
        for (CraftCell c : r.base().outputs()) {
            org.bukkit.inventory.ItemStack s = CraftItemAdapter.toBukkit(c);
            if (s != null && !s.getType().isAir())
                out.add(s);
        }
        return out;
    }

    private static net.momirealms.craftengine.core.util.Key toolItemId(org.bukkit.inventory.ItemStack bukkit) {
        if (bukkit == null || bukkit.getType().isAir() || bukkit.getAmount() <= 0)
            return null;
        net.momirealms.craftengine.core.util.Key custom =
                net.momirealms.craftengine.bukkit.api.CraftEngineItems.getCustomItemId(bukkit);
        if (custom != null)
            return custom;
        org.bukkit.NamespacedKey nk = bukkit.getType().getKey();
        return net.momirealms.craftengine.core.util.Key.of(nk.getNamespace(), nk.getKey());
    }

    private WorkbenchBehavior.RenderOverride override(WorkbenchBehavior cfg, org.bukkit.inventory.ItemStack it) {
        if (cfg == null || cfg.renders.isEmpty() || it == null)
            return null;
        net.momirealms.craftengine.core.util.Key id = toolItemId(it);
        return id == null ? null : cfg.renders.get(id.toString());
    }

    private static float[] addPos(float[] base, WorkbenchBehavior.RenderOverride ov) {
        return ov == null ? base
                : new float[] { base[0] + ov.pos[0], base[1] + ov.pos[1], base[2] + ov.pos[2] };
    }

    private static float[] addRot(float[] base, WorkbenchBehavior.RenderOverride ov) {
        return ov == null ? base
                : new float[] { base[0] + ov.rot[0], base[1] + ov.rot[1], base[2] + ov.rot[2] };
    }

    private void despawn(CEWorld world, BlockPos masterPos, ConveyorItemDisplay d) {
        if (d == null)
            return;
        for (net.momirealms.craftengine.core.entity.player.Player p : world.world().getTrackedBy(new ChunkPos(masterPos)))
            d.despawn(p);
        d.clearShown();
    }

    // ---------------- break / cleanup ----------------

    @Override
    public void onRemove() {
        super.onRemove();
        try {
            CEWorld world = blockEntity().world();
            BlockPos pos = blockEntity().pos();
            // Drop the stored blueprint (use the cache — the block may already be air here).
            org.bukkit.inventory.ItemStack bp = cachedBlueprint != null ? cachedBlueprint : getBlueprint();
            if (bp != null && !bp.getType().isAir() && world != null) {
                org.bukkit.World bw = (org.bukkit.World) world.world().platformWorld();
                if (bw != null)
                    bw.dropItemNaturally(new org.bukkit.Location(bw, pos.x() + 0.5, pos.y() + 0.5, pos.z() + 0.5), bp);
            }
            // Clear the persisted data so a future block at this position doesn't read a stale blueprint.
            remove(BP_KEY.getKey());
            cachedBlueprint = null;
            // Kill any rendered displays.
            if (world != null) {
                despawn(world, pos, bpDisplay);
                despawn(world, pos, out0Display);
                despawn(world, pos, out1Display);
            }
        } catch (Throwable ignored) {
        }
        bpDisplay = out0Display = out1Display = null;
    }
}
