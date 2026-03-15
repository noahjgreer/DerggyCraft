package net.noahsarch.derggycraft.mixin.client;

import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DrawContext.class)
public interface DrawContextInvoker {
    @Invoker("fillGradient")
    void derggycraft$invokeFillGradient(int startX, int startY, int endX, int endY, int colorStart, int colorEnd);
}
