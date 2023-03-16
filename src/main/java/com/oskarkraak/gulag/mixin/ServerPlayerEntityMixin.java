package com.oskarkraak.gulag.mixin;

import com.oskarkraak.gulag.Gulag;
import net.minecraft.entity.Entity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

    @Inject(method = "moveToWorld", at = @At("HEAD"), cancellable = true)
    private void beforeMoveToWorld(ServerWorld destination, CallbackInfoReturnable<Entity> cir) {
        ServerPlayerEntity player = ((ServerPlayerEntity) (Object) this);
        ServerWorld origin = player.getWorld();
        boolean originIsGulagOverworld = Gulag.isGulagOverworld(origin);
        boolean originIsGulagNether = Gulag.isGulagNether(origin);
        boolean destinationIsNether = destination.getRegistryKey() == World.NETHER;
        boolean destinationIsEnd = destination.getRegistryKey() == World.END;
        if (originIsGulagOverworld || originIsGulagNether) {
            ServerWorld world = null;
            if (originIsGulagNether && destinationIsNether) {
                // This condition is due to a quirk where it will go to minecraft:the_nether when coming from
                // gulag:the_nether
                world = Gulag.overworld.asWorld();
            } else if (destinationIsNether) {
                world = Gulag.nether.asWorld();
            } else if (destinationIsEnd) {
                world = Gulag.end.asWorld();
            }
            if (world != null) {
                Entity returnedEntity = player.moveToWorld(world);
                cir.setReturnValue(returnedEntity);
            }
        }
    }

    // TODO This breaks the mixin
    @Redirect(
            method = "moveToWorld",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/World;OVERWORLD:Lnet/minecraft/registry/RegistryKey;",
                    opcode = Opcodes.GETSTATIC
            )
    )
    private RegistryKey<World> replaceOverworldInMoveToWorld() {
        Entity entity = ((Entity) (Object) this);
        World origin = entity.world;
        if (Gulag.isGulagOverworldOrNether(origin)) {
            return Gulag.overworld.getRegistryKey();
        } else {
            return World.OVERWORLD;
        }
    }

    // TODO This breaks the mixin
    @Redirect(
            method = "moveToWorld",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/World;NETHER:Lnet/minecraft/registry/RegistryKey;",
                    opcode = Opcodes.GETSTATIC
            )
    )
    private RegistryKey<World> replaceNetherInMoveToWorld() {
        Entity entity = ((Entity) (Object) this);
        World origin = entity.world;
        if (Gulag.isGulagOverworldOrNether(origin)) {
            return Gulag.nether.getRegistryKey();
        } else {
            return World.NETHER;
        }
    }

    // TODO This breaks the mixin
    @Redirect(
            method = "moveToWorld",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/World;END:Lnet/minecraft/registry/RegistryKey;",
                    opcode = Opcodes.GETSTATIC
            )
    )
    private RegistryKey<World> replaceEndInMoveToWorld() {
        Entity entity = ((Entity) (Object) this);
        World origin = entity.world;
        if (Gulag.isGulagEnd(origin) || Gulag.isGulagOverworld(origin)) {
            return Gulag.end.getRegistryKey();
        } else {
            return World.END;
        }
    }

    @Redirect(
            method = "getTeleportTarget",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/World;OVERWORLD:Lnet/minecraft/registry/RegistryKey;",
                    opcode = Opcodes.GETSTATIC
            )
    )
    private RegistryKey<World> replaceOverworldInGetTeleportTarget() {
        Entity entity = ((Entity) (Object) this);
        World origin = entity.world;
        if (Gulag.isGulagOverworldOrNether(origin)) {
            return Gulag.overworld.getRegistryKey();
        } else {
            return World.OVERWORLD;
        }
    }

    @Redirect(
            method = "getTeleportTarget",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/World;END:Lnet/minecraft/registry/RegistryKey;",
                    opcode = Opcodes.GETSTATIC
            )
    )
    private RegistryKey<World> replaceEndInGetTeleportTarget() {
        Entity entity = ((Entity) (Object) this);
        World origin = entity.world;
        if (Gulag.isGulagEnd(origin) || Gulag.isGulagOverworld(origin)) {
            return Gulag.end.getRegistryKey();
        } else {
            return World.END;
        }
    }

}
