package net.noahsarch.derggycraft.mixin.client;

import net.minecraft.client.network.ClientNetworkHandler;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

@Mixin(ClientNetworkHandler.class)
public abstract class CPMClientNetworkHandlerCompatMixin {
    @Shadow
    private Connection connection;

    @Inject(method = "cpm$getConnectedServer", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void derggycraft$fixCpmConnectedServerLookup(CallbackInfoReturnable<String> cir) {
        SocketAddress address = this.derggycraft$resolveSocketAddress();
        if (!(address instanceof InetSocketAddress inetSocketAddress)) {
            return;
        }

        InetAddress inetAddress = inetSocketAddress.getAddress();
        if (inetAddress != null && (inetAddress.isAnyLocalAddress() || inetAddress.isLoopbackAddress())) {
            cir.setReturnValue(null);
            return;
        }

        String host = inetSocketAddress.getHostString();
        if (host == null || host.isEmpty()) {
            host = inetSocketAddress.getHostName();
        }

        if (host == null || host.isEmpty()) {
            return;
        }

        if ("localhost".equalsIgnoreCase(host) || "127.0.0.1".equals(host) || "::1".equals(host)) {
            cir.setReturnValue(null);
            return;
        }

        cir.setReturnValue(host);
    }

    private SocketAddress derggycraft$resolveSocketAddress() {
        if (this.connection == null) {
            return null;
        }

        try {
            return this.connection.getAddress();
        } catch (Throwable ignored) {
        }

        try {
            Method legacy = this.connection.getClass().getMethod("method_1131");
            Object value = legacy.invoke(this.connection);
            if (value instanceof SocketAddress socketAddress) {
                return socketAddress;
            }
        } catch (Throwable ignored) {
        }

        for (Method method : this.connection.getClass().getMethods()) {
            if (method.getParameterCount() != 0 || !SocketAddress.class.isAssignableFrom(method.getReturnType())) {
                continue;
            }

            try {
                Object value = method.invoke(this.connection);
                if (value instanceof SocketAddress socketAddress) {
                    return socketAddress;
                }
            } catch (Throwable ignored) {
            }
        }

        return null;
    }
}