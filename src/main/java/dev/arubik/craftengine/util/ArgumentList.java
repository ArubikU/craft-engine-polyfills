package dev.arubik.craftengine.util;

import java.util.*;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArgumentList {
    public final Object[] types;

    public ArgumentList(Object... types) {
        this.types = types;
    }

    // ----------------- Matching -----------------
    public boolean matches(String[] args, CommandSender sender) {
        if (args.length != types.length) return false;
        for (int i = 0; i < types.length; i++) {
            Object expected = types[i];
            String input = args[i];
            if (expected instanceof Class<?> clazz) {
                if (!canParse(clazz, input, sender)) return false;
            } else if (expected instanceof String literal) {
                if (!matchesLiteral(literal, input)) return false;
            } else {
                return false;
            }
        }
        return true;
    }

    public Object[] parse(String[] args, CommandSender sender) {
        Object[] parsed = new Object[types.length];
        for (int i = 0; i < types.length; i++) {
            Object expected = types[i];
            String input = args[i];
            if (expected instanceof Class<?> clazz) {
                parsed[i] = parseSingle(clazz, input, sender);
            } else if (expected instanceof String) {
                parsed[i] = input;
            }
        }
        return parsed;
    }

    // ----------------- Tab Completion -----------------
    /**
     * Genera sugerencias de tab para el argumento en la posición dada.
     */
    public List<String> suggest(int argIndex, String current, CommandSender sender) {
        if (argIndex >= types.length) return Collections.emptyList();
        Object expected = types[argIndex];

        if (expected instanceof String literal) {
            return suggestFromLiteral(literal, current);
        }
        if (expected instanceof Class<?> clazz) {
            return suggestFromType(clazz, current, sender);
        }
        return Collections.emptyList();
    }

    private List<String> suggestFromLiteral(String literal, String current) {
        List<String> base = new ArrayList<>();
        if (literal.endsWith("^")) {
            base.add(literal.substring(0, literal.length() - 1));
        } else if (literal.endsWith("*") && literal.startsWith("*")) {
            // contiene -> no es práctico sugerir, devolvemos vacío
        } else if (literal.endsWith("*")) {
            base.add(literal.substring(0, literal.length() - 1));
        } else if (literal.startsWith("*")) {
            // sufijo, tampoco es muy útil para sugerir
        } else {
            base.add(literal);
        }
        return base.stream()
                .filter(s -> s.toLowerCase().startsWith(current.toLowerCase()))
                .collect(Collectors.toList());
    }

    private List<String> suggestFromType(Class<?> clazz, String current, CommandSender sender) {
        if (clazz == Boolean.class || clazz == boolean.class) {
            return Arrays.asList("true", "false").stream()
                    .filter(s -> s.startsWith(current.toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (clazz == XAxisCoordinate.class || clazz == YAxisCoordinate.class || clazz == ZAxisCoordinate.class) {
            // coords -> sugerimos "~", "~1", "~10"
            return Arrays.asList("~", "~1", "~10").stream()
                    .filter(s -> s.startsWith(current))
                    .collect(Collectors.toList());
        }
        // para int, double o string no sugerimos nada por defecto
        return Collections.emptyList();
    }

    // ----------------- Literal Matching -----------------
    private boolean matchesLiteral(String pattern, String input) {
        if (pattern.endsWith("^")) {
            return input.equalsIgnoreCase(pattern.substring(0, pattern.length() - 1));
        }
        if (pattern.endsWith("*") && pattern.startsWith("*")) {
            String inner = pattern.substring(1, pattern.length() - 1);
            return input.contains(inner);
        }
        if (pattern.endsWith("*")) {
            return input.startsWith(pattern.substring(0, pattern.length() - 1));
        }
        if (pattern.startsWith("*")) {
            return input.endsWith(pattern.substring(1));
        }
        return input.equals(pattern);
    }

    // ----------------- Type Parsing -----------------
    public static record XAxisCoordinate() {}
    public static record YAxisCoordinate() {}
    public static record ZAxisCoordinate() {}

    private boolean canParse(Class<?> type, String input, CommandSender sender) {
        try {
            parseSingle(type, input, sender);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Object parseSingle(Class<?> type, String input, CommandSender sender) {
        if (type == XAxisCoordinate.class) {
            int currentX = (sender instanceof Player p) ? p.getLocation().getBlockX() : 0;
            if (input.equals("~")) return currentX;
            if (input.startsWith("~")) {
                int offset = input.length() > 1 ? Integer.parseInt(input.substring(1)) : 0;
                return currentX + offset;
            }
            return Integer.valueOf(input);
        }
        if (type == YAxisCoordinate.class) {
            int currentY = (sender instanceof Player p) ? p.getLocation().getBlockY() : 0;
            if (input.equals("~")) return currentY;
            if (input.startsWith("~")) {
                int offset = input.length() > 1 ? Integer.parseInt(input.substring(1)) : 0;
                return currentY + offset;
            }
            return Integer.valueOf(input);
        }
        if (type == ZAxisCoordinate.class) {
            int currentZ = (sender instanceof Player p) ? p.getLocation().getBlockZ() : 0;
            if (input.equals("~")) return currentZ;
            if (input.startsWith("~")) {
                int offset = input.length() > 1 ? Integer.parseInt(input.substring(1)) : 0;
                return currentZ + offset;
            }
            return Integer.valueOf(input);
        }
        if (type == String.class) return input;
        if (type == Integer.class || type == int.class) return Integer.parseInt(input);
        if (type == Double.class || type == double.class) return Double.parseDouble(input);
        if (type == Boolean.class || type == boolean.class) return Boolean.parseBoolean(input);
        throw new IllegalArgumentException("Unsupported type: " + type.getName());
    }

    // ----------------- Equality -----------------
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArgumentList)) return false;
        return Arrays.equals(types, ((ArgumentList) o).types);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(types);
    }
}
