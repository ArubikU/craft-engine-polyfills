package dev.arubik.craftengine.conveyor.belt;

import java.util.LinkedHashSet;
import java.util.Set;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.data.DataFiles;
import dev.arubik.craftengine.data.JsonView;
import dev.arubik.craftengine.data.Registries;
import net.momirealms.craftengine.core.util.Key;

/**
 * Loads {@code belt_types/*.json} into {@link BeltType#REGISTRY} — mirrors {@code PipeTypeLoader}.
 * Every field optional:
 *
 * <pre>{@code
 * {
 *   "id": "polyfills:conveyor",         // optional, defaults to the file name
 *   "block": "cml:conveyor",            // the custom block this belt places
 *   "height": 0.28,                     // vertical carry offset (0..1, block-local)
 *   "connects_to": ["cml:some_chest"],  // extra non-ConveyorReceiver blocks to hand off into
 *   "energy_type": "none",              // none | kinetic | electric
 *   "energy_per_tick": 0,               // CraftEnergy drawn per active tick (electric only)
 *   "energy_capacity": 0,               // CraftEnergy buffer size (electric only)
 *   "speed": 16,                        // EITHER a number (ticks to cross one segment at base_rpm)
 *                                        // OR a string .pf expression evaluated every tick with
 *                                        // rpm/base_rpm/base_travel_ticks bound, returning the
 *                                        // per-tick progress increment directly, e.g.
 *                                        // "speed": "min(rpm / base_rpm, 4.0) / base_travel_ticks"
 *   "base_rpm": 64.0,                   // the RPM a numeric "speed" is calibrated against
 *   "stress_impact": 4.0,               // kinetic stress this belt reports per segment at full load
 *   "item_scale": 0.5,                  // display-entity scale for items riding this belt
 *   "pickup_radius": 0.75,              // search radius (blocks) for scooping up dropped items
 *   "max_length": 64                    // longest belt line extend-start/extend-end will build
 * }
 * }</pre>
 *
 * <p>{@link #load()} also runs eagerly during {@code onEnable} (before
 * {@code CraftEngineReloadEvent}), same as {@code PipeTypeLoader}.
 */
public final class BeltTypeLoader {

    private BeltTypeLoader() {
    }

    /** Registers this loader with the central load pipeline. */
    public static void bootstrap() {
        Registries.addLoader("belt_types", Registries.PHASE_TYPES, BeltTypeLoader::load);
    }

    public static void load() {
        int count = DataFiles.loadDirectory("belt_types", BeltTypeLoader::apply);
        if (count > 0)
            CraftEnginePolyfills.instance().getLogger()
                    .info("Applied " + count + " belt type definitions (" + BeltType.REGISTRY.size() + " total).");
    }

    private static void apply(JsonView view, String fileName) {
        Key id = view.has("id")
                ? view.key("id", BeltType.NAMESPACE)
                : Key.of(BeltType.NAMESPACE, stripExtension(fileName));

        BeltType type = BeltType.getOrCreate(id);
        if (type == null)
            throw view.error("belt registry is frozen; '" + id + "' cannot be defined now");

        BeltType.BeltProperties base = type.properties();

        Set<String> connectsTo = view.has("connects_to")
                ? new LinkedHashSet<>(view.stringList("connects_to"))
                : base.connectsTo();

        // "speed": number sets baseTravelTicks (clears formula); string sets/replaces the formula.
        int baseTravelTicks = base.baseTravelTicks();
        String speedFormula = base.speedFormula();
        if (view.has("speed")) {
            com.google.gson.JsonElement speedEl = view.raw().get("speed");
            if (speedEl.isJsonPrimitive() && speedEl.getAsJsonPrimitive().isString()) {
                speedFormula = speedEl.getAsString();
            } else {
                baseTravelTicks = Math.max(1, speedEl.getAsInt());
                speedFormula = null;
            }
        }

        type.applyProperties(new BeltType.BeltProperties(
                view.key("block", "cml", base.blockId()),
                view.floating("height", base.height()),
                Set.copyOf(connectsTo),
                view.enumValue("energy_type", BeltType.EnergyKind.class, base.energyType()),
                view.rangedInt("energy_per_tick", base.energyPerTick(), 0, Integer.MAX_VALUE),
                view.rangedInt("energy_capacity", base.energyCapacity(), 0, Integer.MAX_VALUE),
                baseTravelTicks,
                view.floating("base_rpm", base.baseRpm()),
                view.floating("stress_impact", base.stressImpact()),
                speedFormula,
                view.floating("item_scale", base.itemScale()),
                view.rangedInt("slots", base.slots(), 1, 64),
                view.rangedDouble("pickup_radius", base.pickupRadius(), 0.1, 8.0),
                view.rangedInt("max_length", base.maxLength(), 1, 1024)));
    }

    private static String stripExtension(String fileName) {
        String name = fileName.substring(fileName.lastIndexOf('/') + 1);
        int dot = name.lastIndexOf('.');
        return dot < 0 ? name : name.substring(0, dot);
    }
}
