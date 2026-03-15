package net.noahsarch.derggycraft.mixin;

import net.minecraft.network.packet.play.ChatMessagePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ChatMessagePacket.class)
public abstract class ChatMessagePacketMixin {
    private static final int DERGGYCRAFT_MAX_CHAT_LENGTH = 32767;

    @ModifyConstant(method = "<init>(Ljava/lang/String;)V", constant = @Constant(intValue = 119), require = 0)
    private int derggycraft$expandConstructorChatLimit(int original) {
        return DERGGYCRAFT_MAX_CHAT_LENGTH;
    }

    @ModifyConstant(method = "read(Ljava/io/DataInputStream;)V", constant = @Constant(intValue = 119), require = 0)
    private int derggycraft$expandPacketReadChatLimit(int original) {
        return DERGGYCRAFT_MAX_CHAT_LENGTH;
    }
}
