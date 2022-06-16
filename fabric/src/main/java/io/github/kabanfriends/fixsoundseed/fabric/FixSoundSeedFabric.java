package io.github.kabanfriends.fixsoundseed.fabric;

import io.github.kabanfriends.fixsoundseed.FixSoundSeed;
import net.fabricmc.api.ModInitializer;

public class FixSoundSeedFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        FixSoundSeed.init();
    }
}
