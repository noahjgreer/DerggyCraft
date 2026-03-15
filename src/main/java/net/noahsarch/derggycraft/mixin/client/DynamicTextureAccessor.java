package net.noahsarch.derggycraft.mixin.client;

import net.minecraft.client.render.texture.DynamicTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DynamicTexture.class)
public interface DynamicTextureAccessor {
    @Accessor("pixels")
    byte[] derggycraft$getPixels();

    @Accessor("anaglyph")
    boolean derggycraft$isAnaglyph();
}