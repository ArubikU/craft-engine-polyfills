package dev.arubik.craftengine.conveyor.belt;

public enum ConveyorPart {
    START,
    MIDDLE,
    END;


    public static ConveyorPart fromName(String name) {
        if (name == null) {
            return END;
        }
        switch (name.trim().toUpperCase()) {
            case "START": {
                return START;
            }
            case "MIDDLE": {
                return MIDDLE;
            }
        }
        return END;
    }
}

