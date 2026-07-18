package dev.arubik.craftengine.contraption;

import java.util.UUID;

import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

/**
 * Carries the CONTROLLING player smoothly while standing on a moving contraption, without
 * teleport-jitter, without passenger/vehicle mounting. See CONTRAPTIONS.md §1 "MVP choice".
 *
 * <p><b>Velocity-based (2026-07-02 session)</b> — explicit user request to replace the earlier
 * relative-teleport mechanism ({@code ServerGamePacketListenerImpl#teleport} with
 * {@code Relative.X/Y/Z} flags, ack-gated to avoid stacking corrections) with a real
 * delta-velocity impulse instead: every tick, ADD this tick's platform delta to the player's own
 * {@link ServerPlayer#getDeltaMovement()} (never SET an absolute value — preserves whatever
 * jump/fall/walk motion the player already had, same "always a delta, never a position" principle
 * the relative-teleport version already followed, just applied through vanilla's velocity channel
 * instead of a position-correction packet) and push the updated vector to the client via
 * {@link ClientboundSetEntityMotionPacket} — the same packet/mechanism vanilla itself uses for
 * knockback/explosions, i.e. "hey, you're now moving a bit faster in this direction," which the
 * client's own physics smooths out locally instead of snapping to an absolute point.
 *
 * <p><b>Known trade-off vs. the relative-teleport approach</b> (kept here for whoever revisits
 * this next): a real {@link ServerPlayer}'s position is client-authoritative — the server doesn't
 * self-integrate {@code getDeltaMovement()} into position the way it does for a non-player entity;
 * the client applies the received velocity locally and keeps reporting its own position via
 * {@code ServerboundMovePlayerPacket} on top of it. That makes this behave like vanilla knockback
 * (subject to the client's own drag/friction each tick, and to Vanilla's `movedTooQuickly`/
 * `movedWronglyPlayer` server-side sanity checks flagging a sufficiently large single-tick delta)
 * rather than a hard guarantee of exact position tracking. If jitter/rubber-banding or
 * "moved too quickly" kicks reappear under real testing, the previous relative-teleport
 * implementation (ack-gated, exact-delta, immune to client-side drag) is the fallback — see this
 * class's git history / CONTRAPTIONS.md for that version.
 *
 * <p><b>...and back to relative-teleport, for real this time (2026-07-02 session, later same
 * day — "veo que el empuje que hacen los bloques/shulker de hitbox del contraption no es cada
 * tick sino, ejemplo, 1 vez al segundo da un empujoncito, debería ser más realista, o sea aplicar
 * el delta del movimiento para alejarlo cada tick").</b> Confirmed by re-reading
 * {@code ContraptionEngine.render()}/{@code stepKinematics} and every {@code MovementBehavior}
 * that the carry call site and the per-tick delta it reads were NEVER the problem — {@code
 * ContraptionHitboxSwarm#carryRiders}/{@code carryEntities} run unconditionally every tick
 * (independent of the packet-volume "moved" optimization, which only gates re-sending idle
 * hitbox POSITION-SYNC packets, a completely separate concern), and {@code
 * LinearActuatorBehavior}/{@code RotationalBearingBehavior} compute a genuinely continuous
 * {@code speedBlocksPerSec / 20.0} fraction every tick with no internal accumulate-then-flush —
 * motion input here really is smooth and per-tick. The chunkiness lived entirely in THIS
 * class's velocity-based transport described in the paragraph above: {@code setDeltaMovement} +
 * {@link ClientboundSetEntityMotionPacket} is a suggestion the client's own local movement
 * prediction/ground-friction integration is free to partially or wholly reabsorb before it ever
 * becomes a visible position change, tick after tick, until enough client-side drift piles up to
 * cross some perceptibility threshold — which reads exactly like "a jolt once a second" even
 * though the server sent a fresh nudge every single tick. A
 * {@link net.minecraft.server.network.ServerGamePacketListenerImpl#teleport} call with
 * {@link Relative#X}/{@link Relative#Y}/{@link Relative#Z} flags doesn't have this problem: it's
 * a hard, exact position CORRECTION, not a velocity hint, and per that method's own
 * implementation it is safe to call every tick — each call re-arms the {@code awaitingTeleport}
 * id and overwrites {@code awaitingPositionFromClient} with wherever the player ends up THIS
 * tick, so firing again before the previous tick's ack even arrives simply supersedes it
 * (matched by id whenever the ack does land) instead of queuing/stacking multiple corrections.
 * "Ack-gated" describes that same-id-supersedes-previous safety net, not a throttle that skips
 * ticks — see {@link #carry} for the actual call. Y-touch and jump-awareness gating (below) are
 * UNCHANGED by this transport swap — only the mechanism used to apply whichever (dx, dy-or-none,
 * dz) this method already decided on has changed, not the decision logic itself.
 *
 * <p><b>Decompiled ground truth on vanilla jumping (2026-07-02 session, follow-up — "aun no puedo
 * saltar", user asked to decompile {@code Player}/{@code ServerPlayer} tick and manually
 * reimplement the jump force instead of guessing)</b>: this section documents exactly how vanilla
 * jump physics works server-side for a REAL connected player, decompiled from the mapped 1.21.11
 * server jar ({@code LivingEntity.class}, {@code Avatar.class}, {@code Player.class},
 * {@code ServerPlayer.class}, {@code ServerGamePacketListenerImpl.class} — javap bytecode,
 * method-by-method), because the earlier "dy==0 → don't touch Y" fix was a plausible-but-unverified
 * theory and clearly wasn't the whole story.
 * <ul>
 *   <li>{@code LivingEntity.jumpFromGround()} is the real vanilla jump impulse: it reads
 *       {@code getJumpPower()} (attribute {@code Attributes.JUMP_STRENGTH} × block-jump-factor +
 *       jump-boost-potion bonus) and does
 *       {@code setDeltaMovement(dx, jumpPower, dz)} — Y is SET, not added, confirming a jump is a
 *       fresh vertical impulse, not a running total.</li>
 *   <li>{@code jumpFromGround()} is only ever CALLED from inside {@code LivingEntity.aiStep()},
 *       guarded by a private {@code boolean jumping} field (only invoked when
 *       {@code onGround() && jumping && noJumpDelay == 0}, then a 10-tick {@code noJumpDelay}
 *       cooldown is set). {@code Avatar} (new intermediate class between {@code LivingEntity} and
 *       {@code Player} in this MC version) does NOT override {@code aiStep()} at all, so
 *       {@code Player.aiStep()}'s {@code super.aiStep()} call resolves straight to
 *       {@code LivingEntity.aiStep()} — same jump-trigger code path a zombie or any other mob
 *       uses.</li>
 *   <li><b>The decisive finding:</b> nothing server-side ever calls {@code setJumping(true)} for a
 *       real {@link ServerPlayer}. Searched every reachable class ({@code ServerPlayer},
 *       {@code Player}, {@code ServerGamePacketListenerImpl}) — zero callers of
 *       {@code LivingEntity#setJumping}. A real player DOES send jump-key state to the server —
 *       {@code ServerboundPlayerInputPacket} carries a {@code net.minecraft.world.entity.player.Input}
 *       record ({@code forward/backward/left/right/jump/shift/sprint}) — and
 *       {@code ServerGamePacketListenerImpl.handlePlayerInput} DOES store it via
 *       {@code ServerPlayer.setLastClientInput(Input)} (public getter
 *       {@code ServerPlayer#getLastClientInput()}), but that handler only additionally forwards
 *       {@code input.shift()} into {@code setShiftKeyDown} — {@code input.jump()} is received and
 *       stored but literally never read by any vanilla code to drive {@code jumping}/
 *       {@code jumpFromGround()}. Contrast: a mob's {@code MoveControl}/goal AI DOES call
 *       {@code setJumping(true)} itself every tick it wants to hop, which is why mobs' jumps ARE a
 *       real server-side {@code jumpFromGround()} call while a human player's never is.</li>
 *   <li><b>Conclusion:</b> for a real player, "jumping" is ENTIRELY a client-side local physics
 *       prediction. The server never independently decides to apply a jump impulse and has no
 *       {@code jumpFromGround()} call to "cancel" or "override" for a human — there is nothing
 *       running server-side that this class could intercept even in principle. The server's only
 *       view into the jump is indirect: the player's own {@code ServerboundMovePlayerPacket}
 *       stream reports a suddenly-higher Y than the previous tick, and {@code getDeltaMovement()}
 *       (this class's own {@code current} above) lags at LEAST one full network round-trip behind
 *       what the client is actually doing (it's whatever WE last stamped via
 *       {@link ClientboundSetEntityMotionPacket}, not the client's live physics state — the server
 *       has no authoritative "current player velocity" for a human at all).</li>
 * </ul>
 *
 * <p><b>Why "skip Y entirely when dy==0" was directionally correct but incomplete</b>: it correctly
 * stopped the server from re-stamping a stale {@code current.y} every single tick on a purely
 * horizontal contraption (that stale echo was fighting the client's live jump arc once per tick,
 * "jump gets pulled back down"). But it was an all-or-nothing rule with no actual jump-awareness:
 * it doesn't tell us whether the player is CURRENTLY mid-jump, so on a contraption that also moves
 * vertically (dy != 0, e.g. a lift) the old code unconditionally did
 * {@code current.y + dy} — reusing the same stale {@code current.y} baseline as a base to add onto,
 * which is precisely wrong at the moment a player jumps on a moving lift: the added impulse lands
 * on top of a server Y-velocity estimate that has nothing to do with the jump arc the client is
 * actually flying through. There was no mechanism here that distinguished "player is walking/idle"
 * from "player is mid-jump" at all.
 *
 * <p><b>The fix (this session)</b>: use {@link ServerPlayer#getLastClientInput()}'s
 * {@code jump()} boolean — the ACTUAL per-tick "is this player currently holding the jump key"
 * signal the client sends via {@code ServerboundPlayerInputPacket}, real and populated for every
 * connected player in this Paper version (see decompile notes above) — as a much more precise
 * "is a client-side jump arc in flight right now" proxy than inferring it from velocity. While
 * {@code input.jump()} is true: NEVER touch Y at all (not even to add {@code dy}) — the whole
 * point is that the client is already flying its own locally-predicted arc and the server has zero
 * reliable information to add to it; any Y write here, additive or not, is working against
 * information the server doesn't actually have. The platform's own vertical carry (for a lift/etc)
 * still gets applied on every OTHER tick (jump key not currently held) exactly as before, so the
 * player still rides a moving platform vertically the rest of the time — only the actual jump
 * window itself is now protected, rather than gating on the coincidental {@code dy == 0} platform
 * state that had nothing to do with what the player's own input was doing. This composes exactly
 * as asked: "add the platform's own vertical carry motion on top of whatever the player's
 * client-predicted jump is already doing" — except the decompile shows there IS no additive
 * server-side quantity worth adding while a jump is in flight (the server doesn't know the arc's
 * current velocity), so "on top of" collapses to "leave it alone entirely" here, which is also the
 * simplest, least-regression-prone rule: it can never make a jump worse, only ever skip a tick's
 * platform-lift Y nudge while the key is held (a handful of ticks — jump duration is short, and if
 * the platform truly needs to carry the player up mid-jump the very next non-jumping tick resumes
 * doing so with the accumulated platform position, not a lost delta, since deltas here are never
 * accumulated/queued — see {@code ContraptionState#lastDeltaY} — each tick's delta is independent).
 *
 * <p><b>FINAL transport decision — velocity/push, not teleport (2026-07-02 session, explicit user
 * override)</b>: a later fix attempt swapped the transport from {@code setDeltaMovement}+
 * {@link ClientboundSetEntityMotionPacket} to {@code ServerGamePacketListenerImpl#teleport} with
 * {@link net.minecraft.world.entity.Relative} flags, theorizing the velocity approach was the
 * cause of a "chunky, once-a-second" push feel. The user explicitly rejected that swap and asked
 * to keep the ORIGINAL velocity/push mechanism ("que NO nada de teleport, vuelve al empujon,
 * estaba bien") — reverted back to {@code setDeltaMovement}/{@code ClientboundSetEntityMotionPacket}
 * below. The jump-key-aware Y-gating decompile work above is UNCHANGED and still applies; only the
 * underlying apply-mechanism reverted. If chunkiness is reported again, look elsewhere first (e.g.
 * client-side interpolation settings, network tick rate) rather than re-attempting a teleport swap.
 *
 * <p><b>Skip the packet entirely while jumping, not just the Y component (2026-07-02 session,
 * follow-up — "sigo sin poder saltar", re-investigated with total skepticism per explicit
 * instruction, not assuming the earlier Y-gating fix actually works in practice)</b>: re-decompiled
 * {@code ServerGamePacketListenerImpl#handlePlayerInput} (mapped 1.21.11 server jar, javap
 * bytecode) to re-verify the earlier fix's own premise. Confirmed genuinely true this time:
 * {@code handlePlayerInput} unconditionally calls {@code ServerPlayer.setLastClientInput(Input)}
 * for every {@code ServerboundPlayerInputPacket} it receives — the stored {@code Input} is exactly
 * whatever the client just sent, there is no "default/empty Input" substituted anywhere on this
 * path, so {@code getLastClientInput().jump()} being {@code true} while the key is held is
 * trustworthy — that part of the earlier fix holds up.
 * (Side finding while re-decompiling: Paper's {@code handleMovePlayer} — driven by
 * {@code ServerboundMovePlayerPacket} position deltas, a completely different method — has its OWN
 * independent synthetic jump detector: if the player was on-ground last tick, the incoming packet
 * reports airborne, and Y increased, Paper fires a {@code PlayerJumpEvent} and calls
 * {@code LivingEntity#jumpFromGround()} server-side as an anti-cheat-style reconciliation.
 * Decompiled {@code jumpFromGround()} itself does
 * {@code setDeltaMovement(current.x, Math.max(jumpPower, current.y), current.z)} — Y is a
 * {@code max(...)}, so this path can only ever raise Y, never pull it down; ruled out as the
 * source of a "pulled back down" symptom.)
 *
 * <p>The actual remaining gap: the earlier fix only ever special-cased the Y VALUE placed inside
 * the packet ({@code current.y} instead of {@code current.y + dy}) but still sent a fresh
 * {@link ClientboundSetEntityMotionPacket} for X/Z every tick, even while {@code jumping} is true.
 * That packet is unavoidably a full 3-component vector — there's no partial-axis variant — so
 * "echoing current.y" is only truly a no-op if {@code current.y} happens to already match the
 * client's own live physics state. It does not: {@code current.y} here is
 * {@code player.getDeltaMovement().y}, the SERVER's bookkeeping, last written by whatever WE
 * stamped via this very method (or barely touched by vanilla tick code for a real player, since Y
 * is client-predicted per this class's decompile writeup above) — not a live mirror of the
 * client's in-flight jump-arc velocity. Sending ANY
 * {@link ClientboundSetEntityMotionPacket} for the player's own entity is vanilla's documented
 * mechanism for overwriting that entity's ENTIRE local velocity state (this is exactly how
 * knockback/explosions work: "here is your true velocity now," client obeys wholesale) — so even
 * an intended no-op echo still asserts a stale value as authoritative the instant it's sent, which
 * can visibly fight/clip a jump arc the server has no accurate view of at all.
 *
 * <p>The fix: while {@code jumping} is true, skip sending the motion packet AT ALL that tick — not
 * merely skip touching Y — matching the simplest safe rule available given the decompile findings:
 * an atomic packet can't partially update one axis while leaving the others untouched, so the only
 * way to guarantee zero interference with client-side jump physics is to not send it at all for
 * those few ticks. This sacrifices X/Z ride-along smoothness for the handful of ticks a jump is
 * airborne (short-lived, and the very next non-jumping tick resumes normal carry against the
 * platform's current position — no lost/queued delta, since deltas here were never accumulated to
 * begin with).
 *
 * <p><b>SUPERSEDING root cause found (2026-07-02 session, later same day — investigated per an
 * explicit "puede que sea porque estamos saltando sobre el vacío" hint questioning whether this
 * was ever a velocity-packet problem at all)</b>: re-decompiled
 * {@code ServerGamePacketListenerImpl} end-to-end (mapped 1.21.11 server jar) specifically for its
 * anti-fly-kick bookkeeping, since a player carried by this system stands on a packet-only
 * SHULKER/INTERACTION floor that is VISUAL-ONLY — the captured blocks live inside a separate
 * {@code ContraptionLevel}, never placed in the real {@code ServerLevel} the player's own entity
 * actually occupies. From the real world's point of view, a carried player is standing over void.
 * <ul>
 *   <li>{@code ServerGamePacketListenerImpl.handleMovePlayer} computes
 *       {@code clientIsFloating = <client claims on-ground/not-falling> && noBlocksAround(player)}
 *       — and {@code noBlocksAround} (private, same class) inflates the player's REAL bounding box
 *       and scans the REAL {@code Entity.level()} for any real block within ~0.0625 blocks
 *       horizontally / 0.55 blocks below. Since the contraption's blocks are packet-only, this
 *       scan always comes back empty for a carried rider — {@code clientIsFloating} is {@code true}
 *       essentially every tick they stand on a contraption, jump or no jump.</li>
 *   <li>{@code ServerGamePacketListenerImpl.tick()} (runs every server tick regardless of whether
 *       a movement packet arrived that tick) checks {@code clientIsFloating}: if true (and not
 *       sleeping/passenger/dead), it increments {@code aboveGroundTickCount}; once that counter
 *       exceeds {@code getMaximumFlyingTicks()} (decompiled: {@code ceil(80 * max(0.08/gravity,1))}
 *       — 80 ticks / 4 seconds under normal gravity) the player is KICKED with
 *       {@code PlayerKickEvent.Cause.FLYING_PLAYER} ("Flying is not enabled on this server").</li>
 *   <li>This is the actual mechanism behind every symptom reported this session under "can't
 *       jump"/"gets pulled down" — not a velocity-packet/Y-echo problem at all. A player standing
 *       still on a contraption for ~4 seconds would eventually get kicked outright regardless of
 *       whether they ever press jump; jumping merely makes the on-ground/off-ground signal noisier
 *       (each launch/land cycle still satisfies {@code clientIsFloating}'s "claims grounded, no
 *       real block below" condition throughout).</li>
 *   <li><b>Why this can't be fixed by anything velocity-shaped in this class</b>: nothing this
 *       class sends (X/Z ride-along, Y echo/gate, even a fully server-authoritative jump-velocity
 *       impulse) changes what real blocks exist in {@code Entity.level()}. {@code noBlocksAround}
 *       would keep returning {@code true} — and {@code aboveGroundTickCount} would keep climbing
 *       toward the kick threshold — no matter how correct the velocity math is. This class's whole
 *       jump-gating investigation (both sections above) was chasing a real, but ultimately
 *       secondary, correctness issue; the primary, session-defining bug is this kick timer.</li>
 * </ul>
 * <p><b>The fix</b>: {@code ServerGamePacketListenerImpl.resetFlyingTicks()} is a PUBLIC method
 * (decompile-confirmed — {@code aboveGroundTickCount = 0; aboveGroundVehicleTickCount = 0;}, no
 * reflection needed) — call {@code player.connection.resetFlyingTicks()} once per {@link #carry}
 * invocation (i.e. every tick a real player is confirmed standing on/being carried by a
 * contraption). This pins the kick counter at zero for exactly the tick window this system already
 * knows the player is legitimately over a fake floor, without touching anything about vanilla's
 * jump/velocity physics, without reflection into private fields, and without disabling the
 * anti-fly kick globally (a player who wanders off the contraption over a REAL void stops getting
 * this call and the vanilla kick protection resumes normally the next tick). A manually-computed
 * server-authoritative jump-velocity impulse (i.e. reimplementing {@code jumpFromGround()} inside
 * this class, replacing client-predicted jump physics entirely) was considered and explicitly
 * rejected: it cannot address {@code noBlocksAround}/{@code aboveGroundTickCount} at all (see
 * bullet above), so it would add real complexity and change jump FEEL for zero effect on the
 * actual reported symptom — the earlier jump-gating logic (kept, still directionally correct
 * as its own minor fix) is left as-is rather than replaced by a mechanism that doesn't touch the
 * real cause.
 *
 * <p><b>Walk/sprint speed crawls to the platform's own creep speed while riding (2026-07-02
 * session, follow-up — "la velocidad del jugador caminando y corriendo dentro de un contraptions
 * es muy lenta")</b>: re-decompiled {@code ServerGamePacketListenerImpl.handleMovePlayer} and
 * {@code LivingEntity}/{@code Player} tick code (mapped 1.21.11 server jar, javap bytecode) to
 * settle exactly why. Root cause confirmed, and it's a different mechanism than every earlier fix
 * in this file:
 * <ul>
 *   <li>{@code dx}/{@code dz} passed into {@link #carry} are ONLY ever the contraption's own
 *       per-tick platform delta (confirmed by re-reading every call site: {@code
 *       ContraptionEntity#carryRiders}/{@code #carrySeatedRiders}, {@code
 *       ContraptionHitboxSwarm#carryRiders}, {@code SwarmSpike} — all pass {@code
 *       state.lastDeltaX()/Y()/Z()} or an equivalent bearing-delta, i.e. "how far the PLATFORM
 *       itself moved this tick," typically a few hundredths of a block for a realistic contraption
 *       speed). There is no code path anywhere in this system that reads the player's own
 *       forward/sprint input and folds it into {@code dx/dz} — the platform delta and the player's
 *       own WASD-driven displacement have always been two entirely separate quantities that this
 *       method never combined.</li>
 *   <li>Every tick this method ran (before this fix), it did
 *       {@code player.setDeltaMovement(new Vec3(dx, ..., dz))} then sent
 *       {@link ClientboundSetEntityMotionPacket} — and per this class's OWN earlier decompile
 *       writeup above (see the "X/Z: SET, not ADD" section), that packet is vanilla's documented
 *       "here is your TRUE velocity now, obey wholesale" mechanism, identical to knockback. Setting
 *       it to just {@code (dx, dz)} every single tick means the player's horizontal velocity is
 *       reset to the platform's own crawl speed every tick, BEFORE the client's local sprint
 *       acceleration (which normally ramps up over ~1-3 ticks toward a terminal ground speed
 *       balanced against friction/drag) ever gets a chance to build up or persist — the server was
 *       unconditionally overwriting 100% of the player's own movement contribution with the
 *       platform's, regardless of how hard the player was pressing sprint. This reproduces exactly
 *       as reported: the faster/harder the player tries to move, the more their real velocity gets
 *       stomped back down to the platform's own (typically much slower) creep speed every tick.</li>
 *   <li>Why this couldn't be fixed by reading the player's real velocity and re-adding it: this
 *       class's own earlier decompile work (see the jump-physics section above) already established
 *       that {@code player.getDeltaMovement()} for a real, controlled {@link ServerPlayer} is NOT a
 *       live mirror of the client's actual WASD-driven velocity — no vanilla code path feeds
 *       {@code ServerboundMovePlayerPacket} position deltas back into {@code setDeltaMovement} for
 *       a controlled player (re-confirmed by re-scanning {@code handleMovePlayer}'s bytecode this
 *       session — it drives position/rotation/on-ground bookkeeping and the {@code
 *       clientIsFloating}/anti-fly-kick logic described above, never touches {@code
 *       deltaMovement}). So {@code current.x}/{@code current.z} are just "whatever this class
 *       last SET," i.e. last tick's own {@code dx}/{@code dz} echoed back — reading it back and
 *       adding the player's own contribution on top isn't possible because the player's own
 *       contribution was never captured anywhere to begin with.</li>
 * </ul>
 *
 * <p><b>The fix — gate on the player's live input state, don't touch velocity mid-input</b>: this
 * class already has, and already trusts (see the jump-gating section above, same {@code
 * getLastClientInput()} call), the ACTUAL per-tick client input state — a real, populated {@code
 * net.minecraft.world.entity.player.Input} record with {@code forward()}/{@code backward()}/
 * {@code left()}/{@code right()}/{@code sprint()} booleans (decompile-confirmed fields on the
 * mapped {@code Input} record; {@code ServerGamePacketListenerImpl.handlePlayerInput}
 * unconditionally stores whatever the client just sent via {@code setLastClientInput}, no
 * substitution/defaulting on that path — same trust basis as the existing jump check). If the
 * player currently has ANY directional key held ({@code forward || backward || left || right}),
 * they are actively trying to move themselves THIS tick — skip sending a horizontal velocity
 * override entirely (matching the exact same "skip the packet, don't try to partially patch it"
 * pattern already used for the jump-key case above, for the identical underlying reason: the
 * server has no correct value to assert here, so asserting anything is worse than asserting
 * nothing) and let the client's own local WASD/sprint physics run completely unimpeded, exactly as
 * it would while standing on solid, non-moving ground. Only when NO directional key is held is a
 * pure horizontal SET-to-platform-delta applied (the original, already-proven-smooth pure
 * ride-along case for a player standing still on the platform) — this is unconditionally safe
 * precisely because "no input" means there's nothing of the player's own to preserve.</p>
 *
 * <p><b>Why this doesn't reintroduce the "chunky, once-a-second" ride-along bug or clip standing
 * riders</b>: the fix only skips the packet on ticks where the player has directional input
 * pressed — the pure standing-still ride-along path (no input) is completely untouched, still
 * fires every tick exactly as before, so a player just standing on the deck (not walking) rides
 * exactly as smoothly as it already did. {@code isStandingOnFootprint} (see {@code
 * ContraptionHitboxSwarm}) is a generous position-window check (±0.3 blocks horizontally, up to
 * 0.9 blocks vertically above the top face) that never depended on this method having fired that
 * same tick — a player actively walking across the deck stays well within that window on their
 * own, so skipping the velocity packet for however many ticks they hold WASD does not un-register
 * them as a rider or cause them to be dropped/released. The moment they release all directional
 * keys, pure ride-along resumes on the very next tick with no special-case transition needed.</p>
 *
 * <p><b>Residual trade-off, honestly stated</b>: while a player is actively walking/sprinting on
 * a MOVING contraption, this tick's platform delta is not imparted to them at all (traded away
 * in exchange for restoring their own full speed) — so a sprinting player crossing a fast-moving
 * platform diagonally will drift very slightly relative to the platform surface compared to a
 * player standing still on it (which still gets the platform's own delta exactly). This is judged
 * an acceptable trade: real contraption speeds in this system are slow (a "very slow" contraption
 * was the specific example driving this bug report), the platform's per-tick delta is small next
 * to normal walk/sprint speed, {@code isStandingOnFootprint}'s tolerance window comfortably absorbs
 * the accumulated drift for any realistically-bounded walk across a deck, and the alternative
 * (attempting to combine platform delta with the player's own live velocity every tick) runs
 * straight back into the "no reliable read of the player's own live velocity" wall documented
 * above and in this file's earlier ADD-vs-SET history — there is no accurate quantity to combine
 * with here, only an approximate one, and approximating in the direction of "preserve the
 * player's own control feel" is the one that matches the bug report's actual ask.</p>
 *
 * <p><b>Investigated and REJECTED: passenger-mounting instead of velocity pushes (2026-07-02
 * session, follow-up — explicit user direction to consider replacing this whole class with a
 * spawned invisible mount entity + real {@code Entity#addPassenger}, the same mechanism
 * CraftEngine's own seats use, reusing {@code ContraptionMath.renderPosition} for per-tick
 * positioning)</b>. Read CraftEngine's actual seat source directly ({@code BukkitSeat.java},
 * {@code BukkitSeatManager.java} under {@code net.momirealms.craftengine.bukkit.entity.seat} in
 * the real source checkout) to confirm the reference mechanism before deciding:
 * <ul>
 *   <li>{@code BukkitSeat.spawnSeatEntityForPlayer} spawns a small/invisible/no-AI/no-gravity
 *       {@code ArmorStand} (or an {@code ItemDisplay} on older versions) at a computed offset and
 *       calls {@code seatEntity.addPassenger(player)} — a real vanilla passenger relationship,
 *       exactly as the task description assumed.</li>
 *   <li>{@code BukkitSeatManager.tryLeavingSeat} dismounts by removing the seat entity and
 *       teleporting the player to the seat entity's last location plus a small FIXED vertical
 *       offset (+0.3875 for armor stands, -0.35 then +0.301 for item displays) — no forward/side
 *       geometry at all, confirming this is a simple "unstick and drop" dismount, not a general
 *       movement mechanism.</li>
 *   <li>Nothing in CraftEngine's own seat code lets the mounted player move independently — there
 *       is no per-tick input-forwarding, no re-positioning-while-mounted logic at all beyond the
 *       one-time spawn placement. This is consistent with seats being an intentional LOCK: sit
 *       down, stay put, dismount to leave.</li>
 * </ul>
 * <p>That is fully consistent with vanilla's own passenger mechanics, which is the actual reason
 * this approach doesn't generalize to "walk freely while being carried":
 * <ul>
 *   <li>Vanilla's {@code Entity.tick()} calls {@code positionRider(passenger, ...)} on every
 *       vehicle with passengers, EVERY tick, unconditionally, for every entity type — this is not
 *       specific to "steerable" vehicles like boats/horses/minecarts. It forcibly snaps every
 *       passenger to a vehicle-relative seat offset every tick, full stop.</li>
 *   <li>The only entities where a passenger's WASD/jump input has any effect at all are ones that
 *       override {@code getControllingPassenger()}/{@code travel()} to explicitly read the
 *       controlling passenger's movement keys and steer THE VEHICLE with them (boats, horses,
 *       pigs, striders, furnace minecarts). An {@code ArmorStand}/{@code ItemDisplay} mount (no
 *       such override — confirmed by the seat source above, which does nothing beyond spawn +
 *       {@code addPassenger}) simply ignores the passenger's input entirely.</li>
 *   <li>Crucially, these are not independent axes: there is no vanilla vehicle type where a
 *       passenger is BOTH mounted (server-position-locked via {@code positionRider}) AND free to
 *       walk with independent client-driven displacement. Being a passenger overrides the
 *       player's own position resolution every tick regardless of whether the vehicle happens to
 *       read their input for steering purposes — the player cannot walk away from the seat offset
 *       at all while mounted, by vanilla design, for any mount entity type.</li>
 * </ul>
 * <p><b>Verdict: mounting is the right tool for CraftEngine's own seats (explicitly a "sit and
 * lock" interaction) but fundamentally unsuitable for THIS class's job</b>, which is exactly the
 * opposite requirement: a rider who walks/sprints/jumps freely across a moving deck. Spawning an
 * invisible mount and calling {@code addPassenger} would not fix any of the velocity-push
 * mechanism's problems documented above — it would instead regress the entire feature by locking
 * every carried player in place at a fixed seat offset, unable to walk at all, the moment they
 * stepped onto a contraption. This was investigated and explicitly rejected; the existing
 * velocity/push mechanism in {@link #carry} below is kept UNCHANGED. If a future contraption
 * feature genuinely wants "sit in place" semantics (e.g. a driver's seat on a vehicle-like
 * contraption), passenger-mounting is exactly the right mechanism for that — see
 * {@code ContraptionSeatListener}/{@code ContraptionInteractPacketDebug} for this codebase's
 * existing (separate) seat implementation — but it must not be applied to the general
 * standing/walking carry case this class handles.</p>
 *
 * <p><b>Vertical lift sink fix — WASD-held gate must not also drop Y (2026-07-02 session,
 * follow-up — "el jugador se hunde mientras viaja en un ascensor vertical", reported specifically
 * for a vertical lift contraption, not a horizontal one)</b>: the walk/sprint-speed fix directly
 * above this section was correct in INTENT but too broad in IMPLEMENTATION. It gated on "any
 * directional key held" and, on a match, skipped sending {@link ClientboundSetEntityMotionPacket}
 * ENTIRELY — X/Z and Y together — via a bare {@code return}. On a horizontal-only contraption this
 * cost nothing ({@code dy} is always {@code 0} there, so dropping a no-op Y alongside X/Z is free).
 * On a contraption that also moves vertically (a lift), this was a real bug: every tick the rider
 * held ANY movement key — which in practice is nearly every tick, since players constantly nudge
 * themselves for position while riding a moving platform — the lift's own upward carry impulse
 * ({@code dy}) was thrown away completely, even though the packet-only shulker floor beneath the
 * rider had already risen that tick. Real gravity keeps acting on the player unopposed for that
 * tick with nothing to counteract it, which reads exactly as "sinking"/lagging behind the platform
 * while riding up and adjusting position at the same time — the dominant real-world case, since
 * standing perfectly still while riding a lift is the unusual case, not the common one.
 * <p>The root confusion: this method conflates two independent concerns under one gate. "Don't
 * stomp the player's own X/Z control while they're actively driving it via WASD" (the actual,
 * correct intent of the walk/sprint fix) has nothing to do with "don't ever carry the platform's
 * own vertical motion just because X/Z is being left alone this tick." Holding a directional key
 * says nothing about the player's vertical velocity being locally predicted/unreliable the way an
 * in-flight jump arc is (see the jump-gating decompile sections above) — the platform's {@code dy}
 * is exactly as trustworthy to add on a WASD-held tick as on a no-input tick. The jump-key gate
 * above this one is a genuinely different case and is deliberately left completely unchanged by
 * this fix: while {@code input.jump()} is true, the server truly has no reliable read on the
 * client's in-flight jump arc, so skipping the whole packet (Y included) there remains correct.
 * This fix is scoped ONLY to the WASD/directional-input gate, not the jump gate.
 * <p><b>The fix</b>: split "should X/Z be overridden" from "should Y be carried" into two
 * independent checks instead of one shared early-return. {@code applyY = dy != 0.0} now decides Y
 * on its own, exactly as before. The WASD-held check now only causes an early {@code return}
 * (skip the packet entirely, preserving the exact original behavior/shape) when directional input
 * is held AND {@code applyY} is false — i.e. only in the case that was already a no-op for Y, so
 * nothing observable changes for a horizontal-only contraption. Whenever {@code applyY} is true
 * (platform genuinely moving vertically this tick) the method now always proceeds to build and
 * send the packet, but chooses X/Z per the same directional-input flag: if WASD is held, X/Z are
 * set to {@code current.x}/{@code current.z} (a pure echo, the same "echo, don't assert" pattern
 * already established here for Y during the jump-gate case) instead of the platform's {@code dx}/
 * {@code dz}, so the packet we're now forced to send in order to carry Y doesn't also stomp the
 * player's own in-flight WASD/sprint horizontal velocity. If no directional input is held, X/Z
 * still get the platform's {@code dx}/{@code dz} exactly as before (the plain ride-along case).
 * Y itself is computed exactly as it always was — {@code current.y + dy} when {@code applyY}, else
 * a pure echo of {@code current.y} — completely unaffected by whether WASD is held, which is the
 * whole point of this fix: Y carry is no longer collateral damage of the X/Z WASD-preservation
 * gate. {@link ClientboundSetEntityMotionPacket} remains atomic/all-axes-at-once (no partial-axis
 * variant exists — see this class's own earlier decompile notes on this), so achieving "override
 * Y but not X/Z" still means sending a full 3-component packet with X/Z values deliberately chosen
 * to be no-ops, mirroring exactly how the jump-gate case already handles "must send some Y value,
 * make sure it's a no-op" for its own axis.</p>
 */
