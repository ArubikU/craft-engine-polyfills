package dev.arubik.craftengine.machine.render;

/**
 * Implemented by a CraftEngine BlockEntity that owns a {@link RendererManager}.
 *
 * <p>Marking a block entity with this interface lets external systems — most importantly
 * {@code ContraptionBlockEntityElementMirror} — discover the machine's renderer manager
 * and mirror its model visuals into the contraption's moving, real-world transform
 * without the mirror needing to know the concrete BE type.</p>
 *
 * <h2>Contract</h2>
 * <ul>
 *   <li>The returned {@link RendererManager} must be non-null once the block entity
 *       has been initialised and must remain the same instance for the BE's lifetime.</li>
 *   <li>The BE is responsible for calling {@code RendererManager.tick()} each server tick
 *       and {@code RendererManager.close()} on unload / removal.</li>
 * </ul>
 */
public interface ModelRendersDriven {

    /**
     * Return the {@link RendererManager} owned by this block entity.
     * Never null after initialisation.
     */
    RendererManager rendererManager();
}
