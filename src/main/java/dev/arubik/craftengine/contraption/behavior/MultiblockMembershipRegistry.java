package dev.arubik.craftengine.contraption.behavior;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.core.block.ImmutableBlockState;

import dev.arubik.craftengine.contraption.api.MultiblockMember;
import dev.arubik.craftengine.multiblock.HorizontalDoubleBlockBehavior;
import dev.arubik.craftengine.multiblock.HorizontalDoubleGeometry;
import dev.arubik.craftengine.multiblock.MultiBlockBehavior;
import dev.arubik.craftengine.multiblock.MultiBlockMachineBlockEntity;
import dev.arubik.craftengine.multiblock.MultiBlockPartBlockEntity;

/**
 * SPI for "if this block is a MEMBER of some multiblock structure, what are ALL of that
 * structure's member positions?" (CONTRAPTIONS.md — "todos los multiblock ... deben pegarse
 * completos solos": every multiblock a player glues even one member of must be captured as a
 * COMPLETE unit, never partially). Mirrors {@link MovementBehaviorRegistry}'s ordered-list
 * dispatch shape, just resolved by "does this provider recognize the block at pos?" instead of
 * a {@code Key} lookup, since two of the five providers here (vanilla door/bed) aren't
 * CraftEngine custom blocks at all.
 *
 * <p>Used by {@code GlueRegistry#structureAt} to expand a raw glue-component into the full set
 * of blocks that must move/capture together — see that class's javadoc for the fixed-point
 * expansion loop.
 */
public final class MultiblockMembershipRegistry {

    private MultiblockMembershipRegistry() {
    }

    /** One recognizer for one multiblock "family". */
    public interface MultiblockMembershipProvider {
        /**
         * Every member position of the multiblock structure containing {@code pos}, or
         * {@code null}/empty if {@code pos} isn't part of one this provider recognizes.
         */
        Set<BlockPos> membersOf(Level level, BlockPos pos);
    }

    private static final List<MultiblockMembershipProvider> PROVIDERS = new ArrayList<>();

    static {
        PROVIDERS.add(MultiblockMembershipRegistry::fluidTankMembers);
        PROVIDERS.add(MultiblockMembershipRegistry::multiBlockMachineMembers);
        PROVIDERS.add(MultiblockMembershipRegistry::horizontalDoubleMembers);
        PROVIDERS.add(MultiblockMembershipRegistry::vanillaDoorMembers);
        PROVIDERS.add(MultiblockMembershipRegistry::vanillaBedMembers);
    }

    public static void register(MultiblockMembershipProvider provider) {
        PROVIDERS.add(provider);
    }

    /** Tries every provider in order; returns the first non-empty result, or an empty set. */
    public static Set<BlockPos> membersOf(Level level, BlockPos pos) {
        for (MultiblockMembershipProvider provider : PROVIDERS) {
            try {
                Set<BlockPos> members = provider.membersOf(level, pos);
                if (members != null && !members.isEmpty()) {
                    return members;
                }
            } catch (Throwable ignored) {
                // one provider misbehaving must not break structure expansion for the rest
            }
        }
        return Set.of();
    }

    // ---------------- 1. fluid block tank (Create-style w x w x h prism) ----------------

    private static Set<BlockPos> fluidTankMembers(Level level, BlockPos pos) {
        ImmutableBlockState ce = customStateAt(level, pos);
        if (ce == null) {
            return null;
        }
        MultiblockMember member = ce.behavior().getFirst(MultiblockMember.class);
        if (member == null || !member.isStructureComplete(level, pos)) {
            return null;
        }
        Set<BlockPos> positions = member.getStructurePositions(level, pos);
        if (positions.size() <= 1) {
            return null; // singleton — nothing extra to pull in
        }
        return positions;
    }

    // ---------------- 2. multiblock machine (schema-driven, any facing) ----------------

    private static Set<BlockPos> multiBlockMachineMembers(Level level, BlockPos pos) {
        ImmutableBlockState ce = customStateAt(level, pos);
        if (ce == null) {
            return null;
        }
        MultiBlockBehavior beh = ce.behavior().getFirst(MultiBlockBehavior.class);
        if (beh == null) {
            return null;
        }
        var controller = MultiBlockBehavior.controllerAt(level, pos);
        BlockPos corePos;
        if (controller instanceof MultiBlockMachineBlockEntity) {
            corePos = pos; // this block IS the formed core
        } else if (controller instanceof MultiBlockPartBlockEntity part) {
            if (!part.isFormed()) {
                return null; // unformed part: nothing to expand yet
            }
            corePos = part.getCorePos();
            if (corePos == null) {
                return null;
            }
        } else {
            return null;
        }
        Set<BlockPos> members = beh.memberPositions(level, corePos);
        return members.size() <= 1 ? null : members;
    }

    // ---------------- 3. horizontal double block (workbench, and any other 2-wide block) ----------------

    private static Set<BlockPos> horizontalDoubleMembers(Level level, BlockPos pos) {
        ImmutableBlockState ce = customStateAt(level, pos);
        if (ce == null) {
            return null;
        }
        HorizontalDoubleBlockBehavior beh = ce.behavior().getFirst(HorizontalDoubleBlockBehavior.class);
        if (beh == null || !beh.isDoubleBlockPublic(ce)) {
            return null;
        }
        net.momirealms.craftengine.core.util.Direction facing = beh.facingOfPublic(ce);
        HorizontalDoubleGeometry.Half half = beh.halfOfPublic(ce);
        net.momirealms.craftengine.core.world.BlockPos cePos =
                new net.momirealms.craftengine.core.world.BlockPos(pos.getX(), pos.getY(), pos.getZ());
        net.momirealms.craftengine.core.world.BlockPos partnerCe =
                HorizontalDoubleGeometry.partnerPos(cePos, facing, half);
        Set<BlockPos> members = new HashSet<>();
        members.add(pos);
        members.add(new BlockPos(partnerCe.x(), partnerCe.y(), partnerCe.z()));
        return members;
    }

    // ---------------- 4. vanilla door (2 tall) ----------------

    private static Set<BlockPos> vanillaDoorMembers(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof DoorBlock)) {
            return null;
        }
        DoubleBlockHalf half = state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF);
        BlockPos other = half == DoubleBlockHalf.LOWER ? pos.above() : pos.below();
        return Set.of(pos, other);
    }

    // ---------------- 5. vanilla bed (2 wide, horizontal) ----------------

    private static Set<BlockPos> vanillaBedMembers(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof BedBlock)) {
            return null;
        }
        BedPart part = state.getValue(BlockStateProperties.BED_PART);
        net.minecraft.core.Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        // Vanilla convention (BedBlock#getNeighbourDirection / #canSurvive): the FOOT is the block
        // the facing property points away from — i.e. HEAD is one step in `facing` from FOOT, so
        // from HEAD, the FOOT is `facing.getOpposite()`.
        BlockPos other = part == BedPart.HEAD ? pos.relative(facing.getOpposite()) : pos.relative(facing);
        return Set.of(pos, other);
    }

    // ---------------- shared helpers ----------------

    private static ImmutableBlockState customStateAt(Level level, BlockPos pos) {
        return BlockStateUtils.getOptionalCustomBlockState(level.getBlockState(pos)).orElse(null);
    }
}
