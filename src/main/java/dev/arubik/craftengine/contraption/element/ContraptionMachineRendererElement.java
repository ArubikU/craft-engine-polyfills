package dev.arubik.craftengine.contraption.element;

import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.machine.render.ModelRendersDriven;
import dev.arubik.craftengine.machine.render.ParticleUtils;
import dev.arubik.craftengine.machine.render.RendererManager;
import dev.arubik.craftengine.machine.render.RendererSpec;
import dev.arubik.craftengine.util.MNms;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.bukkit.entity.data.DisplayData;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.plugin.CraftEngine;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.world.CEWorld;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Renders a {@link ModelRendersDriven} machine's data-driven renderer specs
 * ({@link RendererSpec.ItemDisplaySpec}, {@link RendererSpec.TextDisplaySpec},
 * {@link RendererSpec.FluidTankSpec}, {@link RendererSpec.ParticleSpec})
 * inside a moving contraption, using the full contraption transform via
 * {@link ContraptionMath#renderPosition}.
 *
 * <p><b>How it works.</b> The machine's own {@code DataMachineBlockEntity.tick()} already
 * calls {@code RendererManager.tick()} each game tick, evaluating conditions and caching
 * the results in {@link RendererManager.EvalResult}. Because the machine lives in a
 * hidden {@link ContraptionLevel}, its {@code serverLevel.players()} is always empty —
 * so no entities are spawned and no particles are sent by the normal tick path. This
 * element reads the cached state and renders each spec at the correct real-world position.
 *
 * <p><b>Spec coverage.</b>
 * <ul>
 *   <li>{@link RendererSpec.ItemDisplaySpec} — packet-only {@code ITEM_DISPLAY} entity,
 *       per-viewer, follows full contraption pose.</li>
 *   <li>{@link RendererSpec.TextDisplaySpec} — packet-only {@code TEXT_DISPLAY} entity,
 *       per-viewer.</li>
 *   <li>{@link RendererSpec.FluidTankSpec} — packet-only {@code ITEM_DISPLAY} entity
 *       showing the {@code cml:fluidlvl_*} item, per-viewer.</li>
 *   <li>{@link RendererSpec.ParticleSpec} — emits via Bukkit {@code spawnParticle} on
 *       the real world (visible to all nearby players on the real level, not per-viewer
 *       condition evaluation — per-viewer particle conditions are deferred to a future pass).</li>
 *   <li>{@link RendererSpec.BetterModelSpec} / {@link RendererSpec.ModelEngineSpec} —
 *       handled by the pre-existing {@code ContraptionBetterModelElement} /
 *       {@code ContraptionModelEngineElement}; excluded here intentionally.</li>
 * </ul>
 */
public final class ContraptionMachineRendererElement implements ContraptionElement {

    private final BlockPos localPos;

    /** Manager resolved from the live CE block entity — null until the first tick. */
    private RendererManager lastManager;

    /** Packet-only ITEM_DISPLAY cells keyed by spec index (ItemDisplaySpec entries). */
    private final Map<Integer, ItemCell> itemCells = new LinkedHashMap<>();
    /** Packet-only TEXT_DISPLAY cells keyed by spec index (TextDisplaySpec entries). */
    private final Map<Integer, TextCell> textCells = new LinkedHashMap<>();
    /** Packet-only ITEM_DISPLAY cells keyed by spec index (FluidTankSpec entries). */
    private final Map<Integer, FluidCell> fluidCells = new LinkedHashMap<>();

    public ContraptionMachineRendererElement(BlockPos localPos) {
        this.localPos = localPos;
    }

    public BlockPos localPos() {
        return localPos;
    }

    @Override
    public Key type() {
        return ElementTypes.MACHINE_RENDERER;
    }

    @Override
    public Vec3 localOffset() {
        return new Vec3(localPos.getX() + 0.5, localPos.getY() + 0.5, localPos.getZ() + 0.5);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public int[] entityIds() {
        List<Integer> ids = new ArrayList<>();
        for (ItemCell c : itemCells.values()) ids.add(c.entityId);
        for (TextCell c : textCells.values()) ids.add(c.entityId);
        for (FluidCell c : fluidCells.values()) ids.add(c.entityId);
        return ids.stream().mapToInt(Integer::intValue).toArray();
    }

    @Override
    public void tick(RenderContext ctx) {
        RendererManager mgr = resolveManager(ctx.level(), localPos);
        if (mgr == lastManager) return; // unchanged — existing cells remain valid
        // Manager changed (or first tick) — rebuild cells.
        despawnAllCells(ctx.viewers());
        itemCells.clear();
        textCells.clear();
        fluidCells.clear();
        lastManager = mgr;
        if (mgr == null) return;
        List<RendererSpec> specs = mgr.specs();
        for (int i = 0; i < specs.size(); i++) {
            RendererSpec spec = specs.get(i);
            if (spec instanceof RendererSpec.ItemDisplaySpec) {
                itemCells.put(i, new ItemCell(i));
            } else if (spec instanceof RendererSpec.TextDisplaySpec) {
                textCells.put(i, new TextCell(i));
            } else if (spec instanceof RendererSpec.FluidTankSpec) {
                fluidCells.put(i, new FluidCell(i));
            }
            // BetterModelSpec / ModelEngineSpec: handled by ContraptionBetterModelElement /
            // ContraptionModelEngineElement; ParticleSpec: handled inline in render().
        }
    }

    @Override
    public void render(RenderContext ctx) {
        if (lastManager == null) return;
        List<RendererSpec> specs = lastManager.specs();

        // Read ambient light at the block's approximate world center, shared across all cells.
        int blockLight = 15, skyLight = 15;
        if (ctx.realLevel() != null) {
            try {
                Vec3 worldCenter = ContraptionMath.renderPosition(
                        new Vec3(localPos.getX() + 0.5, localPos.getY() + 0.5, localPos.getZ() + 0.5),
                        ctx.bearing(), ctx.yawRadians(), ctx.pitchRadians(), ctx.rollRadians(), ctx.scale());
                BlockPos at = BlockPos.containing(worldCenter.x, worldCenter.y, worldCenter.z);
                blockLight = ctx.realLevel().getLightEngine()
                        .getLayerListener(LightLayer.BLOCK).getLightValue(at);
                skyLight = ctx.realLevel().getLightEngine()
                        .getLayerListener(LightLayer.SKY).getLightValue(at);
            } catch (Throwable ignored) {}
        }
        if (ctx.lightMap() != null) {
            blockLight = ctx.lightMap().combinedBlockLight(localPos, blockLight);
        }

        for (int i = 0; i < specs.size(); i++) {
            RendererSpec spec = specs.get(i);
            RendererManager.EvalResult er = lastManager.evalResult(i);
            if (er == null) continue;

            if (spec instanceof RendererSpec.ItemDisplaySpec idSpec) {
                ItemCell cell = itemCells.get(i);
                if (cell != null) cell.render(ctx, er, idSpec, blockLight, skyLight);
            } else if (spec instanceof RendererSpec.TextDisplaySpec tdSpec) {
                TextCell cell = textCells.get(i);
                if (cell != null) cell.render(ctx, er, tdSpec, blockLight, skyLight);
            } else if (spec instanceof RendererSpec.FluidTankSpec ftSpec) {
                FluidCell cell = fluidCells.get(i);
                if (cell != null) cell.render(ctx, er, ftSpec, blockLight, skyLight);
            } else if (spec instanceof RendererSpec.ParticleSpec psSpec) {
                if (er.active && er.emittedThisTick && ctx.realLevel() != null) {
                    emitParticles(ctx, er, psSpec);
                }
            }
        }
    }

    @Override
    public void despawn(List<Player> viewers) {
        despawnAllCells(viewers);
    }

    @Override
    public void disassemble(ServerLevel level, BlockPos bearingPos, int quarterTurns) {
        // Packet-only entities — no real entities placed in the world to clean up.
    }

    // ---- helpers ----

    private void despawnAllCells(List<Player> viewers) {
        for (ItemCell c : itemCells.values()) c.despawnAll(viewers);
        for (TextCell c : textCells.values()) c.despawnAll(viewers);
        for (FluidCell c : fluidCells.values()) c.despawnAll(viewers);
    }

    private static RendererManager resolveManager(ContraptionLevel level, BlockPos local) {
        if (level == null) return null;
        try {
            CEWorld ceWorld = CraftEngine.instance().worldManager().getWorld(level.getWorld().getUID());
            if (ceWorld == null) return null;
            net.momirealms.craftengine.core.world.BlockPos cePos =
                    new net.momirealms.craftengine.core.world.BlockPos(local.getX(), local.getY(), local.getZ());
            BlockEntity be = ceWorld.getBlockEntityAtIfLoaded(cePos, false);
            if (be != null && be.controller instanceof ModelRendersDriven mrd) {
                return mrd.rendererManager();
            }
        } catch (Throwable ignored) {}
        return null;
    }

    /**
     * Emits particles for the given spec in the real world at the block's contraption-projected
     * position. Uses Bukkit {@code spawnParticle} so all nearby players on the real level receive
     * them uniformly (per-viewer condition evaluation for particles in contraptions is deferred).
     */
    private void emitParticles(RenderContext ctx, RendererManager.EvalResult er, RendererSpec.ParticleSpec spec) {
        try {
            Vec3 localWithOffset = new Vec3(
                    localPos.getX() + 0.5 + er.specRelX,
                    localPos.getY() + er.specRelY,
                    localPos.getZ() + 0.5 + er.specRelZ);
            Vec3 worldPos = ContraptionMath.renderPosition(localWithOffset, ctx.bearing(),
                    ctx.yawRadians(), ctx.pitchRadians(), ctx.rollRadians(), ctx.scale());
            org.bukkit.World bworld = ctx.realLevel().getWorld();
            org.bukkit.Particle particle = org.bukkit.Particle.valueOf(
                    er.pType.toUpperCase(java.util.Locale.ROOT));
            ParticleUtils.emit(bworld, particle, worldPos.x, worldPos.y, worldPos.z,
                    er.pSpreadX, er.pSpreadY, er.pSpreadZ,
                    er.pShape, er.pDir,
                    er.particleCount, er.pSpeed, er.pDvx, er.pDvy, er.pDvz);
        } catch (Throwable ignored) {}
    }

    private static void addInterpolation(List<Object> values) {
        DisplayData.PosRotInterpolationDuration.addEntityData(2, values);
        DisplayData.TransformationInterpolationDuration.addEntityData(2, values);
    }

    private static byte itemBillboardByte(String mode) {
        if (mode == null) return 0;
        return switch (mode) {
            case "vertical"   -> (byte) 1;
            case "horizontal" -> (byte) 2;
            case "center"     -> (byte) 3;
            default           -> (byte) 0; // "none"
        };
    }

    // =========================================================================
    // ItemDisplaySpec cell
    // =========================================================================

    private final class ItemCell {
        final int specIndex;
        final int entityId;
        final UUID entityUuid;
        final Object despawnPacket;
        final Set<UUID> shownTo = ConcurrentHashMap.newKeySet();

        // Change-detection state
        org.bukkit.inventory.ItemStack lastItem;
        int lastBlockLight = -1, lastSkyLight = -1;
        double lastScale = 1.0;

        ItemCell(int specIndex) {
            this.specIndex = specIndex;
            this.entityId = Entity.nextEntityId();
            this.entityUuid = UUID.randomUUID();
            this.despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(
                    IntList.of(entityId));
        }

        void render(RenderContext ctx, RendererManager.EvalResult er,
                    RendererSpec.ItemDisplaySpec spec, int blockLight, int skyLight) {
            if (!er.active || er.itemDisplay == null) {
                despawnAll(ctx.viewers());
                shownTo.clear();
                return;
            }
            RendererSpec.EvaluatedItemDisplay ev = er.itemDisplay;
            Vec3 localWithOffset = new Vec3(
                    localPos.getX() + 0.5 + ev.offsetX(),
                    localPos.getY() + ev.offsetY(),
                    localPos.getZ() + 0.5 + ev.offsetZ());
            Vec3 worldPos = ContraptionMath.renderPosition(localWithOffset, ctx.bearing(),
                    ctx.yawRadians(), ctx.pitchRadians(), ctx.rollRadians(), ctx.scale());
            float yawDeg = ev.rotY() + (float) ctx.yawDegrees();

            boolean itemChanged = ev.item() != lastItem;
            boolean metaChanged = itemChanged
                    || blockLight != lastBlockLight || skyLight != lastSkyLight
                    || ctx.scale() != lastScale;
            lastItem = ev.item();
            lastBlockLight = blockLight;
            lastSkyLight = skyLight;
            lastScale = ctx.scale();

            Set<UUID> current = new HashSet<>();
            for (Player p : ctx.viewers()) {
                UUID id = p.uuid();
                if (id == null) continue;
                current.add(id);
                if (shownTo.add(id)) {
                    spawnFor(p, worldPos, yawDeg, ev, blockLight, skyLight, spec);
                } else if (ctx.moved()) {
                    updatePositionFor(p, worldPos, yawDeg, ev);
                    if (metaChanged) updateMetaFor(p, ev, blockLight, skyLight, spec);
                } else if (metaChanged) {
                    updateMetaFor(p, ev, blockLight, skyLight, spec);
                }
            }
            shownTo.retainAll(current);
        }

        private void spawnFor(Player p, Vec3 pos, float yawDeg,
                               RendererSpec.EvaluatedItemDisplay ev,
                               int bl, int sl, RendererSpec.ItemDisplaySpec spec) {
            Object add = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(entityId, entityUuid,
                    pos.x, pos.y, pos.z, ev.rotX(), yawDeg, EntityType.ITEM_DISPLAY, 0, Vec3.ZERO, 0);
            Object data = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(
                    entityId, buildMeta(ev, bl, sl, spec));
            p.sendPackets(List.of(add, data), false);
        }

        private void updatePositionFor(Player p, Vec3 pos, float yawDeg,
                                        RendererSpec.EvaluatedItemDisplay ev) {
            p.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(
                    entityId, pos.x, pos.y, pos.z, yawDeg, ev.rotX(), false), false);
        }

        private void updateMetaFor(Player p, RendererSpec.EvaluatedItemDisplay ev,
                                    int bl, int sl, RendererSpec.ItemDisplaySpec spec) {
            p.sendPacket(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(
                    entityId, buildMeta(ev, bl, sl, spec)), false);
        }

        private List<Object> buildMeta(RendererSpec.EvaluatedItemDisplay ev,
                                        int bl, int sl, RendererSpec.ItemDisplaySpec spec) {
            List<Object> values = new ArrayList<>();
            if (ev.item() != null) {
                Object nmsItem = org.bukkit.craftbukkit.inventory.CraftItemStack.asNMSCopy(ev.item());
                DisplayData.ItemDisplayData.ItemStack.addEntityData(nmsItem, values);
            }
            float s = ev.scale();
            DisplayData.Scale.addEntityData(new Vector3f(s, s, s), values);
            float rx = ev.rotX(), ry = ev.rotY(), rz = ev.rotZ();
            if (rx != 0 || ry != 0 || rz != 0) {
                Quaternionf q = new Quaternionf()
                        .rotateY((float) Math.toRadians(ry))
                        .rotateX((float) Math.toRadians(rx))
                        .rotateZ((float) Math.toRadians(rz));
                DisplayData.LeftRotation.addEntityData(q, values);
            }
            byte bb = itemBillboardByte(spec.billboard());
            if (bb != 0) DisplayData.BillboardConstraints.addEntityData(bb, values);
            DisplayData.BrightnessOverride.addEntityData((bl << 4) | (sl << 20), values);
            addInterpolation(values);
            return values;
        }

        void despawnAll(List<Player> viewers) {
            for (Player p : viewers) {
                if (shownTo.remove(p.uuid())) p.sendPacket(despawnPacket, false);
            }
        }
    }

    // =========================================================================
    // TextDisplaySpec cell
    // =========================================================================

    private final class TextCell {
        final int specIndex;
        final int entityId;
        final UUID entityUuid;
        final Object despawnPacket;
        final Set<UUID> shownTo = ConcurrentHashMap.newKeySet();

        String lastText;
        float lastScale = -1f;
        int lastBlockLight = -1, lastSkyLight = -1;
        double lastScale3d = 1.0;

        TextCell(int specIndex) {
            this.specIndex = specIndex;
            this.entityId = Entity.nextEntityId();
            this.entityUuid = UUID.randomUUID();
            this.despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(
                    IntList.of(entityId));
        }

        void render(RenderContext ctx, RendererManager.EvalResult er,
                    RendererSpec.TextDisplaySpec spec, int blockLight, int skyLight) {
            if (!er.active) {
                despawnAll(ctx.viewers());
                shownTo.clear();
                return;
            }
            Vec3 localWithOffset = new Vec3(
                    localPos.getX() + 0.5 + er.tdOx,
                    localPos.getY() + er.tdOy,
                    localPos.getZ() + 0.5 + er.tdOz);
            Vec3 worldPos = ContraptionMath.renderPosition(localWithOffset, ctx.bearing(),
                    ctx.yawRadians(), ctx.pitchRadians(), ctx.rollRadians(), ctx.scale());
            float yawDeg = (float) ctx.yawDegrees();

            boolean metaChanged = !Objects.equals(er.textContent, lastText)
                    || Float.compare(er.textScale, lastScale) != 0
                    || blockLight != lastBlockLight || skyLight != lastSkyLight
                    || ctx.scale() != lastScale3d;
            lastText = er.textContent;
            lastScale = er.textScale;
            lastBlockLight = blockLight;
            lastSkyLight = skyLight;
            lastScale3d = ctx.scale();

            Set<UUID> current = new HashSet<>();
            for (Player p : ctx.viewers()) {
                UUID id = p.uuid();
                if (id == null) continue;
                current.add(id);
                if (shownTo.add(id)) {
                    spawnFor(p, worldPos, yawDeg, er, spec, blockLight, skyLight);
                } else if (ctx.moved()) {
                    updatePositionFor(p, worldPos, yawDeg);
                    if (metaChanged) updateMetaFor(p, er, spec, blockLight, skyLight);
                } else if (metaChanged) {
                    updateMetaFor(p, er, spec, blockLight, skyLight);
                }
            }
            shownTo.retainAll(current);
        }

        private void spawnFor(Player p, Vec3 pos, float yawDeg,
                               RendererManager.EvalResult er, RendererSpec.TextDisplaySpec spec,
                               int bl, int sl) {
            Object add = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(entityId, entityUuid,
                    pos.x, pos.y, pos.z, 0f, yawDeg, EntityType.TEXT_DISPLAY, 0, Vec3.ZERO, 0);
            Object data = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(
                    entityId, buildMeta(er, spec, bl, sl));
            p.sendPackets(List.of(add, data), false);
        }

        private void updatePositionFor(Player p, Vec3 pos, float yawDeg) {
            p.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(
                    entityId, pos.x, pos.y, pos.z, yawDeg, 0f, false), false);
        }

        private void updateMetaFor(Player p, RendererManager.EvalResult er,
                                    RendererSpec.TextDisplaySpec spec, int bl, int sl) {
            p.sendPacket(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(
                    entityId, buildMeta(er, spec, bl, sl)), false);
        }

        private List<Object> buildMeta(RendererManager.EvalResult er,
                                        RendererSpec.TextDisplaySpec spec, int bl, int sl) {
            List<Object> values = new ArrayList<>();
            try {
                DisplayData.TextDisplayData.Text.addEntityData(
                        net.minecraft.network.chat.Component.literal(
                                er.textContent != null ? er.textContent : ""),
                        values);
            } catch (Throwable ignored) {}
            DisplayData.Scale.addEntityData(
                    new Vector3f(er.textScale, er.textScale, er.textScale), values);
            try {
                byte bb = textBillboardByte(spec.billboard());
                DisplayData.BillboardConstraints.addEntityData(bb, values);
            } catch (Throwable ignored) {}
            try {
                int bgColor = parseColor(spec.backgroundExpr());
                DisplayData.TextDisplayData.BackgroundColor.addEntityData(bgColor, values);
            } catch (Throwable ignored) {}
            // Style flags, opacity, line-width via NMS reflection (same pattern as TextLevelDisplay).
            try {
                byte styleFlags = 0;
                if (spec.shadow()) styleFlags |= 0x01;
                if (spec.seeThrough()) styleFlags |= 0x02;
                var sf = net.minecraft.world.entity.Display.TextDisplay.class.getDeclaredField("DATA_STYLE_FLAGS");
                sf.setAccessible(true);
                @SuppressWarnings("unchecked")
                var acc = (net.minecraft.network.syncher.EntityDataAccessor<Byte>) sf.get(null);
                values.add(net.minecraft.network.syncher.SynchedEntityData.DataValue.create(acc, styleFlags));
            } catch (Throwable ignored) {}
            try {
                var opField = net.minecraft.world.entity.Display.TextDisplay.class.getDeclaredField("DATA_TEXT_OPACITY");
                opField.setAccessible(true);
                @SuppressWarnings("unchecked")
                var acc = (net.minecraft.network.syncher.EntityDataAccessor<Byte>) opField.get(null);
                values.add(net.minecraft.network.syncher.SynchedEntityData.DataValue.create(acc, (byte) spec.opacity()));
            } catch (Throwable ignored) {}
            try {
                var lwField = net.minecraft.world.entity.Display.TextDisplay.class.getDeclaredField("DATA_LINE_WIDTH");
                lwField.setAccessible(true);
                @SuppressWarnings("unchecked")
                var acc = (net.minecraft.network.syncher.EntityDataAccessor<Integer>) lwField.get(null);
                values.add(net.minecraft.network.syncher.SynchedEntityData.DataValue.create(acc, spec.lineWidth()));
            } catch (Throwable ignored) {}
            DisplayData.BrightnessOverride.addEntityData((bl << 4) | (sl << 20), values);
            addInterpolation(values);
            return values;
        }

        void despawnAll(List<Player> viewers) {
            for (Player p : viewers) {
                if (shownTo.remove(p.uuid())) p.sendPacket(despawnPacket, false);
            }
        }

        private static byte textBillboardByte(String mode) {
            if (mode == null) return 3; // center default
            return switch (mode.toLowerCase(java.util.Locale.ROOT)) {
                case "none" -> (byte) 0;
                case "vertical" -> (byte) 1;
                case "horizontal" -> (byte) 2;
                default -> (byte) 3;
            };
        }

        private static int parseColor(String expr) {
            if (expr == null || expr.equals("0") || expr.isBlank()) return 0;
            try {
                String s = expr.startsWith("#") ? expr.substring(1) : expr;
                return (int) Long.parseLong(s, 16);
            } catch (Throwable ignored) {
                return 0;
            }
        }
    }

    // =========================================================================
    // FluidTankSpec cell
    // =========================================================================

    private final class FluidCell {
        final int specIndex;
        final int entityId;
        final UUID entityUuid;
        final Object despawnPacket;
        final Set<UUID> shownTo = ConcurrentHashMap.newKeySet();

        int lastCeLevel = -1;
        String lastFluidType;
        int lastBlockLight = -1, lastSkyLight = -1;
        double lastScale = 1.0;

        FluidCell(int specIndex) {
            this.specIndex = specIndex;
            this.entityId = Entity.nextEntityId();
            this.entityUuid = UUID.randomUUID();
            this.despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(
                    IntList.of(entityId));
        }

        void render(RenderContext ctx, RendererManager.EvalResult er,
                    RendererSpec.FluidTankSpec spec, int blockLight, int skyLight) {
            if (!er.active || er.ceFluidLevel <= 0) {
                despawnAll(ctx.viewers());
                shownTo.clear();
                return;
            }
            Vec3 localWithOffset = new Vec3(
                    localPos.getX() + 0.5 + er.specRelX,
                    localPos.getY() + er.specRelY,
                    localPos.getZ() + 0.5 + er.specRelZ);
            Vec3 worldPos = ContraptionMath.renderPosition(localWithOffset, ctx.bearing(),
                    ctx.yawRadians(), ctx.pitchRadians(), ctx.rollRadians(), ctx.scale());

            boolean metaChanged = er.ceFluidLevel != lastCeLevel
                    || !Objects.equals(er.fluidTypeValue, lastFluidType)
                    || blockLight != lastBlockLight || skyLight != lastSkyLight
                    || ctx.scale() != lastScale;
            lastCeLevel = er.ceFluidLevel;
            lastFluidType = er.fluidTypeValue;
            lastBlockLight = blockLight;
            lastSkyLight = skyLight;
            lastScale = ctx.scale();

            Set<UUID> current = new HashSet<>();
            for (Player p : ctx.viewers()) {
                UUID id = p.uuid();
                if (id == null) continue;
                current.add(id);
                if (shownTo.add(id)) {
                    spawnFor(p, worldPos, er, spec, blockLight, skyLight);
                } else if (ctx.moved()) {
                    updatePositionFor(p, worldPos);
                    if (metaChanged) updateMetaFor(p, er, spec, blockLight, skyLight);
                } else if (metaChanged) {
                    updateMetaFor(p, er, spec, blockLight, skyLight);
                }
            }
            shownTo.retainAll(current);
        }

        private void spawnFor(Player p, Vec3 pos, RendererManager.EvalResult er,
                               RendererSpec.FluidTankSpec spec, int bl, int sl) {
            Object add = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(entityId, entityUuid,
                    pos.x, pos.y, pos.z, 0f, 0f, EntityType.ITEM_DISPLAY, 0, Vec3.ZERO, 0);
            Object data = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(
                    entityId, buildMeta(er, spec, bl, sl));
            p.sendPackets(List.of(add, data), false);
        }

        private void updatePositionFor(Player p, Vec3 pos) {
            p.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(
                    entityId, pos.x, pos.y, pos.z, 0f, 0f, false), false);
        }

        private void updateMetaFor(Player p, RendererManager.EvalResult er,
                                    RendererSpec.FluidTankSpec spec, int bl, int sl) {
            p.sendPacket(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(
                    entityId, buildMeta(er, spec, bl, sl)), false);
        }

        private List<Object> buildMeta(RendererManager.EvalResult er,
                                        RendererSpec.FluidTankSpec spec, int bl, int sl) {
            List<Object> values = new ArrayList<>();
            try {
                Key fluidKey = Key.of("cml", "fluidlvl_" + er.fluidTypeValue + "_" + er.ceFluidLevel);
                var def = CraftEngineItems.byId(fluidKey);
                if (def != null) {
                    org.bukkit.inventory.ItemStack bukkit = def.buildBukkitItem();
                    if (bukkit != null) {
                        Object nmsItem = org.bukkit.craftbukkit.inventory.CraftItemStack.asNMSCopy(bukkit);
                        DisplayData.ItemDisplayData.ItemStack.addEntityData(nmsItem, values);
                    }
                }
            } catch (Throwable ignored) {}
            float scaleY = spec.maxHeight() * (er.ceFluidLevel / 16f);
            DisplayData.Scale.addEntityData(new Vector3f(1f, scaleY, 1f), values);
            DisplayData.BrightnessOverride.addEntityData((bl << 4) | (sl << 20), values);
            addInterpolation(values);
            return values;
        }

        void despawnAll(List<Player> viewers) {
            for (Player p : viewers) {
                if (shownTo.remove(p.uuid())) p.sendPacket(despawnPacket, false);
            }
        }
    }
}
