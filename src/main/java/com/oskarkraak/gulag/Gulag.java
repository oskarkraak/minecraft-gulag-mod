package com.oskarkraak.gulag;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
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
            BlockPos spawnPos = overworld.asWorld().getSpawnPos();
            TeleportTarget target = new TeleportTarget(spawnPos.toCenterPos(), newPlayer.getVelocity(), newPlayer.getYaw(), newPlayer.getPitch());
            FabricDimensions.teleport(newPlayer, overworld.asWorld(), target);
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
