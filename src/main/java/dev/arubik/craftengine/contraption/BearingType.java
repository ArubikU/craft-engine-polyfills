package dev.arubik.craftengine.contraption;

/**
 * The bearing kinds (CONTRAPTIONS.md §1 "Assembly/disassembly", Phase 6): LINEAR and
 * ROTATIONAL are block-anchored (the bearing block itself stays fixed in the real world
 * forever), MINECART is entity-anchored (a real vanilla minecart carries the contraption
 * along a rail).
 *
 * <p>{@link #PHYS} (roadmap item #9 — PhysContraption, {@code .migration/ROADMAP-claims-and-phys.md}
 * §2) is a fourth, kinematically distinct kind: it has NO pinning bearing at all. It goes through the
 * SAME block-anchored capture path as LINEAR/ROTATIONAL (so it assembles/persists/rehydrates through
 * the existing {@code ContraptionAssembler}/{@code BlockAnchoredContraptionStore} machinery
 * unchanged), but {@code ContraptionAssembler#attachDefaultBehavior} attaches a
 * {@code behavior.PhysicsBehavior} — a hand-rolled gravity integrator — instead of a LINEAR/ROTATIONAL
 * bearing behavior, so the captured structure FALLS under gravity and rests on the ground rather than
 * being driven by a motor. See {@code behavior.PhysicsBehavior} for the milestone-1 scope
 * (gravity + falling + ground collision; no torque/rotation/off-thread).
 *
 * <p>{@link #GHAST} (2026-07-16 — "happy ghast harness contraption") is the second entity-anchored kind,
 * built to the same shape as {@link #MINECART}: a real vanilla {@code HappyGhast} carries the contraption,
 * which inherits its movement, yaw and scale. Equipping/removing the harness is what assembles/disassembles
 * it — see {@code GhastHarnessBearing} and {@code GhastHarnessListener}.
 */
public enum BearingType {
    LINEAR,
    ROTATIONAL,
    MINECART,
    PHYS,
    GHAST,
    /**
     * A PILOTED phys contraption (the "vehicle bearing"). Kinematically identical to {@link #PHYS} — a free
     * rigid body under gravity, collision and buoyancy — but its default behavior is a
     * {@code VehicleControlBehavior} (which EXTENDS {@code PhysicsBehavior}), so a seated/standing driver's
     * live movement input (WASD + jump/sprint) is turned into thrust/steering impulses each tick via
     * {@code PhysicsWorld.applyThrust}. Everything else (solver, body-vs-body collision, buoyancy,
     * power-to-mass via inverse mass, self-levelling) is inherited from the PHYS path unchanged.
     */
    VEHICLE
}
