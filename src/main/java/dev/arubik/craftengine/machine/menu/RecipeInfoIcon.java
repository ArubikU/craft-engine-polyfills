/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.papermc.paper.adventure.PaperAdventure
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.TextComponent
 *  net.kyori.adventure.text.format.NamedTextColor
 *  net.kyori.adventure.text.format.TextColor
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.item.ItemStack
 *  net.momirealms.craftengine.bukkit.api.CraftEngineItems
 *  net.momirealms.craftengine.bukkit.item.BukkitItemDefinition
 *  net.momirealms.craftengine.core.util.Key
 *  org.bukkit.Material
 *  org.bukkit.craftbukkit.inventory.CraftItemStack
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 */
package dev.arubik.craftengine.machine.menu;

import dev.arubik.craftengine.fluid.FluidStack;
import dev.arubik.craftengine.fluid.FluidType;
import dev.arubik.craftengine.gas.GasStack;
import dev.arubik.craftengine.gas.GasType;
import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import dev.arubik.craftengine.machine.menu.MenuText;
import dev.arubik.craftengine.machine.recipe.AbstractProcessingRecipe;
import dev.arubik.craftengine.machine.recipe.CraftEngineItemInput;
import dev.arubik.craftengine.machine.recipe.FluidInput;
import dev.arubik.craftengine.machine.recipe.FluidOutput;
import dev.arubik.craftengine.machine.recipe.GasInput;
import dev.arubik.craftengine.machine.recipe.GasOutput;
import dev.arubik.craftengine.machine.recipe.ItemInput;
import dev.arubik.craftengine.machine.recipe.ItemOutput;
import dev.arubik.craftengine.machine.recipe.RecipeInput;
import dev.arubik.craftengine.machine.recipe.RecipeOutput;
import dev.arubik.craftengine.machine.recipe.TagInput;
import io.papermc.paper.adventure.PaperAdventure;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.network.chat.Component;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.bukkit.item.BukkitItemDefinition;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class RecipeInfoIcon {
    private RecipeInfoIcon() {
    }

    public static ItemStack build(AbstractMachineBlockEntity machine, String machineId, double speedMultiplier, double generationBonus) {
        ItemMeta meta;
        AbstractProcessingRecipe r = machine.getCurrentRecipe();
        if (r == null) {
            return MenuText.emptyFiller();
        }
        double speed = speedMultiplier > 0.0 ? speedMultiplier : 1.0;
        double genMul = 1.0 + Math.max(0.0, generationBonus);
        ArrayList<net.kyori.adventure.text.Component> lore = new ArrayList<net.kyori.adventure.text.Component>();
        RecipeInfoIcon.appendRecipe(machine, lore, r, speed, genMul);
        ItemStack base = RecipeInfoIcon.iconForRecipe(r);
        if (base == null || base.getType() == Material.AIR) {
            base = new ItemStack(Material.BOOK);
        }
        if ((meta = base.getItemMeta()) != null) {
            meta.displayName(MenuText.noI(MenuText.tr("polyfill.ui.info", NamedTextColor.AQUA)));
            ArrayList<net.kyori.adventure.text.Component> ls = new ArrayList<net.kyori.adventure.text.Component>();
            for (net.kyori.adventure.text.Component c : lore) {
                ls.add(MenuText.noI(c));
            }
            meta.lore(ls);
            base.setItemMeta(meta);
        }
        return base;
    }

    private static ItemStack iconForRecipe(AbstractProcessingRecipe r) {
        for (RecipeOutput o : r.getOutputs()) {
            if (!(o instanceof ItemOutput)) continue;
            ItemOutput io = (ItemOutput)o;
            net.minecraft.world.item.ItemStack nms = (net.minecraft.world.item.ItemStack)io.getOutput();
            return CraftItemStack.asBukkitCopy((net.minecraft.world.item.ItemStack)nms);
        }
        for (RecipeOutput o : r.getOutputs()) {
            if (o instanceof GasOutput) {
                GasOutput go = (GasOutput)o;
                GasStack gs = (GasStack)go.getOutput();
                String name = gs.getType() == null ? "" : gs.getType().name().toLowerCase(Locale.ROOT);
                Key id = name.contains("heavy") ? Key.of((String)"cml", (String)"heavy_steam_icon") : Key.of((String)"cml", (String)"steam_icon");
                return RecipeInfoIcon.ceItemOrNull(id);
            }
            if (!(o instanceof FluidOutput)) continue;
            FluidOutput fo = (FluidOutput)o;
            FluidStack fs = (FluidStack)fo.getOutput();
            return new ItemStack(RecipeInfoIcon.bucketFor(fs.getType()));
        }
        return null;
    }

    private static ItemStack ceItemOrNull(Key id) {
        try {
            BukkitItemDefinition def = CraftEngineItems.byId((Key)id);
            if (def != null) {
                return def.buildBukkitItem();
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return new ItemStack(Material.BOOK);
    }

    private static Material bucketFor(FluidType type) {
        String n;
        if (type == null) {
            return Material.BUCKET;
        }
        switch (n = type.name().toLowerCase(Locale.ROOT)) {
            case "snow": {
                return Material.POWDER_SNOW_BUCKET;
            }
            case "powder_snow": {
                return Material.POWDER_SNOW_BUCKET;
            }
            case "slime": {
                return Material.SLIME_BALL;
            }
            case "xp": 
            case "experience": {
                return Material.EXPERIENCE_BOTTLE;
            }
            case "honey": {
                return Material.HONEY_BOTTLE;
            }
        }
        Material m = Material.matchMaterial((String)(n + "_bucket"));
        return m != null ? m : Material.BUCKET;
    }

    private static void appendRecipe(AbstractMachineBlockEntity machine, List<net.kyori.adventure.text.Component> lore, AbstractProcessingRecipe r, double speed, double genMul) {
        boolean anyInput;
        double effTicks = (double)r.getProcessTime() / speed;
        double craftsPerMin = effTicks > 0.0 ? 1200.0 / effTicks : 0.0;
        for (RecipeOutput o : r.getOutputs()) {
            int mb;
            if (o instanceof ItemOutput) {
                ItemOutput io = (ItemOutput)o;
                net.minecraft.world.item.ItemStack os = (net.minecraft.world.item.ItemStack)io.getOutput();
                int c = os.getCount();
                float chance = io.getChance() <= 0.0f ? 1.0f : io.getChance();
                net.kyori.adventure.text.Component line = ((TextComponent)net.kyori.adventure.text.Component.text((String)"\u2192 ", (TextColor)NamedTextColor.WHITE).append(PaperAdventure.asAdventure((Component)os.getHoverName()))).append((net.kyori.adventure.text.Component)net.kyori.adventure.text.Component.text((String)(" x" + c), (TextColor)NamedTextColor.WHITE));
                if (chance < 1.0f) {
                    line = line.append((net.kyori.adventure.text.Component)net.kyori.adventure.text.Component.text((String)String.format(" (%.0f%%)", Float.valueOf(chance * 100.0f)), (TextColor)NamedTextColor.GOLD));
                }
                lore.add(MenuText.noI(line));
                double perMin = craftsPerMin * (double)c * (double)chance * genMul;
                lore.add(MenuText.noI((net.kyori.adventure.text.Component)net.kyori.adventure.text.Component.text((String)String.format("   %.1f/min", perMin), (TextColor)NamedTextColor.YELLOW)));
                continue;
            }
            if (o instanceof GasOutput) {
                GasOutput go = (GasOutput)o;
                GasStack gs = (GasStack)go.getOutput();
                mb = gs.getAmount();
                net.kyori.adventure.text.Component tn = gs.getType() == null ? net.kyori.adventure.text.Component.text((String)"?") : net.kyori.adventure.text.Component.translatable((String)gs.getType().translationKey());
                lore.add(MenuText.noI(((TextComponent)net.kyori.adventure.text.Component.text((String)"\u2192 ", (TextColor)NamedTextColor.WHITE).append(tn.colorIfAbsent((TextColor)NamedTextColor.WHITE))).append((net.kyori.adventure.text.Component)net.kyori.adventure.text.Component.text((String)(" " + mb + "mB"), (TextColor)NamedTextColor.WHITE))));
                double mbPerTick = effTicks > 0.0 ? (double)mb / effTicks : 0.0;
                lore.add(MenuText.noI((net.kyori.adventure.text.Component)net.kyori.adventure.text.Component.text((String)String.format("   %.1f mB/tick", mbPerTick), (TextColor)NamedTextColor.YELLOW)));
                continue;
            }
            if (!(o instanceof FluidOutput)) continue;
            FluidOutput fo = (FluidOutput)o;
            FluidStack fs = (FluidStack)fo.getOutput();
            mb = fs.getAmount();
            net.kyori.adventure.text.Component tn = fs.getType() == null ? net.kyori.adventure.text.Component.text((String)"?") : net.kyori.adventure.text.Component.translatable((String)fs.getType().translationKey());
            lore.add(MenuText.noI(((TextComponent)net.kyori.adventure.text.Component.text((String)"\u2192 ", (TextColor)NamedTextColor.WHITE).append(tn.colorIfAbsent((TextColor)NamedTextColor.WHITE))).append((net.kyori.adventure.text.Component)net.kyori.adventure.text.Component.text((String)(" " + mb + "mB"), (TextColor)NamedTextColor.WHITE))));
            double mbPerTick = effTicks > 0.0 ? (double)mb / effTicks : 0.0;
            lore.add(MenuText.noI((net.kyori.adventure.text.Component)net.kyori.adventure.text.Component.text((String)String.format("   %.1f mB/tick", mbPerTick), (TextColor)NamedTextColor.YELLOW)));
        }
        lore.add(MenuText.kv("polyfill.ui.time", NamedTextColor.GRAY, String.format("%.1fs", effTicks / 20.0), NamedTextColor.WHITE));
        boolean bl = anyInput = !r.getInputs().isEmpty();
        if (anyInput) {
            lore.add(MenuText.tr("polyfill.ui.ingredients", NamedTextColor.GOLD));
        }
        for (RecipeInput in : r.getInputs()) {
            int amt = in.getAmount();
            net.kyori.adventure.text.Component name = RecipeInfoIcon.inputName(in);
            double perMin = craftsPerMin * (double)amt;
            String unit = in instanceof FluidInput || in instanceof GasInput ? "mB" : "";
            net.kyori.adventure.text.Component line = ((TextComponent)((TextComponent)net.kyori.adventure.text.Component.text((String)"\u2022 ", (TextColor)NamedTextColor.GRAY).append(name)).append((net.kyori.adventure.text.Component)net.kyori.adventure.text.Component.text((String)(" " + amt + unit), (TextColor)NamedTextColor.WHITE))).append((net.kyori.adventure.text.Component)net.kyori.adventure.text.Component.text((String)String.format("  (%.1f%s/min)", perMin, unit.isEmpty() ? "" : unit), (TextColor)NamedTextColor.DARK_GRAY));
            lore.add(MenuText.noI(line));
        }
        if (r.getMinRpm() > 0) {
            lore.add(MenuText.kv("polyfill.ui.rpm", NamedTextColor.GRAY, String.valueOf(machine.effectiveRpm(r)), NamedTextColor.WHITE));
        }
        if (r.getSuCost() > 0) {
            lore.add(MenuText.kv("polyfill.ui.su", NamedTextColor.GRAY, String.valueOf(machine.effectiveSu(r)), NamedTextColor.WHITE));
        }
    }

    private static net.kyori.adventure.text.Component inputName(RecipeInput in) {
        if (in instanceof ItemInput) {
            ItemInput ii = (ItemInput)in;
            return PaperAdventure.asAdventure((Component)ii.getStack().getHoverName());
        }
        if (in instanceof CraftEngineItemInput) {
            CraftEngineItemInput ci = (CraftEngineItemInput)in;
            try {
                Key id = Key.of((String)ci.getItemId());
                BukkitItemDefinition def = CraftEngineItems.byId((Key)id);
                if (def != null) {
                    ItemStack b = def.buildBukkitItem();
                    net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy((ItemStack)b);
                    return PaperAdventure.asAdventure((Component)nms.getHoverName());
                }
            }
            catch (Throwable id) {
                // empty catch block
            }
            return net.kyori.adventure.text.Component.text((String)ci.getItemId(), (TextColor)NamedTextColor.WHITE);
        }
        if (in instanceof TagInput) {
            TagInput ti = (TagInput)in;
            return net.kyori.adventure.text.Component.text((String)("#" + String.valueOf(ti.getTag().location())), (TextColor)NamedTextColor.WHITE);
        }
        if (in instanceof FluidInput) {
            FluidInput fi = (FluidInput)in;
            FluidType ty = fi.getFluid().getType();
            return ty == null ? net.kyori.adventure.text.Component.text((String)"?", (TextColor)NamedTextColor.WHITE) : net.kyori.adventure.text.Component.translatable((String)ty.translationKey()).color((TextColor)NamedTextColor.WHITE);
        }
        if (in instanceof GasInput) {
            GasInput gi = (GasInput)in;
            GasType ty = gi.getGas().getType();
            return ty == null ? net.kyori.adventure.text.Component.text((String)"?", (TextColor)NamedTextColor.WHITE) : net.kyori.adventure.text.Component.translatable((String)ty.translationKey()).color((TextColor)NamedTextColor.WHITE);
        }
        return net.kyori.adventure.text.Component.text((String)"?", (TextColor)NamedTextColor.WHITE);
    }
}

