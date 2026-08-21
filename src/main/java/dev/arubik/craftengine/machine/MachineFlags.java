package dev.arubik.craftengine.machine;

/**
 * Per-machine behavioural switches, one field per responsibility.
 *
 * <p>These used to be loose top-level booleans, and {@code no_processing} in particular conflated
 * three unrelated decisions: "do not run recipes", "do join the kinetic network", and — only as an
 * accident of where the early-out sat in {@code tick()} — "do not refresh the open GUI". That last
 * one silently froze the menu of every {@code no_processing} machine, which is why gas gauges and
 * RPM/SU buttons looked dead. Splitting them means a machine can turn off recipes without also
 * turning off its UI, and lets a pack author load only the subsystems the machine actually uses.
 *
 * <p>Declared in a machine JSON under {@code "flags"}, naming only what differs from the defaults:
 * <pre>
 *   "flags": { "recipes": false, "kinetics": true, "renderers": false }
 * </pre>
 *
 * <p>Every field gates a concrete code path — there are no decorative flags here.
 *
 * @param recipes        run the recipe/processing pipeline each tick (fuel burn, progress, crafting,
 *                       output push). Turning this off is how a machine becomes a pure
 *                       script/kinetic block.
 * @param fuel           this machine consumes fuel.
 * @param continuousFuel burn fuel continuously rather than per-craft.
 * @param ui             right-clicking opens the GUI.
 * @param uiTick         refresh the open GUI every tick — bars, script-driven layout icons, button
 *                       lore. Deliberately independent of {@link #recipes}; conflating the two is
 *                       the bug this record exists to prevent. Costs nothing when nobody is looking:
 *                       the menu tick early-returns with no viewers.
 * @param kinetics       participate in the RPM/stress network: pull rotational power from
 *                       neighbours, relay it, and count toward network stress.
 * @param ioPull         automatically pull fluid/gas in through the faces declared in {@code io.input}.
 *                       Off means the machine only receives what is pushed to it.
 * @param renderers      drive the {@code renderers} block — display entities, model animation,
 *                       spec displays. Off skips all per-tick render context building, which is the
 *                       single most expensive part of the tick for a decorative-heavy machine.
 * @param scripts        run {@code action_script}, {@code status} and the placement hooks. Off makes
 *                       the machine inert to scripting without having to strip the fields.
 * @param animations     tick script-triggered animations.
 * @param redstone       may emit a redstone signal ({@code Machine.redstone.set(n)} /
 *                       {@code emit_redstone}). Off makes the emit calls no-ops, so a machine that
 *                       is not a sensor cannot accidentally power a neighbour.
 */
public record MachineFlags(
        boolean recipes,
        boolean fuel,
        boolean continuousFuel,
        boolean ui,
        boolean uiTick,
        boolean kinetics,
        boolean ioPull,
        boolean renderers,
        boolean scripts,
        boolean animations,
        boolean redstone
) {

    /** Defaults for a plain recipe machine with a GUI and no kinetics. */
    public static final MachineFlags DEFAULT =
            new MachineFlags(true, true, false, true, true, false, true, true, true, true, true);

    public MachineFlags withRecipes(boolean v) {
        return new MachineFlags(v, fuel, continuousFuel, ui, uiTick, kinetics, ioPull, renderers, scripts, animations, redstone);
    }

    public MachineFlags withFuel(boolean v) {
        return new MachineFlags(recipes, v, continuousFuel, ui, uiTick, kinetics, ioPull, renderers, scripts, animations, redstone);
    }

    public MachineFlags withContinuousFuel(boolean v) {
        return new MachineFlags(recipes, fuel, v, ui, uiTick, kinetics, ioPull, renderers, scripts, animations, redstone);
    }

    public MachineFlags withUi(boolean v) {
        return new MachineFlags(recipes, fuel, continuousFuel, v, uiTick, kinetics, ioPull, renderers, scripts, animations, redstone);
    }

    public MachineFlags withUiTick(boolean v) {
        return new MachineFlags(recipes, fuel, continuousFuel, ui, v, kinetics, ioPull, renderers, scripts, animations, redstone);
    }

    public MachineFlags withKinetics(boolean v) {
        return new MachineFlags(recipes, fuel, continuousFuel, ui, uiTick, v, ioPull, renderers, scripts, animations, redstone);
    }

    public MachineFlags withIoPull(boolean v) {
        return new MachineFlags(recipes, fuel, continuousFuel, ui, uiTick, kinetics, v, renderers, scripts, animations, redstone);
    }

    public MachineFlags withRenderers(boolean v) {
        return new MachineFlags(recipes, fuel, continuousFuel, ui, uiTick, kinetics, ioPull, v, scripts, animations, redstone);
    }

    public MachineFlags withScripts(boolean v) {
        return new MachineFlags(recipes, fuel, continuousFuel, ui, uiTick, kinetics, ioPull, renderers, v, animations, redstone);
    }

    public MachineFlags withAnimations(boolean v) {
        return new MachineFlags(recipes, fuel, continuousFuel, ui, uiTick, kinetics, ioPull, renderers, scripts, v, redstone);
    }

    public MachineFlags withRedstone(boolean v) {
        return new MachineFlags(recipes, fuel, continuousFuel, ui, uiTick, kinetics, ioPull, renderers, scripts, animations, v);
    }
}
