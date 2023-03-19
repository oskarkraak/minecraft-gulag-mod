package com.oskarkraak.gulag.mixin;

import com.oskarkraak.gulag.Gulag;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

    @Inject(method = "moveToWorld", at = @At("HEAD"), cancellable = true)
    private void beforeMoveToWorld(ServerWorld destination, CallbackInfoReturnable<Entity> cir) {
        ServerPlayerEntity player = ((ServerPlayerEntity) (Object) this);
        ServerWorld origin = player.getWorld();
        boolean originIsGulagOverworld = Gulag.isGulagOverworld(origin);
        boolean originIsGulagNether = Gulag.isGulagNether(origin);
        boolean originIsGulagEnd = Gulag.isGulagEnd(origin);
        boolean destinationIsNether = destination.getRegistryKey() == World.NETHER;
        boolean destinationIsEnd = destination.getRegistryKey() == World.END;
        ServerWorld world = null;
        if (originIsGulagOverworld && destinationIsNether) {
            // gulag:overworld -> gulag:the_nether
            world = Gulag.nether.asWorld();
        } else if (originIsGulagNether && destinationIsNether) {
            // gulag:the_nether -> gulag:overworld
            world = Gulag.overworld.asWorld();
        } else if (originIsGulagOverworld && destinationIsEnd) {
            // gulag:overworld -> gulag:the_end
            world = Gulag.end.asWorld();
            // Because Minecraft does this only for the World.END, we have to create the spawn platform ourselves
            invokeCreateEndSpawnPlatform(world, new BlockPos(ServerWorld.END_SPAWN_POS.toCenterPos()));
        } else if (originIsGulagEnd && destinationIsEnd) {
            // gulag:the_end -> minecraft:overworld
            world = Gulag.server.getOverworld();
        }
        if (world != null) {
            Entity returnedPlayer = player.moveToWorld(world);
            cir.setReturnValue(returnedPlayer);
        }
    }

    @Invoker("createEndSpawnPlatform")
    abstract void invokeCreateEndSpawnPlatform(ServerWorld world, BlockPos centerPos);

}
