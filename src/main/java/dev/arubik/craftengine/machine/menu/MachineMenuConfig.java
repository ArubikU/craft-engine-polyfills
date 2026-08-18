/*
 * Decompiled with CFR 0.152.
 */
package dev.arubik.craftengine.machine.menu;

import dev.arubik.craftengine.machine.render.formula.PolyContext;
import dev.arubik.craftengine.machine.render.formula.PolyFormula;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

public final class MachineMenuConfig {
    public final int menuSize;
    public final String guiImage;
    public final int[] inputSlots;
    public final int[] outputSlots;
    public final int[] fuelSlots;
    public final List<Button> buttons;
    public final int infoSlot;

    public MachineMenuConfig(int menuSize, String guiImage, int[] inputSlots, int[] outputSlots, int[] fuelSlots, List<Button> buttons) {
        this(menuSize, guiImage, inputSlots, outputSlots, fuelSlots, buttons, -1);
    }

    public MachineMenuConfig(int menuSize, String guiImage, int[] inputSlots, int[] outputSlots, int[] fuelSlots, List<Button> buttons, int infoSlot) {
        this.menuSize = menuSize;
        this.guiImage = guiImage;
        this.inputSlots = inputSlots;
        this.outputSlots = outputSlots;
        this.fuelSlots = fuelSlots;
        this.buttons = buttons;
        this.infoSlot = infoSlot;
    }

    public static MachineMenuConfig parse(Function<String, Object> args) {
        int menuSize = MachineMenuConfig.intOr(args.apply("menu_size"), 54);
        String guiImage = args.apply("gui_image") == null ? null : String.valueOf(args.apply("gui_image"));
        int[] inputs = MachineMenuConfig.slots(args.apply("input_slots"));
        int[] outputs = MachineMenuConfig.slots(args.apply("output_slots"));
        int[] fuels = args.apply("fuel_slots") != null ? MachineMenuConfig.slots(args.apply("fuel_slots")) : (args.apply("fuel_slot") != null ? new int[]{MachineMenuConfig.intOr(args.apply("fuel_slot"), 0)} : new int[]{});
        List<Button> buttons = MachineMenuConfig.buttons(args.apply("buttons"));
        int infoSlot = MachineMenuConfig.intOr(args.apply("info_slot"), -1);
        return new MachineMenuConfig(menuSize, guiImage, inputs, outputs, fuels, buttons, infoSlot);
    }

    private static List<Button> buttons(Object o) {
        ArrayList<Button> out = new ArrayList<Button>();
        if (!(o instanceof List)) {
            return out;
        }
        List list = (List)o;
        for (Object e : list) {
            Map m;
            int slot;
            if (!(e instanceof Map) || (slot = MachineMenuConfig.intOr((m = (Map)e).get("slot"), -1)) < 0) continue;
            String icon = m.get("icon") == null ? null : String.valueOf(m.get("icon"));
            Action action = Action.parse(m.get("action") == null ? null : String.valueOf(m.get("action")));
            String name = m.get("name") == null ? null : String.valueOf(m.get("name"));
            ArrayList<String> lore = new ArrayList<String>();
            Object object = m.get("lore");
            if (object instanceof List) {
                List ll = (List)object;
                for (Object l : ll) {
                    lore.add(String.valueOf(l));
                }
            }
            String lockedIcon = m.get("locked_icon") == null ? null : String.valueOf(m.get("locked_icon"));
            LockedWhen lockedWhen = LockedWhen.parse(m.get("locked_when") == null ? null : String.valueOf(m.get("locked_when")));
            out.add(new Button(slot, icon, action, name, lore, lockedIcon, lockedWhen));
        }
        return out;
    }

    private static int[] slots(Object o) {
        if (!(o instanceof List)) {
            return new int[0];
        }
        List list = (List)o;
        int[] s = new int[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            s[i] = ((Number)list.get(i)).intValue();
        }
        return s;
    }

    private static int intOr(Object o, int def) {
        int n;
        if (o instanceof Number) {
            Number n2 = (Number)o;
            n = n2.intValue();
        } else {
            n = def;
        }
        return n;
    }

    public static final class Action {
        public final Kind kind;
        public final int page;
        public final String target;

        private Action(Kind kind, int page) {
            this(kind, page, "all");
        }

        private Action(Kind kind, int page, String target) {
            this.kind = kind;
            this.page = page;
            this.target = target == null || target.isBlank() ? "all" : target.trim();
        }

        public boolean targets(String tankName) {
            return "all".equalsIgnoreCase(this.target) || this.target.equalsIgnoreCase(tankName);
        }

        private static String suffix(String t) {
            int i = t.indexOf(58);
            return i < 0 ? "all" : t.substring(i + 1);
        }

        public static Action parse(String s) {
            if (s == null) {
                return new Action(Kind.NONE, 0);
            }
            String t = s.trim().toLowerCase(Locale.ROOT);
            if (t.startsWith("open_page")) {
                int page = 0;
                int i = t.indexOf(58);
                if (i >= 0) {
                    try {
                        page = Integer.parseInt(t.substring(i + 1).trim());
                    }
                    catch (NumberFormatException numberFormatException) {
                        // empty catch block
                    }
                }
                return new Action(Kind.OPEN_PAGE, page);
            }
            if (t.startsWith("deplete_fluid")) {
                return new Action(Kind.DEPLETE_FLUID, 0, Action.suffix(t));
            }
            if (t.startsWith("deplete_gas")) {
                return new Action(Kind.DEPLETE_GAS, 0, Action.suffix(t));
            }
            if (t.startsWith("script:")) {
                return new Action(Kind.SCRIPT, 0, s.trim().substring(7));
            }
            if (t.startsWith("run:")) {
                return new Action(Kind.SCRIPT, 0, s.trim().substring(4));
            }
            return new Action(Kind.NONE, 0);
        }

        public static enum Kind {
            OPEN_PAGE,
            DEPLETE_FLUID,
            DEPLETE_GAS,
            SCRIPT,
            NONE;

        }
    }

    public static enum LockedWhen {
        NEVER,
        NO_OVERCLOCK;

        public String expr;

        public static LockedWhen parse(String s) {
            if (s == null) {
                return NEVER;
            }
            if ("no_overclock".equalsIgnoreCase(s.trim())) {
                return NO_OVERCLOCK;
            }
            if ("never".equalsIgnoreCase(s.trim())) {
                return NEVER;
            }
            LockedWhen lw = NEVER;
            lw.expr = null;
            LockedWhen custom = NEVER;
            try {
                PolyFormula.compile(s.trim());
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            LockedWhen result = NO_OVERCLOCK;
            result.expr = s.trim();
            return result;
        }

        public boolean isLocked(PolyContext ctx, double curOverclockLimit) {
            if (this == NEVER) {
                return false;
            }
            if (this.expr != null) {
                try {
                    return PolyFormula.compile(this.expr).evaluateBool(ctx);
                }
                catch (Throwable ignored) {
                    return false;
                }
            }
            return curOverclockLimit <= 0.0;
        }
    }

    public static final class Button {
        public final int slot;
        public final String icon;
        public final Action action;
        public final String name;
        public final List<String> lore;
        public final String lockedIcon;
        public final LockedWhen lockedWhen;

        public Button(int slot, String icon, Action action, String name, List<String> lore, String lockedIcon, LockedWhen lockedWhen) {
            this.slot = slot;
            this.icon = icon;
            this.action = action;
            this.name = name;
            this.lore = lore;
            this.lockedIcon = lockedIcon;
            this.lockedWhen = lockedWhen;
        }
    }
}

