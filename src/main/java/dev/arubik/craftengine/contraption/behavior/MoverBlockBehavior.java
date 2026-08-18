/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.text.Component
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior
 *  net.momirealms.craftengine.bukkit.plugin.user.BukkitServerPlayer
 *  net.momirealms.craftengine.bukkit.util.BlockStateUtils
 *  net.momirealms.craftengine.bukkit.util.LocationUtils
 *  net.momirealms.craftengine.bukkit.world.BukkitWorld
 *  net.momirealms.craftengine.core.block.BlockDefinition
 *  net.momirealms.craftengine.core.block.ImmutableBlockState
 *  net.momirealms.craftengine.core.block.behavior.BlockBehavior
 *  net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory
 *  net.momirealms.craftengine.core.block.behavior.EntityBlock
 *  net.momirealms.craftengine.core.block.entity.BlockEntity
 *  net.momirealms.craftengine.core.block.entity.BlockEntityController
 *  net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker
 *  net.momirealms.craftengine.core.block.property.Property
 *  net.momirealms.craftengine.core.entity.player.InteractionResult
 *  net.momirealms.craftengine.core.entity.player.Player
 *  net.momirealms.craftengine.core.plugin.config.ConfigSection
 *  net.momirealms.craftengine.core.util.Direction
 *  net.momirealms.craftengine.core.util.Key
 *  net.momirealms.craftengine.core.world.BlockPos
 *  net.momirealms.craftengine.core.world.CEWorld
 *  net.momirealms.craftengine.core.world.context.UseOnContext
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.Sound
 *  org.bukkit.craftbukkit.CraftWorld
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 */
package dev.arubik.craftengine.contraption.behavior;

