package dev.arubik.craftengine.machine.examples;

import org.bukkit.Material;

import dev.arubik.craftengine.fluid.FluidTank;
import dev.arubik.craftengine.fluid.FluidType;
import dev.arubik.craftengine.gas.GasStack;
import dev.arubik.craftengine.gas.GasTank;
import dev.arubik.craftengine.gas.GasType;
import dev.arubik.craftengine.multiblock.MultiBlockMachineBlockEntity;
import dev.arubik.craftengine.multiblock.MultiBlockSchema;
import dev.arubik.craftengine.machine.menu.MenuText;
import dev.arubik.craftengine.machine.menu.layout.MachineLayout;
import dev.arubik.craftengine.machine.menu.layout.MenuSlotType;
import dev.arubik.craftengine.machine.recipe.AbstractProcessingRecipe;
import dev.arubik.craftengine.machine.recipe.CraftEngineItemInput;
import dev.arubik.craftengine.machine.recipe.FluidInput;
import dev.arubik.craftengine.machine.recipe.ItemInput;
import dev.arubik.craftengine.machine.recipe.ItemOutput;
import dev.arubik.craftengine.machine.recipe.RecipeInput;
import dev.arubik.craftengine.machine.recipe.RecipeOutput;
import dev.arubik.craftengine.machine.recipe.TagInput;
import dev.arubik.craftengine.machine.recipe.loader.RecipeManager;
import dev.arubik.craftengine.multiblock.IOConfiguration;
import dev.arubik.craftengine.multiblock.IOConfiguration.IOType;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.core.block.entity.BlockEntity;

/**
 * [TASK-03] Refinery — washes aluminium scraping with quartz using water + steam.
 * Bottom controller of the refinery multiblock (a mixer block sits on top). Steam-powered:
 * it only advances while steam is present in its gas tank. Recipes are MACHINE recipes
 * (machineId "refinery") with item + fluid inputs (see resources/recipes/refinery_aluminum.json).
 */
public class RefineryBlockEntity extends MultiBlockMachineBlockEntity {

    public static final int SLOT_A = 21;      // scraping
    public static final int SLOT_B = 22;      // quartz (agent)
    public static final int SLOT_OUTPUT = 24; // purified dust
    public static final int WATER_CAP = 8000;
    public static final int STEAM_CAP = 8000;
    public static final int STEAM_PER_CRAFT = 100; // steam consumed per completed craft

    private static final int MENU_SIZE = 54; // 6 rows
    private static final int SLOT_PROGRESS = 31;
    private static final int SLOT_INFO = 4;
    private static final org.bukkit.inventory.ItemStack FILLER = buildFiller();

    private final MachineLayout layout;

    public RefineryBlockEntity(BlockEntity blockEntity, MultiBlockSchema schema,
            java.util.List<dev.arubik.craftengine.machine.menu.bar.MachineBar> bars) {
        super(MENU_SIZE, blockEntity, schema);
        // Accept ANY liquid / gas — the recipe decides what's actually usable.
        addFluidTank(new FluidTank("fluid", WATER_CAP));
        addGasTank(new GasTank("gas", STEAM_CAP));

        // Directional (LOCAL directions, rotated to the world via the `facing` property):
        //   UP (through the mixer) + DOWN  -> fluid (water) + gas (steam) INPUT
        //   BACK (south) + LEFT (west)     -> ITEM input  (back = scraping slot A, left = quartz slot B)
        //   FRONT (north) + RIGHT (east)   -> ITEM output (purified dust)
        // Directional IO (LOCAL dirs, rotated to world via `facing`). The mixer PART on top adds
        // the UP fluid/gas intake (see RefineryBehavior.refineryIO), routed to these same tanks.
        IOConfiguration.Simple cfg = new IOConfiguration.Simple();
        cfg.addInput(IOType.FLUID, Direction.DOWN); // water in from the bottom
        cfg.addInput(IOType.GAS, Direction.DOWN);   // steam in from the bottom
        cfg.withInputSlot(IOType.FLUID, 0, Direction.DOWN);
        cfg.withInputSlot(IOType.GAS, 0, Direction.DOWN);

        cfg.addInput(IOType.ITEM, Direction.NORTH); // back -> scraping (A)
        cfg.addInput(IOType.ITEM, Direction.EAST);  // left -> quartz   (B)
        cfg.withInputSlot(IOType.ITEM, SLOT_A, Direction.NORTH);
        cfg.withInputSlot(IOType.ITEM, SLOT_B, Direction.EAST);
        cfg.setSlots(IOType.ITEM, IOConfiguration.IORole.INPUT, SLOT_A, SLOT_B);

        cfg.addOutput(IOType.ITEM, Direction.SOUTH); // front -> output
        cfg.addOutput(IOType.ITEM, Direction.WEST);  // right -> output
        cfg.setSlots(IOType.ITEM, IOConfiguration.IORole.OUTPUT, SLOT_OUTPUT);
        setIOConfiguration(cfg);

        this.layout = buildLayout(bars);
    }

