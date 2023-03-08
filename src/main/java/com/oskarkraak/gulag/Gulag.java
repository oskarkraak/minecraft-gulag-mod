package com.oskarkraak.gulag;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Gulag implements ModInitializer {

    public static final Logger LOGGER = LogManager.getLogger("Gulag");

    public static MinecraftServer server;
    public static Overworld overworld;
    public static Nether nether;
    public static End end;

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(this::loadGulag);
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register(this::playerChangeWorld);
    }

    private void loadGulag(MinecraftServer server) {
        Gulag.server = server;
        overworld = new Overworld(server, "gulag", 100L, Difficulty.HARD);
        LOGGER.info("Loaded dimension gulag:overworld");
        nether = new Nether(server, "gulag", 100L, Difficulty.HARD);
        LOGGER.info("Loaded dimension gulag:the_nether");
        end = new End(server, "gulag", 100L, Difficulty.HARD);
        LOGGER.info("Loaded dimension gulag:the_end");
    }

    private void playerChangeWorld(ServerPlayerEntity player, ServerWorld origin, ServerWorld destination) {
        boolean originIsGulagOverworld = origin.getRegistryKey() == Gulag.overworld.getRegistryKey();
        boolean originIsGulagNether = origin.getRegistryKey() == Gulag.nether.getRegistryKey();
        boolean destinationIsNether = destination.getRegistryKey() == World.NETHER;
        boolean destinationIsEnd = destination.getRegistryKey() == World.END;
        if (originIsGulagOverworld || originIsGulagNether) {
            if (originIsGulagNether && destinationIsNether) {
                // This condition is due to a quirk where it will go to minecraft:the_nether when coming from
                // gulag:the_nether
                player.moveToWorld(Gulag.overworld.asWorld());
            } else if (destinationIsNether) {
                player.moveToWorld(Gulag.nether.asWorld());
            } else if (destinationIsEnd) {
                ServerWorld world = Gulag.end.asWorld();
                // Inspired from ServerPlayerEntity.getTeleportTarget (super.getTeleportTarget)
                BlockPos spawn = ServerWorld.END_SPAWN_POS;
                // Because Minecraft does this only for the World.END, we have to create the spawn platform ourselves
                createEndSpawnPlatform(world, new BlockPos(spawn.toCenterPos()));
                player.teleport(world, spawn.getX() + 0.5, spawn.getY(), spawn.getZ() + 0.5, player.getYaw(), player.getPitch());
            }
        }
    }

    // TODO use an access widener on ServerPlayerEntity for this
    private static void createEndSpawnPlatform(ServerWorld world, BlockPos centerPos) {
        BlockPos.Mutable mutable = centerPos.mutableCopy();
        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                for (int k = -1; k < 3; ++k) {
                    BlockState blockState = k == -1 ? Blocks.OBSIDIAN.getDefaultState() : Blocks.AIR.getDefaultState();
                    world.setBlockState(mutable.set(centerPos).move(j, k, i), blockState);
                }
            }
        }
    }

}
