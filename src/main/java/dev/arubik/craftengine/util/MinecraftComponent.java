package dev.arubik.craftengine.util;

import java.util.List;

import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecorationAndState;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.network.chat.Component;

public class MinecraftComponent {
    public net.kyori.adventure.text.Component kyoriComponent;
    public Component minecraftComponent;

    public MinecraftComponent(net.kyori.adventure.text.Component kyoriComponent, Component minecraftComponent) {
        this.kyoriComponent = kyoriComponent;
        this.minecraftComponent = minecraftComponent;
    }

    public static MinecraftComponent fromKyori(net.kyori.adventure.text.Component kyoriComponent) {
        return new MinecraftComponent(kyoriComponent, PaperAdventure.asVanilla(kyoriComponent));
    }

    public static MinecraftComponent fromMinecraft(Component minecraftComponent) {
        return new MinecraftComponent(PaperAdventure.asAdventure(minecraftComponent), minecraftComponent);
    }

    public MinecraftComponent append(MinecraftComponent other) {
        net.kyori.adventure.text.Component newKyoriComponent = this.kyoriComponent.append(other.kyoriComponent);
        Component newMinecraftComponent = this.minecraftComponent.copy().append(other.minecraftComponent);
        return new MinecraftComponent(newKyoriComponent, newMinecraftComponent);
    }

    public MinecraftComponent append(net.kyori.adventure.text.Component other) {
        net.kyori.adventure.text.Component newKyoriComponent = this.kyoriComponent.append(other);
        Component newMinecraftComponent = this.minecraftComponent.copy()
                .append(PaperAdventure.asVanilla(other));
        return new MinecraftComponent(newKyoriComponent, newMinecraftComponent);
    }

    public MinecraftComponent append(Component other) {
        net.kyori.adventure.text.Component newKyoriComponent = this.kyoriComponent
                .append(PaperAdventure.asAdventure(other));
        Component newMinecraftComponent = this.minecraftComponent.copy().append(other);
        return new MinecraftComponent(newKyoriComponent, newMinecraftComponent);
    }

    public MinecraftComponent color(net.kyori.adventure.text.format.TextColor color) {
        net.kyori.adventure.text.Component newKyoriComponent = this.kyoriComponent.color(color);
        Component newMinecraftComponent = this.minecraftComponent.copy()
                .withStyle(style -> style.withColor(color.value()));
        return new MinecraftComponent(newKyoriComponent, newMinecraftComponent);
    }

    public MinecraftComponent decorate(net.kyori.adventure.text.format.TextDecoration decoration) {
        net.kyori.adventure.text.Component newKyoriComponent = this.kyoriComponent.decorate(decoration);
        Component newMinecraftComponent = this.minecraftComponent.copy()
                .withStyle(style -> style
                        .withUnderlined(decoration.compareTo(TextDecoration.BOLD) == 0 ? Boolean.TRUE : null)
                        .withBold(decoration.compareTo(TextDecoration.BOLD) == 0 ? Boolean.TRUE : null)
                        .withItalic(decoration.compareTo(TextDecoration.ITALIC) == 0 ? Boolean.TRUE : null)
                        .withStrikethrough(
                                decoration.compareTo(TextDecoration.STRIKETHROUGH) == 0 ? Boolean.TRUE : null)
                        .withObfuscated(decoration.compareTo(TextDecoration.OBFUSCATED) == 0 ? Boolean.TRUE : null));
        return new MinecraftComponent(newKyoriComponent, newMinecraftComponent);
    }

    public MinecraftComponent decorate(TextDecorationAndState decorationAndState) {
        net.kyori.adventure.text.Component newKyoriComponent = this.kyoriComponent
                .decoration(decorationAndState.decoration(),decorationAndState.state());
        Component newMinecraftComponent = this.minecraftComponent.copy()
                .withStyle(style -> style
                        .withUnderlined(decorationAndState.decoration().compareTo(TextDecoration.UNDERLINED) == 0
                                ? (decorationAndState.state().equals(TextDecoration.State.TRUE) ? Boolean.TRUE : Boolean.FALSE)
                                : null)
                        .withBold(decorationAndState.decoration().compareTo(TextDecoration.BOLD) == 0
                                ? (decorationAndState.state().equals(TextDecoration.State.TRUE) ? Boolean.TRUE : Boolean.FALSE)
                                : null)
                        .withItalic(decorationAndState.decoration().compareTo(TextDecoration.ITALIC) == 0
                                ? (decorationAndState.state().equals(TextDecoration.State.TRUE) ? Boolean.TRUE : Boolean.FALSE)
                                : null)
                        .withStrikethrough(
                                decorationAndState.decoration().compareTo(TextDecoration.STRIKETHROUGH) == 0
                                        ? (decorationAndState.state().equals(TextDecoration.State.TRUE) ? Boolean.TRUE : Boolean.FALSE)
                                        : null)
                        .withObfuscated(decorationAndState.decoration().compareTo(TextDecoration.OBFUSCATED) == 0
                                ? (decorationAndState.state().equals(TextDecoration.State.TRUE) ? Boolean.TRUE : Boolean.FALSE)
                                : null));
        return new MinecraftComponent(newKyoriComponent, newMinecraftComponent);
    }

    public static MinecraftComponent empty() {
        return new MinecraftComponent(net.kyori.adventure.text.Component.empty(), Component.empty());
    }

    public static MinecraftComponent space() {
        return new MinecraftComponent(net.kyori.adventure.text.Component.text(" "), Component.literal(" "));
    }

    public static MinecraftComponent fromString(String text) {
        return fromKyori(MiniMessage.miniMessage().deserialize(text));
    }

    public static MinecraftComponent fromAny(Object obj) {
        if (obj instanceof MinecraftComponent) {
            return (MinecraftComponent) obj;
        } else if (obj instanceof net.kyori.adventure.text.Component) {
            return fromKyori((net.kyori.adventure.text.Component) obj);
        } else if (obj instanceof Component) {
            return fromMinecraft((Component) obj);
        } else if (obj instanceof String) {
            return fromString((String) obj);
        } else {
            throw new IllegalArgumentException("Cannot convert object to MinecraftComponent: " + obj);
        }
    }

    public Component toMinecraft() {
        return this.minecraftComponent;
    }
    public net.kyori.adventure.text.Component toKyori() {
        return this.kyoriComponent;
    }
    public String toString() {
        return MiniMessage.miniMessage().serialize(this.kyoriComponent);
    }

    public static List<MinecraftComponent> fromAnyList(List<?> list) {
        return list.stream().map(MinecraftComponent::fromAny).toList();
    }
}
