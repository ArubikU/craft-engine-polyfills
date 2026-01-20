// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package net.momirealms.craftengine.bukkit.nms;

import com.google.common.hash.HashCode;
import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntList;
import java.lang.reflect.Constructor;
import java.util.BitSet;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.momirealms.craftengine.core.block.StatePropertyAccessor;
import net.momirealms.craftengine.core.item.recipe.CustomBlastingRecipe;
import net.momirealms.craftengine.core.item.recipe.CustomCampfireRecipe;
import net.momirealms.craftengine.core.item.recipe.CustomShapedRecipe;
import net.momirealms.craftengine.core.item.recipe.CustomShapelessRecipe;
import net.momirealms.craftengine.core.item.recipe.CustomSmeltingRecipe;
import net.momirealms.craftengine.core.item.recipe.CustomSmithingTransformRecipe;
import net.momirealms.craftengine.core.item.recipe.CustomSmithingTrimRecipe;
import net.momirealms.craftengine.core.item.recipe.CustomSmokingRecipe;
import net.momirealms.craftengine.core.item.recipe.CustomStoneCuttingRecipe;
import net.momirealms.craftengine.core.item.recipe.Ingredient;
import net.momirealms.craftengine.core.plugin.network.ConnectionState;
import net.momirealms.craftengine.core.plugin.network.PacketFlow;
import net.momirealms.craftengine.core.util.VersionHelper;
import net.momirealms.craftengine.core.world.CEWorld;
import net.momirealms.craftengine.core.world.chunk.InjectedHolder;
import org.bukkit.Chunk;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class FastNMS {
   public static final FastNMS INSTANCE = instance();

   public FastNMS() {
   }

   private static FastNMS instance() {
      try {
         String path = getImplPath();
         Class<?> clazz = Class.forName("net.momirealms.craftengine.bukkit.nms." + path + ".FastNMSImpl");
         Constructor<?> constructor = clazz.getDeclaredConstructor();
         constructor.setAccessible(true);
         return (FastNMS)constructor.newInstance();
      } catch (ReflectiveOperationException var3) {
         throw new RuntimeException("Failed to initialize craftengine nms helper", var3);
      }
   }

   private static @NotNull String getImplPath() throws IllegalAccessException {
      String var10000;
      switch (VersionHelper.MINECRAFT_VERSION.version()) {
         case "1.21.11":
            var10000 = "v1_21_11";
            break;
         case "1.21.9":
         case "1.21.10":
            var10000 = "v1_21_9";
            break;
         case "1.21.6":
         case "1.21.7":
         case "1.21.8":
            var10000 = "v1_21_6";
            break;
         case "1.21.5":
            var10000 = "v1_21_5";
            break;
         case "1.21.4":
            var10000 = "v1_21_4";
            break;
         case "1.21.2":
         case "1.21.3":
            var10000 = "v1_21_2";
            break;
         case "1.21":
         case "1.21.1":
            var10000 = "v1_21";
            break;
         case "1.20.5":
         case "1.20.6":
            var10000 = "v1_20_5";
            break;
         case "1.20.3":
         case "1.20.4":
            var10000 = "v1_20_3";
            break;
         case "1.20.2":
            var10000 = "v1_20_2";
            break;
         case "1.20":
         case "1.20.1":
            var10000 = "v1_20";
            break;
         default:
            throw new UnsupportedVersionException();
      }

      return var10000;
   }

   public abstract Object createInjectedEntityCallbacks(Object var1, Object var2);

   public abstract StatePropertyAccessor createStatePropertyAccessor(Object var1);

   public abstract Object toMinecraftIngredient(Ingredient<ItemStack> var1);

   public abstract Object getCraftEngineLootItemType();

   public abstract Object getCraftEngineCustomSimpleStateProviderType();

   public abstract Object getCraftEngineCustomWeightedStateProviderType();

   public abstract Object getCraftEngineCustomRotatedBlockProviderType();

   public abstract Object getCraftEngineCustomRandomizedIntStateProviderType();

   public abstract Object getCraftEngineCustomSimpleBlockFeature();

   public abstract InjectedHolder.Palette createInjectedPalettedContainerHolder(Object var1) throws InstantiationException;

   public abstract InjectedHolder.Section createInjectedLevelChunkSectionHolder(Object var1);

   public abstract void injectedWorldGen(CEWorld var1, Object var2);

   public abstract CollisionEntity createCollisionBoat(Object var1, Object var2, double var3, double var5, double var7, boolean var9, boolean var10, boolean var11);

   public abstract CollisionEntity createCollisionInteraction(Object var1, Object var2, double var3, double var5, double var7, boolean var9, boolean var10, boolean var11);

   public abstract Object createShapedRecipe(CustomShapedRecipe<ItemStack> var1);

   public abstract Object createShapelessRecipe(CustomShapelessRecipe<ItemStack> var1);

   public abstract Object createSmokingRecipe(CustomSmokingRecipe<ItemStack> var1);

   public abstract Object createSmeltingRecipe(CustomSmeltingRecipe<ItemStack> var1);

   public abstract Object createBlastingRecipe(CustomBlastingRecipe<ItemStack> var1);

   public abstract Object createCampfireRecipe(CustomCampfireRecipe<ItemStack> var1);

   public abstract Object createStonecuttingRecipe(CustomStoneCuttingRecipe<ItemStack> var1);

   public abstract Object createSmithingTransformRecipe(CustomSmithingTransformRecipe<ItemStack> var1);

   public abstract Object createSmithingTrimRecipe(CustomSmithingTrimRecipe<ItemStack> var1);

   public abstract Object createInjectedFallingBlockEntity(Object var1, Object var2, Object var3);

   public abstract Object method$PalettedContainer$getAndSet(Object var1, int var2, int var3, int var4, Object var5);

   public abstract BlockData method$CraftBlockData$fromData(Object var1);

   public abstract int method$IdMapper$getId(Object var1, Object var2);

   public abstract Object method$IdMapper$byId(Object var1, int var2);

   public abstract Object method$CraftBlockData$getState(BlockData var1);

   public abstract int method$BlockStateBase$getLightEmission(Object var1);

   public abstract boolean method$BlockStateBase$canOcclude(Object var1);

   public abstract Object method$LevelChunkSection$setBlockState(Object var1, int var2, int var3, int var4, Object var5, boolean var6);

   public abstract Object method$LevelChunkSection$getBlockState(Object var1, int var2, int var3, int var4);

   public abstract Object field$CraftChunk$worldServer(Chunk var1);

   public abstract Object method$ServerLevel$getChunkSource(Object var1);

   public abstract Object method$ServerChunkCache$getChunkAtIfLoadedMainThread(Object var1, int var2, int var3);

   public abstract Object method$ServerChunkCache$getChunk(Object var1, int var2, int var3, boolean var4);

   public abstract Object field$LevelChunkSection$states(Object var1);

   public abstract Object[] method$ChunkAccess$getSections(Object var1);

   public abstract Object field$ChunkAccess$blockEntities(Object var1);

   public abstract Object field$CraftWorld$ServerLevel(World var1);

   public abstract Block method$CraftBlock$at(Object var1, Object var2);

   public abstract Object field$AbstractFurnaceBlockEntity$recipeType(Object var1);

   public abstract ItemStack method$CraftItemStack$asCraftMirror(Object var1);

   public abstract Object field$ResourceKey$location(Object var1);

   public abstract Object field$RecipeHolder$id(Object var1);

   public abstract World method$Level$getCraftWorld(Object var1);

   public abstract boolean method$Level$removeBlock(Object var1, Object var2, boolean var3);

   public abstract int field$Vec3i$x(Object var1);

   public abstract int field$Vec3i$y(Object var1);

   public abstract int field$Vec3i$z(Object var1);

   public abstract double field$Vec3$x(Object var1);

   public abstract double field$Vec3$y(Object var1);

   public abstract double field$Vec3$z(Object var1);

   public abstract Object constructor$BlockPos(int var1, int var2, int var3);

   public abstract Object method$BlockGetter$getBlockState(Object var1, Object var2);

   public abstract Object method$CraftPlayer$getHandle(Player var1);

   public abstract Object constructor$AABB(double var1, double var3, double var5, double var7, double var9, double var11);

   public abstract boolean method$LevelWriter$addFreshEntity(Object var1, Object var2);

   public abstract Object method$CraftEntity$getHandle(Object var1);

   public abstract Object constructor$ClientboundSetPassengersPacket(int var1, int... var2);

   public abstract boolean method$ServerLevel$isPreventingStatusUpdates(Object var1, int var2, int var3);

   public abstract int method$Entity$getId(Object var1);

   public abstract boolean method$LevelWriter$setBlock(Object var1, Object var2, Object var3, int var4);

   public abstract Object method$ServerChunkCache$getVisibleChunkIfPresent(Object var1, long var2);

   public abstract Object constructor$ChunkPos(int var1, int var2);

   public abstract Object constructor$ClientboundLightUpdatePacket(Object var1, Object var2, BitSet var3, BitSet var4);

   public abstract void method$ServerChunkCache$blockChanged(Object var1, Object var2);

   public abstract void method$Connection$send(Object var1, Object var2, Object var3);

   public abstract List<Object> method$ChunkHolder$getPlayers(Object var1);

   public abstract Object constructor$ClientboundBundlePacket(List<Object> var1);

   public abstract Object field$Player$connection(Object var1);

   public abstract Object field$ServerGamePacketListenerImpl$connection(Object var1);

   public abstract void method$ServerPlayerConnection$send(Object var1, Object var2);

   public abstract Channel field$Connection$channel(Object var1);

   public abstract void method$BlockStateBase$onPlace(Object var1, Object var2, Object var3, Object var4, boolean var5);

   public abstract void method$LevelAccessor$levelEvent(Object var1, int var2, Object var3, int var4);

   public abstract Iterable<Object> method$ClientboundBundlePacket$subPackets(Object var1);

   public abstract Object method$ResourceLocation$fromNamespaceAndPath(String var1, String var2);

   public abstract Object field$SoundEvent$location(Object var1);

   public abstract Object field$ServerboundSwingPacket$hand(Object var1);

   public abstract Object field$BlockParticleOption$blockState(Object var1);

   public abstract Object field$ServerboundPlayerActionPacket$pos(Object var1);

   public abstract Object field$ServerboundPlayerActionPacket$action(Object var1);

   public abstract Object method$CraftItemStack$asNMSCopy(ItemStack var1);

   public abstract int field$SynchedEntityData$DataValue$id(Object var1);

   public abstract Object field$SynchedEntityData$DataValue$value(Object var1);

   public abstract Object field$SynchedEntityData$DataValue$serializer(Object var1);

   public abstract Object constructor$SynchedEntityData$DataValue(int var1, Object var2, Object var3);

   public abstract Object method$Component$Serializer$fromJson(JsonElement var1);

   public abstract Object method$Component$Serializer$fromJson(String var1);

   public abstract String method$Component$Serializer$toJson(Object var1);

   public abstract Entity method$Entity$getBukkitEntity(Object var1);

   public abstract Optional<Object> method$IdMap$byId(Object var1, int var2);

   public abstract Optional<Integer> method$IdMap$getId(Object var1, Object var2);

   public abstract void method$SoundEvent$directEncode(ByteBuf var1, Object var2);

   public abstract List<Object> field$ClientboundPlayerInfoUpdatePacket$entries(Object var1);

   public abstract EnumSet<? extends Enum> field$ClientboundPlayerInfoUpdatePacket$actions(Object var1);

   public abstract Object constructor$ClientboundPlayerInfoUpdatePacket(EnumSet var1, List var2);

   public abstract Optional method$RecipeManager$getRecipeFor(Object var1, Object var2, Object var3, Object var4, Object var5);

   public abstract Object field$ClientboundPlayerInfoUpdatePacket$Entry$displayName(Object var1);

   public abstract Object constructor$ClientboundPlayerInfoUpdatePacket$Entry(Object var1, Object var2);

   public abstract void method$ItemStack$setComponent(Object var1, Object var2, Object var3);

   public abstract Object field$CraftItemStack$handle(ItemStack var1);

   public abstract Object field$ServerPlayer$gameMode(Object var1);

   public abstract void field$Player$mayBuild(Object var1, boolean var2);

   public abstract boolean field$Player$mayBuild(Object var1);

   public abstract double method$Player$getInteractionRange(Object var1);

   public abstract int field$MinecraftServer$currentTick();

   public abstract float method$BlockStateBase$getDestroyProgress(Object var1, Object var2, Object var3, Object var4);

   public abstract boolean method$ItemStack$isCorrectToolForDrops(Object var1, Object var2);

   public abstract boolean method$Player$hasCorrectToolForDrops(Object var1, Object var2);

   public abstract Object constructor$ClientboundBlockDestructionPacket(int var1, Object var2, int var3);

   public abstract Object constructor$ClientboundLevelEventPacket(int var1, Object var2, int var3, boolean var4);

   public abstract Object constructor$BlockInWorld(Object var1, Object var2, boolean var3);

   public abstract boolean method$ItemStack$canBreakInAdventureMode(Object var1, Object var2);

   public abstract boolean method$ItemStack$canPlaceInAdventureMode(Object var1, Object var2);

   public abstract Object method$Direction$getOpposite(Object var1);

   public abstract Object method$BlockPos$relative(Object var1, Object var2);

   public abstract String field$ClientboundResourcePackPushPacket$url(Object var1);

   public abstract Object constructor$ClientboundResourcePackPushPacket(UUID var1, String var2, String var3, boolean var4, Object var5);

   public abstract Object constructor$ClientboundResourcePackPopPacket(UUID var1);

   public abstract UUID field$ClientboundResourcePackPushPacket$uuid(Object var1);

   public abstract Object constructor$ServerboundResourcePackPacket$SUCCESSFULLY_LOADED(UUID var1);

   public abstract Object method$CraftEntityType$toNMSEntityType(EntityType var1);

   public abstract boolean method$BonemealableBlock$isValidBonemealTarget(Object var1, Object var2, Object var3, Object var4);

   public abstract Object constructor$ClientboundSetEntityDataPacket(int var1, List var2);

   public abstract Object constructor$ClientboundAddEntityPacket(int var1, UUID var2, double var3, double var5, double var7, float var9, float var10, Object var11, int var12, Object var13, double var14);

   public abstract Object method$SynchedEntityData$DataValue$create(Object var1, Object var2);

   public abstract Object constructor$EntityDataAccessor(int var1, Object var2);

   public abstract void simulateInteraction(Object var1, Object var2, double var3, double var5, double var7, Object var9);

   public abstract boolean checkEntityCollision(Object var1, List<Object> var2, Predicate<Object> var3);

   public abstract void method$ItemStack$applyComponents(Object var1, Object var2);

   public abstract Object method$ItemStack$getItem(Object var1);

   public abstract Object method$ItemStack$transmuteCopy(Object var1, Object var2, int var3);

   public abstract Object method$ItemStack$getComponentsPatch(Object var1);

   public abstract Object method$ItemStack$getComponent(Object var1, Object var2);

   public abstract boolean method$ItemStack$hasComponent(Object var1, Object var2);

   public abstract boolean method$ItemStack$hasNonDefaultComponent(Object var1, Object var2);

   public abstract Object method$ItemStack$removeComponent(Object var1, Object var2);

   public abstract String getCustomItemId(Object var1);

   public abstract void setCustomItemId(Object var1, String var2);

   public abstract void method$LevelChunk$markUnsaved(Object var1);

   public abstract boolean method$LevelChunk$isUnsaved(Object var1);

   public abstract Object constructor$ClientboundSystemChatPacket(Object var1, boolean var2);

   public abstract Object constructor$LevelChunkSection(Object var1);

   public abstract Object field$LevelChunkSection$biomes(Object var1);

   public abstract boolean field$Entity$wasTouchingWater(Object var1);

   public abstract Object constructor$ClientboundUpdateTagsPacket(Map<?, ?> var1);

   public abstract Object constructor$ClientboundActionBarPacket(Object var1);

   public abstract Object constructor$ClientboundSetTitleTextPacket(Object var1);

   public abstract Object constructor$ClientboundSetSubtitleTextPacket(Object var1);

   public abstract Object constructor$ClientboundSetTitlesAnimationPacket(int var1, int var2, int var3);

   public abstract Object field$Player$containerMenu(Object var1);

   public abstract void field$Player$containerMenu(Object var1, Object var2);

   public abstract Particle method$CraftParticle$toBukkit(Object var1);

   public abstract boolean method$SignalGetter$hasNeighborSignal(Object var1, Object var2);

   public abstract Object method$BlockState$getBlock(Object var1);

   public abstract Object method$BlockState$getShape(Object var1, Object var2, Object var3, Object var4);

   public abstract Object method$BlockState$getCollisionShape(Object var1, Object var2, Object var3, Object var4);

   public abstract Object method$BlockState$getBlockSupportShape(Object var1, Object var2, Object var3);

   public abstract boolean method$LightEngine$hasDifferentLightProperties(Object var1, Object var2);

   public abstract Object constructor$FriendlyByteBuf(ByteBuf var1);

   public abstract ItemStack method$FriendlyByteBuf$readItem(Object var1);

   public abstract void method$FriendlyByteBuf$writeItem(Object var1, ItemStack var2);

   public abstract ItemStack method$FriendlyByteBuf$readUntrustedItem(Object var1);

   public abstract void method$FriendlyByteBuf$writeUntrustedItem(Object var1, ItemStack var2);

   public abstract Codec method$DataComponentType$codec(Object var1);

   public abstract Object field$ItemStack$getOrCreateTag(Object var1);

   public abstract Object constructor$ShortTag(short var1);

   public abstract short method$ShortTag$value(Object var1);

   public abstract Object constructor$IntTag(int var1);

   public abstract int method$IntTag$value(Object var1);

   public abstract Object constructor$LongTag(long var1);

   public abstract long method$LongTag$value(Object var1);

   public abstract Object constructor$ByteTag(byte var1);

   public abstract byte method$ByteTag$value(Object var1);

   public abstract Object constructor$FloatTag(float var1);

   public abstract float method$FloatTag$value(Object var1);

   public abstract Object constructor$DoubleTag(double var1);

   public abstract double method$DoubleTag$value(Object var1);

   public abstract Object constructor$ByteArrayTag(byte[] var1);

   public abstract byte[] method$ByteArrayTag$value(Object var1);

   public abstract Object constructor$IntArrayTag(int[] var1);

   public abstract int[] method$IntArrayTag$value(Object var1);

   public abstract Object constructor$LongArrayTag(long[] var1);

   public abstract long[] method$LongArrayTag$value(Object var1);

   public abstract Object constructor$StringTag(String var1);

   public abstract String method$StringTag$value(Object var1);

   public abstract Object constructor$ListTag();

   public abstract Object method$ListTag$get(Object var1, int var2);

   public abstract void method$ListTag$add(Object var1, int var2, Object var3);

   public abstract Object method$ListTag$remove(Object var1, int var2);

   public abstract Object method$CompoundTag$get(Object var1, String var2);

   public abstract void method$CompoundTag$put(Object var1, String var2, Object var3);

   public abstract void method$CompoundTag$remove(Object var1, String var2);

   public abstract Object constructor$CompoundTag();

   public abstract Object registryAccess();

   public abstract Object method$StatePredicate$always(boolean var1);

   public abstract Object method$ResourceKey$create(Object var1, Object var2);

   public abstract List<Object> field$SimpleContainer$items(Object var1);

   public abstract Object field$SingleRecipeInput$item(Object var1);

   public abstract Object field$AbstractFurnaceBlockEntity$getItem(Object var1, int var2);

   public abstract Object method$TagParser$parseCompoundFully(String var1) throws CommandSyntaxException;

   public abstract Object method$Registry$getValue(Object var1, Object var2);

   public abstract Object constructor$ItemStack(Object var1, int var2);

   public abstract void method$ScheduledTickAccess$scheduleBlockTick(Object var1, Object var2, Object var3, int var4);

   public abstract void method$ScheduledTickAccess$scheduleFluidTick(Object var1, Object var2, Object var3, int var4);

   public abstract void method$ItemStack$setTag(Object var1, Object var2);

   public abstract Object method$AbstractContainerMenu$getCarried(Object var1);

   public abstract void method$AbstractContainerMenu$broadcastFullState(Object var1);

   public abstract List<Object> field$AbstractContainerMenu$dataSlots(Object var1);

   public abstract int method$DataSlot$get(Object var1);

   public abstract int field$AbstractContainerMenu$containerId(Object var1);

   public abstract Object method$AbstractContainerMenu$getSlot(Object var1, int var2);

   public abstract Object method$Slot$getItem(Object var1);

   public abstract Object constructor$ClientboundContainerSetDataPacket(int var1, int var2, int var3);

   public abstract Object method$Block$defaultState(Object var1);

   public abstract boolean method$BlockStateBase$isSignalSource(Object var1);

   public abstract Object method$BlockGetter$getFluidState(Object var1, Object var2);

   public abstract Object method$FluidState$getType(Object var1);

   public abstract boolean method$Explosion$canTriggerBlocks(Object var1);

   public abstract boolean method$BlockStateBase$canSurvive(Object var1, Object var2, Object var3);

   public abstract boolean method$BlockStateBase$isCollisionShapeFullBlock(Object var1, Object var2, Object var3);

   public abstract String method$ResourceLocation$namespace(Object var1);

   public abstract String method$ResourceLocation$path(Object var1);

   public abstract boolean method$BlockStateBase$is(Object var1, Object var2);

   public abstract boolean method$BlockStateBase$isAir(Object var1);

   public abstract boolean method$Block$canSupportRigidBlock(Object var1, Object var2);

   public abstract boolean method$Block$canSupportCenter(Object var1, Object var2, Object var3);

   public abstract void method$Level$setBlocksDirty(Object var1, Object var2, Object var3, Object var4);

   public abstract boolean method$BlockStateBase$isFaceSturdy(Object var1, Object var2, Object var3, Object var4, Object var5);

   public abstract List<Object> method$EntityGetter$getEntitiesOfClass(Object var1, Class var2, Object var3, Predicate var4);

   public abstract Object method$AABB$move(Object var1, Object var2);

   public abstract void method$Level$updateNeighborsAt(Object var1, Object var2, Object var3);

   public abstract Object field$Entity$trackedEntity(Object var1);

   public abstract Object field$ChunkMap$TrackedEntity$serverEntity(Object var1);

   public abstract boolean method$AbstractArrow$isInGround(Object var1);

   public abstract Map method$TagNetworkSerialization$serializeTagsToNetwork();

   public abstract void method$TagNetworkSerialization$NetworkPayload$write(Object var1, Object var2);

   public abstract Object method$TagNetworkSerialization$NetworkPayload$read(Object var1);

   public abstract Object method$Registry$getKey(Object var1, Object var2);

   public abstract boolean method$ItemStack$isEmpty(Object var1);

   public abstract ItemStack method$CraftItemStack$asCraftCopy(ItemStack var1);

   public abstract Object method$Item$components(Object var1);

   public abstract Object method$DataComponentMap$get(Object var1, Object var2);

   public abstract Object method$TagKey$create(Object var1, Object var2);

   public abstract boolean method$BlockStateBase$isReplaceable(Object var1);

   public abstract void method$ClientboundSetEntityDataPacket$pack(List<?> var1, Object var2);

   public abstract List<Object> method$ClientboundSetEntityDataPacket$unpack(Object var1);

   public abstract int method$ClientboundEntityPositionSyncPacket$id(Object var1);

   public abstract Object field$ClientboundEntityPositionSyncPacket$values(Object var1);

   public abstract boolean field$ClientboundEntityPositionSyncPacket$onGround(Object var1);

   public abstract Object field$PositionMoveRotation$position(Object var1);

   public abstract Object field$PositionMoveRotation$deltaMovement(Object var1);

   public abstract float field$PositionMoveRotation$yRot(Object var1);

   public abstract float field$PositionMoveRotation$xRot(Object var1);

   public abstract Object constructor$PositionMoveRotation(Object var1, Object var2, float var3, float var4);

   public abstract Object constructor$ClientboundEntityPositionSyncPacket(int var1, Object var2, boolean var3);

   public abstract short field$ClientboundMoveEntityPacket$xa(Object var1);

   public abstract short field$ClientboundMoveEntityPacket$ya(Object var1);

   public abstract short field$ClientboundMoveEntityPacket$za(Object var1);

   public abstract byte field$ClientboundMoveEntityPacket$yRot(Object var1);

   public abstract byte field$ClientboundMoveEntityPacket$xRot(Object var1);

   public abstract boolean field$ClientboundMoveEntityPacket$onGround(Object var1);

   public abstract Object constructor$ClientboundMoveEntityPacket$PosRot(int var1, short var2, short var3, short var4, byte var5, byte var6, boolean var7);

   public abstract Map<ConnectionState, Map<PacketFlow, Map<Class<?>, Integer>>> gamePacketIdsByClazz();

   public abstract Map<ConnectionState, Map<PacketFlow, Map<String, Integer>>> gamePacketIdsByName();

   public abstract Object method$FriendlyByteBuf$readById(Object var1, Object var2);

   public abstract Object method$ClientboundLevelParticlesPacket$readParticle(Object var1, Object var2);

   public abstract Object method$BlockParticleOption$getType(Object var1);

   public abstract Object constructor$BlockParticleOption(Object var1, Object var2);

   public abstract void method$FriendlyByteBuf$writeId(Object var1, Object var2, Object var3);

   public abstract void method$ParticleOptions$writeToNetwork(Object var1, Object var2);

   public abstract Object method$SoundEvent$location(Object var1);

   public abstract Object constructor$SoundEvent(Object var1, Optional<Float> var2);

   public abstract Optional<Float> method$SoundEvent$fixedRange(Object var1);

   public abstract Object method$LootParams$Builder$getOptionalParameter(Object var1, Object var2);

   public abstract Object method$LootParams$Builder$getLevel(Object var1);

   public abstract Player method$ServerPlayer$getBukkitEntity(Object var1);

   public abstract void method$Block$dropResources(Object var1, Object var2, Object var3);

   public abstract BlockRedstoneEvent method$CraftEventFactory$callRedstoneChange(Object var1, Object var2, int var3, int var4);

   public abstract boolean method$LevelWriter$destroyBlock(Object var1, Object var2, boolean var3);

   public abstract Object method$itemStack$save(Object var1, Object var2);

   public abstract Object method$ItemStack$getTag(Object var1);

   public abstract Set<Map.Entry> method$CompoundTag$entrySet(Object var1);

   public abstract Object method$CompoundTag$merge(Object var1, Object var2);

   public abstract Object method$CompoundTag$copy(Object var1);

   public abstract void method$Player$startSleepInBed(Object var1, Object var2, boolean var3);

   public abstract Object field$ServerboundResourcePackPacket$action(Object var1);

   public abstract UUID field$ServerboundResourcePackPacket$id(Object var1);

   public abstract Object method$Block$asItem(Object var1);

   public abstract Object method$RegistryAccess$lookupOrThrow(Object var1, Object var2);

   public abstract int method$Registry$getId(Object var1, Object var2);

   public abstract Optional<Object> method$Registry$getHolderByResourceLocation(Object var1, Object var2);

   public abstract Optional<Object> method$Registry$getHolderByResourceKey(Object var1, Object var2);

   public abstract Object method$ServerLevel$getEntityLookup(Object var1);

   public abstract Object method$EntityLookup$get(Object var1, int var2);

   public abstract boolean field$BlockBehavior$hasCollision(Object var1);

   public abstract Object method$Connection$getPacketListener(Object var1);

   public abstract Object constructor$ServerResourcePackConfigurationTask(Object var1);

   public abstract Object constructor$ServerResourcePackInfo(UUID var1, String var2, String var3, boolean var4, @Nullable Object var5);

   public abstract void method$ServerConfigurationPacketListenerImpl$returnToWorld(Object var1);

   public abstract boolean method$BlockStateBase$isPathFindable(Object var1, Object var2, Object var3, Object var4);

   public abstract void method$LevelAccessor$levelEvent(Object var1, Object var2, int var3, Object var4, int var5);

   public abstract Object field$Player$abilities(Object var1);

   public abstract boolean field$Abilities$instabuild(Object var1);

   public abstract GameProfile field$ClientboundLoginFinishedPacket$gameProfile(Object var1);

   public abstract Optional method$TrimMaterials$getFromIngredient(Object var1);

   public abstract Optional method$TrimPatterns$getFromTemplate(Object var1);

   public abstract Object constructor$ArmorTrim(Object var1, Object var2);

   public abstract Object method$CustomData$getUnsafe(Object var1);

   public abstract boolean method$ServerLevel$setChunkForced(Object var1, int var2, int var3, boolean var4);

   public abstract boolean method$LevelReader$isClientSide(Object var1);

   public abstract void method$ScheduledTickAccess$scheduleBlockTick(Object var1, Object var2, Object var3, int var4, Object var5);

   public abstract Object method$StreamDecoder$decode(Object var1, Object var2);

   public abstract void method$StreamEncoder$encode(Object var1, Object var2, Object var3);

   public abstract boolean method$HashedStack$matches(Object var1, Object var2, Object var3);

   public abstract Object method$Player$getInventory(Object var1);

   public abstract Object method$Container$getItem(Object var1, int var2);

   public abstract Object method$HashedStack$create(Object var1, Object var2);

   public abstract Object createDecoratedHashOpsGenerator(DynamicOps<HashCode> var1);

   public abstract Object method$StateHolder$getValue(Object var1, Object var2);

   public abstract Object method$Entity$getType(Object var1);

   public abstract void method$BlockableEventLoop$scheduleOnMain(Runnable var1);

   public abstract void method$Connection$handleDisconnection(Object var1);

   public abstract Object method$PacketSendListener$thenRun(Runnable var1);

   public abstract void method$Connection$disconnect(Object var1, Object var2);

   public abstract boolean method$ItemStack$is(Object var1, Object var2);

   public abstract Object method$DyeItem$getDyeColor(Object var1);

   public abstract int method$DyeColor$getTextureDiffuseColor(Object var1);

   public abstract Object method$CraftInventoryCrafting$getMatrixInventory(CraftingInventory var1);

   public abstract void method$CraftingContainer$setCurrentRecipe(Object var1, Object var2);

   public abstract Object method$CraftInventoryCrafting$getResultInventory(CraftingInventory var1);

   public abstract void method$ResultContainer$setRecipeUsed(Object var1, Object var2);

   public abstract void method$RecipeManager$addRecipe(Object var1, Object var2);

   public abstract Object method$Ingredient$of(Object[] var1);

   public abstract void method$RecipeMap$removeRecipe(Object var1, Object var2);

   public abstract void method$RecipeManager$removeRecipe(Object var1, Object var2);

   public abstract Object constructor$RecipeHolder(Object var1, Object var2);

   public abstract int method$CraftingInput$ingredientCount(Object var1);

   public abstract int method$CraftingInput$size(Object var1);

   public abstract Object method$CraftingInput$getItem(Object var1, int var2);

   public abstract int method$Container$getContainerSize(Object var1);

   public abstract boolean method$Item$canBeDepleted(Object var1);

   public abstract int method$DyeColor$getFireworkColor(Object var1);

   public abstract Object method$MinecraftServer$getRecipeManager(Object var1);

   public abstract Object field$RecipeManager$recipes(Object var1);

   public abstract Object method$MinecraftServer$getServer();

   public abstract Object constructor$InjectedHashedStack(Object var1, net.momirealms.craftengine.core.entity.player.Player var2);

   public abstract int field$ServerboundContainerClickPacket$containerId(Object var1);

   public abstract int field$ServerboundContainerClickPacket$stateId(Object var1);

   public abstract short field$ServerboundContainerClickPacket$slotNum(Object var1);

   public abstract byte field$ServerboundContainerClickPacket$buttonNum(Object var1);

   public abstract Object field$ServerboundContainerClickPacket$clickType(Object var1);

   public abstract Int2ObjectMap field$ServerboundContainerClickPacket$changedSlots(Object var1);

   public abstract Object field$ServerboundContainerClickPacket$carriedItem(Object var1);

   public abstract Object constructor$ServerboundContainerClickPacket(int var1, int var2, short var3, byte var4, Object var5, Int2ObjectMap var6, Object var7);

   public abstract Object method$CraftInventory$getInventory(Inventory var1);

   public abstract boolean method$BlockStateBase$isBlock(Object var1, Object var2);

   public abstract Object field$BlockHitResult$blockPos(Object var1);

   public abstract Object field$HitResult$location(Object var1);

   public abstract boolean field$BlockHitResul$miss(Object var1);

   public abstract Object field$BlockHitResul$direction(Object var1);

   public abstract Object method$ChunkSource$getLightEngine(Object var1);

   public abstract void method$Level$updateNeighbourForOutputSignal(Object var1, Object var2, Object var3);

   public abstract Object method$ItemStack$of(Object var1);

   public abstract Object constructor$ClientboundRemoveEntitiesPacket(IntList var1);

   public abstract boolean method$Entity$causeFallDamage(Object var1, Number var2, float var3, Object var4);

   public abstract Object method$Entity$damageSources(Object var1);

   public abstract Object method$DamageSources$fall(Object var1);

   public abstract boolean method$Entity$getSharedFlag(Object var1, int var2);

   public abstract Object method$Entity$getDeltaMovement(Object var1);

   public abstract void method$Entity$setDeltaMovement(Object var1, double var2, double var4, double var6);

   public abstract boolean field$Entity$hurtMarked(Object var1);

   public abstract void field$Entity$hurtMarked(Object var1, boolean var2);

   public abstract Inventory createSimpleStorageContainer(InventoryHolder var1, int var2, boolean var3, boolean var4);

   public abstract Object method$FluidState$createLegacyBlock(Object var1);

   public abstract boolean method$LevelSection$hasOnlyAir(Object var1);

   public abstract void method$LightEventListener$updateSectionStatus(Object var1, Object var2, boolean var3);

   public abstract void method$ThreadedLevelLightEngine$checkBlock(Object var1, Object var2);

   public abstract Object method$SectionPos$of(int var1, int var2, int var3);

   public abstract Object method$BlockBehaviour$BlockStateBase$getSoundType(Object var1);

   public abstract Object field$SoundType$breakSound(Object var1);

   public abstract Object field$SoundType$placeSound(Object var1);

   public abstract Object field$SoundType$hitSound(Object var1);

   public abstract Object field$SoundType$fallSound(Object var1);

   public abstract Object field$SoundType$stepSound(Object var1);

   public abstract float field$SoundType$volume(Object var1);

   public abstract float field$SoundType$pitch(Object var1);

   public abstract Object method$Holder$direct(Object var1);

   public abstract Object constructor$ClientboundSoundPacket(Object var1, Object var2, double var3, double var5, double var7, float var9, float var10, long var11);

   public abstract boolean method$LeadItem$bindPlayerMobs(Object var1, Object var2, Object var3);

   public abstract boolean method$FenceGateBlock$connectsToDirection(Object var1, Object var2);

   public abstract boolean method$Entity$isSpectator(Object var1);

   public abstract boolean method$Entity$isIgnoringBlockTriggers(Object var1);

   public abstract Object method$VoxelShape$bounds(Object var1);

   public abstract void method$Level$updateNeighborsAt(Object var1, Object var2, Object var3, @Nullable Object var4);

   public abstract @Nullable Object method$ExperimentalRedstoneUtils$initialOrientation(Object var1, @Nullable Object var2, @Nullable Object var3);

   public abstract void method$LevelAccessor$playSound(Object var1, @Nullable Object var2, Object var3, Object var4, Object var5, float var6, float var7);

   public abstract void method$LevelAccessor$gameEvent(Object var1, @Nullable Object var2, Object var3, Object var4);

   public abstract void method$BlockBehaviour$BlockStateBase$tick(Object var1, Object var2, Object var3);

   public abstract Object method$Holder$value(Object var1);

   public abstract Object method$SynchedEntityData$get(Object var1, Object var2);

   public abstract void method$BlockBehaviour$BlockStateBase$randomTick(Object var1, Object var2, Object var3);

   public abstract Object field$BlockBehaviour$BlockStateBase$fluidState(Object var1);

   public abstract int field$FluidState$amount(Object var1);

   public abstract Object method$StateHolder$trySetValue(Object var1, Object var2, Comparable var3);

   public abstract boolean method$Inventory$add(Object var1, Object var2);

   public abstract Object method$ServerPlayer$drop(Object var1, Object var2, boolean var3, boolean var4, boolean var5, Consumer var6);

   public abstract void method$ItemEntity$makeFakeItem(Object var1);

   public abstract void method$ItemEntity$setNoPickUpDelay(Object var1);

   public abstract void method$ItemEntity$setTarget(Object var1, UUID var2);

   public abstract void method$AbstractContainerMenu$broadcastChanges(Object var1);

   public abstract Object field$Entity$entityData(Object var1);

   public abstract void method$SynchedEntityData$set(Object var1, Object var2, Object var3, boolean var4);

   public abstract Object method$DataComponentExactPredicate$allOf(Object var1);

   public abstract Object method$ItemStack$getComponents(Object var1);

   public abstract Object method$Item$builtInRegistryHolder(Object var1);

   public abstract Object field$ItemCost$itemStack(Object var1);

   public abstract Object constructor$ItemCost(Object var1, int var2, Object var3);

   public abstract Iterable method$BundleContents$items(Object var1);

   public abstract Object constructor$BundleContents(List var1);

   public abstract List field$ItemContainerContents$items(Object var1);

   public abstract Object method$ItemContainerContents$fromItems(List var1);

   public abstract Object constructor$BlockPlaceContext(Object var1, Object var2, Object var3, Object var4, Object var5);

   public abstract Object constructor$BlockHitResult(Object var1, Object var2, Object var3, boolean var4);

   public abstract Object method$BlockItem$getBlock(Object var1);

   public abstract Object method$Block$getStateForPlacement(Object var1, Object var2);

   public abstract Object constructor$Vec3(double var1, double var3, double var5);

   public abstract Object method$AbstractContainerMenu$quickMoveStack(Object var1, Object var2, int var3);

   public abstract Object method$CraftingContainer$getCurrentRecipe(Object var1);

   public abstract void method$ItemStack$hurtAndBreak(Object var1, int var2, Object var3, Object var4);

   public abstract Object constructor$ClientboundEntityPositionSyncPacket(int var1, double var2, double var4, double var6, float var8, float var9, boolean var10);

   public abstract Object field$ServerChunkCache$chunkMap(Object var1);

   public abstract Map field$ClientboundUpdateTagsPacket$tags(Object var1);

   public abstract int method$LightEngine$getLightBlockInto(@Nullable("1.21.2+") Object var1, Object var2, @Nullable("1.21.2+") Object var3, Object var4, @Nullable("1.21.2+") Object var5, Object var6, int var7);

   public abstract int method$BlockBehaviour$BlockStateBase$getLightBlock(Object var1, @Nullable("1.21.2+") Object var2, @Nullable("1.21.2+") Object var3);

   public abstract boolean method$FluidState$is(Object var1, Object var2);

   public abstract int method$LevelReader$getMaxLocalRawBrightness(Object var1, Object var2);

   public abstract Object method$BlockPos$offset(Object var1, int var2, int var3, int var4);

   public abstract Object field$Player$inventoryMenu(Object var1);

   public abstract Object method$InventoryMenu$getCraftSlots(Object var1);

   public abstract void method$InventoryMenu$slotsChanged(Object var1, Object var2);

   public abstract int method$Inventory$clearOrCountMatchingItems(Object var1, Predicate var2, int var3, Object var4);

   public abstract Object method$Entity$getPassengerRidingPosition(Object var1, Object var2);

   public abstract Object method$Entity$getOnPos(Object var1);

   public abstract Object method$ClientboundSetPlayerTeamPacket$createMultiplePlayerPacket(Object var1, Collection<String> var2, boolean var3);

   public abstract UUID method$Entity$getUUID(Object var1);

   public abstract Object constructor$ClientboundSetEquipmentPacket(int var1, List<Object> var2);

   public abstract Object constructor$AttributeInstance(Object var1, Consumer var2);

   public abstract void method$AttributeInstance$setBaseValue(Object var1, double var2);

   public abstract Object constructor$ClientboundUpdateAttributesPacket(int var1, Collection var2);

   public abstract Object field$ClientboundPlayerChatPacket$unsignedContent(Object var1);

   public abstract Object field$ClientboundPlayerChatPacket$body(Object var1);

   public abstract Object field$ClientboundPlayerChatPacket$chatType(Object var1);

   public abstract UUID field$ClientboundPlayerChatPacket$sender(Object var1);

   public abstract String field$SignedMessageBody$Packed$content(Object var1);

   public abstract Object method$ChatType$Bound$decorate(Object var1, Object var2);

   public abstract Object method$Component$literal(String var1);

   public abstract Object method$ChatType$BoundNetwork$resolve(Object var1);

   public abstract Object field$MinecraftServer$scoreboard();

   public abstract Object constructor$PlayerTeam(Object var1, String var2);

   public abstract void method$PlayerTeam$setColor(Object var1, String var2);

   public abstract Object method$ClientboundSetPlayerTeamPacket$createAddOrModifyPacket(Object var1, boolean var2);

   public abstract Object method$MapItem$getSavedData(Object var1, Object var2);

   public abstract Object method$MapItem$getMapId(Object var1);

   public abstract byte[] field$RenderData$buffer(Object var1);

   public abstract Object constructor$MapItemSavedData$MapPatch(int var1, int var2, int var3, int var4, byte[] var5);

   public abstract Object constructor$ClientboundMapItemDataPacket(Object var1, byte var2, boolean var3, Collection var4, Object var5);

   public abstract byte field$MapItemSavedData$scale(Object var1);

   public abstract boolean field$MapItemSavedData$locked(Object var1);

   public abstract byte[] field$MapItemSavedData$colors(Object var1);

   public abstract Object method$LevelReader$getNoiseBiome(Object var1, int var2, int var3, int var4);

   public abstract Object method$Holder$Reference$identifier(Object var1);
}