    /** Name of the liquid currently stored (or "—"). */
    private String fluidLabel() {
        try {
            dev.arubik.craftengine.fluid.FluidStack f = fluidTanks.get(0).getFluid(getNMSLevel(), getMachinePos());
            return (f == null || f.isEmpty()) ? "—" : f.getType().name().toLowerCase();
        } catch (Throwable t) {
            return "—";
        }
    }

    /** Name of the gas currently stored (or "—"). */
    private String gasLabel() {
        try {
            dev.arubik.craftengine.gas.GasStack g = gasTanks.get(0).getGas(getNMSLevel(), getMachinePos());
            return (g == null || g.isEmpty()) ? "—" : g.getType().name().toLowerCase();
        } catch (Throwable t) {
            return "—";
        }
    }

    /** A fluid/gas type name as a translatable component ({@code prefix+type}), or "—" when empty. */
    private static net.kyori.adventure.text.Component typeComp(String prefix, String label, NamedTextColor color) {
        if (label == null || label.isEmpty() || "—".equals(label))
            return net.kyori.adventure.text.Component.text("—", color);
        return net.kyori.adventure.text.Component.translatable(prefix + label, color);
    }

    /** Translatable display name of an item recipe input (vanilla, CraftEngine, or a tag). */
    private static net.kyori.adventure.text.Component inputName(RecipeInput in) {
        try {
            if (in instanceof dev.arubik.craftengine.machine.recipe.ItemInput ii)
                return net.kyori.adventure.text.Component
                        .translatable(ii.getStack().getItem().getDescriptionId());
            if (in instanceof CraftEngineItemInput ci)
                return net.kyori.adventure.text.Component
                        .translatable("item." + ci.getItemId().replace(':', '.'));
            if (in instanceof TagInput ti)
                return net.kyori.adventure.text.Component.text("#" + ti.getTag().location());
        } catch (Throwable ignored) {
        }
        return net.kyori.adventure.text.Component.text("?");
    }

    /** Translatable display name of a built ItemStack (CraftEngine custom id or vanilla). */
    private static net.kyori.adventure.text.Component itemStackName(net.minecraft.world.item.ItemStack nms) {
        try {
            org.bukkit.inventory.ItemStack b = org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(nms);
            net.momirealms.craftengine.core.util.Key ce =
                    net.momirealms.craftengine.bukkit.api.CraftEngineItems.getCustomItemId(b);
            if (ce != null)
                return net.kyori.adventure.text.Component
                        .translatable("item." + ce.toString().replace(':', '.'), NamedTextColor.WHITE);
            return net.kyori.adventure.text.Component
                    .translatable(nms.getItem().getDescriptionId(), NamedTextColor.WHITE);
        } catch (Throwable t) {
            return net.kyori.adventure.text.Component.text(nms.getHoverName().getString(), NamedTextColor.WHITE);
        }
    }

    /** The pre-baked CraftEngine recipe-card item (image lore rendered by CE, like the blueprints). */
    private static org.bukkit.inventory.ItemStack recipeCard() {
        try {
            var def = net.momirealms.craftengine.bukkit.api.CraftEngineItems.byId(
                    net.momirealms.craftengine.core.util.Key.of("cml", "refinery_recipe"));
            if (def != null)
                return def.buildBukkitItem();
        } catch (Throwable ignored) {
        }
        return new org.bukkit.inventory.ItemStack(Material.BOOK);
    }

    /** Empty the water tank (deplete button). */
    public void depleteFluid() {
        try {
            fluidTanks.get(0).extract(getNMSLevel(), getMachinePos(), WATER_CAP, null);
        } catch (Throwable ignored) {
        }
    }

    /** Empty the steam tank (deplete button). */
    public void depleteGas() {
        try {
            gasTanks.get(0).extract(getNMSLevel(), getMachinePos(), STEAM_CAP, null);
        } catch (Throwable ignored) {
        }
    }

