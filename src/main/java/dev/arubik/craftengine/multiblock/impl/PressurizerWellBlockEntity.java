package dev.arubik.craftengine.multiblock.impl;

import org.bukkit.Material;

import dev.arubik.craftengine.gas.GasCarrier;
import dev.arubik.craftengine.gas.GasKeys;
import dev.arubik.craftengine.gas.GasStack;
import dev.arubik.craftengine.gas.GasTank;
import dev.arubik.craftengine.gas.GasTransferHelper;
import dev.arubik.craftengine.gas.GasType;
import dev.arubik.craftengine.machine.menu.MenuText;
import dev.arubik.craftengine.machine.menu.layout.MachineLayout;
import dev.arubik.craftengine.machine.menu.layout.MenuSlotType;
import dev.arubik.craftengine.machine.recipe.AbstractProcessingRecipe;
import dev.arubik.craftengine.machine.recipe.RecipeOutput;
import dev.arubik.craftengine.multiblock.MultiBlockMachineBlockEntity;
import dev.arubik.craftengine.multiblock.MultiBlockSchema;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.core.block.entity.BlockEntity;

/**
 * Pressurizer Well core (the central-bottom block of a 3×3×6 {@code cml:pressurizer_well} tower). While
 * the structure is formed AND fed steam, it raises the pump limit of the cal vein anchored directly
 * below the core from 1 to {@link GasKeys#WELL_PUMP_LIMIT}. It publishes that state by writing
 * {@link GasKeys#WELL_ACTIVE} (1/0) to its own CustomBlockData, which gas pumps read.
 *
 * <p>Steam is consumed CONTINUOUSLY: it pulls steam from any gas carrier touching the tower into its
 * buffer and burns {@link #STEAM_PER_CYCLE} per cycle. With no steam the well stays formed but goes
 * inactive (the extra pumps on the vein pause; one pump still runs).</p>
 */
public class PressurizerWellBlockEntity extends MultiBlockMachineBlockEntity {

    public static final int STEAM_CAP = 16000;
    public static final int STEAM_PER_CYCLE = 200; // steam burned per active cycle
    public static final int CYCLE_TICKS = 20;       // one cycle per second
    private static final int PULL_INTERVAL = 5;     // throttle the steam intake scan
    private static final int PULL_PER_FACE = 400;   // max mB pulled per carrier face per scan

    private static final int MENU_SIZE = 27;
    private static final int SLOT_INFO = 13;
    private static final org.bukkit.inventory.ItemStack FILLER = MenuText.emptyFiller();

    private final MachineLayout layout;
    private int cycleCd = 0;
    private int pullCd = 0;
    private boolean active = false;

    public PressurizerWellBlockEntity(BlockEntity blockEntity, MultiBlockSchema schema) {
        super(MENU_SIZE, blockEntity, schema);
        addGasTank(new GasTank("steam", STEAM_CAP, GasType.STEAM));
        this.layout = buildLayout();
    }

    private int steamAmount() {
        try {
            GasStack g = gasTanks.get(0).getGas(getNMSLevel(), getMachinePos());
            return g == null ? 0 : g.getAmount();
        } catch (Throwable t) {
            return 0;
        }
    }

    @Override
    protected void processTick(Level level) {
        if (level.isClientSide())
            return;
        BlockPos core = getMachinePos();

        // Steam intake (throttled): pull from any gas carrier touching the tower into the buffer.
        if (pullCd-- <= 0) {
            pullCd = PULL_INTERVAL;
            pullSteam(level, core);
        }

        // Consume steam once per cycle; active only while there is steam to burn.
        boolean nowActive;
        if (cycleCd-- <= 0) {
            cycleCd = CYCLE_TICKS;
            if (steamAmount() >= STEAM_PER_CYCLE) {
                gasTanks.get(0).extract(level, core, STEAM_PER_CYCLE, null);
                nowActive = true;
            } else {
                nowActive = false;
            }
            // Publish the active flag for gas pumps to read off the anchored cal vein.
            var data = dev.arubik.craftengine.util.CustomBlockData.from(level, core);
            data.set(GasKeys.WELL_ACTIVE, nowActive ? 1 : 0);
            this.active = nowActive;
            this.isProcessing = nowActive;
        }
    }

