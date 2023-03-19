package com.oskarkraak.gulag.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ServerPlayerEntity.class)
public interface ServerPlayerEntityInvoker {

    @Invoker("createEndSpawnPlatform")
    void invokeCreateEndSpawnPlatform(ServerWorld world, BlockPos centerPos);

}
