package dev.arubik.craftengine.fluid.render;

import java.util.ArrayList;
import java.util.List;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import dev.arubik.craftengine.fluid.FluidStack;
import dev.arubik.craftengine.fluid.FluidType;
import dev.arubik.craftengine.fluid.FluidTransferHelper;
import dev.arubik.craftengine.fluid.behavior.FluidCarrier;
import dev.arubik.craftengine.util.Utils;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.bukkit.world.BukkitWorld;
import net.momirealms.craftengine.core.block.entity.render.element.BlockEntityElementConfig;
import net.momirealms.craftengine.core.block.entity.render.element.BlockEntityElementConfigFactory;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.world.BlockPos;
import net.momirealms.craftengine.core.world.chunk.CEChunk;

/**
 * Config-driven fluid renderer element (see {@link FluidDisplayElement}). Parameterised entirely from the
 * block's {@code entity-renderer} config so it can be reused by any block/multiblock — NOT tied to the tank:
 * box dimensions (width/height/length), a position offset, an arbitrary rotation about a pivot, the per-cell
 * scale, the fluid source (read from the block's {@link FluidCarrier}, or explicit type/amount/max), and the
 * frame-cap clamps (which levels each capped layer may show + the bottom lift) — all configurable.
 */
public final class FluidDisplayElementConfig implements BlockEntityElementConfig<FluidDisplayElement> {

    public static final Factory FACTORY = new Factory();

    public final Vector3f position;     // offset from the block origin (block units)
    public final float width, height, length; // EXACT box size (block units); cell count = round(dim)
    public final Quaternionf rotation;  // arbitrary orientation (also the display LeftRotation)
    public final Vector3f pivot;        // rotation pivot, local to position
    public final Vector3f scale;        // per-cell display scale
    public final int blockLight, skyLight;

    // fluid source
    public final boolean fromBlock;     // true: read the block's carrier; false: explicit values below
    public final FluidType explicitType;
    public final long explicitAmount, explicitMax;

    // frame caps
    public final boolean capBottom, capTop;
    public final float lift;            // bottom-layer Y lift (block units)
    public final int oneCapMax, twoCapMax, openMax, minLevel;

    public final String itemNamespace, itemTemplate; // e.g. cml / "fluidlvl_%s_%d"

    public FluidDisplayElementConfig(Vector3f position, float width, float height, float length, Quaternionf rotation,
            Vector3f pivot, Vector3f scale, int blockLight, int skyLight, boolean fromBlock,
            FluidType explicitType, long explicitAmount, long explicitMax, boolean capBottom, boolean capTop,
            float lift, int oneCapMax, int twoCapMax, int openMax, int minLevel, String itemNamespace,
            String itemTemplate) {
        this.position = position;
        this.width = Math.max(0.01f, width);
        this.height = Math.max(0.01f, height);
        this.length = Math.max(0.01f, length);
        this.rotation = rotation;
        this.pivot = pivot;
        this.scale = scale;
        this.blockLight = blockLight;
        this.skyLight = skyLight;
        this.fromBlock = fromBlock;
        this.explicitType = explicitType;
        this.explicitAmount = explicitAmount;
        this.explicitMax = explicitMax;
        this.capBottom = capBottom;
        this.capTop = capTop;
        this.lift = lift;
        this.oneCapMax = oneCapMax;
        this.twoCapMax = twoCapMax;
        this.openMax = openMax;
        this.minLevel = minLevel;
        this.itemNamespace = itemNamespace;
        this.itemTemplate = itemTemplate;
    }

    /** One item_display: absolute world centre + scale + the NMS level item. */
    static final class Slot {
        final double x, y, z;
        final Vector3f scale;
        final Object nmsItem;

