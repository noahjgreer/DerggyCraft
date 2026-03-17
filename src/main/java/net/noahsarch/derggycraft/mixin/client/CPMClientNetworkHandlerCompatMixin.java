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
        try {
            SocketAddress address = this.derggycraft$resolveSocketAddress();
            if (!(address instanceof InetSocketAddress inetSocketAddress)) {
                cir.setReturnValue(null);
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
                cir.setReturnValue(null);
                return;
            }

            if ("localhost".equalsIgnoreCase(host) || "127.0.0.1".equals(host) || "::1".equals(host)) {
                cir.setReturnValue(null);
                return;
            }

            cir.setReturnValue(host);
        } catch (Throwable ignored) {
            cir.setReturnValue(null);
        }
    }

    private SocketAddress derggycraft$resolveSocketAddress() {
        if (this.connection == null) {
            return null;
        }

        SocketAddress modern = this.derggycraft$invokeSocketAddressAccessor("getAddress");
        if (modern != null) {
            return modern;
        }

        SocketAddress legacy = this.derggycraft$invokeSocketAddressAccessor("method_1131");
        if (legacy != null) {
            return legacy;
        }

        return null;
    }

    private SocketAddress derggycraft$invokeSocketAddressAccessor(String methodName) {
        try {
            Method method = this.connection.getClass().getMethod(methodName);
            Object value = method.invoke(this.connection);
            if (value instanceof SocketAddress socketAddress) {
                return socketAddress;
            }
        } catch (Throwable ignored) {
        }
        return null;
    }
}