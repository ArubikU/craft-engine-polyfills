package dev.arubik.craftengine.block.behavior;

import java.util.Optional;

import dev.arubik.craftengine.block.entity.CustomCrafterBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeCache;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.bukkit.world.BukkitWorld;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.UpdateFlags;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.behavior.EntityBlock;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.block.property.Property;
import net.momirealms.craftengine.core.entity.player.InteractionResult;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.world.context.UseOnContext;

/**
 * Custom Crafter — a faithful port of vanilla {@code net.minecraft.world.level.block.CrafterBlock},
 * re-based on CraftEngine. ONE behavioural change: in the craft step, a damageable ingredient (a tool
 * with durability, e.g. the hammers using {@code craft_remainder: hurt_and_break}) is DAMAGED IN ITS
 * SLOT instead of being ejected out the front. Everything else (bucket remainders, normal consumption)
 * matches vanilla.
 *
 * <p>Backed by {@link CustomCrafterBlockEntity} using CraftEngine's native block-entity NBT storage
 * (NOT the Bukkit PDC). Exposes itself as a vanilla {@link Container} via {@code getContainer} so
 * hoppers/droppers/comparators interact with it like the real crafter.</p>
 */
public class CustomCrafterBehavior extends BukkitBlockBehavior
        implements EntityBlock, net.momirealms.craftengine.core.block.behavior.WorldlyContainerHolder {

    public static final Factory FACTORY = new Factory();
    private static final RecipeCache RECIPE_CACHE = new RecipeCache(10);
    private static final int CRAFTING_TICKS = 6;
    private static final int CRAFTING_TICK_DELAY = 4;

    /** player UUID -> crafter BE they currently have open (for the SLOT_STATE_CHANGE interception). */
    static final java.util.Map<java.util.UUID, CustomCrafterBlockEntity> OPEN = new java.util.concurrent.ConcurrentHashMap<>();

    private final Property<Boolean> craftingProp;
    private final Property<Boolean> triggeredProp;
    private final Property<net.momirealms.craftengine.core.util.Direction> facingProp;

    @SuppressWarnings("unchecked")
    public CustomCrafterBehavior(BlockDefinition block) {
        super(block);
        this.craftingProp = (Property<Boolean>) block.getProperty("crafting");
        this.triggeredProp = (Property<Boolean>) block.getProperty("triggered");
        this.facingProp = (Property<net.momirealms.craftengine.core.util.Direction>) block.getProperty("facing");
    }

    // ---------------- entity ----------------

    private int controllerId;

    @Override
    public void initControllerId(int id) {
        this.controllerId = id;
    }

    @Override
    public BlockEntityController createBlockEntityController(BlockEntity blockEntity) {
        return new CustomCrafterBlockEntity(blockEntity);
    }

    private static CustomCrafterBlockEntity at(Level level, BlockPos pos) {
        BlockEntity be = dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes.getIfLoaded(level, pos);
        return be != null && be.controller instanceof CustomCrafterBlockEntity c ? c : null;
    }

    // ---------------- hopper / comparator bridge ----------------

    // The hopper/comparator bridge contexts don't always pass args in the (state, level, pos) layout the
    // block-behavior hooks use — scan for the Level and BlockPos by type so we never cast the wrong slot
    // (that ClassCastException is what crashed the hopper and made funnels see no container).
    private static Level levelArg(Object[] args) {
        for (Object o : args)
            if (o instanceof Level l)
                return l;
        return null;
    }

    private static BlockPos posArg(Object[] args) {
        for (Object o : args)
            if (o instanceof BlockPos p)
                return p;
        return null;
    }

    @Override
    public Object getContainer(Object thisBlock, Object[] args) {
        try {
            Level level = levelArg(args);
            BlockPos pos = posArg(args);
            if (level == null || pos == null)
                return null;
            CustomCrafterBlockEntity c = at(level, pos);
            if (c instanceof Container)
                return c;
        } catch (Throwable ignored) {
        }
        return null;
    }

    @Override
    public boolean hasAnalogOutputSignal(Object thisBlock, Object[] args) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(Object thisBlock, Object[] args) {
        Level level = levelArg(args);
        BlockPos pos = posArg(args);
        if (level == null || pos == null)
            return 0;
        CustomCrafterBlockEntity c = at(level, pos);
        return c == null ? 0 : c.getRedstoneSignal();
    }

    // ---------------- redstone trigger (vanilla edge logic) ----------------

    @Override
    public void neighborChanged(Object thisBlock, Object[] args) {
        net.minecraft.world.level.block.state.BlockState state = (net.minecraft.world.level.block.state.BlockState) args[0];
        Level level = (Level) args[1];
        BlockPos pos = (BlockPos) args[2];
        ImmutableBlockState cs = BlockStateUtils.getOptionalCustomBlockState(state).orElse(null);
        CustomCrafterBlockEntity be = at(level, pos);
        if (cs == null || be == null)
            return;
        boolean hasSignal = level.hasNeighborSignal(pos);
        // Debounce on the BLOCK ENTITY's triggered flag (in-memory, reliable) — NOT the blockstate prop,
        // which the host crafter block also touches. Only the RISING edge schedules one craft; while the
        // signal stays high nothing re-triggers (vanilla parity, no infinite crafting).
        boolean triggered = be.isTriggered();
        if (hasSignal && !triggered) {
            be.setTriggered(true);
            be.requestCraft(); // one craft per rising edge
            setProps(level, pos, cs, true, null);
            level.scheduleTick(pos, state.getBlock(), CRAFTING_TICK_DELAY);
        } else if (!hasSignal && triggered) {
            be.setTriggered(false);
            setProps(level, pos, cs, false, false);
        }
    }

    @Override
    public void onPlace(Object thisBlock, Object[] args) {
        try {
            net.minecraft.world.level.block.state.BlockState state = (net.minecraft.world.level.block.state.BlockState) args[0];
            Level level = (Level) args[1];
            BlockPos pos = (BlockPos) args[2];
            ImmutableBlockState cs = BlockStateUtils.getOptionalCustomBlockState(state).orElse(null);
            if (cs != null && triggeredProp != null && Boolean.TRUE.equals(cs.get(triggeredProp)))
                level.scheduleTick(pos, state.getBlock(), CRAFTING_TICK_DELAY);
        } catch (Throwable ignored) {
        }
        // NOTE: don't call super.onPlace — CE's default impl logs "Default onPlace behavior".
    }

    /** Scheduled tick → run the craft. */
    @Override
    public void tick(Object thisBlock, Object[] args) {
        net.minecraft.world.level.block.state.BlockState state = (net.minecraft.world.level.block.state.BlockState) args[0];
        Level level = (Level) args[1];
        BlockPos pos = (BlockPos) args[2];
        if (level instanceof ServerLevel sl)
            dispenseFrom(sl, pos, state);
    }

    // ---------------- open the vanilla crafter menu ----------------

    @Override
    public InteractionResult useWithoutItem(UseOnContext context, ImmutableBlockState state) {
        try {
            ServerLevel level = (ServerLevel) ((BukkitWorld) context.getLevel()).minecraftWorld();
            BlockPos pos = new BlockPos(context.getClickedPos().x(), context.getClickedPos().y(),
                    context.getClickedPos().z());
            CustomCrafterBlockEntity be = at(level, pos);
            if (be == null)
                return InteractionResult.PASS;
            net.minecraft.server.level.ServerPlayer sp = (net.minecraft.server.level.ServerPlayer) context.getPlayer()
                    .serverPlayer();
            // Track which crafter BE this player has open: vanilla's slot-disable handler is hard-gated on a
            // real CrafterBlockEntity, so it never fires for our custom container. We intercept the
            // SLOT_STATE_CHANGE packet (see SlotStateListener) and apply it to the BE we record here.
            OPEN.put(sp.getUUID(), be);
            net.minecraft.world.MenuProvider provider = new net.minecraft.world.SimpleMenuProvider(
                    (id, inv, p) -> new net.minecraft.world.inventory.CrafterMenu(id, inv, be, be.containerData()),
                    net.minecraft.network.chat.Component.translatable("container.crafter"));
            sp.openMenu(provider);
            return InteractionResult.SUCCESS_AND_CANCEL;
        } catch (Throwable t) {
            return InteractionResult.PASS;
        }
    }

    // ---------------- the craft (vanilla dispenseFrom + damage-in-slot fix) ----------------

    private void dispenseFrom(ServerLevel level, BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        CustomCrafterBlockEntity crafter = at(level, pos);
        if (crafter == null)
            return;
        // One craft per redstone pulse: CE may route a tick here every game tick, so only craft when a
        // request is pending from the rising edge (consumed here), otherwise it would craft forever.
        if (!crafter.consumeCraftRequest())
            return;
        CraftingInput input = crafter.asCraftInput();
        Optional<RecipeHolder<CraftingRecipe>> result = RECIPE_CACHE.get(level, input);
        if (result.isEmpty()) {
            level.levelEvent(1050, pos, 0); // fail sound
            return;
        }
        RecipeHolder<CraftingRecipe> holder = result.get();
        ItemStack assembled = holder.value().assemble(input, level.registryAccess());
        if (assembled.isEmpty()) {
            level.levelEvent(1050, pos, 0);
            return;
        }
        ImmutableBlockState cs = BlockStateUtils.getOptionalCustomBlockState(state).orElse(null);
        crafter.setCraftingTicksRemaining(CRAFTING_TICKS);
        if (cs != null)
            setProps(level, pos, cs, null, true); // CRAFTING = true (animation)
        assembled.onCraftedBySystem(level);
        dispenseItem(level, pos, crafter, assembled);

        // Remainders: dispense only NON-damageable ones (buckets, etc). A damageable tool's remainder is
        // the damaged tool itself — we keep it in its slot below instead of ejecting it.
        for (ItemStack rem : holder.value().getRemainingItems(input)) {
            if (!rem.isEmpty() && !rem.isDamageableItem())
                dispenseItem(level, pos, crafter, rem);
        }

        // Consume ingredients: a damageable tool is DAMAGED IN PLACE (stays in its slot); everything else
        // shrinks by 1 (vanilla).
        for (int i = 0; i < CustomCrafterBlockEntity.CONTAINER_SIZE; i++) {
            ItemStack s = crafter.getItem(i);
            if (s.isEmpty())
                continue;
            if (s.isDamageableItem()) {
                int dmg = s.getDamageValue() + 1;
                if (dmg >= s.getMaxDamage()) {
                    crafter.getItems().set(i, ItemStack.EMPTY);
                    level.levelEvent(1029, pos, 0); // anvil/break-ish; tool broke
                } else {
                    s.setDamageValue(dmg);
                }
            } else {
                s.shrink(1);
            }
        }
        crafter.setChanged();
    }

    private void dispenseItem(ServerLevel level, BlockPos pos, CustomCrafterBlockEntity crafter, ItemStack stack) {
        Direction front = frontOf(level, pos);
        // HopperBlockEntity.getContainerAt only resolves a VANILLA Container/WorldlyContainer — a
        // CraftEngine machine in front of this crafter is never one, it's its own
        // PersistentWorldlyBlockEntity. ItemTransferHelper.getContainer (the item pipe's own
        // resolver for this) also checks the block's WorldlyContainerHolder capability, so this
        // crafter can dispense straight into a CraftEngine machine's input, not just vanilla
        // containers. HopperBlockEntity.addItem below is a generic vanilla helper written against
        // the Container/WorldlyContainer interfaces, not any vanilla-specific implementation, so it
        // works unchanged against whatever Container this now resolves to.
        Container target = dev.arubik.craftengine.pipe.item.ItemTransferHelper.getContainer(
                level, pos.relative(front), pos, front.getOpposite()).orElse(null);
        ItemStack remaining = stack.copy();
        if (target != null) {
            while (!remaining.isEmpty()) {
                int before = remaining.getCount();
                remaining = HopperBlockEntity.addItem(crafter, target, remaining, front.getOpposite());
                if (remaining.getCount() == before)
                    break;
            }
        }
        if (!remaining.isEmpty()) {
            Vec3 c = Vec3.atCenterOf(pos);
            Vec3 spawn = c.relative(front, 0.7);
            DefaultDispenseItemBehavior.spawnItem(level, remaining, 6, front, spawn);
            level.levelEvent(1049, pos, 0);
            level.levelEvent(2010, pos, front.get3DDataValue());
        }
    }

    private Direction frontOf(Level level, BlockPos pos) {
        try {
            ImmutableBlockState cs = BlockStateUtils.getOptionalCustomBlockState(level.getBlockState(pos)).orElse(null);
            if (cs != null && facingProp != null) {
                net.momirealms.craftengine.core.util.Direction d = cs.get(facingProp);
                if (d != null)
                    return dev.arubik.craftengine.util.Utils.fromDirection(d);
            }
        } catch (Throwable ignored) {
        }
        return Direction.NORTH;
    }

    // ---------------- blockstate helpers ----------------

    /** Set crafting/triggered props (null = leave unchanged) and push the new state to the world. */
    private void setProps(Level level, BlockPos pos, ImmutableBlockState cs, Boolean triggered, Boolean crafting) {
        ImmutableBlockState next = cs;
        if (triggered != null && triggeredProp != null)
            next = next.with(triggeredProp, triggered);
        if (crafting != null && craftingProp != null)
            next = next.with(craftingProp, crafting);
        dev.arubik.craftengine.util.MNms.INSTANCE.method$LevelWriter$setBlock(level, pos,
                next.customBlockState().minecraftState(), UpdateFlags.UPDATE_CLIENTS);
    }

    /** Called by the block entity ticker when the crafting animation ends. */
    public static void clearCrafting(Level level, BlockPos pos) {
        if (level == null)
            return;
        ImmutableBlockState cs = BlockStateUtils.getOptionalCustomBlockState(level.getBlockState(pos)).orElse(null);
        if (cs == null)
            return;
        @SuppressWarnings("unchecked")
        Property<Boolean> cp = (Property<Boolean>) cs.owner().value().getProperty("crafting");
        if (cp == null)
            return;
        dev.arubik.craftengine.util.MNms.INSTANCE.method$LevelWriter$setBlock(level, pos,
                cs.with(cp, false).customBlockState().minecraftState(), UpdateFlags.UPDATE_CLIENTS);
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            return new CustomCrafterBehavior(block);
        }
    }
}
