package ru.obabok.mixins;

import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.obabok.event.EventManager;
import ru.obabok.event.events.PlayerJoinEvent;
import ru.obabok.models.Server;

@Mixin(PlayerList.class)
public class PlayerListMixin {
    @Inject(method = "placeNewPlayer", at = @At("TAIL"))
    private void placeNewPlayer(Connection connection, ServerPlayer player, CommonListenerCookie cookie, CallbackInfo ci){
        ru.obabok.models.ServerPlayer serverPlayer = new ru.obabok.models.ServerPlayer(player);
        Server server = new Server();
        PlayerJoinEvent event = new PlayerJoinEvent(serverPlayer, server);
        EventManager.post(event);
    }
}
