package dev.arubik.craftengine.machine.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Fully config-driven description of a machine's MAIN menu, parsed from the behavior yml so that
 * NOTHING about the layout (slot numbers, buttons, the title image) is hardcoded in Java.
 *
 * <p>Parsed by the behavior Factory and stored on the behavior, then handed to the block entity
 * (mirrors how {@code bars:} and {@code upgrades:} already flow). The block entity uses these
 * slot arrays to build its {@link dev.arubik.craftengine.multiblock.IOConfiguration} item-slot
 * roles and its recipe loop, and renders the {@link #buttons} generically in its main layout.</p>
 *
 * <pre>
 * behavior:
 *   menu_size: 54                 # inventory size (must stay a multiple of 9)
 *   gui_image: cml:copper_furnace_gui   # image id used as the menu title (optional)
 *   input_slots:  [11, 12]
 *   output_slots: [14, 15]
 *   fuel_slots:   [31]            # or `fuel_slot: 31`
 *   buttons:
 *     - { slot: 33, icon: "cml:upgrade_button",   action: "open_page:1", name: "polyfill.ui.upgrades", lore: ["polyfill.ui.upgrades_desc"] }
 *     - { slot: 29, icon: "cml:overclock_button", action: "open_page:2", name: "polyfill.ui.overclock",
 *         locked_icon: "cml:locked_icon", locked_when: "no_overclock" }
 *     - { slot: 36, icon: "cml:empty_button", action: "deplete_fluid", name: "polyfill.ui.deplete", lore: ["polyfill.ui.fluid"] }
 *     - { slot: 44, icon: "cml:empty_button", action: "deplete_gas",   name: "polyfill.ui.deplete", lore: ["polyfill.ui.gas"]  }
 * </pre>
 */
public final class MachineMenuConfig {

    public final int menuSize;
    public final String guiImage;        // image id (e.g. "cml:copper_furnace_gui"); nullable
    public final int[] inputSlots;
    public final int[] outputSlots;
    public final int[] fuelSlots;
    public final List<Button> buttons;
    /** Slot that renders the reusable recipe-info icon ({@link dev.arubik.craftengine.machine.menu.RecipeInfoIcon}); -1 = none. */
    public final int infoSlot;

    public MachineMenuConfig(int menuSize, String guiImage, int[] inputSlots, int[] outputSlots,
            int[] fuelSlots, List<Button> buttons) {
        this(menuSize, guiImage, inputSlots, outputSlots, fuelSlots, buttons, -1);
    }

    public MachineMenuConfig(int menuSize, String guiImage, int[] inputSlots, int[] outputSlots,
            int[] fuelSlots, List<Button> buttons, int infoSlot) {
        this.menuSize = menuSize;
        this.guiImage = guiImage;
        this.inputSlots = inputSlots;
        this.outputSlots = outputSlots;
        this.fuelSlots = fuelSlots;
        this.buttons = buttons;
        this.infoSlot = infoSlot;
    }

    /** A single config-declared button: an icon, a name/lore and an {@link Action}. */
    public static final class Button {
        public final int slot;
        public final String icon;        // cml item id or vanilla material
        public final Action action;
        public final String name;        // lang key or literal; nullable
        public final List<String> lore;  // lang keys or literals; possibly empty
        public final String lockedIcon;  // icon shown while locked; nullable
        public final LockedWhen lockedWhen; // condition that locks the button; null = never locked

        public Button(int slot, String icon, Action action, String name, List<String> lore,
                String lockedIcon, LockedWhen lockedWhen) {
            this.slot = slot;
            this.icon = icon;
            this.action = action;
            this.name = name;
            this.lore = lore;
            this.lockedIcon = lockedIcon;
            this.lockedWhen = lockedWhen;
        }
    }

    /** Button action: open a submenu page, or empty a tank. */
    public static final class Action {
        public enum Kind { OPEN_PAGE, DEPLETE_FLUID, DEPLETE_GAS, NONE }

        public final Kind kind;
        public final int page; // for OPEN_PAGE

        private Action(Kind kind, int page) {
            this.kind = kind;
            this.page = page;
        }

        public static Action parse(String s) {
            if (s == null)
                return new Action(Kind.NONE, 0);
            String t = s.trim().toLowerCase(java.util.Locale.ROOT);
            if (t.startsWith("open_page")) {
                int page = 0;
                int i = t.indexOf(':');
                if (i >= 0) {
                    try {
                        page = Integer.parseInt(t.substring(i + 1).trim());
                    } catch (NumberFormatException ignored) {
                    }
                }
                return new Action(Kind.OPEN_PAGE, page);
            }
            if (t.equals("deplete_fluid"))
                return new Action(Kind.DEPLETE_FLUID, 0);
            if (t.equals("deplete_gas"))
                return new Action(Kind.DEPLETE_GAS, 0);
            return new Action(Kind.NONE, 0);
        }
    }

    /** Condition that keeps a button locked (shows {@code lockedIcon} and swallows the click). */
    public enum LockedWhen {
        NEVER, NO_OVERCLOCK;

        public static LockedWhen parse(String s) {
            if (s == null)
                return NEVER;
            return "no_overclock".equalsIgnoreCase(s.trim()) ? NO_OVERCLOCK : NEVER;
        }
    }

    // ---------------- parsing ----------------

    /** Parse the menu config from the behavior arguments (a key->value getter, e.g. {@code ConfigSection::get}). */
    public static MachineMenuConfig parse(Function<String, Object> args) {
        int menuSize = intOr(args.apply("menu_size"), 54);
        String guiImage = args.apply("gui_image") == null ? null : String.valueOf(args.apply("gui_image"));
        int[] inputs = slots(args.apply("input_slots"));
        int[] outputs = slots(args.apply("output_slots"));
        int[] fuels;
        if (args.apply("fuel_slots") != null)
            fuels = slots(args.apply("fuel_slots"));
        else if (args.apply("fuel_slot") != null)
            fuels = new int[] { intOr(args.apply("fuel_slot"), 0) };
        else
            fuels = new int[0];
        List<Button> buttons = buttons(args.apply("buttons"));
        int infoSlot = intOr(args.apply("info_slot"), -1);
        return new MachineMenuConfig(menuSize, guiImage, inputs, outputs, fuels, buttons, infoSlot);
    }

    private static List<Button> buttons(Object o) {
        List<Button> out = new ArrayList<>();
        if (!(o instanceof List<?> list))
            return out;
        for (Object e : list) {
            if (!(e instanceof Map<?, ?> m))
                continue;
            int slot = intOr(m.get("slot"), -1);
            if (slot < 0)
                continue;
            String icon = m.get("icon") == null ? null : String.valueOf(m.get("icon"));
            Action action = Action.parse(m.get("action") == null ? null : String.valueOf(m.get("action")));
            String name = m.get("name") == null ? null : String.valueOf(m.get("name"));
            List<String> lore = new ArrayList<>();
            if (m.get("lore") instanceof List<?> ll)
                for (Object l : ll)
                    lore.add(String.valueOf(l));
            String lockedIcon = m.get("locked_icon") == null ? null : String.valueOf(m.get("locked_icon"));
            LockedWhen lockedWhen = LockedWhen.parse(m.get("locked_when") == null ? null
                    : String.valueOf(m.get("locked_when")));
            out.add(new Button(slot, icon, action, name, lore, lockedIcon, lockedWhen));
        }
        return out;
    }

    private static int[] slots(Object o) {
        if (!(o instanceof List<?> list))
            return new int[0];
        int[] s = new int[list.size()];
        for (int i = 0; i < list.size(); i++)
            s[i] = ((Number) list.get(i)).intValue();
        return s;
    }

    private static int intOr(Object o, int def) {
        return (o instanceof Number n) ? n.intValue() : def;
    }
}