    /** Scan every tower block's outward neighbours for a steam carrier and pull steam into the buffer. */
    private void pullSteam(Level level, BlockPos core) {
        int cap = STEAM_CAP;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                for (int dy = 0; dy <= 5; dy++) {
                    if (steamAmount() >= cap)
                        return;
                    BlockPos p = core.offset(dx, dy, dz);
                    for (Direction d : Direction.values()) {
                        // Skip neighbours that are part of the tower (no carrier there).
                        int nx = dx + d.getStepX(), ny = dy + d.getStepY(), nz = dz + d.getStepZ();
                        if (nx >= -1 && nx <= 1 && nz >= -1 && nz <= 1 && ny >= 0 && ny <= 5)
                            continue;
                        BlockPos src = p.relative(d);
                        GasCarrier carrier = GasTransferHelper.getCarrier(level, src).orElse(null);
                        if (carrier == null)
                            continue;
                        GasStack avail = carrier.getStoredGas(level, src);
                        if (avail == null || avail.isEmpty() || avail.getType() != GasType.STEAM)
                            continue;
                        int free = cap - steamAmount();
                        if (free <= 0)
                            return;
                        int want = Math.min(PULL_PER_FACE, free);
                        int[] got = { 0 };
                        carrier.extractGas(level, src, want, gs -> {
                            if (gs != null && gs.getType() == GasType.STEAM)
                                got[0] = gs.getAmount();
                        }, d.getOpposite());
                        if (got[0] > 0)
                            gasTanks.get(0).insert(level, core, new GasStack(GasType.STEAM, got[0]));
                    }
                }
            }
        }
    }

    // ---------------- menu (status readout) ----------------

    private MachineLayout buildLayout() {
        MachineLayout l = new MachineLayout(org.bukkit.event.inventory.InventoryType.CHEST, MENU_SIZE,
                "Pressurizer Well");
        l.setTitleComponent(MenuText.noI(MenuText.tr("polyfill.ui.pressurizer_title", NamedTextColor.AQUA)));
        l.setDynamicProvider(SLOT_INFO, (m, t) -> ((PressurizerWellBlockEntity) m).infoIcon());
        for (int i = 0; i < MENU_SIZE; i++)
            if (l.getSlotType(i) == MenuSlotType.BACKGROUND)
                l.setDynamicProvider(i, (m, t) -> FILLER);
        return l;
    }

    private org.bukkit.inventory.ItemStack infoIcon() {
        var GRAY = NamedTextColor.GRAY;
        var WHITE = NamedTextColor.WHITE;
        var AQUA = NamedTextColor.AQUA;
        Material mat = active ? Material.LIME_STAINED_GLASS : Material.RED_STAINED_GLASS;
        org.bukkit.inventory.ItemStack stack = new org.bukkit.inventory.ItemStack(mat);
        org.bukkit.inventory.meta.ItemMeta meta = stack.getItemMeta();
        meta.displayName(MenuText.noI(MenuText.tr("polyfill.ui.pressurizer_title", AQUA)));
        meta.lore(java.util.List.of(
                MenuText.noI(MenuText.kv("polyfill.ui.gas", GRAY,
                        steamAmount() + " / " + STEAM_CAP + " mB", WHITE)),
                MenuText.noI(MenuText.tr(active ? "polyfill.ui.pressurizer_active"
                        : "polyfill.ui.pressurizer_idle", active ? NamedTextColor.GREEN : NamedTextColor.RED))));
        stack.setItemMeta(meta);
        return stack;
    }

    @Override
    public MachineLayout getLayout() {
        return layout;
    }

    @Override
    public String barSubtype(String id) {
        if ("steam".equals(id) || "gas".equals(id))
            return "steam";
        return super.barSubtype(id);
    }

    @Override
    public double[] barStat(String id) {
        if ("steam".equals(id) || "gas".equals(id))
            return new double[] { steamAmount(), STEAM_CAP };
        return super.barStat(id);
    }

    @Override
    protected boolean requiresFuel() {
        return false;
    }

    @Override
    protected String getMachineId() {
        return "pressurizer_well";
    }

    @Override
    protected AbstractProcessingRecipe getMatchingRecipe(Level level) {
        return null;
    }

    @Override
    protected boolean canFitOutput(Level level, RecipeOutput output) {
        return false;
    }

    @Override
    protected void consumeInputs(Level level, AbstractProcessingRecipe recipe) {
    }
}
