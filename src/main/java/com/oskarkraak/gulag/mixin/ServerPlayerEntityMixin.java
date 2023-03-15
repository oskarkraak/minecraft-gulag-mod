package com.oskarkraak.gulag.mixin;

import com.oskarkraak.gulag.Gulag;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

    @Inject(method = "moveToWorld", at = @At("HEAD"), cancellable = true)
    private void beforeMoveToWorld(ServerWorld destination, CallbackInfoReturnable<Entity> cir) {
        ServerPlayerEntity player = ((ServerPlayerEntity) (Object) this);
        ServerWorld origin = ((ServerPlayerEntity) (Object) this).getWorld();
        boolean originIsGulagOverworld = Gulag.isOverworld(origin);
        boolean originIsGulagNether = Gulag.isNether(origin);
        boolean destinationIsNether = destination.getRegistryKey() == World.NETHER;
        boolean destinationIsEnd = destination.getRegistryKey() == World.END;
        if (originIsGulagOverworld || originIsGulagNether) {
            if (originIsGulagNether && destinationIsNether) {
                // This condition is due to a quirk where it will go to minecraft:the_nether when coming from
                // gulag:the_nether
                Entity returnedPlayer = player.moveToWorld(Gulag.overworld.asWorld());
                cir.setReturnValue(returnedPlayer);
            } else if (destinationIsNether) {
                Entity returnedPlayer = player.moveToWorld(Gulag.nether.asWorld());
                cir.setReturnValue(returnedPlayer);
            } else if (destinationIsEnd) {
                ServerWorld world = Gulag.end.asWorld();
                // Inspired from ServerPlayerEntity.getTeleportTarget (super.getTeleportTarget)
                BlockPos spawn = ServerWorld.END_SPAWN_POS;
                // Because Minecraft does this only for the World.END, we have to create the spawn platform ourselves
                player.createEndSpawnPlatform(world, new BlockPos(spawn.toCenterPos()));
                player.teleport(world, spawn.getX() + 0.5, spawn.getY(), spawn.getZ() + 0.5, player.getYaw(), player.getPitch());
                cir.setReturnValue(player);
            }
        }
    }

}
