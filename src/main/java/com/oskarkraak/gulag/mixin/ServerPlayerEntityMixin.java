package com.oskarkraak.gulag.mixin;

import com.oskarkraak.gulag.Gulag;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

    @Inject(method = "moveToWorld", at = @At("HEAD"), cancellable = true)
    private void beforeMoveToWorld(ServerWorld destination, CallbackInfoReturnable<Entity> cir) {
        ServerPlayerEntity player = ((ServerPlayerEntity) (Object) this);
        ServerWorld origin = player.getWorld();
        ServerWorld correctDestination = Gulag.getCorrectDestination(player, origin, destination);
        if (destination != correctDestination) {
            Entity returnedPlayer = player.moveToWorld(correctDestination);
            cir.setReturnValue(returnedPlayer);
        }
    }

}
