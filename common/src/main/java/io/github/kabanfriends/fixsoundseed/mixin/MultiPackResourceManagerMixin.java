package io.github.kabanfriends.fixsoundseed.mixin;

import net.minecraft.server.packs.resources.MultiPackResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Function;
import java.util.stream.Stream;

@Mixin(MultiPackResourceManager.class)
public class MultiPackResourceManagerMixin {

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;flatMap(Ljava/util/function/Function;)Ljava/util/stream/Stream;"))
    private <R> Stream overrideFlatMap(Stream instance, Function<?, ? extends Stream<? extends R>> function) {
        return instance.flatMap(function).distinct();
    }
}