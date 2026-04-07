package ru.obabok.event.events;

import ru.obabok.models.Server;
import ru.obabok.models.ServerPlayer;

public class PlayerJoinEvent {
    private final ServerPlayer player;
    private final Server server;

    public PlayerJoinEvent(ServerPlayer player, Server server) {
        this.player = player;
        this.server = server;
    }

    public ServerPlayer getPlayer() { return player; }
    public Server getServer() { return server; }
}
