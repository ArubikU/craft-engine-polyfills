package dev.arubik.craftengine.virtualui.model;

/**
 * One element of a {@code VirtualUI} screen — the widget vocabulary the script builder exposes
 * (buttons, images, icons, items, interactive slots, a player-bust render, plain labels), each of
 * which renders as one or more packet-only display entities positioned relative to the viewer's
 * locked camera anchor, in "world units at camera distance" space.
 *
 * <p>{@code offsetX}/{@code offsetY} is the widget's CENTER; {@code width}/{@code height} define
 * its clickable hit-box (centered on that point) — a non-interactive widget (a label, most icons)
 * simply has a {@code null} {@code onClick} and never registers a hit-box. {@code offsetZ} is
 * depth from the camera anchor (more negative = further away).
 *
 * <p>Every item-bearing field is an {@link ItemSource} — a plain id string, an already-built
 * {@code ScriptValue.Item} (full fidelity: custom NBT, a specific player's held item, whatever —
 * same convention {@code Menu.set_item}/{@code Dialog.body_item} use), or a dynamic
 * {@code "file.pf:func"} ref evaluated each render tick with that widget's own context bound (see
 * {@code dev.arubik.craftengine.script.types.util.VirtualUIWidgetContextTypes}).
 *
 * <p>{@code onClick}/{@code onHover}/{@code onUnhover} are plain {@code "file.pf:func[:args]"}
 * script refs (or {@code null}) — resolved and executed exactly like a Menu button's action, via
 * {@code ScriptCall}. There is no built-in action vocabulary (no "open a URL", "run a command"
 * kind baked into the engine) — a script that wants those calls {@code Player.open_url(...)},
 * {@code Player.exec_command(...)}, {@code Player.switch_server(...)}, or
 * {@code VirtualUI.change_hologram(...)} itself, the same way any other script would.
 */
public sealed interface Widget {

    String id();
    double offsetX();
    double offsetY();
    double offsetZ();
    double width();
    double height();
    String onClick();
    String onHover();
    String onUnhover();
    int priority();

    /**
     * A clickable text label — 's core "hologram + clickable area" combo. {@code width}/
     * {@code height} is its INTERACTION AREA, centered on {@code hologram}'s own offset — already
     * fully independent of the text's rendered size (a hit-box can be bigger or smaller than what
     * the label visually looks like; nothing ties them together), so a script can make the
     * clickable region as generous or as tight as it wants regardless of the label's length.
     *
     * <p>{@code icon} (an {@link ItemSource}, possibly {@link ItemSource#EMPTY}) renders an item
     * alongside the text as a SECOND packet entity (see
     * {@code dev.arubik.craftengine.virtualui.render.PositionedVisual}), offset from the button's
     * own position by {@code iconOffsetX}/{@code iconOffsetY}.
     */
    record ButtonWidget(String id, HologramLineConfig hologram, double width, double height,
                         ItemSource icon, double iconOffsetX, double iconOffsetY, float iconScale,
                         String onClick, String onHover, String onUnhover, int priority) implements Widget {
        public double offsetX() { return hologram.offsetX(); }
        public double offsetY() { return hologram.offsetY(); }
        public double offsetZ() { return hologram.offsetZ(); }

        /** Convenience constructor for a plain text-only button (most callers) — no icon. */
        public ButtonWidget(String id, HologramLineConfig hologram, double width, double height,
                             String onClick, String onHover, String onUnhover, int priority) {
            this(id, hologram, width, height, ItemSource.EMPTY, 0, 0, 0.5f, onClick, onHover, onUnhover, priority);
        }
    }

    /** A plain, non-interactive text label (no hit-box). */
    record LabelWidget(String id, HologramLineConfig hologram) implements Widget {
        public double offsetX() { return hologram.offsetX(); }
        public double offsetY() { return hologram.offsetY(); }
        public double offsetZ() { return hologram.offsetZ(); }
        public double width() { return 0; }
        public double height() { return 0; }
        public String onClick() { return null; }
        public String onHover() { return null; }
        public String onUnhover() { return null; }
        public int priority() { return 0; }
    }

    /**
     * A scaled flat panel rendered from an item's texture (a resource-pack custom-model-data item
     * shown edge-on via {@code item_display}, the standard "GUI image via item display" technique)
     * — the closest achievable analogue to a Forge/Fabric {@code Screen}'s background image without
     * a real 2D HUD renderer.
     */
    record ImageWidget(String id, ItemSource item, double offsetX, double offsetY, double offsetZ,
                        double width, double height, float rotationZ,
                        String onClick, String onHover, String onUnhover, int priority) implements Widget {}

