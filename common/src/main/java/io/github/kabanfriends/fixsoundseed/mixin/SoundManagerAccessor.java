package io.github.kabanfriends.fixsoundseed.mixin;

import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SoundManager.class)
public interface SoundManagerAccessor {

    @Invoker("validateSoundResource")
    static boolean validateSoundResource(Sound sound, ResourceLocation resourceLocation, ResourceManager resourceManager) {
        throw new AssertionError();
    }

    @Accessor("LOGGER")
    static Logger getLogger() {
        throw new AssertionError();
    }
}
