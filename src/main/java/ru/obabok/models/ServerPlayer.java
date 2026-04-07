package ru.obabok.models;

import net.minecraft.network.chat.Component;

public class ServerPlayer {
    private final Object handle; // Здесь будет лежать настоящий игрок (Object, чтобы не светить импорты)

    public ServerPlayer(Object handle) {
        this.handle = handle;
    }

    public void sendMessage(String text) {
        // Здесь мы превращаем строку в Minecraft-текст и отправляем реальному игроку
        if (handle instanceof net.minecraft.world.entity.player.Player player) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal(text));
            //player.sendSystemMessage(Component.literal(text));
        }
    }

    public String getName() {
        if (handle instanceof net.minecraft.world.entity.player.Player player) {
            //return player.getName().getString();
        }
        return "Unknown";
    }
}
