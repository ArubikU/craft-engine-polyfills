/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  org.bukkit.World
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 */
package dev.arubik.craftengine.machine.render.formula;

import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import dev.arubik.craftengine.crafting.WorkbenchDefinition;
import dev.arubik.craftengine.machine.render.formula.BlockClass;
import dev.arubik.craftengine.machine.render.formula.ContraptionClass;
import dev.arubik.craftengine.machine.render.formula.FluidTanksClass;
import dev.arubik.craftengine.machine.render.formula.GasTanksClass;
import dev.arubik.craftengine.machine.render.formula.InventoryClass;
import dev.arubik.craftengine.machine.render.formula.LocationClass;
import dev.arubik.craftengine.machine.render.formula.MachineClass;
import dev.arubik.craftengine.machine.render.formula.MultiBlockClass;
import dev.arubik.craftengine.machine.render.formula.PlayerClass;
import dev.arubik.craftengine.machine.render.formula.PolyValue;
import dev.arubik.craftengine.machine.render.formula.UpgradesClass;
import dev.arubik.craftengine.machine.render.formula.WorkbenchClass;
import dev.arubik.craftengine.machine.render.formula.WorldClass;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class PolyContext {
    private final Map<String, PolyValue> vars;
    private final Map<String, PolyClass> classes;

    private PolyContext(Map<String, PolyValue> vars, Map<String, PolyClass> classes) {
        this.vars = vars;
        this.classes = classes;
    }

    public PolyValue get(String name) {
        return this.vars.getOrDefault(name, PolyValue.NULL);
    }

    public PolyClass getClass(String name) {
        return this.classes.get(name);
    }

    Map<String, PolyValue> vars() {
        return this.vars;
    }

    Map<String, PolyClass> classes() {
        return this.classes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static PolyContext forMachine(double rpm, double overclock, double efficiency, double progress, double maxProgress, double tier, boolean processing, boolean powered, boolean overclocked, boolean hasFuel, Inventory inventory, Map<String, Integer> upgradesByType) {
        Builder b = PolyContext.builder().num("rpm", rpm).num("overclock", overclock).num("efficiency", efficiency).num("progress", progress).num("max_progress", maxProgress).num("tier", tier).bool("processing", processing).bool("powered", powered).bool("overclocked", overclocked).bool("has_fuel", hasFuel);
        if (inventory != null) {
            b.cls("Inventory", new InventoryClass(inventory));
        }
        if (upgradesByType != null && !upgradesByType.isEmpty()) {
            b.upgradeMap(upgradesByType);
        }
        return b.build();
    }

    public static PolyContext forMachine(double rpm, double overclock, double efficiency, double progress, double maxProgress, double tier, boolean processing, boolean powered, boolean overclocked, boolean hasFuel, Inventory inventory) {
        return PolyContext.forMachine(rpm, overclock, efficiency, progress, maxProgress, tier, processing, powered, overclocked, hasFuel, inventory, null);
    }

    public static PolyContext forMachine(double rpm, double overclock, double efficiency, double progress, double maxProgress, double tier, boolean processing, boolean powered, boolean overclocked, boolean hasFuel, Inventory inventory, Map<String, Integer> upgradesByType, Map<String, double[]> fluidTanks, Map<String, double[]> gasTanks) {
        Builder b = PolyContext.builder().num("rpm", rpm).num("overclock", overclock).num("efficiency", efficiency).num("progress", progress).num("max_progress", maxProgress).num("tier", tier).bool("processing", processing).bool("powered", powered).bool("overclocked", overclocked).bool("has_fuel", hasFuel).fluidTanks(fluidTanks).gasTanks(gasTanks);
        if (inventory != null) {
            b.cls("Inventory", new InventoryClass(inventory));
        }
        if (upgradesByType != null && !upgradesByType.isEmpty()) {
            b.upgradeMap(upgradesByType);
        }
        return b.build();
    }

    public static PolyContext forMotor(double rpm, double torque, double overclock, double efficiency, boolean overstressed, boolean hasFuel) {
        return PolyContext.builder().num("rpm", rpm).num("torque", torque).num("su", torque).num("overclock", overclock).num("efficiency", efficiency).bool("overstressed", overstressed).bool("has_fuel", hasFuel).build();
    }

    public static PolyContext forMultiblock(double rpm, double overclock, double efficiency, double progress, double maxProgress, double tier, boolean processing, boolean powered, boolean overclocked, boolean hasFuel, Inventory inventory, Map<String, Integer> upgradesByType, int partCount, boolean formed) {
        Builder b = PolyContext.builder().num("rpm", rpm).num("overclock", overclock).num("efficiency", efficiency).num("progress", progress).num("max_progress", maxProgress).num("tier", tier).bool("processing", processing).bool("powered", powered).bool("overclocked", overclocked).bool("has_fuel", hasFuel).num("part_count", partCount).bool("formed", formed);
        if (inventory != null) {
            b.cls("Inventory", new InventoryClass(inventory));
        }
        if (upgradesByType != null && !upgradesByType.isEmpty()) {
            b.upgradeMap(upgradesByType);
        }
        return b.build();
    }

    public static final class Builder {
        private final LinkedHashMap<String, PolyValue> vars = new LinkedHashMap();
        private final LinkedHashMap<String, PolyClass> classes = new LinkedHashMap();
        private Map<String, Integer> upgrades = null;
        private Inventory upgradeInventory = null;
        private Map<String, double[]> fluidTanks = Map.of();
        private Map<String, double[]> gasTanks = Map.of();

        public Builder copyFrom(PolyContext other) {
            this.vars.putAll(other.vars());
            this.classes.putAll(other.classes());
            return this;
        }

        public Builder num(String name, double v) {
            this.vars.put(name, PolyValue.of(v));
            return this;
        }

        public Builder bool(String name, boolean v) {
            this.vars.put(name, PolyValue.of(v));
            return this;
        }

        public Builder str(String name, String v) {
            this.vars.put(name, PolyValue.of(v));
            return this;
        }

        public Builder item(String name, ItemStack s) {
            this.vars.put(name, PolyValue.ofItem(s));
            return this;
        }

        public Builder val(String name, PolyValue v) {
            this.vars.put(name, v);
            return this;
        }

        public Builder cls(String name, PolyClass c) {
            this.classes.put(name, c);
            return this;
        }

        public Builder valAll(Map<String, PolyValue> values) {
            this.vars.putAll(values);
            return this;
        }

        public Builder workbench(WorkbenchDefinition def, Inventory inv) {
            if (def != null) {
                this.classes.put("Workbench", new WorkbenchClass(def, inv));
            }
            return this;
        }

        private static int[] facingOffset(String facing) {
            int[] nArray;
            if (facing == null) {
                return new int[]{0, 0, -1};
            }
            switch (facing.toLowerCase()) {
                case "south": {
                    int[] nArray2 = new int[3];
                    nArray2[0] = 0;
                    nArray2[1] = 0;
                    nArray = nArray2;
                    nArray2[2] = 1;
                    break;
                }
                case "east": {
                    int[] nArray3 = new int[3];
                    nArray3[0] = 1;
                    nArray3[1] = 0;
                    nArray = nArray3;
                    nArray3[2] = 0;
                    break;
                }
                case "west": {
                    int[] nArray4 = new int[3];
                    nArray4[0] = -1;
                    nArray4[1] = 0;
                    nArray = nArray4;
                    nArray4[2] = 0;
                    break;
                }
                case "up": {
                    int[] nArray5 = new int[3];
                    nArray5[0] = 0;
                    nArray5[1] = 1;
                    nArray = nArray5;
                    nArray5[2] = 0;
                    break;
                }
                case "down": {
                    int[] nArray6 = new int[3];
                    nArray6[0] = 0;
                    nArray6[1] = -1;
                    nArray = nArray6;
                    nArray6[2] = 0;
                    break;
                }
                default: {
                    int[] nArray7 = new int[3];
                    nArray7[0] = 0;
                    nArray7[1] = 0;
                    nArray = nArray7;
                    nArray7[2] = -1;
                }
            }
            return nArray;
        }

        private void putFacingVars(String facing, float facingYaw) {
            this.vars.putIfAbsent("facing", PolyValue.of(facing != null ? facing : "north"));
            this.vars.putIfAbsent("facing_angle", PolyValue.of(facingYaw));
            int[] off = Builder.facingOffset(facing);
            this.vars.putIfAbsent("facing_dx", PolyValue.of(off[0]));
            this.vars.putIfAbsent("facing_dy", PolyValue.of(off[1]));
            this.vars.putIfAbsent("facing_dz", PolyValue.of(off[2]));
        }

        public Builder machinePos(double x, double y, double z, String facing, float facingYaw, World world) {
            this.classes.put("Machine", new MachineClass(x, y, z, facing, facingYaw, world));
            this.putFacingVars(facing, facingYaw);
            return this;
        }

        public Builder machinePos(double x, double y, double z, String facing, float facingYaw, World world, int[] ... footprint) {
            this.classes.put("Machine", new MachineClass(x, y, z, facing, facingYaw, world).withFootprint(footprint));
            this.putFacingVars(facing, facingYaw);
            return this;
        }

        public Builder machinePos(double x, double y, double z, String facing, float facingYaw, World world, PersistentBlockEntity be) {
            this.classes.put("Machine", new MachineClass(x, y, z, facing, facingYaw, world).withBlockEntity(be));
            this.putFacingVars(facing, facingYaw);
            return this;
        }

        public Builder multiBlock(int relX, int relY, int relZ, boolean isMaster, int partCount, boolean formed) {
            this.classes.put("MultiBlock", new MultiBlockClass(relX, relY, relZ, isMaster, partCount, formed));
            this.vars.putIfAbsent("part_count", PolyValue.of(partCount));
            this.vars.putIfAbsent("formed", PolyValue.of(formed));
            return this;
        }

        public Builder machinePosForPlayer(double x, double y, double z, String facing, float facingYaw, Player player) {
            this.classes.put("Machine", new MachineClass(x, y, z, facing, facingYaw, player));
            this.putFacingVars(facing, facingYaw);
            return this;
        }

        public Builder machinePosForPlayer(double x, double y, double z, String facing, float facingYaw, Player player, int[] ... footprint) {
            this.classes.put("Machine", new MachineClass(x, y, z, facing, facingYaw, player).withFootprint(footprint));
            this.putFacingVars(facing, facingYaw);
            return this;
        }

        public Builder player(ServerPlayer p) {
            if (p != null) {
                this.classes.put("Player", new PlayerClass(p));
            }
            return this;
        }

        public Builder world(ServerLevel w) {
            if (w != null) {
                this.classes.put("World", WorldClass.forLevel(w));
            }
            return this;
        }

        public Builder location(ServerLevel level, double x, double y, double z) {
            if (level != null) {
                this.classes.put("Location", LocationClass.forLevel(level, x, y, z));
            }
            return this;
        }

        public Builder upgradeMap(Map<String, Integer> upgradesByType) {
            this.upgrades = upgradesByType;
            return this;
        }

        public Builder upgradeInventory(Inventory inv) {
            this.upgradeInventory = inv;
            return this;
        }

        public Builder fluidTanks(Map<String, double[]> tanks) {
            this.fluidTanks = tanks != null ? tanks : Map.of();
            return this;
        }

        public Builder gasTanks(Map<String, double[]> tanks) {
            this.gasTanks = tanks != null ? tanks : Map.of();
            return this;
        }

        public Builder redstone(int power) {
            this.vars.put("redstone", PolyValue.of(power));
            this.vars.put("powered", PolyValue.of(power > 0));
            this.vars.put("redstone_power", PolyValue.of(power));
            return this;
        }

        public Builder block(ServerLevel level, BlockPos pos) {
            if (level != null && pos != null) {
                this.classes.put("Block", BlockClass.of(level, pos));
            }
            return this;
        }

        public Builder contraption(Object level) {
            this.classes.put("Contraption", ContraptionClass.of(level));
            return this;
        }

        public PolyContext build() {
            double[] first;
            if (!this.fluidTanks.isEmpty()) {
                this.classes.put("FluidTanks", new FluidTanksClass(this.fluidTanks));
                first = this.fluidTanks.values().iterator().next();
                this.vars.putIfAbsent("fluid_level", PolyValue.of(first[0]));
                this.vars.putIfAbsent("fluid_capacity", PolyValue.of(first[1]));
                this.vars.putIfAbsent("fluid_fraction", PolyValue.of(first[1] > 0.0 ? first[0] / first[1] : 0.0));
            }
            if (!this.gasTanks.isEmpty()) {
                this.classes.put("GasTanks", new GasTanksClass(this.gasTanks));
                first = this.gasTanks.values().iterator().next();
                this.vars.putIfAbsent("gas_level", PolyValue.of(first[0]));
                this.vars.putIfAbsent("gas_capacity", PolyValue.of(first[1]));
                this.vars.putIfAbsent("gas_fraction", PolyValue.of(first[1] > 0.0 ? first[0] / first[1] : 0.0));
            }
            if (this.upgrades != null && !this.upgrades.isEmpty()) {
                int total = this.upgrades.values().stream().mapToInt(i -> i != null ? i : 0).sum();
                this.vars.put("upgrade_count", PolyValue.of(total));
                this.upgrades.forEach((type, count) -> this.vars.put("upgrade_" + type, PolyValue.of(count != null ? (double)count.intValue() : 0.0)));
                this.vars.putIfAbsent("overclock_count", PolyValue.of(this.upgrades.getOrDefault("overclock", 0).intValue()));
                this.vars.putIfAbsent("upgrade_efficiency", PolyValue.of(this.upgrades.getOrDefault("efficiency", 0).intValue()));
                this.classes.put("Upgrades", new UpgradesClass(this.upgrades, this.upgradeInventory));
            } else {
                this.vars.putIfAbsent("upgrade_count", PolyValue.of(0.0));
                this.vars.putIfAbsent("overclock_count", PolyValue.of(0.0));
                this.vars.putIfAbsent("upgrade_efficiency", PolyValue.of(0.0));
                if (this.upgradeInventory != null) {
                    this.classes.put("Upgrades", new UpgradesClass(Map.of(), this.upgradeInventory));
                }
            }
            return new PolyContext(Map.copyOf(this.vars), Map.copyOf(this.classes));
        }
    }
}