    private MachineLayout buildLayout(java.util.List<dev.arubik.craftengine.machine.menu.bar.MachineBar> bars) {
        MachineLayout l = new MachineLayout(org.bukkit.event.inventory.InventoryType.CHEST, MENU_SIZE, "Refinery");
        l.setTitleComponent(MenuText.noI(MenuText.tr("polyfill.ui.refinery_title", NamedTextColor.AQUA)));
        l.addSlot(SLOT_A, MenuSlotType.INPUT);
        l.addSlot(SLOT_B, MenuSlotType.INPUT);
        l.addSlot(SLOT_OUTPUT, MenuSlotType.OUTPUT);

        // Center progress item.
        l.setDynamicProvider(SLOT_PROGRESS, (m, t) -> {
            RefineryBlockEntity s = (RefineryBlockEntity) m;
            int max = s.getMaxProgress();
            if (!s.isProcessing() || max <= 0)
                return MenuText.icon(Material.GRAY_DYE, MenuText.tr("polyfill.ui.idle", NamedTextColor.GRAY));
            int pct = (int) Math.round(Math.min(1.0, (double) s.getProgress() / max) * 100);
            return MenuText.icon(Material.LIME_STAINED_GLASS_PANE, MenuText.tr("polyfill.ui.processing", NamedTextColor.GREEN),
                    MenuText.noI(net.kyori.adventure.text.Component.text(pct + "%", NamedTextColor.WHITE)));
        });

        // Dynamic recipe info (ingredient list + stats). Supports multiple recipes.
        l.setDynamicProvider(SLOT_INFO, (m, t) -> ((RefineryBlockEntity) m).infoIcon());

        // Config-driven side bars (water / steam / …).
        dev.arubik.craftengine.machine.menu.bar.MachineBars.install(l, bars);

        // Deplete buttons under each bar (water column -> 45, steam column -> 53).
        l.addButton(45,
                (m, t) -> MenuText.icon(Material.BUCKET, MenuText.tr("polyfill.ui.deplete", NamedTextColor.RED),
                        MenuText.tr("polyfill.ui.fluid", NamedTextColor.GRAY)),
                (m, p) -> ((RefineryBlockEntity) m).depleteFluid());
        l.addButton(53,
                (m, t) -> MenuText.icon(Material.BUCKET, MenuText.tr("polyfill.ui.deplete", NamedTextColor.RED),
                        MenuText.tr("polyfill.ui.gas", NamedTextColor.GRAY)),
                (m, p) -> ((RefineryBlockEntity) m).depleteGas());

        for (int i = 0; i < MENU_SIZE; i++)
            if (l.getSlotType(i) == MenuSlotType.BACKGROUND)
                l.setDynamicProvider(i, (m, t) -> FILLER);
        return l;
    }

    private static org.bukkit.inventory.ItemStack buildFiller() {
        org.bukkit.inventory.ItemStack s = new org.bukkit.inventory.ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        org.bukkit.inventory.meta.ItemMeta meta = s.getItemMeta();
        meta.displayName(net.kyori.adventure.text.Component.text(" "));
        s.setItemMeta(meta);
        return s;
    }

