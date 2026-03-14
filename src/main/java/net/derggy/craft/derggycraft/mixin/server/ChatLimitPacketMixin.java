package net.derggy.craft.derggycraft.mixin.server;

import net.minecraft.network.packet.play.ChatMessagePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Extends the chat message character limit from 119 to 32768.
 */
@Mixin(ChatMessagePacket.class)
public class ChatLimitPacketMixin {

    @ModifyConstant(method = "<init>(Ljava/lang/String;)V", constant = @Constant(intValue = 119))
    private int extendConstructorLimit(int original) {
        return 32768;
    }

    @ModifyConstant(method = "read", constant = @Constant(intValue = 119))
    private int extendReadLimit(int original) {
        return 32768;
    }
}