        Slot(double x, double y, double z, Vector3f scale, Object nmsItem) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.scale = scale;
            this.nmsItem = nmsItem;
        }
    }

    @Override
    public FluidDisplayElement create(CEChunk chunk, BlockPos pos) {
        double fill;
        FluidType type;
        if (fromBlock) {
            FluidStack stored = readFluid(chunk, pos);
            type = (stored == null || stored.isEmpty()) ? FluidType.EMPTY : stored.getType();
            long max = Math.max(1L, readCapacity(chunk, pos));
            fill = type == FluidType.EMPTY ? 0.0 : Math.min(1.0, stored.getAmount() / (double) max);
        } else {
            type = explicitType;
            fill = explicitType == FluidType.EMPTY ? 0.0 : Math.min(1.0, explicitAmount / (double) Math.max(1L, explicitMax));
        }
        return new FluidDisplayElement(this, buildSlots(pos, type, fill));
    }

    private List<Slot> buildSlots(BlockPos pos, FluidType type, double fill) {
        List<Slot> slots = new ArrayList<>();
        if (type == FluidType.EMPTY || fill <= 0.0)
            return slots;
        String name = fluidName(type);
        // Cell count = nearest integer to each dimension (1.9 -> 2). Each cell is then sized so the WHOLE box
        // is EXACTLY width×height×length and cells abut with no gaps (native models, just scaled per axis).
        int cellsX = Math.max(1, Math.round(width));
        int cellsY = Math.max(1, Math.round(height));
        int cellsZ = Math.max(1, Math.round(length));
        float cellW = width / cellsX, cellH = height / cellsY, cellL = length / cellsZ;
        double fluidBlocks = fill * cellsY; // fill expressed in layer units
        for (int y = 0; y < cellsY; y++) {
            double layerFill = Math.max(0.0, Math.min(1.0, fluidBlocks - y));
            if (y > 0 && layerFill <= 0.001)
                break;
            int rawLevel = (int) Math.round(layerFill * 15);
            if (y > 0 && rawLevel <= 0)
                break;
            boolean cap0 = capBottom && y == 0;
            boolean capT = capTop && y == cellsY - 1;
            int hi = (cap0 && capT) ? twoCapMax : ((cap0 || capT) ? oneCapMax : openMax);
            int lvl = Math.max(minLevel, Math.min(hi, rawLevel));
            float yoff = cap0 ? lift : 0f;
            Object nms = FluidDisplayElement.toNms(levelItem(name, lvl));
            if (nms == null)
                continue;
            for (int dx = 0; dx < cellsX; dx++)
                for (int dz = 0; dz < cellsZ; dz++) {
                    // span of this cell within the exact box (cells abut, no gaps).
                    float x0 = dx * cellW, x1 = (dx + 1) * cellW;
                    float z0 = dz * cellL, z1 = (dz + 1) * cellL;
                    Vector3f local = new Vector3f(position.x + (x0 + x1) / 2f,
                            position.y + y * cellH + cellH / 2f + yoff, position.z + (z0 + z1) / 2f);
                    Vector3f rel = new Vector3f(local).sub(pivot);
                    rotation.transform(rel);
                    Vector3f world = rel.add(pivot);
                    Vector3f cellScale = new Vector3f(scale.x * (x1 - x0), scale.y * cellH, scale.z * (z1 - z0));
                    slots.add(new Slot(pos.x() + world.x, pos.y() + world.y, pos.z() + world.z, cellScale, nms));
                }
        }
        return slots;
    }

    private static String fluidName(FluidType type) {
        switch (type) {
            case LAVA:
            case HONEY:
            case SLIME:
                return "lava";
            case EXPERIENCE:
                return "xp";
            default:
                return "water";
        }
    }

    private org.bukkit.inventory.ItemStack levelItem(String name, int lvl) {
        try {
            String path = String.format(itemTemplate, name, Math.max(0, Math.min(15, lvl)));
            var d = CraftEngineItems.byId(Key.of(itemNamespace, path));
            return d != null ? d.buildBukkitItem() : null;
        } catch (Throwable t) {
            return null;
        }
    }

    private static Level levelOf(CEChunk chunk) {
        return (Level) ((BukkitWorld) chunk.world().world()).minecraftWorld();
    }

    private FluidStack readFluid(CEChunk chunk, BlockPos pos) {
        try {
            Level level = levelOf(chunk);
            net.minecraft.core.BlockPos np = new net.minecraft.core.BlockPos(pos.x(), pos.y(), pos.z());
            FluidCarrier c = FluidTransferHelper.getCarrier(level, np).orElse(null);
            return c != null ? c.getStored(level, np) : FluidStack.EMPTY;
        } catch (Throwable t) {
            return FluidStack.EMPTY;
        }
    }

    private long readCapacity(CEChunk chunk, BlockPos pos) {
        try {
            Level level = levelOf(chunk);
            net.minecraft.core.BlockPos np = new net.minecraft.core.BlockPos(pos.x(), pos.y(), pos.z());
            FluidCarrier c = FluidTransferHelper.getCarrier(level, np).orElse(null);
            return c != null ? c.getCapacity(level, np) : 1L;
        } catch (Throwable t) {
            return 1L;
        }
    }

    @Override
    public Class<FluidDisplayElement> elementClass() {
        return FluidDisplayElement.class;
    }

    public static final class Factory implements BlockEntityElementConfigFactory<FluidDisplayElement> {
        @Override
        public FluidDisplayElementConfig create(ConfigSection a) {
            float w = Utils.getAsFloat(a.getOrDefault("width", 1f), "width");
            float h = Utils.getAsFloat(a.getOrDefault("height", 1f), "height");
            float l = Utils.getAsFloat(a.getOrDefault("length", 1f), "length");
            Vector3f pos = Utils.getAsVector3f(a.getOrDefault("position", 0f), "position");
            Vector3f euler = Utils.getAsVector3f(a.getOrDefault("rotation", 0f), "rotation"); // degrees
            Quaternionf rot = new Quaternionf().rotationXYZ(
                    (float) Math.toRadians(euler.x), (float) Math.toRadians(euler.y), (float) Math.toRadians(euler.z));
            Vector3f pivot = Utils.getAsVector3f(a.getOrDefault("pivot",
                    new java.util.ArrayList<>(List.of(w / 2f, 0f, l / 2f))), "pivot");
            Vector3f scale = Utils.getAsVector3f(a.getOrDefault("scale", 1f), "scale");
            boolean fromBlock = !"explicit"
                    .equalsIgnoreCase(String.valueOf(a.getOrDefault("fluid-source", "block")));
            FluidType type = Utils.getAsEnum(a.getOrDefault("fluid-type", "EMPTY"), FluidType.class, FluidType.EMPTY);
            long amount = (long) Utils.getAsInt(a.getOrDefault("fluid-amount", 0), "fluid-amount");
            long max = (long) Utils.getAsInt(a.getOrDefault("fluid-max", 1), "fluid-max");
            boolean capB = Boolean.parseBoolean(String.valueOf(a.getOrDefault("cap-bottom", true)));
            boolean capT = Boolean.parseBoolean(String.valueOf(a.getOrDefault("cap-top", true)));
            float lift = Utils.getAsFloat(a.getOrDefault("cap-lift", 0f), "cap-lift");
            int oneMax = Utils.getAsInt(a.getOrDefault("cap-one-max", 11), "cap-one-max");
            int twoMax = Utils.getAsInt(a.getOrDefault("cap-two-max", 7), "cap-two-max");
            int openMax = Utils.getAsInt(a.getOrDefault("cap-open-max", 15), "cap-open-max");
            int minLvl = Utils.getAsInt(a.getOrDefault("cap-min", 0), "cap-min");
            int bl = Utils.getAsInt(a.getOrDefault("block-light", 15), "block-light");
            int sl = Utils.getAsInt(a.getOrDefault("sky-light", 15), "sky-light");
            String ns = String.valueOf(a.getOrDefault("item-namespace", "cml"));
            String tpl = String.valueOf(a.getOrDefault("item-template", "fluidlvl_%s_%d"));
            return new FluidDisplayElementConfig(pos, w, h, l, rot, pivot, scale, bl, sl, fromBlock, type,
                    amount, max, capB, capT, lift, oneMax, twoMax, openMax, minLvl, ns, tpl);
        }
    }
}