    private org.bukkit.inventory.ItemStack infoIcon() {
        java.util.List<net.kyori.adventure.text.Component> lore = new java.util.ArrayList<>();
        int water = waterAmount(), steam = steamAmount();

        // Current resource levels — generic (any liquid/gas), showing the stored TYPE (i18n) + amount.
        NamedTextColor fc = water > 0 ? NamedTextColor.AQUA : NamedTextColor.RED;
        lore.add(MenuText.noI(MenuText.tr("polyfill.ui.fluid", NamedTextColor.GRAY)
                .append(net.kyori.adventure.text.Component.text(": ", NamedTextColor.GRAY))
                .append(typeComp("polyfill.liquid.", fluidLabel(), fc))
                .append(net.kyori.adventure.text.Component.text(" " + water + "/" + WATER_CAP + " mB", fc))));
        NamedTextColor sc = steam >= STEAM_PER_CRAFT ? NamedTextColor.WHITE : NamedTextColor.RED;
        lore.add(MenuText.noI(MenuText.tr("polyfill.ui.gas", NamedTextColor.GRAY)
                .append(net.kyori.adventure.text.Component.text(": ", NamedTextColor.GRAY))
                .append(typeComp("polyfill.gas.", gasLabel(), sc))
                .append(net.kyori.adventure.text.Component.text(" " + steam + "/" + STEAM_CAP + " mB", sc))));

        // Always list the possible recipes + their REAL requirements (read from the recipe, not
        // assumed) so the player sees what fluid/gas/items each needs even when missing.
        java.util.List<AbstractProcessingRecipe> recipes = RecipeManager.getRecipes(getMachineId());
        if (recipes == null || recipes.isEmpty()) {
            lore.add(MenuText.noI(MenuText.tr("polyfill.ui.no_recipe", NamedTextColor.GRAY)));
        } else {
            lore.add(net.kyori.adventure.text.Component.empty());
            lore.add(MenuText.noI(MenuText.tr("polyfill.ui.recipes", NamedTextColor.GOLD)));
            for (AbstractProcessingRecipe r : recipes) {
                int outCount = 0;
                net.kyori.adventure.text.Component outComp = net.kyori.adventure.text.Component.text("?");
                for (RecipeOutput o : r.getOutputs()) {
                    if (o instanceof ItemOutput io) {
                        net.minecraft.world.item.ItemStack os = (net.minecraft.world.item.ItemStack) io.getOutput();
                        outCount += os.getCount();
                        outComp = itemStackName(os);
                    }
                }
                lore.add(MenuText.noI(net.kyori.adventure.text.Component.text("→ ", NamedTextColor.WHITE)
                        .append(outComp).append(net.kyori.adventure.text.Component.text(
                                " x" + outCount, NamedTextColor.WHITE))));

                // Ingredients: each item by its (translatable) name + amount; fluid/gas by type+amount.
                int fluidNeed = 0, gasNeed = 0;
                String fluidType = "", gasType = "";
                for (RecipeInput in : r.getInputs()) {
                    if (in instanceof FluidInput fi) {
                        fluidNeed += fi.getAmount();
                        try { fluidType = fi.getFluid().getType().name().toLowerCase(); } catch (Throwable ignored) {}
                    } else if (in instanceof dev.arubik.craftengine.machine.recipe.GasInput gi) {
                        gasNeed += gi.getAmount();
                        try { gasType = gi.getGas().getType().name().toLowerCase(); } catch (Throwable ignored) {}
                    } else {
                        lore.add(MenuText.noI(net.kyori.adventure.text.Component.text("   • ", NamedTextColor.GRAY)
                                .append(inputName(in)).append(net.kyori.adventure.text.Component.text(
                                        " x" + in.getAmount(), NamedTextColor.GRAY))));
                    }
                }
                if (gasNeed == 0) {
                    gasNeed = STEAM_PER_CRAFT; // steam consumed at the machine level
                    gasType = "steam";
                }
                if (fluidNeed > 0) {
                    NamedTextColor c = water >= fluidNeed ? NamedTextColor.GRAY : NamedTextColor.RED;
                    lore.add(MenuText.noI(net.kyori.adventure.text.Component.text("   • ", c)
                            .append(net.kyori.adventure.text.Component.translatable("polyfill.liquid." + fluidType, c))
                            .append(net.kyori.adventure.text.Component.text(
                                    " " + fluidNeed + " mB " + (water >= fluidNeed ? "✔" : "✘"), c))));
                }
                NamedTextColor gc = steam >= gasNeed ? NamedTextColor.GRAY : NamedTextColor.RED;
                lore.add(MenuText.noI(net.kyori.adventure.text.Component.text("   • ", gc)
                        .append(net.kyori.adventure.text.Component.translatable("polyfill.gas." + gasType, gc))
                        .append(net.kyori.adventure.text.Component.text(
                                " " + gasNeed + " mB " + (steam >= gasNeed ? "✔" : "✘"), gc))));

                int procTicks = r.getProcessTime();
                lore.add(MenuText.noI(net.kyori.adventure.text.Component.text(
                        String.format("   %.1fs · %.1f/min", procTicks / 20.0,
                                procTicks > 0 ? (1200.0 / procTicks) * Math.max(1, outCount) : 0),
                        NamedTextColor.YELLOW)));
            }
        }
        return MenuText.icon(Material.BOOK, MenuText.tr("polyfill.ui.info", NamedTextColor.AQUA),
                lore.toArray(new net.kyori.adventure.text.Component[0]));
    }

