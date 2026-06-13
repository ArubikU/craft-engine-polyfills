package dev.arubik.craftengine.machine.render.element;

import com.google.common.base.Objects;
import net.momirealms.craftengine.bukkit.entity.data.BaseEntityData;
import net.momirealms.craftengine.bukkit.entity.data.monster.ShulkerData;
import net.momirealms.craftengine.core.block.entity.render.element.BlockEntityElementConfig;
import net.momirealms.craftengine.core.block.entity.render.element.BlockEntityElementConfigFactory;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.world.BlockPos;
import net.momirealms.craftengine.core.world.World;
import org.bukkit.block.BlockFace;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ShulkerBoxHitboxElementConfig implements BlockEntityElementConfig<ShulkerBoxHitboxElement> {
    public static final Factory FACTORY = new Factory();

    public final Function<Player, List<Object>> lazyMetadataPacket;
    public final Vector3f position;
    public final float xRot;
    public final float yRot;
    public final int color;
    public final int peek;
    public final float scale;
    public final BlockFace attachFace;

    public ShulkerBoxHitboxElementConfig(Vector3f position, float xRot, float yRot, int color, int peek, float scale,
            BlockFace attachFace) {
        this.position = position;
        this.xRot = xRot;
        this.yRot = yRot;
        this.color = color;
        this.peek = peek;
        this.scale = scale;
        this.attachFace = attachFace;

        this.lazyMetadataPacket = player -> {
            List<Object> dataValues = new ArrayList<>();
            // Index 0: Status (Invisible)
            BaseEntityData.SharedFlags.addEntityData((byte) 0x20, dataValues);

            // Index 16: Attach Face
            Object nmsDirection = convertToNMSDirection(attachFace);
            if (nmsDirection != null) {
                ShulkerData.AttachFace.addEntityData(nmsDirection, dataValues);
            }

            // Index 17: Peek
            ShulkerData.RawPeekAmount.addEntityData((byte) peek, dataValues);

            // Index 18: Color
            ShulkerData.Color.addEntityData((byte) color, dataValues);

            return dataValues;
        };
    }

    // Helper to map BlockFace to NMS Direction Object expected by ShulkerData
    private Object convertToNMSDirection(BlockFace face) {
        if (face == null)
            return net.minecraft.core.Direction.DOWN;
        switch (face) {
            case UP:
                return net.minecraft.core.Direction.UP;
            case DOWN:
                return net.minecraft.core.Direction.DOWN;
            case NORTH:
                return net.minecraft.core.Direction.NORTH;
            case SOUTH:
                return net.minecraft.core.Direction.SOUTH;
            case WEST:
                return net.minecraft.core.Direction.WEST;
            case EAST:
                return net.minecraft.core.Direction.EAST;
            default:
                return net.minecraft.core.Direction.DOWN;
        }
    }

    @Override
    public ShulkerBoxHitboxElement create(World world, BlockPos pos) {
        return new ShulkerBoxHitboxElement(this, pos);
    }

    @Override
    public ShulkerBoxHitboxElement create(World world, BlockPos pos, ShulkerBoxHitboxElement previous) {
        return new ShulkerBoxHitboxElement(this, pos, previous.entityId,
                previous.config.yRot != this.yRot ||
                        previous.config.xRot != this.xRot ||
                        !previous.config.position.equals(this.position));
    }

    @Override
    public ShulkerBoxHitboxElement createExact(World world, BlockPos pos, ShulkerBoxHitboxElement previous) {
        if (!isSamePosition(previous.config)) {
            return null;
        }
        return new ShulkerBoxHitboxElement(this, pos, previous.entityId, false);
    }

    @Override
    public Class<ShulkerBoxHitboxElement> elementClass() {
        return ShulkerBoxHitboxElement.class;
    }

    public List<Object> metadataValues(Player player) {
        return this.lazyMetadataPacket.apply(player);
    }

    public boolean isSamePosition(ShulkerBoxHitboxElementConfig that) {
        return Float.compare(xRot, that.xRot) == 0 &&
                Float.compare(yRot, that.yRot) == 0 &&
                Objects.equal(position, that.position);
    }

    public static class Factory implements BlockEntityElementConfigFactory<ShulkerBoxHitboxElement> {
        @Override
        public ShulkerBoxHitboxElementConfig create(net.momirealms.craftengine.core.plugin.config.ConfigSection arguments) {
            return new ShulkerBoxHitboxElementConfig(
                    dev.arubik.craftengine.util.Utils.getAsVector3f(arguments.getOrDefault("position", 0.5f), "position"),
                    dev.arubik.craftengine.util.Utils.getAsFloat(arguments.getOrDefault("pitch", 0f), "pitch"),
                    dev.arubik.craftengine.util.Utils.getAsFloat(arguments.getOrDefault("yaw", 0f), "yaw"),
                    dev.arubik.craftengine.util.Utils.getAsInt(arguments.getOrDefault("color", 16), "color"),
                    dev.arubik.craftengine.util.Utils.getAsInt(arguments.getOrDefault("peek", 0), "peek"),
                    dev.arubik.craftengine.util.Utils.getAsFloat(arguments.getOrDefault("scale", 1f), "scale"),
                    dev.arubik.craftengine.util.Utils.getAsEnum(arguments.getOrDefault("attach-face", "DOWN"), BlockFace.class,
                            BlockFace.DOWN));
        }
    }

    // postion xrot and yrot methods
    public Vector3f position() {
        return position;
    }

    public float xRot() {
        return xRot;
    }

    public float yRot() {
        return yRot;
    }
}
