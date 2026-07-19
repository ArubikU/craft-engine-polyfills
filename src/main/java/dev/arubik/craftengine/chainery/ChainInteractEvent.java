package dev.arubik.craftengine.chainery;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.joml.Vector3d;

/**
 * Fired when a player clicks a rendered chain (CHAINERY — "agrega interaction entity para permitir interactuar
 * con chains ... incluye el Chain Line ... para que alguien quiera hacer cosas raras como una tirolesa").
 * Carries the whole chain "line": its id, endpoints, live rope points, material characteristics (max blocks,
 * stretch, max tension, pull) and the segment/point that was clicked — everything a downstream plugin needs to
 * build a zipline, attach more chains, read tension, etc. Detection is packet-based (see
 * {@code ChainInteractPacketListener}); this event is dispatched on the main thread.
 */
public class ChainInteractEvent extends Event {

    public enum Action {
        LEFT_CLICK, RIGHT_CLICK
    }

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final Chain chain;
    private final Action action;
    private final int segment;
    private final Vector3d point;

    public ChainInteractEvent(Player player, Chain chain, Action action, int segment, Vector3d point) {
        this.player = player;
        this.chain = chain;
        this.action = action;
        this.segment = segment;
        this.point = point;
    }

    public Player getPlayer() {
        return player;
    }

    /** The chain that was clicked — full access to its endpoints, material and live rope. */
    public Chain getChain() {
        return chain;
    }

    public Action getAction() {
        return action;
    }

    /** Index of the clicked link/segment along the chain (0 = at endpoint A). */
    public int getSegment() {
        return segment;
    }

    /** Approximate world point of the click (the clicked link's centre). */
    public Vector3d getPoint() {
        return new Vector3d(point);
    }

    // ---- convenience "Chain Line" accessors (also all reachable via getChain()) ----

    /** The live rope points in world space, endpoint A → B (the sagging line the player sees). */
    public List<Vector3d> getLine() {
        List<Vector3d> pts = new ArrayList<>();
        int n = chain.rope.particleCount();
        for (int i = 0; i < n; i++) {
            pts.add(new Vector3d(chain.rope.particle(i)));
        }
        return pts;
    }

    /** Current straight-line tension proxy: how far the endpoints are stretched past the chain's length
     *  (0 when slack). A zipline could ride only when this is low, snap logic could watch it, etc. */
    public double getTension() {
        int n = chain.rope.particleCount();
        if (n < 2) {
            return 0.0;
        }
        double dist = chain.rope.particle(0).distance(chain.rope.particle(n - 1));
        double span = chain.blocks * (1.0 + chain.material.stretch());
        return Math.max(0.0, dist - span);
    }

    public ChainMaterial getMaterial() {
        return chain.material;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
