package dev.arubik.craftengine.machine.render.formula;

import net.minecraft.server.level.ServerPlayer;
import java.util.List;

public final class PlayerClass extends PolyValue.Obj {
    private final ServerPlayer player;

    public PlayerClass(ServerPlayer player) {
        this.player = player;
    }
    public ServerPlayer player() {
        return this.player;
    }

    @Override
    public PolyValue get(String property) {
        if (player == null) return PolyValue.NULL;
        return switch (property) {
            case "name" -> PolyValue.of(player.getGameProfile().name());
            case "x" -> PolyValue.of(player.getX());
            case "y" -> PolyValue.of(player.getY());
            case "z" -> PolyValue.of(player.getZ());
            case "yaw" -> PolyValue.of(player.getYRot());
            case "pitch" -> PolyValue.of(player.getXRot());
            case "health" -> PolyValue.of(player.getHealth());
            case "is_sneaking" -> PolyValue.of(player.isShiftKeyDown());
            case "is_sprinting" -> PolyValue.of(player.isSprinting());
            case "gamemode" -> PolyValue.of(player.gameMode.getGameModeForPlayer().getName());
            case "food_level" -> PolyValue.of(player.getFoodData().getFoodLevel());
            default -> PolyValue.NULL;
        };
    }

    @Override
    public PolyValue call(String method, List<PolyValue> args) {
        return PolyValue.NULL;
    }
}
