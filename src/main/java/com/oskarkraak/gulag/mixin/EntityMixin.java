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
import net.minecraft.world.Heightmap;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.NetherPortal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "moveToWorld", at = @At("HEAD"), cancellable = true)
    private void beforeMoveToWorld(ServerWorld destination, CallbackInfoReturnable<Entity> cir) {
        Entity entity = ((Entity) (Object) this);
        if (!(entity.world instanceof ServerWorld origin)) {
            cir.setReturnValue(null);
            return;
        }
        ServerWorld correctDestination = Gulag.getCorrectDestination(entity, origin, destination);
        if (destination != correctDestination) {
            Entity returnedPlayer = entity.moveToWorld(correctDestination);
            cir.setReturnValue(returnedPlayer);
        }
    }

    @Shadow
    public World world;

    @Inject(method = "getTeleportTarget", at = @At("HEAD"), cancellable = true)
    private void getTeleportTarget(ServerWorld destination, CallbackInfoReturnable<TeleportTarget> cir) {
        Entity entity = ((Entity) (Object) this);
        boolean returnFromGulag = entity.world.getRegistryKey() == Gulag.end.getRegistryKey()
                && destination.getRegistryKey() == World.OVERWORLD;
        if (Gulag.isGulagWorld(destination) || returnFromGulag) {
            boolean bl2 = destination.getRegistryKey() == Gulag.end.getRegistryKey();
            if (returnFromGulag || bl2) {
                BlockPos blockPos = bl2 ? ServerWorld.END_SPAWN_POS :
                        destination.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, destination.getSpawnPos());
                TeleportTarget toReturn = new TeleportTarget(new Vec3d((double) blockPos.getX() + 0.5,
                        blockPos.getY(), (double) blockPos.getZ() + 0.5),
                        entity.getVelocity(), entity.getYaw(), entity.getPitch());
                cir.setReturnValue(toReturn);
                return;
            }
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
                            pos -> entity.world.getBlockState(pos) == blockState);
                    vec3d = invokePositionInPortal(axis, rectangle);
                } else {
                    axis = Direction.Axis.X;
                    vec3d = new Vec3d(0.5, 0.0, 0.0);
                }
                return NetherPortal.getNetherTeleportTarget(destination, rect, axis, vec3d, entity,
                        entity.getVelocity(), entity.getYaw(), entity.getPitch());
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
