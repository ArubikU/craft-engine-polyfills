package dev.arubik.craftengine.multiblock;

/**
 * Role of a block in a multiblock structure.
 * Now supports BlockState property values for persistence.
 */
public enum MultiBlockRole {
    NONE("none"),
    CORE("core"),
    PART("part");

    private final String propertyName;

    MultiBlockRole(String propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * Get property value for BlockState
     */
    public String getPropertyValue() {
        return this.propertyName;
    }

    /**
     * Convert from BlockState property value to enum
     */
    public static MultiBlockRole fromProperty(String value) {
        if (value == null)
            return NONE;

        for (MultiBlockRole role : values()) {
            if (role.propertyName.equals(value)) {
                return role;
            }
        }
        return NONE;
    }
}