public final class PlayerCarry {

    private PlayerCarry() {
    }

    /**
     * Call once per server tick per carried player with THIS tick's platform movement
     * (world-space blocks: how far the platform itself moved since last tick).
     *
     * <p><b>X/Z: SET, not ADD</b> (2026-07-02 session — "reduce el delta movement... siempre
     * termina empujandome mas de lo que debe"). Real {@link ServerPlayer} velocity is
     * client-authoritative: after we send a horizontal impulse, the client decays it over SEVERAL
     * ticks via its own ground/air drag (vanilla ~0.6-0.91 per tick, never instant), not in one
     * shot. Blindly ADDING every tick's fresh delta on top of that still-decaying leftover from
     * the PREVIOUS tick compounds — each new push lands on top of momentum that hasn't fully
     * bled off yet, so the player visibly overshoots and drifts faster than the platform itself
     * is actually moving. Since the horizontal component here is meant to be a pure "ride along"
     * (not a real throw/knockback the player should keep coasting on), we instead SET X/Z to
     * exactly this tick's platform delta every time — no residual to compound, no overshoot,
     * still framed as "add a delta" in spirit (never an absolute WORLD position) but without
     * inheriting whatever fraction of last tick's impulse the client hadn't finished draining.
     * Y is still ADDED (not set) so falling/jumping arcs while riding stay physically continuous
     * — replacing vertical velocity outright would clip a jump mid-air or cancel gravity.
     *
     * <p><b>Y must never be MODIFIED when {@code dy == 0}</b> (2026-07-02 session — "no se puede
     * correr bien... y saltar te jala para abajo"). A real {@link ServerPlayer}'s vertical
     * velocity is client-predicted: the client integrates its own jump/fall arc locally, and the
     * server only learns the RESULT via {@code ServerboundMovePlayerPacket} — the server's own
     * {@code getDeltaMovement().y} lags a tick behind and is not a faithful mirror of an
     * in-progress jump. {@link ClientboundSetEntityMotionPacket} is unavoidably a full X/Y/Z
     * vector (there is no partial-axis variant), so whenever this method needs to push an X/Z
     * ride-along impulse it necessarily re-transmits SOME Y value too — the fix is to make sure
     * that value is always exactly {@code current.y} (a pure echo of whatever the server already
     * had) and NEVER {@code current.y + dy} when {@code dy == 0}. Before this fix the code did
     * {@code current.y + dy} unconditionally, which is mathematically a no-op (+0), but the
     * surrounding intent was wrong: it re-armed a "we are touching Y" packet every single tick a
     * player stood on ANY horizontal-only contraption, which is what let a stale/lagged Y sneak
     * back in and fight the client's own locally-predicted jump ("pulled back down"). Only when
     * the contraption is ACTUALLY moving vertically this tick ({@code dy != 0}) does Y get a real
     * additive impulse; otherwise it passes through untouched.
     */
    public static void carry(ServerPlayer player, double dx, double dy, double dz) {
        // Anti-fly-kick suppression (2026-07-02 session — see class javadoc's "SUPERSEDING root
        // cause found" section for the full decompile-backed mechanism): a carried rider stands on
        // a packet-only floor with nothing real beneath them in the actual ServerLevel, so vanilla/
        // Paper's own handleMovePlayer#noBlocksAround check sets clientIsFloating=true essentially
        // every tick, and ServerGamePacketListenerImpl#tick's aboveGroundTickCount climbs toward a
        // FLYING_PLAYER kick regardless of whether dx/dy/dz are zero this tick (a STALLED
        // contraption's rider is standing over the same fake floor as a moving one) or whether the
        // player is currently jumping (jump/land cycling doesn't stop noBlocksAround from being
        // true). This call MUST happen unconditionally, every tick this method is invoked for a
        // rider — placed before the early-return below and before the jump-gating skip further
        // down, since both of those historically skipped the rest of this method for exactly the
        // ticks this reset is most needed. resetFlyingTicks() is public, no reflection needed —
        // decompile-confirmed to only zero aboveGroundTickCount/aboveGroundVehicleTickCount, so it
        // never touches velocity/position and can't interact with anything else this method does.
        player.connection.resetFlyingTicks();

        if (dx == 0.0 && dy == 0.0 && dz == 0.0) {
            return;
        }
        // Real per-tick "is the jump key currently held" signal (ServerboundPlayerInputPacket →
        // ServerPlayer#setLastClientInput, see this class's javadoc for the decompile proving this
        // is the only genuine server-side jump signal that exists for a real player — there is no
        // server-side jumpFromGround() call to hook for a human, ever). getLastClientInput() can be
        // null very early (before the client has sent its first input packet this connection), so
        // treat "no data yet" as "not jumping" rather than risk an NPE gating every single carry.
        net.minecraft.world.entity.player.Input input = player.getLastClientInput();
        boolean jumping = input != null && input.jump();

        // Skip the packet ENTIRELY while jumping (2026-07-02 session, follow-up fix — see class
        // javadoc's "Skip the packet entirely while jumping" section for the full decompile-backed
        // reasoning): a Y-only echo/gate was tried first and was still wrong, because
        // ClientboundSetEntityMotionPacket is an atomic full-vector packet — there's no way to
        // touch X/Z without ALSO re-asserting some Y value as the client's new authoritative
        // velocity, and current.y is never a faithful mirror of a live client-predicted jump arc.
        // The only way to guarantee zero interference with the client's own jump physics is to not
        // send this entity's motion at all for the ticks the jump key is held — X/Z ride-along is
        // sacrificed for those few ticks only; the very next non-jumping tick resumes carrying
        // normally against the platform's current position (deltas here are never
        // accumulated/queued, so nothing is lost by skipping a tick).
        if (jumping) {
            return;
        }

        // Walk/sprint-speed fix (2026-07-02 session — see class javadoc's "Walk/sprint speed
        // crawls to the platform's own creep speed while riding" section for the full
        // decompile-backed reasoning): if the player currently has ANY directional key held, they
        // are actively driving their own movement this tick via the client's local WASD/sprint
        // physics. This method has no correct value to combine with that — the server never
        // observes the player's own live velocity for a controlled ServerPlayer (same "current is
        // just whatever we last stamped" finding as the jump section above) — so DON'T override
        // X/Z at all and let the client's own movement run unimpeded, exactly as it would on
        // ordinary solid ground. Only the X/Z axes are gated here: this is strictly about not
        // stomping horizontal WASD/sprint speed, and does not change the separate jump-key
        // handling above (which already returns before this point).
        //
        // <p><b>Vertical-lift sink fix (2026-07-02 session, follow-up — "el jugador se hunde
        // mientras viaja en un ascensor vertical")</b>: this gate used to be a bare {@code return},
        // skipping the ENTIRE packet — X/Z AND Y together — whenever any directional key was held.
        // That was fine for a horizontal-only contraption (dy is always 0 there, so dropping Y
        // alongside X/Z cost nothing) but silently broke vertical lifts: a lift's own upward
        // carry (dy != 0) was thrown away on every tick the rider so much as tapped W/A/S/D while
        // riding up — which is most ticks, since players constantly nudge themselves while
        // standing on a moving platform — leaving real gravity to act on the player unopposed
        // that tick while the shulker floor had already risen out from under them. The two
        // concerns this gate conflates are actually independent: "don't stomp the player's own
        // X/Z control while they're driving it" has nothing to do with "don't ever carry the
        // platform's own vertical motion." The jump-key gate above is DELIBERATELY left
        // untouched by this fix — while jumping, the server has no reliable view of the client's
        // in-flight jump arc at all (see the jump-physics decompile sections above), so skipping
        // Y too in that specific case remains correct and is NOT the case being changed here.
        // The WASD case is different: holding a directional key says nothing about the player's
        // vertical velocity being locally predicted/untrustworthy the way a jump arc is — the
        // platform's own dy is exactly as reliable on a WASD-held tick as on a no-input tick.
        boolean hasDirectionalInput = input != null
                && (input.forward() || input.backward() || input.left() || input.right());
        boolean applyY = dy != 0.0;

        if (hasDirectionalInput && !applyY) {
            // Pure horizontal contraption + WASD held: nothing here needs to touch Y anyway, so
            // preserve the original "skip the whole packet" shape for this case exactly as before.
            return;
        }

        // BACK to velocity-based push (2026-07-02 session, reverted AGAIN — "que NO nada de
        // teleport, vuelve al empujon, estaba bien"): the teleport-transport swap above was an
        // unrequested "fix" for a symptom that traced back to something else entirely — explicit
        // user instruction is to keep the velocity/push mechanism, not replace it. X/Z: SET (not
        // ADD) to exactly this tick's delta — avoids the overshoot-from-residual-drag bug fixed
        // earlier — UNLESS the player has directional input held (see above), in which case X/Z
        // are echoed back as `current.x`/`current.z` instead (mirrors the exact same "echo,
        // don't assert" pattern this class already uses for Y during the jump-gate case) so the
        // packet we're now forced to send (because dy != 0) doesn't stomp the player's own
        // in-flight WASD/sprint velocity — it only ever needs to change Y. Y: left completely
        // untouched (echoed from `current.y`) whenever the platform has no vertical motion this
        // tick (jump-in-flight case is handled above by skipping the whole packet, not by echoing
        // Y) — only a genuinely-vertical platform motion gets an additive Y impulse, regardless of
        // whether WASD is held.
        Vec3 current = player.getDeltaMovement();
        double outX = hasDirectionalInput ? current.x : dx;
        double outZ = hasDirectionalInput ? current.z : dz;
        // Y: SET to the platform's own vertical velocity, do NOT add it to the player's (2026-07-17 — "al
        // saltar en un contraption ... la caida como que la gravedad es altisima y caigo re rapido"). A
        // rider standing on a platform is SUPPORTED by it — their vertical motion IS the platform's, so
        // gravity must not stack on top. The old `current.y + dy` added the player's own gravity-fall
        // (already in current.y) to a DESCENDING platform's fall, so a rider on anything sinking dropped at
        // roughly double gravity. Setting it makes the rider track the platform exactly, up or down; jumps
        // are unaffected (this whole block is skipped while the jump key is held, above).
        Vec3 combined = new Vec3(outX, applyY ? dy : current.y, outZ);
        player.setDeltaMovement(combined);
        player.connection.send(new ClientboundSetEntityMotionPacket(player));
    }

    /** Stop carrying a player (dismounted the platform, spike stopped, etc) — zeroes any residual carry impulse. */
    public static void release(UUID playerId) {
        org.bukkit.entity.Player bukkit = org.bukkit.Bukkit.getPlayer(playerId);
        if (bukkit == null) {
            return;
        }
        ServerPlayer player = ((org.bukkit.craftbukkit.entity.CraftPlayer) bukkit).getHandle();
        player.setDeltaMovement(Vec3.ZERO);
        player.connection.send(new ClientboundSetEntityMotionPacket(player));
    }
}
