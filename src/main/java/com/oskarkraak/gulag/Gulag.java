package com.oskarkraak.gulag;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.SpawnLocating;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.Heightmap;
import net.minecraft.world.TeleportTarget;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Gulag implements ModInitializer {

    public static final Logger LOGGER = LogManager.getLogger("Gulag");

    public static MinecraftServer server;
    private Overworld overworld;
    private Nether nether;
    private End end;

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(this::loadGulag);
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            ServerWorld overworldWorld = overworld.asWorld();


            ChunkPos chunkPos = new ChunkPos(overworldWorld.getChunkManager().getNoiseConfig().getMultiNoiseSampler().findBestSpawnPosition());
            BlockPos pos3 = SpawnLocating.findServerSpawnPoint(overworldWorld, new ChunkPos(chunkPos.x, chunkPos.z ));

            TeleportTarget target = new TeleportTarget(pos3.toCenterPos(), Vec3d.ZERO, 0.0f, 0.0f);
            FabricDimensions.teleport(newPlayer, overworldWorld, target);
        });
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

}
