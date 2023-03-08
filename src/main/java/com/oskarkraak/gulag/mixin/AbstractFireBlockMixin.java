package com.oskarkraak.gulag.mixin;

import com.oskarkraak.gulag.Gulag;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractFireBlock.class)
public abstract class AbstractFireBlockMixin  {

    @Inject(method = "isOverworldOrNether", at = @At("RETURN"), cancellable = true)
    private static void isOverworldOrNether(World world, CallbackInfoReturnable<Boolean> cir) {
        boolean gulagOverworld = world.getRegistryKey() == Gulag.overworld.getRegistryKey();
        boolean gulagNether = world.getRegistryKey() == Gulag.nether.getRegistryKey();
        boolean minecraftOverworldOrNether = cir.getReturnValue();
        cir.setReturnValue(minecraftOverworldOrNether || gulagOverworld || gulagNether);
    }

}