    /** A small item/block icon, no inherent hit-box unless {@code onClick} is set. */
    record IconWidget(String id, ItemSource item, double offsetX, double offsetY, double offsetZ,
                       float scale, String onClick, String onHover, String onUnhover, int priority) implements Widget {
        public double width() { return scale; }
        public double height() { return scale; }
    }

    /** An explicit item render (distinct name from {@link IconWidget} per the script API's own
     *  vocabulary — functionally identical, kept separate so scripts read naturally: {@code .icon}
     *  for UI chrome, {@code .item} for "this widget IS an item"). */
    record ItemWidget(String id, ItemSource item, double offsetX, double offsetY, double offsetZ,
                       float scale, String onClick, String onHover, String onUnhover, int priority) implements Widget {
        public double width() { return scale; }
        public double height() { return scale; }
    }

    /**
     * An interactive inventory-like slot: renders an item and reports its {@code slotIndex} back
     * to the click script as a trailing argument — see {@code VirtualUIClickSystem}.
     */
    record SlotWidget(String id, int slotIndex, ItemSource item, double offsetX, double offsetY, double offsetZ,
                       double width, double height,
                       String onClick, String onHover, String onUnhover, int priority) implements Widget {}

    /**
     * A player "bust" preview — rendered as a scaled {@code item_display} of a player-head item
     * carrying the target's skin texture (a real 3D full-body puppet isn't feasible from pure
     * packet displays without a resource-pack rig, so this renders the skin the same way an
     * item-frame player head does, scaled up as a portrait). {@code targetPlayer == null} means
     * "the viewer's own face" — resolved per-session in {@code CameraSession}.
     */
    record PlayerRenderWidget(String id, java.util.UUID targetPlayer, double offsetX, double offsetY, double offsetZ,
                               float scale, String onClick, String onHover, String onUnhover, int priority) implements Widget {
        public double width() { return scale; }
        public double height() { return scale; }
    }

    /** A 3D block icon ({@code block_display} of a block id/state string, e.g.
     *  {@code "minecraft:diamond_block"}) — the block-model counterpart to {@link IconWidget}'s
     *  item render, for widgets that read better as a cube than a flat item sprite. */
    record BlockWidget(String id, String blockId, double offsetX, double offsetY, double offsetZ,
                        float scale, String onClick, String onHover, String onUnhover, int priority) implements Widget {
        public double width() { return scale; }
        public double height() { return scale; }
    }

    /**
     * A draggable value control (0.0-1.0) — port of 's hotbar-scrollbar/grab-panorama
     * "click to arm, move the cursor to drag, click again to release" gesture. {@code vertical}
     * chooses which cursor axis drives the value. {@code onChange} fires every tick the value
     * moves while armed, with the new value (0.0-1.0) as its trailing argument (after the widget
     * id) — see {@code VirtualUIClickSystem}. {@code onClick} is unused (click toggles drag
     * state, handled internally, not scriptable) — armed/released transitions instead fire
     * {@code onHover}/{@code onUnhover} respectively, so a script can show/hide a "dragging..."
     * hint without a separate callback vocabulary.
     *
     * <p>{@code width}/{@code height} is this widget's INTERACTION AREA — the hit-box that arms
     * the drag and the span the thumb's value is mapped across (it moves with the cursor across
     * exactly this rectangle, no separate "visual track length" to keep in sync). The rendered
     * THUMB itself (one packet entity, moving along the track as the value changes) is, in order
     * of preference: {@code tracker} (an item icon, e.g. a custom CraftEngine item shaped like a
     * slider handle) if non-empty; else {@code thumbText} (a plain MiniMessage string — same
     * pipeline as any button/label text, including {@code ${expr}}/{@code ".pf:"} dynamic
     * resolution against this scrollbar's own {@code VUIScrollbar} context, e.g.
     * {@code "<yellow>" + round(VUIScrollbar.value()*100) + "%"}) if set; else a generated
     * box-drawing bar with the thumb position highlighted (the original fallback). A script is
     * free to place its own background/track-groove label or image widget behind this one —
     * rendering that groove art isn't this widget's job, only the moving handle is.
     */
    record ScrollbarWidget(String id, boolean vertical, double value, double offsetX, double offsetY, double offsetZ,
                            double width, double height, ItemSource tracker, String thumbText,
                            String onChange, String onHover, String onUnhover, int priority) implements Widget {
        public String onClick() { return null; }
    }

