package dev.arubik.craftengine.virtualui.render;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.momirealms.craftengine.bukkit.entity.data.DisplayData;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** {@link WidgetVisual} for a {@code block_display} — the block-model counterpart to
 *  {@link ItemIdVisual}, backing {@link dev.arubik.craftengine.virtualui.model.Widget.BlockWidget}.
 *  Resolves through CraftEngine's own block registry FIRST (so a widget can show a CraftEngine
 *  custom block, e.g. {@code "cml:some_custom_block"} — {@code customBlockState().minecraftState()}
 *  is the same "CE state -> the real vanilla-mapped NMS BlockState" call this codebase's own
 *  renderers use, see {@code ChainRenderer}/{@code ContraptionEngine}), falling back to vanilla's
 *  default state for a plain id. A full blockstate-property string isn't supported (out of scope
 *  for a UI icon — the block's DEFAULT state is what renders). */
public final class BlockIdVisual implements WidgetVisual {

    private final String blockId;
    private final float scale;
    private final Quaternionf rotation;

    public BlockIdVisual(String blockId, float scale, Quaternionf rotation) {
        this.blockId = blockId;
        this.scale = scale;
        // No billboard on a block display — its "north" face renders in a fixed world direction
        // regardless of where the player is standing, which read as facing away/backwards for most
        // camera headings. Bake in a 180-degree yaw flip so the block's default-facing side reads
        // toward the player like the item/icon widgets do.
        this.rotation = (rotation != null ? new Quaternionf(rotation) : new Quaternionf())
                .rotateY((float) Math.PI);
    }

    @Override
    public EntityType<?> entityType() { return EntityType.BLOCK_DISPLAY; }

    @Override
    public List<Object> buildMetadata() {
        List<Object> values = new ArrayList<>();
        BlockState state = resolveState();
        if (state != null) {
            try { DisplayData.BlockDisplayData.BlockState.addEntityData(state, values); } catch (Throwable ignored) {}
        }
        // Pivot about the model centre, same reasoning as ChainBlockDisplay's own Translation math.
        Vector3f t = rotation.transform(new Vector3f(-0.5f * scale, -0.5f * scale, -0.5f * scale));
        DisplayData.Translation.addEntityData(t, values);
        DisplayData.LeftRotation.addEntityData(rotation, values);
        DisplayData.Scale.addEntityData(new Vector3f(scale, scale, scale), values);
        DisplayData.BrightnessOverride.addEntityData((15 << 4) | (15 << 20), values);
        DisplayData.PosRotInterpolationDuration.addEntityData(2, values);
        DisplayData.TransformationInterpolationDuration.addEntityData(2, values);
        return values;
    }

    private BlockState resolveState() {
        if (blockId == null || blockId.isBlank()) return null;
        try {
            net.momirealms.craftengine.core.util.Key key = net.momirealms.craftengine.core.util.Key.of(blockId);
            net.momirealms.craftengine.core.block.BlockDefinition def =
                    net.momirealms.craftengine.bukkit.api.CraftEngineBlocks.byId(key);
            if (def != null) {
                Object nms = def.defaultState().customBlockState().minecraftState();
                if (nms instanceof BlockState bs) return bs;
            }
        } catch (Throwable ignored) {}
        try {
            Identifier loc = Identifier.parse(blockId.contains(":") ? blockId : "minecraft:" + blockId);
            net.minecraft.world.level.block.Block block = BuiltInRegistries.BLOCK.getValue(loc);
            return block != null ? block.defaultBlockState() : null;
        } catch (Throwable t) {
            return null;
        }
    }

    @Override
    public boolean sameAs(WidgetVisual other) {
        return other instanceof BlockIdVisual b
                && Objects.equals(blockId, b.blockId)
                && Math.abs(scale - b.scale) < 1.0e-4f
                && rotation.equals(b.rotation, 1.0e-4f);
    }
}
