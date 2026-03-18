package net.noahsarch.derggycraft.mixin.client;

import net.minecraft.client.network.ClientNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.BufferedReader;
import java.io.IOException;

@Mixin(ClientNetworkHandler.class)
public class ClientNetworkHandlerHandshakeMixin {
    @Redirect(
        method = "onHandshake",
        at = @At(value = "INVOKE", target = "Ljava/io/BufferedReader;readLine()Ljava/lang/String;")
    )
    private String derggycraft$normalizeJoinServerResponse(BufferedReader reader) throws IOException {
        String response = reader.readLine();
        return response != null ? response : "Session server returned no response";
    }

    @Redirect(
        method = "onHandshake",
        at = @At(value = "INVOKE", target = "Ljava/lang/String;equalsIgnoreCase(Ljava/lang/String;)Z")
    )
    private boolean derggycraft$nullSafeJoinServerResponseCheck(String response, String expected) {
        return response != null && response.equalsIgnoreCase(expected);
    }
}
