package com.oskarkraak.gulag.mixin;

import com.oskarkraak.gulag.event.ServerWorldEvents;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {

    @Inject(method = "saveLevel", at = @At("TAIL"))
    private void afterSaveLevel(CallbackInfo ci) {
        ServerWorld world = (ServerWorld) (Object) this;
        ServerWorldEvents.LEVEL_SAVED.invoker().onLevelSaved(world);
    }

}
