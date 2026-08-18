/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.BedBlock
 *  net.minecraft.world.level.block.DoorBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BedPart
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.DoubleBlockHalf
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.momirealms.craftengine.bukkit.util.BlockStateUtils
 *  net.momirealms.craftengine.core.block.ImmutableBlockState
 *  net.momirealms.craftengine.core.block.entity.BlockEntityController
 *  net.momirealms.craftengine.core.util.Direction
 *  net.momirealms.craftengine.core.world.BlockPos
 */
package dev.arubik.craftengine.contraption.behavior;

import dev.arubik.craftengine.contraption.api.MultiblockMember;
import dev.arubik.craftengine.multiblock.HorizontalDoubleBlockBehavior;
import dev.arubik.craftengine.multiblock.HorizontalDoubleGeometry;
import dev.arubik.craftengine.multiblock.MultiBlockBehavior;
import dev.arubik.craftengine.multiblock.MultiBlockMachineBlockEntity;
import dev.arubik.craftengine.multiblock.MultiBlockPartBlockEntity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Property;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;

public final class MultiblockMembershipRegistry {
    private static final List<MultiblockMembershipProvider> PROVIDERS = new ArrayList<MultiblockMembershipProvider>();

    private MultiblockMembershipRegistry() {
    }

    public static void register(MultiblockMembershipProvider provider) {
        PROVIDERS.add(provider);
    }

    public static Set<BlockPos> membersOf(Level level, BlockPos pos) {
        for (MultiblockMembershipProvider provider : PROVIDERS) {
            try {
                Set<BlockPos> members = provider.membersOf(level, pos);
                if (members == null || members.isEmpty()) continue;
                return members;
            }
            catch (Throwable throwable) {
            }
        }
        return Set.of();
    }

    private static Set<BlockPos> fluidTankMembers(Level level, BlockPos pos) {
        ImmutableBlockState ce = MultiblockMembershipRegistry.customStateAt(level, pos);
        if (ce == null) {
            return null;
        }
        MultiblockMember member = (MultiblockMember)ce.behavior().getFirst(MultiblockMember.class);
        if (member == null || !member.isStructureComplete(level, pos)) {
            return null;
        }
        Set<BlockPos> positions = member.getStructurePositions(level, pos);
        if (positions.size() <= 1) {
            return null;
        }
        return positions;
    }

    private static Set<BlockPos> multiBlockMachineMembers(Level level, BlockPos pos) {
        BlockPos corePos;
        ImmutableBlockState ce = MultiblockMembershipRegistry.customStateAt(level, pos);
        if (ce == null) {
            return null;
        }
        MultiBlockBehavior beh = (MultiBlockBehavior)ce.behavior().getFirst(MultiBlockBehavior.class);
        if (beh == null) {
            return null;
        }
        BlockEntityController controller = MultiBlockBehavior.controllerAt(level, pos);
        if (controller instanceof MultiBlockMachineBlockEntity) {
            corePos = pos;
        } else if (controller instanceof MultiBlockPartBlockEntity) {
            MultiBlockPartBlockEntity part = (MultiBlockPartBlockEntity)controller;
            if (!part.isFormed()) {
                return null;
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

    private static Set<BlockPos> horizontalDoubleMembers(Level level, BlockPos pos) {
        ImmutableBlockState ce = MultiblockMembershipRegistry.customStateAt(level, pos);
        if (ce == null) {
            return null;
        }
        HorizontalDoubleBlockBehavior beh = (HorizontalDoubleBlockBehavior)(ce.behavior().getFirst(HorizontalDoubleBlockBehavior.class));
        if (beh == null || !beh.isDoubleBlockPublic(ce)) {
            return null;
        }
        net.momirealms.craftengine.core.util.Direction facing = beh.facingOfPublic(ce);
        HorizontalDoubleGeometry.Half half = beh.halfOfPublic(ce);
        net.momirealms.craftengine.core.world.BlockPos cePos = new net.momirealms.craftengine.core.world.BlockPos(pos.getX(), pos.getY(), pos.getZ());
        net.momirealms.craftengine.core.world.BlockPos partnerCe = HorizontalDoubleGeometry.partnerPos(cePos, facing, half);
        HashSet<BlockPos> members = new HashSet<BlockPos>();
        members.add(pos);
        members.add(new BlockPos(partnerCe.x(), partnerCe.y(), partnerCe.z()));
        return members;
    }

    private static Set<BlockPos> vanillaDoorMembers(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof DoorBlock)) {
            return null;
        }
        DoubleBlockHalf half = (DoubleBlockHalf)state.getValue((Property)BlockStateProperties.DOUBLE_BLOCK_HALF);
        BlockPos other = half == DoubleBlockHalf.LOWER ? pos.above() : pos.below();
        return Set.of(pos, other);
    }

    private static Set<BlockPos> vanillaBedMembers(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof BedBlock)) {
            return null;
        }
        BedPart part = (BedPart)state.getValue((Property)BlockStateProperties.BED_PART);
        Direction facing = (Direction)state.getValue((Property)BlockStateProperties.HORIZONTAL_FACING);
        BlockPos other = part == BedPart.HEAD ? pos.relative(facing.getOpposite()) : pos.relative(facing);
        return Set.of(pos, other);
    }

    private static ImmutableBlockState customStateAt(Level level, BlockPos pos) {
        return BlockStateUtils.getOptionalCustomBlockState(level.getBlockState(pos)).orElse(null);
    }

    static {
        PROVIDERS.add(MultiblockMembershipRegistry::fluidTankMembers);
        PROVIDERS.add(MultiblockMembershipRegistry::multiBlockMachineMembers);
        PROVIDERS.add(MultiblockMembershipRegistry::horizontalDoubleMembers);
        PROVIDERS.add(MultiblockMembershipRegistry::vanillaDoorMembers);
        PROVIDERS.add(MultiblockMembershipRegistry::vanillaBedMembers);
    }

    public static interface MultiblockMembershipProvider {
        public Set<BlockPos> membersOf(Level var1, BlockPos var2);
    }
}

