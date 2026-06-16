package dev.arubik.craftengine.machine.examples;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.persistence.PersistentDataType;

import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.machine.attribute.MachineAttributes;
import dev.arubik.craftengine.machine.attribute.MachineAttributes.Mod;
import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import dev.arubik.craftengine.machine.menu.MachineMenu;
import dev.arubik.craftengine.machine.menu.MenuText;
import dev.arubik.craftengine.machine.menu.layout.MachineLayout;
import dev.arubik.craftengine.machine.menu.layout.MenuSlotType;
import dev.arubik.craftengine.machine.recipe.AbstractProcessingRecipe;
import dev.arubik.craftengine.machine.recipe.CraftEngineItemInput;
import dev.arubik.craftengine.machine.recipe.ItemInput;
import dev.arubik.craftengine.machine.recipe.ItemOutput;
import dev.arubik.craftengine.machine.recipe.RecipeInput;
import dev.arubik.craftengine.machine.recipe.RecipeOutput;
import dev.arubik.craftengine.machine.recipe.TagInput;
import dev.arubik.craftengine.machine.recipe.loader.RecipeManager;
import dev.arubik.craftengine.multiblock.IOConfiguration;
import dev.arubik.craftengine.multiblock.IOConfiguration.IOType;
import dev.arubik.craftengine.rotation.RpmConsumer;
import dev.arubik.craftengine.rotation.RpmProvider;
import dev.arubik.craftengine.util.TypedKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.util.Key;

/**
 * [TASK-02] Crusher — RPM-powered processing machine with an attribute-based upgrade
 * system (same definition form as the advanced vapor motor's {@code upgrades:} config).
 *
 * <p>Upgrade attributes are reinterpreted for a consumer machine:</p>
 * <ul>
 *   <li>{@code polyfill:generation} &mdash; raises BASE speed. Does NOT change the rpm/SU it needs.</li>
 *   <li>{@code polyfill:overclock_limit} &mdash; headroom for the player-tunable OVERCLOCK. Base 0
 *       (speed cannot be pushed past base). When &gt; 0, an Overclock button/menu unlocks; pushing
 *       overclock raises speed AND the rpm required AND the SU drawn.</li>
 *   <li>{@code polyfill:fuel_efficiency} &mdash; reduces the rpm the recipe demands (NOT the SU).</li>
 *   <li>{@code polyfill:extra_slots} &mdash; unlocks more upgrade slots.</li>
 * </ul>
 *
 * <p>Wide engineer-style main page: upgrade storage lives in container slots 0..8 (hidden on the
 * main page, shown on the Upgrades page), a 2x2 input grid, a 2x2 output grid, a center progress
 * item, plus status / info / upgrades / overclock buttons.</p>
 */
public class CrusherBlockEntity extends AbstractMachineBlockEntity implements RpmConsumer {

    private static final int MENU_SIZE = 54; // 6 rows; slots 0..8 reserved for upgrades
    public static final int UPGRADE_SLOTS = 9; // container indices 0..8
    private static final int BASE_UNLOCKED = 3;

    public static final int[] INPUT_SLOTS = { 19, 20, 28, 29 };
    public static final int[] OUTPUT_SLOTS = { 23, 24, 32, 33 };
    private static final int SLOT_PROGRESS = 31;
    private static final int SLOT_STATUS = 13;
    private static final int SLOT_INFO = 11;
    private static final int SLOT_UPGRADES_BTN = 15;
    private static final int SLOT_OVERCLOCK_BTN = 17;

    private static final Material[] PROGRESS_RAMP = {
            Material.WHITE_STAINED_GLASS_PANE,
            Material.LIGHT_BLUE_STAINED_GLASS_PANE,
            Material.CYAN_STAINED_GLASS_PANE,
            Material.LIME_STAINED_GLASS_PANE,
            Material.GREEN_STAINED_GLASS_PANE,
    };
    private static final org.bukkit.inventory.ItemStack FILLER = buildFiller();

    private final Map<Key, List<Mod>> upgradeDefs;

    private float inputRpm = 0f;
    private int activeSu = 0;
    private RpmProvider activeMotor;

    // Player-tunable overclock fraction in [0, overclockLimit]. 0 = base speed only.
    private float overclock = 0f;

