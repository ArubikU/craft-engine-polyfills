package dev.arubik.craftengine.item.behavior;

import org.bukkit.craftbukkit.damage.CraftDamageSource;
import org.bukkit.damage.DamageType;

import com.mojang.datafixers.util.Pair;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.momirealms.craftengine.core.item.behavior.ItemBehaviorFactory;

public class FakeThornsLevel extends ExtendedItemBehavior {
    public static Factory FACTORY = new Factory();
    private final int extraThornsLevel;

    public FakeThornsLevel(int extraThornsLevel) {
        this.extraThornsLevel = extraThornsLevel;
    }

    public Pair<Double, Double> getEffect() {
        return Pair.of(
            1.0 + (this.extraThornsLevel - 1) * 0.15,
            2.0 + (this.extraThornsLevel - 1) * 2.0
        );
    }

    @Override
    public ItemStack onAttackEntity(ItemStack stack, Object... args) { // Fixed varargs method signature
        if (args.length > 1 && args[1] instanceof LivingEntity attacker) {
            int slot = (int) args[3];
            Entity receiver = (Entity) args[2];
            CraftDamageSource source = (CraftDamageSource) org.bukkit.damage.DamageSource.builder(DamageType.THORNS).withCausingEntity(attacker.getBukkitEntity()).build();
            Pair<Double, Double> effect = this.getEffect();
            receiver.hurtOrSimulate(source.getHandle(), effect.getFirst().floatValue());
            stack.hurtAndBreak(effect.getSecond().intValue(), attacker,equipmentSlotForEntityEvent(slot),true);
        }
        return stack;
    }

        public static EquipmentSlot equipmentSlotForEntityEvent(int event) {
            return switch (event) {
                case 47 -> EquipmentSlot.MAINHAND;
                case 48 -> EquipmentSlot.OFFHAND;
                case 49 -> EquipmentSlot.HEAD;
                case 50 -> EquipmentSlot.CHEST;
                case 51 -> EquipmentSlot.LEGS;
                case 52 -> EquipmentSlot.FEET;
                case 65 -> EquipmentSlot.BODY;
                case 68 -> EquipmentSlot.SADDLE;
                default -> throw new IllegalArgumentException("Unknown equipment break event: " + event);
            };
        }
    public static class Factory implements ItemBehaviorFactory {
        @Override
        public FakeThornsLevel create(net.momirealms.craftengine.core.pack.Pack pack, java.nio.file.Path path, net.momirealms.craftengine.core.util.Key key, java.util.Map<String, Object> arguments) {
            if (arguments.containsKey("extra_thorns_level")) {
                Object levelObj = arguments.get("extra_thorns_level");
                if (!(levelObj instanceof Integer level)) {
                    throw new IllegalArgumentException("'extra_thorns_level' must be an Integer");
                }
                return new FakeThornsLevel(level);
            }
            throw new IllegalArgumentException("Missing 'extra_thorns_level' argument");
        }
    }
}