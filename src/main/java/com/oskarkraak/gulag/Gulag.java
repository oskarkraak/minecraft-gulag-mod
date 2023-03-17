package com.oskarkraak.gulag;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.SpawnLocating;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Gulag implements ModInitializer {

    public static final Logger LOGGER = LogManager.getLogger("Gulag");
    private static final String RESPAWN_MESSAGE_TIME = "title @a times 20 200 20";
    private static final String RESPAWN_MESSAGE_TITLE = "title @a title {\"text\":\"Gulag\",\"color\":\"dark_red\"}";
    private static final String RESPAWN_MESSAGE_SUBTITLE = "title @a subtitle {\"text\":\"Defeat the ender dragon!\"}";

    public static MinecraftServer server;
    public static Overworld overworld;
    public static Nether nether;
    public static End end;

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(this::loadGulag);
        ServerPlayerEvents.AFTER_RESPAWN.register(this::onRespawn);
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

    private void onRespawn(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
        respawnInGulag(oldPlayer, newPlayer, alive);
        sendInfoMessage(newPlayer);
    }

    private void respawnInGulag(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
        BlockPos bestSpawnPos = overworld.asWorld().getChunkManager().getNoiseConfig().getMultiNoiseSampler()
                .findBestSpawnPosition();
        BlockPos spawnPos = SpawnLocating.findServerSpawnPoint(overworld.asWorld(), new ChunkPos(bestSpawnPos));
        TeleportTarget target = new TeleportTarget(spawnPos.toCenterPos(), Vec3d.ZERO, 0.0f, 0.0f);
        FabricDimensions.teleport(newPlayer, overworld.asWorld(), target);
    }

    private void sendInfoMessage(ServerPlayerEntity player) {
        server.getCommandManager().executeWithPrefix(server.getCommandSource(), RESPAWN_MESSAGE_TIME);
        server.getCommandManager().executeWithPrefix(server.getCommandSource(), RESPAWN_MESSAGE_TITLE);
        server.getCommandManager().executeWithPrefix(server.getCommandSource(), RESPAWN_MESSAGE_SUBTITLE);
    }

    public static boolean isGulagOverworld(World world) {
        return world.getRegistryKey() == Gulag.overworld.getRegistryKey();
    }

    public static boolean isGulagNether(World world) {
        return world.getRegistryKey() == Gulag.nether.getRegistryKey();
    }

    public static boolean isGulagEnd(World world) {
        return world.getRegistryKey() == Gulag.end.getRegistryKey();
    }

    public static boolean isGulagOverworldOrNether(World world) {
        return isGulagOverworld(world) || isGulagNether(world);
    }

    public static boolean isGulagWorld(World world) {
        return isGulagOverworld(world) || isGulagNether(world) || isGulagEnd(world);
    }


}