    /**
     * A non-interactive fill/gauge indicator — the display-only counterpart to
     * {@link ScrollbarWidget}: no drag gesture, no {@code onChange}, {@code value} (0-1) is pushed
     * from a script via {@code VirtualUI.set_progress(player, widget_id, value)} whenever the
     * underlying state changes (a machine's processing progress, a speed reading, etc.) rather than
     * being driven by cursor input. Renders as a continuous fill bar unless {@code tracker} is set,
     * in which case that item renders at the fill's leading edge instead (port of Create's
     * {@code GaugeIcon}-style needle). Same "script owns the groove/background art" split as
     * {@link ScrollbarWidget} — layer a frame/track icon behind this one via {@code .icon(...)}'s
     * {@code z} param if a real texture backdrop is wanted.
     */
    record ProgressWidget(String id, double value, double offsetX, double offsetY, double offsetZ,
                           double width, double height, boolean vertical, ItemSource tracker,
                           String onHover, String onUnhover, int priority) implements Widget {
        public String onClick() { return null; }
    }

    /**
     * A stateful on/off control (checkbox/switch) — click flips {@code value} and fires
     * {@code onChange} with the new boolean as its trailing argument (after the widget id), same
     * calling convention as {@link ScrollbarWidget}'s own {@code onChange}. Renders as a text
     * hologram like {@link ButtonWidget} (its {@code onLabel}/{@code offLabel} pick which one shows
     * per current state — plain MiniMessage strings through the same dynamic {@code ${expr}}/
     * {@code ".pf:"} pipeline as any other widget text, evaluated against this toggle's own
     * {@code VUIToggle} context, e.g. {@code onLabel = "<green>ON"}, {@code offLabel = "<red>OFF"}),
     * plus an optional {@code onIcon}/{@code offIcon} rendered alongside it exactly like
     * {@link ButtonWidget}'s icon. {@code onClick} is unused (click toggles state internally, not
     * scriptable) — same reasoning as {@link ScrollbarWidget}.
     */
    record ToggleWidget(String id, boolean value, HologramLineConfig onLabel, HologramLineConfig offLabel,
                         double width, double height, ItemSource onIcon, ItemSource offIcon,
                         double iconOffsetX, double iconOffsetY, float iconScale,
                         String onChange, String onHover, String onUnhover, int priority) implements Widget {
        public double offsetX() { return onLabel.offsetX(); }
        public double offsetY() { return onLabel.offsetY(); }
        public double offsetZ() { return onLabel.offsetZ(); }
        public String onClick() { return null; }
    }

    /**
     * A discrete option cycler (port of Create's {@code SelectionScrollInput}) — click OR
     * scroll-wheel (see {@code VirtualUIScrollPacketListener}) advances {@code index} through
     * {@code options}, wrapping at both ends, and fires {@code onChange} with the widget id, the
     * newly-selected option string, AND its index as trailing arguments. {@code template} is a
     * plain script-provided MiniMessage string through the SAME dynamic {@code ${expr}}/
     * {@code ".pf:"} pipeline every other widget's text uses (see
     * {@code dev.arubik.craftengine.virtualui.DynamicFieldResolver}), evaluated against this
     * widget's own {@code VUISelect} context ({@code .value()} the current option string,
     * {@code .index()} its index) — nothing about the visual format (arrows, colors, whether the
     * option even shows literally) is baked into the engine; e.g.
     * {@code "<white>◀ <yellow>${VUISelect.value()}</yellow> ▶"}. {@code onClick} is unused (click
     * cycles state internally, not scriptable) — same reasoning as
     * {@link ScrollbarWidget}/{@link ToggleWidget}.
     */
    record SelectWidget(String id, java.util.List<String> options, int index, HologramLineConfig template,
                         double width, double height,
                         String onChange, String onHover, String onUnhover, int priority) implements Widget {
        public double offsetX() { return template.offsetX(); }
        public double offsetY() { return template.offsetY(); }
        public double offsetZ() { return template.offsetZ(); }
        public String onClick() { return null; }
        public String current() { return options.isEmpty() ? "" : options.get(Math.floorMod(index, options.size())); }
    }
}