    @Override
    public String barSubtype(String id) {
        switch (id) {
            case "water":
                return fluidLabel();
            case "steam":
                return gasLabel();
            default:
                return super.barSubtype(id);
        }
    }

    @Override
    public double[] barStat(String id) {
        switch (id) {
            case "water":
                return new double[] { waterAmount(), WATER_CAP };
            case "steam":
                return new double[] { steamAmount(), STEAM_CAP };
            default:
                return super.barStat(id);
        }
    }

    private int waterAmount() {
        try {
            return fluidTanks.get(0).getFluid(getNMSLevel(), getMachinePos()).getAmount();
        } catch (Throwable t) {
            return 0;
        }
    }

    private int steamAmount() {
        try {
            return gasTanks.get(0).getGas(getNMSLevel(), getMachinePos()).getAmount();
        } catch (Throwable t) {
            return 0;
        }
    }

    @Override
    public MachineLayout getLayout() {
        return layout;
    }

    @Override
    protected boolean requiresFuel() {
        return false; // powered by steam, gated below
    }

    @Override
    protected String getMachineId() {
        return "refinery";
    }

    @Override
    public int[] getOutputSlots() {
        return new int[] { SLOT_OUTPUT };
    }

    @Override
    protected AbstractProcessingRecipe getMatchingRecipe(Level level) {
        for (AbstractProcessingRecipe r : RecipeManager.getRecipes(getMachineId())) {
            if (matchesAll(level, r))
                return r;
        }
        return null;
    }

    /** All item inputs map to distinct input slots, and the water input fits the tank. */
    private boolean matchesAll(Level level, AbstractProcessingRecipe recipe) {
        boolean usedA = false, usedB = false;
        for (RecipeInput in : recipe.getInputs()) {
            if (isItem(in)) {
                ItemStack a = getItem(SLOT_A), b = getItem(SLOT_B);
                if (!usedA && in.matches(a)) {
                    usedA = true;
                } else if (!usedB && in.matches(b)) {
                    usedB = true;
                } else {
                    return false;
                }
            } else if (in instanceof FluidInput fi) {
                if (waterAmount() < fi.getAmount())
                    return false;
            } else if (in instanceof dev.arubik.craftengine.machine.recipe.GasInput gi) {
                if (steamAmount() < gi.getAmount())
                    return false;
            }
        }
        return true;
    }

    private static boolean isItem(RecipeInput in) {
        return in instanceof ItemInput || in instanceof CraftEngineItemInput || in instanceof TagInput;
    }

    @Override
    protected boolean canProcess(Level level, AbstractProcessingRecipe recipe) {
        if (!super.canProcess(level, recipe))
            return false;
        // Steam-powered. The multiblock only ticks while formed (the mixer-on-top is part of
        // the structure), so no ad-hoc neighbour check is needed here.
        return steamAmount() > 0;
    }

    @Override
    protected boolean canFitOutput(Level level, RecipeOutput output) {
        if (output instanceof ItemOutput io) {
            ItemStack out = (ItemStack) io.getOutput();
            ItemStack cur = getItem(SLOT_OUTPUT);
            if (cur == null || cur.isEmpty())
                return true;
            if (ItemStack.isSameItem(cur, out))
                return cur.getCount() + out.getCount() <= cur.getMaxStackSize();
            return false;
        }
        return true;
    }

    @Override
    protected void consumeInputs(Level level, AbstractProcessingRecipe recipe) {
        boolean usedA = false, usedB = false, burnedGas = false;
        for (RecipeInput in : recipe.getInputs()) {
            if (isItem(in)) {
                if (!usedA && in.matches(getItem(SLOT_A))) {
                    removeItem(SLOT_A, in.getAmount());
                    usedA = true;
                } else if (!usedB && in.matches(getItem(SLOT_B))) {
                    removeItem(SLOT_B, in.getAmount());
                    usedB = true;
                }
            } else if (in instanceof FluidInput fi) {
                fluidTanks.get(0).extract(level, getMachinePos(), fi.getAmount(), null);
            } else if (in instanceof dev.arubik.craftengine.machine.recipe.GasInput gi) {
                gasTanks.get(0).extract(level, getMachinePos(), gi.getAmount(), null);
                burnedGas = true;
            }
        }
        // If the recipe didn't define a gas cost, burn the default steam for the craft.
        if (!burnedGas)
            gasTanks.get(0).extract(level, getMachinePos(), STEAM_PER_CRAFT, null);
    }
}