    // Derived each processTick from the installed upgrades + overclock setting.
    private double curGeneration = 0;
    private double curOverclockLimit = 0;
    private double curFuelEff = 0;
    private int curUnlocked = BASE_UNLOCKED;

    private int page = 0; // 0 main, 1 upgrades, 2 overclock
    private MachineMenu active;

    private static final TypedKey<Float> KEY_OC = TypedKey.of("craftengine", "crusher_overclock",
            PersistentDataType.FLOAT);

    public CrusherBlockEntity(BlockEntity blockEntity) {
        this(blockEntity, new java.util.HashMap<>());
    }

    public CrusherBlockEntity(BlockEntity blockEntity, Map<Key, List<Mod>> upgradeDefs) {
        super(blockEntity, MENU_SIZE);
        this.upgradeDefs = upgradeDefs == null ? new java.util.HashMap<>() : upgradeDefs;

        IOConfiguration.Simple cfg = new IOConfiguration.Simple();
        cfg.addInput(IOType.ITEM, Direction.UP);
        cfg.addInput(IOType.ITEM, Direction.NORTH);
        cfg.addOutput(IOType.ITEM, Direction.DOWN);
        cfg.addOutput(IOType.ITEM, Direction.SOUTH);
        cfg.setSlots(IOType.ITEM, IOConfiguration.IORole.INPUT, INPUT_SLOTS);
        cfg.setSlots(IOType.ITEM, IOConfiguration.IORole.OUTPUT, OUTPUT_SLOTS);
        setIOConfiguration(cfg);
    }

    private static org.bukkit.inventory.ItemStack buildFiller() {
        org.bukkit.inventory.ItemStack s = new org.bukkit.inventory.ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        org.bukkit.inventory.meta.ItemMeta meta = s.getItemMeta();
        meta.displayName(Component.text(" "));
        s.setItemMeta(meta);
        return s;
    }

    // ---------------- upgrades (attribute system, like the advanced motor) ----------------

    @Override
    public int[] getUpgradeSlots() {
        int[] s = new int[UPGRADE_SLOTS];
        for (int i = 0; i < UPGRADE_SLOTS; i++)
            s[i] = i;
        return s;
    }

    private Key itemId(int slot) {
        ItemStack nms = getItem(slot);
        if (nms == null || nms.isEmpty())
            return null;
        org.bukkit.inventory.ItemStack b = org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(nms);
        Key custom = CraftEngineItems.getCustomItemId(b);
        if (custom != null)
            return custom;
        org.bukkit.NamespacedKey nk = b.getType().getKey();
        return Key.of(nk.getNamespace(), nk.getKey());
    }

    private List<Mod> modsOf(int slot) {
        Key id = itemId(slot);
        return id == null ? null : upgradeDefs.get(id);
    }

    private static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    /** Re-reads the upgrade slots and folds the attribute modifiers; feeds the speed multiplier
     *  back into {@link AbstractMachineBlockEntity}'s progress loop via {@code upgradeModifiers}. */
    @Override
    protected void recomputeUpgrades() {
        // Pass 1: unlocked slot count from EXTRA_SLOTS across ALL installed upgrades.
        List<Mod> all = new ArrayList<>();
        for (int i = 0; i < UPGRADE_SLOTS; i++) {
            List<Mod> m = modsOf(i);
            if (m != null)
                all.addAll(m);
        }
        int extra = (int) Math.round(MachineAttributes.compute(all)
                .getOrDefault(MachineAttributes.EXTRA_SLOTS, 0.0));
        this.curUnlocked = Math.max(BASE_UNLOCKED, Math.min(UPGRADE_SLOTS, BASE_UNLOCKED + extra));

        // Pass 2: effect attributes only from upgrades in unlocked slots.
        List<Mod> active = new ArrayList<>();
        for (int i = 0; i < curUnlocked; i++) {
            List<Mod> m = modsOf(i);
            if (m != null)
                active.addAll(m);
        }
        var attrs = MachineAttributes.compute(active);
        this.curGeneration = clamp(attrs.getOrDefault(MachineAttributes.GENERATION, 0.0), -0.95, 32.0);
        this.curOverclockLimit = clamp(attrs.getOrDefault(MachineAttributes.OVERCLOCK_LIMIT, 0.0), 0.0, 32.0);
        this.curFuelEff = clamp(attrs.getOrDefault(MachineAttributes.FUEL_EFFICIENCY, 0.0), -32.0, 0.95);

        // Overclock setting can never exceed the current limit.
        this.overclock = (float) clamp(this.overclock, 0.0, this.curOverclockLimit);

        // Speed = base (+generation) × (1 + overclock). generation does NOT cost rpm/SU; overclock does.
        double speed = (1.0 + curGeneration) * (1.0 + overclock);
        this.upgradeModifiers = new dev.arubik.craftengine.machine.upgrade.UpgradeModifiers(speed, 1.0, 0.0);
    }

