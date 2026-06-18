package dev.arubik.craftengine.machine.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.bukkit.Material;

import dev.arubik.craftengine.gas.GasStack;
import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
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
import dev.arubik.craftengine.machine.recipe.loader.RecipeManager;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.world.item.ItemStack;
import net.momirealms.craftengine.core.util.Key;

/**
 * Reusable recipe-info icon for machine menus. Renders ONE icon item whose:
 * <ul>
 *   <li><b>icon</b> = the primary recipe's main output (item → that item; gas → a steam icon;
 *       fluid → the matching vanilla bucket), and</li>
 *   <li><b>lore</b> = per-recipe stats: output (+chance), output rate (items/min or mB/tick),
 *       cook time (effective after speed), each ingredient (+input/min), and rpm/su when the
 *       recipe is mechanical.</li>
 * </ul>
 *
 * <p>Any machine can call {@link #build(AbstractMachineBlockEntity, String, double, double)} —
 * it pulls all recipe data from {@link RecipeManager} by {@code machineId} and is otherwise
 * generic. {@code speedMultiplier} shortens the effective process time (e.g. furnace
 * {@code (1+generation)*(1+overclock)}, crusher {@code (1+overclock)}); {@code generationBonus}
 * is the deterministic extra-output multiplier (the furnace passes 0 here since its generation is
 * already folded into speed, the crusher passes its {@code curGeneration}).</p>
 */
public final class RecipeInfoIcon {

    private RecipeInfoIcon() {
    }

    /** Build the info icon for {@code machineId}. Falls back to a BOOK "no recipe" icon when empty. */
    public static org.bukkit.inventory.ItemStack build(AbstractMachineBlockEntity machine, String machineId,
            double speedMultiplier, double generationBonus) {
        // Show the CURRENT matching recipe (regardless of whether it's processing). No match -> the
        // slot is invisible (tooltip-less filler), unified across machines (no "no recipe" book).
        AbstractProcessingRecipe r = machine.getCurrentRecipe();
        if (r == null)
            return MenuText.emptyFiller();

        double speed = speedMultiplier > 0 ? speedMultiplier : 1.0;
        double genMul = 1.0 + Math.max(0.0, generationBonus);

        List<Component> lore = new ArrayList<>();
        appendRecipe(machine, lore, r, speed, genMul);

        // Icon = the current recipe's main output.
        org.bukkit.inventory.ItemStack base = iconForRecipe(r);
        if (base == null || base.getType() == Material.AIR)
            base = new org.bukkit.inventory.ItemStack(Material.BOOK);
        org.bukkit.inventory.meta.ItemMeta meta = base.getItemMeta();
        if (meta != null) {
            meta.displayName(MenuText.noI(MenuText.tr("polyfill.ui.info", NamedTextColor.AQUA)));
            List<Component> ls = new ArrayList<>();
            for (Component c : lore)
                ls.add(MenuText.noI(c));
            meta.lore(ls);
            base.setItemMeta(meta);
        }
        return base;
    }

    // ---------------- icon ----------------

    /** The icon item for a recipe's main output (item / gas / fluid). Null if none found. */
    private static org.bukkit.inventory.ItemStack iconForRecipe(AbstractProcessingRecipe r) {
        for (RecipeOutput o : r.getOutputs()) {
            if (o instanceof ItemOutput io) {
                ItemStack nms = (ItemStack) io.getOutput();
                return org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(nms);
            }
        }
        for (RecipeOutput o : r.getOutputs()) {
            if (o instanceof GasOutput go) {
                GasStack gs = (GasStack) go.getOutput();
                String name = gs.getType() == null ? "" : gs.getType().name().toLowerCase(Locale.ROOT);
                Key id = name.contains("heavy") ? Key.of("cml", "heavy_steam_icon") : Key.of("cml", "steam_icon");
                return ceItemOrNull(id);
            }
            if (o instanceof FluidOutput fo) {
                dev.arubik.craftengine.fluid.FluidStack fs = (dev.arubik.craftengine.fluid.FluidStack) fo.getOutput();
                return new org.bukkit.inventory.ItemStack(bucketFor(fs.getType()));
            }
        }
        return null;
    }

    private static org.bukkit.inventory.ItemStack ceItemOrNull(Key id) {
        try {
            var def = net.momirealms.craftengine.bukkit.api.CraftEngineItems.byId(id);
            if (def != null)
                return def.buildBukkitItem();
        } catch (Throwable ignored) {
        }
        return new org.bukkit.inventory.ItemStack(Material.BOOK);
    }

    /** Map a fluid type to its vanilla "in hand" Material; special-cases types with no plain bucket. */
    private static Material bucketFor(dev.arubik.craftengine.fluid.FluidType type) {
        if (type == null)
            return Material.BUCKET;
        String n = type.name().toLowerCase(Locale.ROOT);
        switch (n) {
            case "snow":         return Material.POWDER_SNOW_BUCKET;
            case "powder_snow":  return Material.POWDER_SNOW_BUCKET;
            case "slime":        return Material.SLIME_BALL;
            case "xp":
            case "experience":   return Material.EXPERIENCE_BOTTLE;
            case "honey":        return Material.HONEY_BOTTLE;
            default:
                Material m = Material.matchMaterial(n + "_bucket");
                return m != null ? m : Material.BUCKET;
        }
    }

    // ---------------- lore ----------------

