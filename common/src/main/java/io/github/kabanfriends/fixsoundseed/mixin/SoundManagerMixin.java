package io.github.kabanfriends.fixsoundseed.mixin;

import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundEventRegistration;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.client.sounds.Weighted;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.MultipliedFloats;
import net.minecraft.util.valueproviders.SampledFloat;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(SoundManager.Preparations.class)
public class SoundManagerMixin {

    private static Map<WeighedSoundEvents, List<String>> resourceListMap;

    @Shadow @Final Map<ResourceLocation, WeighedSoundEvents> registry;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void initOverride(CallbackInfo ci) {
        resourceListMap = new HashMap<>();
    }

    @Inject(method = "handleRegistration", at = @At("HEAD"), cancellable = true)
    private void registerOverride(ResourceLocation resourceLocation, SoundEventRegistration soundEventRegistration, ResourceManager resourceManager, CallbackInfo ci) {
        WeighedSoundEvents weighedSoundEvents = this.registry.get(resourceLocation);
        boolean isNull = weighedSoundEvents == null;
        if (isNull || soundEventRegistration.isReplace()) {
            if (!isNull) {
                SoundManagerAccessor.getLogger().debug("Replaced sound event location {}", resourceLocation);
            }

            weighedSoundEvents = new WeighedSoundEvents(resourceLocation, soundEventRegistration.getSubtitle());
            this.registry.put(resourceLocation, weighedSoundEvents);
        }

        Iterator var6 = soundEventRegistration.getSounds().iterator();

        while(var6.hasNext()) {
            final Sound sound = (Sound)var6.next();
            final ResourceLocation resourceLocation2 = sound.getLocation();

            List<String> list = resourceListMap.computeIfAbsent(weighedSoundEvents, k -> new ArrayList<>());

            String key = resourceLocation2.toString();
            if (list.contains(key)) {

            }

            if (!list.contains(key)) {
                list.add(key);

                Object weighted;
                switch(sound.getType()) {
                    case FILE:
                        if (!SoundManagerAccessor.validateSoundResource(sound, resourceLocation, resourceManager)) {
                            continue;
                        }

                        weighted = sound;
                        break;
                    case SOUND_EVENT:
                        SoundManagerMixin instance = this;

                        weighted = new Weighted<Sound>() {
                            public int getWeight() {
                                WeighedSoundEvents weighedSoundEvents = instance.registry.get(resourceLocation2);
                                return weighedSoundEvents == null ? 0 : weighedSoundEvents.getWeight();
                            }

                            public Sound getSound(RandomSource randomSource) {
                                WeighedSoundEvents weighedSoundEvents = instance.registry.get(resourceLocation2);
                                if (weighedSoundEvents == null) {
                                    return SoundManager.EMPTY_SOUND;
                                } else {
                                    Sound soundx = weighedSoundEvents.getSound(randomSource);
                                    return new Sound(soundx.getLocation().toString(), new MultipliedFloats(new SampledFloat[]{soundx.getVolume(), sound.getVolume()}), new MultipliedFloats(new SampledFloat[]{soundx.getPitch(), sound.getPitch()}), sound.getWeight(), Sound.Type.FILE, soundx.shouldStream() || sound.shouldStream(), soundx.shouldPreload(), soundx.getAttenuationDistance());
                                }
                            }

                            public void preloadIfRequired(SoundEngine soundEngine) {
                                WeighedSoundEvents weighedSoundEvents = instance.registry.get(resourceLocation2);
                                if (weighedSoundEvents != null) {
                                    weighedSoundEvents.preloadIfRequired(soundEngine);
                                }
                            }
                        };
                        break;
                    default:
                        throw new IllegalStateException("Unknown SoundEventRegistration type: " + sound.getType());
                }

                weighedSoundEvents.addSound((Weighted)weighted);
            }
        }

        ci.cancel();
    }
}