    /** rpm the matched recipe actually demands after fuel-efficiency + overclock. */
    private float effMinRpm(AbstractProcessingRecipe recipe) {
        return (float) (recipe.getMinRpm() * (1.0 + overclock) * (1.0 - curFuelEff));
    }

    /** SU drawn after overclock (generation/efficiency do not change SU). */
    private int effSu(AbstractProcessingRecipe recipe) {
        return (int) Math.round(recipe.getSuCost() * (1.0 + overclock));
    }

    // ---------------- recipe loop ----------------

    private int matchingInputSlot(AbstractProcessingRecipe recipe) {
        for (int s : INPUT_SLOTS) {
            ItemStack in = getItem(s);
            if (in == null || in.isEmpty())
                continue;
            if (matchesInputs(recipe, in))
                return s;
        }
        return -1;
    }

    private boolean matchesInputs(AbstractProcessingRecipe recipe, ItemStack input) {
        boolean any = false;
        for (RecipeInput in : recipe.getInputs()) {
            if (in instanceof ItemInput || in instanceof CraftEngineItemInput || in instanceof TagInput) {
                if (!in.matches(input))
                    return false;
                any = true;
            }
        }
        return any;
    }

    private AbstractProcessingRecipe currentRecipe() {
        for (AbstractProcessingRecipe r : RecipeManager.getRecipes(getMachineId())) {
            if (matchingInputSlot(r) >= 0)
                return r;
        }
        return null;
    }

    @Override
    protected boolean requiresFuel() {
        return false; // mechanical power
    }

    @Override
    protected String getMachineId() {
        return "crusher";
    }

    @Override
    public int[] getOutputSlots() {
        return OUTPUT_SLOTS;
    }

    @Override
    protected AbstractProcessingRecipe getMatchingRecipe(Level level) {
        return currentRecipe();
    }

    @Override
    protected boolean canProcess(Level level, AbstractProcessingRecipe recipe) {
        if (!super.canProcess(level, recipe))
            return false;
        this.activeSu = effSu(recipe);
        return recipe.getMinRpm() <= 0 || inputRpm >= effMinRpm(recipe);
    }

    @Override
    protected boolean canFitOutput(Level level, RecipeOutput output) {
        if (output instanceof ItemOutput io) {
            ItemStack out = (ItemStack) io.getOutput();
            for (int s : OUTPUT_SLOTS) {
                ItemStack cur = getItem(s);
                if (cur == null || cur.isEmpty())
                    return true;
                if (ItemStack.isSameItem(cur, out)
                        && cur.getCount() + out.getCount() <= cur.getMaxStackSize())
                    return true;
            }
            return false;
        }
        return true;
    }

    @Override
    protected void consumeInputs(Level level, AbstractProcessingRecipe recipe) {
        int slot = matchingInputSlot(recipe);
        if (slot < 0)
            return;
        for (RecipeInput in : recipe.getInputs()) {
            if (in instanceof ItemInput || in instanceof CraftEngineItemInput || in instanceof TagInput) {
                removeItem(slot, in.getAmount());
            }
        }
    }

