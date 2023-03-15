package com.oskarkraak.gulag.mixin;

import com.oskarkraak.gulag.Gulag;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.NetherPortal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "getTeleportTarget", at = @At("HEAD"), cancellable = true)
    private void getTeleportTarget(ServerWorld destination, CallbackInfoReturnable<TeleportTarget> cir) {
        boolean destinationIsGulagOverworld = Gulag.isGulagOverworld(destination);
        boolean destinationIsGulagNether = Gulag.isGulagNether(destination);
        if (destinationIsGulagOverworld || destinationIsGulagNether) {
            Entity entity = ((Entity) (Object) this);
            boolean bl3 = destination.getRegistryKey() == Gulag.nether.getRegistryKey();
            WorldBorder worldBorder = destination.getWorldBorder();
            double d = DimensionType.getCoordinateScaleFactor(entity.world.getDimension(), destination.getDimension());
            BlockPos blockPos2 = worldBorder.clamp(entity.getX() * d, entity.getY(), entity.getZ() * d);
            TeleportTarget toReturn = invokeGetPortalRect(destination, blockPos2, bl3, worldBorder).map(rect -> {
                Vec3d vec3d;
                Direction.Axis axis;
                BlockState blockState = entity.world.getBlockState(getLastNetherPortalPosition());
                if (blockState.contains(Properties.HORIZONTAL_AXIS)) {
                    axis = blockState.get(Properties.HORIZONTAL_AXIS);
                    BlockLocating.Rectangle rectangle = BlockLocating.getLargestRectangle(getLastNetherPortalPosition(),
                            axis, 21, Direction.Axis.Y, 21,
                            pos -> entity.world.getBlockState((BlockPos) pos) == blockState);
                    vec3d = invokePositionInPortal(axis, rectangle);
                } else {
                    axis = Direction.Axis.X;
                    vec3d = new Vec3d(0.5, 0.0, 0.0);
                }
                return NetherPortal.getNetherTeleportTarget(destination, rect, axis, vec3d, entity, entity.getVelocity(),
                        entity.getYaw(), entity.getPitch());
            }).orElse(null);
            cir.setReturnValue(toReturn);
        }
    }

    @Accessor
    abstract BlockPos getLastNetherPortalPosition();

    @Invoker("getPortalRect")
    abstract Optional<BlockLocating.Rectangle> invokeGetPortalRect(ServerWorld destWorld, BlockPos destPos, boolean destIsNether, WorldBorder worldBorder);

    @Invoker("positionInPortal")
    abstract Vec3d invokePositionInPortal(Direction.Axis portalAxis, BlockLocating.Rectangle portalRect);

}