package dev.arubik.craftengine.machine.render;

/**
 * Implemented by a CraftEngine BlockEntity that owns a ModelEngineMachineRenderer
 * (e.g. a block entity that uses ModelEngine R4 for its visual). Exists so
 * ContraptionModelEngineElement can mirror the visual into the contraption bearing
 * without depending on any specific machine class.
 */
public interface ModelEngineDriven {
    ModelEngineMachineRenderer modelEngineRenderer();
}