    @Override
    public void tick(Level level, BlockPos pos, net.momirealms.craftengine.core.block.ImmutableBlockState state) {
        if (active != null)
            active.tick(); // keep the multi-page UI (progress/info/rpm) live
        if (!level.isClientSide()) {
            // PULL rpm from the strongest adjacent motor.
            this.activeMotor = null;
            float best = 0f;
            for (Direction d : Direction.values()) {
                BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, getMachinePos().relative(d));
                if (be != null && be.controller instanceof RpmProvider p && p.isRpmSource()
                        && p.getRpm() > best) {
                    // Only accept power from a real motor SOURCE (not a conveyor router relay).
                    net.momirealms.craftengine.core.world.BlockPos cePos =
                            new net.momirealms.craftengine.core.world.BlockPos(getMachinePos().getX(),
                                    getMachinePos().getY(), getMachinePos().getZ());
                    if (!p.rpmReaches(cePos))
                        continue;
                    best = p.getRpm();
                    this.activeMotor = p;
                }
            }
            this.inputRpm = best;
        }
        super.tick(level, pos, state); // recomputeUpgrades + recipe loop (reads inputRpm)
        if (!level.isClientSide() && isProcessing() && activeSu > 0 && activeMotor != null) {
            activeMotor.reportStressLoad(activeSu);
        }
    }

    // ---------------- multi-page menu ----------------

    @Override
    public MachineLayout getLayout() {
        return switch (page) {
            case 1 -> buildUpgradeLayout();
            case 2 -> buildOverclockLayout();
            default -> buildMainLayout();
        };
    }

    @Override
    public MachineMenu getMenu() {
        if (active == null) {
            active = new MachineMenu(this, getLayout());
            active.syncFromMachine();
        }
        return active;
    }

    @Override
    public void openMenu(net.minecraft.world.entity.player.Player player) {
        openPage((org.bukkit.entity.Player) player.getBukkitEntity(), 0);
    }

    public void openPage(org.bukkit.entity.Player player, int newPage) {
        this.page = newPage;
        this.active = new MachineMenu(this, getLayout());
        this.active.syncFromMachine();
        this.active.open(player);
    }

    private MachineLayout buildMainLayout() {
        MachineLayout l = new MachineLayout(org.bukkit.event.inventory.InventoryType.CHEST, MENU_SIZE, "Crusher");
        l.setTitleComponent(MenuText.noI(MenuText.tr("polyfill.ui.crusher_title", NamedTextColor.GOLD)));

        for (int s : INPUT_SLOTS)
            l.addSlot(s, MenuSlotType.INPUT);
        for (int s : OUTPUT_SLOTS)
            l.addSlot(s, MenuSlotType.OUTPUT);

        // Status (rpm).
        l.setDynamicProvider(SLOT_STATUS, (m, t) -> {
            CrusherBlockEntity s = (CrusherBlockEntity) m;
            AbstractProcessingRecipe r = s.currentRecipe();
            int need = r == null ? 0 : Math.round(s.effMinRpm(r));
            boolean ok = need == 0 || s.inputRpm >= need;
            return MenuText.icon(ok ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE,
                    s.isProcessing() ? MenuText.tr("polyfill.ui.processing", NamedTextColor.GREEN)
                            : MenuText.tr("polyfill.ui.idle", NamedTextColor.RED),
                    MenuText.kv("polyfill.ui.rpm", NamedTextColor.GRAY,
                            String.format("%.0f", s.inputRpm) + (need > 0 ? " / " + need : ""), NamedTextColor.WHITE),
                    MenuText.kv("polyfill.ui.su", NamedTextColor.GRAY, String.valueOf(s.activeSu), NamedTextColor.WHITE));
        });

        // Progress item.
        l.setDynamicProvider(SLOT_PROGRESS, (m, t) -> {
            CrusherBlockEntity s = (CrusherBlockEntity) m;
            int max = s.getMaxProgress();
            if (!s.isProcessing() || max <= 0)
                return MenuText.icon(Material.GRAY_DYE, MenuText.tr("polyfill.ui.idle", NamedTextColor.GRAY));
            double frac = Math.max(0, Math.min(1.0, (double) s.getProgress() / max));
            int stage = Math.min(PROGRESS_RAMP.length - 1, (int) (frac * PROGRESS_RAMP.length));
            int pct = (int) Math.round(frac * 100);
            return MenuText.icon(PROGRESS_RAMP[stage], MenuText.tr("polyfill.ui.processing", NamedTextColor.GREEN),
                    MenuText.noI(Component.text(pct + "%", NamedTextColor.WHITE)));
        });

        // Info: current recipe + items/min.
        l.setDynamicProvider(SLOT_INFO, (m, t) -> ((CrusherBlockEntity) m).infoIcon());

        // Upgrades button.
        l.addButton(SLOT_UPGRADES_BTN,
                (m, t) -> MenuText.icon(Material.ANVIL, MenuText.tr("polyfill.ui.upgrades", NamedTextColor.GOLD),
                        MenuText.tr("polyfill.ui.upgrades_desc", NamedTextColor.GRAY)),
                (m, p) -> ((CrusherBlockEntity) m).openPage(p, 1));

        // Overclock button — only useful once an overclock_limit upgrade is installed.
        l.addButton(SLOT_OVERCLOCK_BTN, (m, t) -> {
            CrusherBlockEntity s = (CrusherBlockEntity) m;
            if (s.curOverclockLimit <= 0)
                return MenuText.icon(Material.BARRIER, MenuText.tr("polyfill.ui.oc_locked", NamedTextColor.DARK_GRAY),
                        MenuText.tr("polyfill.ui.oc_locked_desc", NamedTextColor.GRAY));
            return MenuText.icon(Material.REDSTONE,
                    MenuText.tr("polyfill.ui.overclock", NamedTextColor.RED),
                    MenuText.kv("polyfill.ui.overclock", NamedTextColor.GRAY,
                            String.format("+%.0f%%", s.overclock * 100), NamedTextColor.WHITE));
        }, (m, p) -> {
            CrusherBlockEntity s = (CrusherBlockEntity) m;
            if (s.curOverclockLimit > 0)
                s.openPage(p, 2);
        });

        fillRest(l);
        return l;
    }

    private org.bukkit.inventory.ItemStack infoIcon() {
        AbstractProcessingRecipe r = currentRecipe();
        if (r == null)
            return MenuText.icon(Material.BOOK, MenuText.tr("polyfill.ui.info", NamedTextColor.AQUA),
                    MenuText.tr("polyfill.ui.no_recipe", NamedTextColor.GRAY));
        // Output count + effective process time -> items/min.
        int outCount = 0;
        String outName = "?";
        for (RecipeOutput o : r.getOutputs()) {
            if (o instanceof ItemOutput io) {
                ItemStack os = (ItemStack) io.getOutput();
                outCount += os.getCount();
                outName = os.getHoverName().getString();
            }
        }
        double speed = (1.0 + curGeneration) * (1.0 + overclock);
        double effTicks = speed > 0 ? r.getProcessTime() / speed : r.getProcessTime();
        double perMin = effTicks > 0 ? (1200.0 / effTicks) * Math.max(1, outCount) : 0;
        List<Component> lore = new ArrayList<>();
        lore.add(MenuText.kv("polyfill.ui.output", NamedTextColor.GRAY, outName + " x" + outCount, NamedTextColor.WHITE));
        lore.add(MenuText.kv("polyfill.ui.per_min", NamedTextColor.GRAY,
                String.format("%.1f", perMin), NamedTextColor.YELLOW));
        lore.add(MenuText.kv("polyfill.ui.rpm", NamedTextColor.GRAY,
                String.format("%.0f", effMinRpm(r)), NamedTextColor.WHITE));
        lore.add(MenuText.kv("polyfill.ui.su", NamedTextColor.GRAY, String.valueOf(effSu(r)), NamedTextColor.WHITE));
        return MenuText.icon(Material.BOOK, MenuText.tr("polyfill.ui.info", NamedTextColor.AQUA),
                lore.toArray(new Component[0]));
    }

    private MachineLayout buildUpgradeLayout() {
        int unlocked = curUnlocked;
        MachineLayout l = new MachineLayout(org.bukkit.event.inventory.InventoryType.CHEST, 18, "Upgrades");
        l.setTitleComponent(MenuText.noI(MenuText.tr("polyfill.ui.upgrades", NamedTextColor.DARK_AQUA)
                .append(Component.text(" (" + unlocked + "/" + UPGRADE_SLOTS + ")", NamedTextColor.GRAY))));
        for (int i = 0; i < UPGRADE_SLOTS; i++) {
            if (i < unlocked) {
                l.addSlot(i, MenuSlotType.INPUT);
            } else {
                l.setDynamicProvider(i, (m, t) -> MenuText.icon(Material.BARRIER,
                        MenuText.tr("polyfill.ui.locked", NamedTextColor.RED),
                        MenuText.tr("polyfill.ui.locked_desc", NamedTextColor.GRAY)));
            }
        }
        l.addButton(17, (m, t) -> MenuText.icon(Material.ARROW, MenuText.tr("polyfill.ui.back", NamedTextColor.YELLOW)),
                (m, p) -> ((CrusherBlockEntity) m).openPage(p, 0));
        fillRest(l);
        return l;
    }

    private MachineLayout buildOverclockLayout() {
        MachineLayout l = new MachineLayout(org.bukkit.event.inventory.InventoryType.CHEST, 27, "Overclock");
        l.setTitleComponent(MenuText.noI(MenuText.tr("polyfill.ui.overclock", NamedTextColor.RED)));
        l.addClickButton(11, (m, t) -> MenuText.icon(Material.RED_STAINED_GLASS_PANE,
                MenuText.tr("polyfill.ui.oc_minus", NamedTextColor.RED),
                MenuText.tr("polyfill.ui.tune_hint", NamedTextColor.GRAY)),
                (m, p, c) -> ((CrusherBlockEntity) m).bumpOverclock(false, c));
        l.setDynamicProvider(13, (m, t) -> {
            CrusherBlockEntity s = (CrusherBlockEntity) m;
            return MenuText.icon(Material.REDSTONE,
                    MenuText.kv("polyfill.ui.overclock", NamedTextColor.RED,
                            String.format("+%.0f%%", s.overclock * 100), NamedTextColor.WHITE),
                    MenuText.kv("polyfill.ui.max", NamedTextColor.GRAY,
                            String.format("+%.0f%%", s.curOverclockLimit * 100), NamedTextColor.WHITE),
                    MenuText.kv("polyfill.ui.rpm", NamedTextColor.GRAY,
                            String.format("x%.2f", 1.0 + s.overclock), NamedTextColor.WHITE));
        });
        l.addClickButton(15, (m, t) -> MenuText.icon(Material.GREEN_STAINED_GLASS_PANE,
                MenuText.tr("polyfill.ui.oc_plus", NamedTextColor.GREEN),
                MenuText.tr("polyfill.ui.tune_hint", NamedTextColor.GRAY)),
                (m, p, c) -> ((CrusherBlockEntity) m).bumpOverclock(true, c));
        l.addButton(22, (m, t) -> MenuText.icon(Material.ARROW, MenuText.tr("polyfill.ui.back", NamedTextColor.YELLOW)),
                (m, p) -> ((CrusherBlockEntity) m).openPage(p, 0));
        fillRest(l);
        return l;
    }

    private static int ocStep(ClickType c) {
        if (c == ClickType.DROP || c == ClickType.CONTROL_DROP)
            return 100; // jump to max/min (handled by clamp)
        if (c == ClickType.RIGHT || c == ClickType.SHIFT_RIGHT)
            return 50;
        return 10; // 10% per click
    }

    public void bumpOverclock(boolean up, ClickType c) {
        float delta = ocStep(c) / 100f;
        this.overclock += up ? delta : -delta;
        this.overclock = (float) clamp(this.overclock, 0.0, this.curOverclockLimit);
        setChanged();
    }

    private void fillRest(MachineLayout l) {
        for (int i = 0; i < l.getSize(); i++) {
            if (l.getSlotType(i) == MenuSlotType.BACKGROUND)
                l.setDynamicProvider(i, (m, t) -> FILLER);
        }
    }

    // ---------------- persistence ----------------
    @Override
    public void saveCustomData(net.momirealms.craftengine.libraries.nbt.CompoundTag tag) {
        set(KEY_OC, overclock);
        super.saveCustomData(tag);
    }

    @Override
    public void loadCustomData(net.momirealms.craftengine.libraries.nbt.CompoundTag tag) {
        super.loadCustomData(tag);
        this.overclock = getOrDefault(KEY_OC, 0f);
    }

    // ---------------- RpmConsumer ----------------
    @Override
    public void setInputRpm(float rpm) {
        this.inputRpm = rpm;
    }

    @Override
    public float getInputRpm() {
        return inputRpm;
    }
}