import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.contraption.MovementBehavior;
import dev.arubik.craftengine.contraption.behavior.MoverBehavior;
import dev.arubik.craftengine.rotation.RpmConsumer;
import dev.arubik.craftengine.rotation.RpmProvider;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior;
import net.momirealms.craftengine.bukkit.plugin.user.BukkitServerPlayer;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.bukkit.util.LocationUtils;
import net.momirealms.craftengine.bukkit.world.BukkitWorld;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.behavior.EntityBlock;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker;
import net.momirealms.craftengine.core.block.property.Property;
import net.momirealms.craftengine.core.entity.player.InteractionResult;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.util.Direction;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.world.CEWorld;
import net.momirealms.craftengine.core.world.context.UseOnContext;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MoverBlockBehavior
extends BukkitBlockBehavior
implements EntityBlock {
    public static final Key FACTORY_KEY = Key.of((String)"polyfills:mover_block");
    public static final Factory FACTORY = new Factory();
    public static final double DEFAULT_SPEED = 1.0;
    public static final double DEFAULT_GEAR_RATIO = 1.0;
    public static final double DEFAULT_MIN_RPM = 0.0;
    public static final double DEFAULT_SU_COST = 2.0;
    public static final boolean DEFAULT_REQUIRES_POWER = false;
    private final Property<Direction> facingProperty;
    private final Property<Boolean> enabledProperty;
    private final double speedBlocksPerSec;
    private final double gearRatio;
    private final double minRpm;
    private final double suCost;
    private final boolean requiresPower;
    private int controllerId;

    public MoverBlockBehavior(BlockDefinition customBlock, Property<Direction> facingProperty, Property<Boolean> enabledProperty, double speedBlocksPerSec, double gearRatio, double minRpm, double suCost, boolean requiresPower) {
        super(customBlock);
        this.facingProperty = facingProperty;
        this.enabledProperty = enabledProperty;
        this.speedBlocksPerSec = speedBlocksPerSec;
        this.gearRatio = gearRatio;
        this.minRpm = minRpm;
        this.suCost = suCost;
        this.requiresPower = requiresPower;
    }

    public double speedBlocksPerSec() {
        return this.speedBlocksPerSec;
    }

    public double gearRatio() {
        return this.gearRatio;
    }

    public double minRpm() {
        return this.minRpm;
    }

    public double suCost() {
        return this.suCost;
    }

    public boolean requiresPower() {
        return this.requiresPower;
    }

    public void initControllerId(int id) {
        this.controllerId = id;
    }

    public InteractionResult useWithoutItem(UseOnContext context, ImmutableBlockState state) {
        try {
            BlockEntityController blockEntityController;
            BukkitServerPlayer cePlayer;
            ServerLevel level = ((CraftWorld)((BukkitWorld)context.getLevel()).platformWorld()).getHandle();
            BlockPos pos = (BlockPos)LocationUtils.toBlockPos((net.momirealms.craftengine.core.world.BlockPos)context.getClickedPos());
           
            //BukkitServerPlayer player = (BukkitServerPlayer) context.getPlayer();
            //Player bukkit = player.platformPlayer();
            //if (bukkit instanceof Player) {
            //  Player p = bukkit;
            //  holder.open(p);
            //}
            //if (!(player instanceof BukkitServerPlayer) || !((player = (cePlayer = (BukkitServerPlayer)player).platformPlayer()) instanceof org.bukkit.entity.Player)) {
            //    return InteractionResult.PASS;
            //}
            BukkitServerPlayer player = (BukkitServerPlayer)context.getPlayer();
            org.bukkit.entity.Player bukkit = player.platformPlayer();
            if (this.enabledProperty != null && !bukkit.isSneaking() && state != null && !state.isEmpty()) {
                boolean cur = Boolean.TRUE.equals(state.get(this.enabledProperty));
                BlockState toggled = (BlockState)ImmutableBlockState.with((ImmutableBlockState)state, this.enabledProperty, (!cur ? 1 : 0)).customBlockState().minecraftState();
                level.setBlock(pos, toggled, 3);
                bukkit.playSound(bukkit.getLocation(), !cur ? Sound.BLOCK_LEVER_CLICK : Sound.BLOCK_LEVER_CLICK, 0.7f, !cur ? 1.2f : 0.8f);
                bukkit.sendMessage(!cur ? "\u00a7aMover enabled." : "\u00a77Mover disabled.");
                return InteractionResult.SUCCESS_AND_CANCEL;
            }
            BlockEntity be = BukkitBlockEntityTypes.getIfLoaded((Level)level, pos);
            if (be != null && (blockEntityController = be.controller) instanceof MoverController) {
                MoverController mover = (MoverController)blockEntityController;
                mover.openStatusMenu((org.bukkit.entity.Player)bukkit);
                return InteractionResult.SUCCESS_AND_CANCEL;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return InteractionResult.PASS;
    }

    public BlockEntityController createBlockEntityController(BlockEntity blockEntity) {
        return new MoverController(blockEntity, this);
    }

    public static MovementBehavior buildMovementBehavior(BlockPos localOffset, BlockState state) {
        ImmutableBlockState ce = BlockStateUtils.getOptionalCustomBlockState(state).orElse(null);
        if (ce == null || ce.isEmpty()) {
            return null;
        }
        MoverBlockBehavior behavior = (MoverBlockBehavior)(ce.behavior().getFirst(MoverBlockBehavior.class));
        if (behavior == null) {
            return null;
        }
        Direction ceFacing = (Direction)ce.get(behavior.facingProperty);
        Vec3 localDir = MoverBlockBehavior.toLocalDir(ceFacing);
        boolean enabled = behavior.enabledProperty == null || Boolean.TRUE.equals(ce.get(behavior.enabledProperty));
        return new MoverBehavior(localDir, behavior.speedBlocksPerSec, behavior.gearRatio, behavior.minRpm, behavior.suCost, behavior.requiresPower, enabled);
    }

    private static Vec3 toLocalDir(Direction dir) {
        return switch (dir) {
            default -> throw new MatchException(null, null);
            case Direction.UP -> new Vec3(0.0, 1.0, 0.0);
            case Direction.DOWN -> new Vec3(0.0, -1.0, 0.0);
            case Direction.NORTH -> new Vec3(0.0, 0.0, -1.0);
            case Direction.SOUTH -> new Vec3(0.0, 0.0, 1.0);
            case Direction.EAST -> new Vec3(1.0, 0.0, 0.0);
            case Direction.WEST -> new Vec3(-1.0, 0.0, 0.0);
        };
    }

    public static class MoverController
    extends BlockEntityController
    implements RpmConsumer {
        private static final int STRESS_GRACE_TICKS = 20;
        private final MoverBlockBehavior behavior;
        private float inputRpm;
        private RpmProvider activeMotor;
        private int stressGrace;

        public MoverController(BlockEntity blockEntity, MoverBlockBehavior behavior) {
            super(blockEntity);
            this.behavior = behavior;
        }

        public <C extends BlockEntityController> BlockEntityTicker<C> createBlockEntityTicker(CEWorld world, ImmutableBlockState state) {
            return BlockEntityController.createTickerHelper(MoverController::tick);
        }

        public static void tick(CEWorld world, net.momirealms.craftengine.core.world.BlockPos cePos, ImmutableBlockState ceState, MoverController self) {
            boolean operating;
            Object levelObj = world.world().minecraftWorld();
            if (!(levelObj instanceof ServerLevel)) {
                return;
            }
            ServerLevel level = (ServerLevel)levelObj;
            BlockPos nmsPos = new BlockPos(cePos.x(), cePos.y(), cePos.z());
            float bestPotential = 0.0f;
            float actual = 0.0f;
            RpmProvider best = null;
            for (net.minecraft.core.Direction d : net.minecraft.core.Direction.values()) {
                net.momirealms.craftengine.core.world.BlockPos consumerPos;
                RpmProvider provider;
                BlockEntityController blockEntityController;
                BlockPos neighborPos = nmsPos.relative(d);
                BlockEntity be = BukkitBlockEntityTypes.getIfLoaded((Level)level, neighborPos);
                if (be == null || !((blockEntityController = be.controller) instanceof RpmProvider) || !(provider = (RpmProvider)blockEntityController).isRpmSource() || !(provider.potentialRpm() > bestPotential) || !provider.rpmReaches(consumerPos = new net.momirealms.craftengine.core.world.BlockPos(cePos.x(), cePos.y(), cePos.z()))) continue;
                bestPotential = provider.potentialRpm();
                actual = provider.getRpm();
                best = provider;
            }
            self.inputRpm = actual;
            self.activeMotor = best;
            boolean bl = operating = (double)self.inputRpm * self.behavior.gearRatio() >= self.behavior.minRpm() && self.inputRpm > 0.0f;
            if (operating) {
                self.stressGrace = 20;
            }
            if (self.stressGrace > 0 && self.activeMotor != null) {
                self.activeMotor.reportStressLoad((float)self.behavior.suCost());
                --self.stressGrace;
            }
        }

        @Override
        public void setInputRpm(float rpm) {
            this.inputRpm = rpm;
        }

        @Override
        public float getInputRpm() {
            return this.inputRpm;
        }

        public void openStatusMenu(org.bukkit.entity.Player player) {
            Inventory inv = Bukkit.createInventory(null, (int)9, (Component)Component.text((String)"Mover Status"));
            boolean powered = (double)this.inputRpm * this.behavior.gearRatio() >= this.behavior.minRpm() && this.inputRpm > 0.0f;
            boolean pushing = powered || !this.behavior.requiresPower();
            ItemStack status = new ItemStack(pushing ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);
            ItemMeta meta = status.getItemMeta();
            meta.displayName((Component)Component.text((String)(pushing ? "Pushing" : "Idle")));
            meta.lore(List.of(Component.text((String)("Speed: " + this.behavior.speedBlocksPerSec() + " blocks/s")), Component.text((String)("Requires power: " + this.behavior.requiresPower())), Component.text((String)("RPM in: " + this.inputRpm)), Component.text((String)("Gear ratio: " + this.behavior.gearRatio())), Component.text((String)("Effective rpm: " + (double)this.inputRpm * this.behavior.gearRatio())), Component.text((String)("Min rpm required: " + this.behavior.minRpm())), Component.text((String)("SU cost while pushing: " + this.behavior.suCost())), Component.text((String)(this.activeMotor != null ? "Driven by a real adjacent motor" : "No motor detected"))));
            status.setItemMeta(meta);
            inv.setItem(4, status);
            player.openInventory(inv);
        }
    }

    public static class Factory
    implements BlockBehaviorFactory<BlockBehavior> {
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            Property facing = block.getProperty("facing");
            if (facing == null) {
                throw new IllegalArgumentException("Missing property 'facing' for mover_block");
            }
            Property enabled = block.getProperty("enabled");
            double speed = Double.parseDouble(arguments.getOrDefault("speed", 1.0).toString());
            double gearRatio = Double.parseDouble(arguments.getOrDefault("gearRatio", 1.0).toString());
            double minRpm = Double.parseDouble(arguments.getOrDefault("minRpm", 0.0).toString());
            double suCost = Double.parseDouble(arguments.getOrDefault("suCost", 2.0).toString());
            boolean requiresPower = Boolean.parseBoolean(arguments.getOrDefault("requiresPower", false).toString());
            return new MoverBlockBehavior(block, (Property<Direction>)facing, (Property<Boolean>)enabled, speed, gearRatio, minRpm, suCost, requiresPower);
        }
    }
}