    private static void appendRecipe(AbstractMachineBlockEntity machine, List<Component> lore,
            AbstractProcessingRecipe r, double speed, double genMul) {
        double effTicks = r.getProcessTime() / speed;
        double craftsPerMin = effTicks > 0 ? 1200.0 / effTicks : 0;

        // Outputs.
        for (RecipeOutput o : r.getOutputs()) {
            if (o instanceof ItemOutput io) {
                ItemStack os = (ItemStack) io.getOutput();
                int c = os.getCount();
                float chance = io.getChance() <= 0f ? 1f : io.getChance();
                Component line = Component.text("→ ", NamedTextColor.WHITE)
                        .append(PaperAdventure.asAdventure(os.getHoverName()))
                        .append(Component.text(" x" + c, NamedTextColor.WHITE));
                if (chance < 1.0f)
                    line = line.append(Component.text(String.format(" (%.0f%%)", chance * 100), NamedTextColor.GOLD));
                lore.add(MenuText.noI(line));
                double perMin = craftsPerMin * c * chance * genMul;
                lore.add(MenuText.noI(Component.text(String.format("   %.1f/min", perMin), NamedTextColor.YELLOW)));
            } else if (o instanceof GasOutput go) {
                GasStack gs = (GasStack) go.getOutput();
                int mb = gs.getAmount();
                Component tn = gs.getType() == null ? Component.text("?")
                        : Component.translatable(gs.getType().translationKey());
                lore.add(MenuText.noI(Component.text("→ ", NamedTextColor.WHITE).append(tn.colorIfAbsent(NamedTextColor.WHITE))
                        .append(Component.text(" " + mb + "mB", NamedTextColor.WHITE))));
                double mbPerTick = effTicks > 0 ? mb / effTicks : 0;
                lore.add(MenuText.noI(Component.text(String.format("   %.1f mB/tick", mbPerTick), NamedTextColor.YELLOW)));
            } else if (o instanceof FluidOutput fo) {
                dev.arubik.craftengine.fluid.FluidStack fs = (dev.arubik.craftengine.fluid.FluidStack) fo.getOutput();
                int mb = fs.getAmount();
                Component tn = fs.getType() == null ? Component.text("?")
                        : Component.translatable(fs.getType().translationKey());
                lore.add(MenuText.noI(Component.text("→ ", NamedTextColor.WHITE).append(tn.colorIfAbsent(NamedTextColor.WHITE))
                        .append(Component.text(" " + mb + "mB", NamedTextColor.WHITE))));
                double mbPerTick = effTicks > 0 ? mb / effTicks : 0;
                lore.add(MenuText.noI(Component.text(String.format("   %.1f mB/tick", mbPerTick), NamedTextColor.YELLOW)));
            }
        }

        // Cook time (effective seconds).
        lore.add(MenuText.kv("polyfill.ui.time", NamedTextColor.GRAY,
                String.format("%.1fs", effTicks / 20.0), NamedTextColor.WHITE));

        // Ingredients (+ input per minute).
        boolean anyInput = !r.getInputs().isEmpty();
        if (anyInput)
            lore.add(MenuText.tr("polyfill.ui.ingredients", NamedTextColor.GOLD));
        for (RecipeInput in : r.getInputs()) {
            int amt = in.getAmount();
            Component name = inputName(in);
            double perMin = craftsPerMin * amt;
            String unit;
            if (in instanceof FluidInput || in instanceof GasInput)
                unit = "mB";
            else
                unit = "";
            Component line = Component.text("• ", NamedTextColor.GRAY).append(name)
                    .append(Component.text(" " + amt + unit, NamedTextColor.WHITE))
                    .append(Component.text(String.format("  (%.1f%s/min)", perMin, unit.isEmpty() ? "" : unit),
                            NamedTextColor.DARK_GRAY));
            lore.add(MenuText.noI(line));
        }

        // Mechanical requirements — EFFECTIVE values (after the machine's overclock/efficiency), so
        // they match the live rpm/su the power gauge reports.
        if (r.getMinRpm() > 0)
            lore.add(MenuText.kv("polyfill.ui.rpm", NamedTextColor.GRAY, String.valueOf(machine.effectiveRpm(r)),
                    NamedTextColor.WHITE));
        if (r.getSuCost() > 0)
            lore.add(MenuText.kv("polyfill.ui.su", NamedTextColor.GRAY, String.valueOf(machine.effectiveSu(r)),
                    NamedTextColor.WHITE));
    }

    /** Localized name for a recipe input (item hover name, CE item name, tag path, or fluid/gas type). */
    private static Component inputName(RecipeInput in) {
        if (in instanceof ItemInput ii) {
            return PaperAdventure.asAdventure(ii.getStack().getHoverName());
        }
        if (in instanceof CraftEngineItemInput ci) {
            try {
                Key id = Key.of(ci.getItemId());
                var def = net.momirealms.craftengine.bukkit.api.CraftEngineItems.byId(id);
                if (def != null) {
                    org.bukkit.inventory.ItemStack b = def.buildBukkitItem();
                    ItemStack nms = org.bukkit.craftbukkit.inventory.CraftItemStack.asNMSCopy(b);
                    return PaperAdventure.asAdventure(nms.getHoverName());
                }
            } catch (Throwable ignored) {
            }
            return Component.text(ci.getItemId(), NamedTextColor.WHITE);
        }
        if (in instanceof TagInput ti) {
            return Component.text("#" + ti.getTag().location(), NamedTextColor.WHITE);
        }
        if (in instanceof FluidInput fi) {
            var ty = fi.getFluid().getType();
            return ty == null ? Component.text("?", NamedTextColor.WHITE)
                    : Component.translatable(ty.translationKey()).color(NamedTextColor.WHITE);
        }
        if (in instanceof GasInput gi) {
            var ty = gi.getGas().getType();
            return ty == null ? Component.text("?", NamedTextColor.WHITE)
                    : Component.translatable(ty.translationKey()).color(NamedTextColor.WHITE);
        }
        return Component.text("?", NamedTextColor.WHITE);
    }
}
