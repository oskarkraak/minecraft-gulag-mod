package com.oskarkraak.gulag.mixin;

import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerWorld.class)
public interface ServerWorldAccessor {

    @Accessor("enderDragonFight")
    @Mutable
    void setEnderDragonFight(EnderDragonFight enderDragonFight);

}
