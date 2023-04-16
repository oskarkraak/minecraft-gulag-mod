package com.oskarkraak.gulag;

import com.oskarkraak.gulag.mixin.ServerPlayerEntityInvoker;
import com.oskarkraak.gulag.world.End;
import com.oskarkraak.gulag.world.Nether;
import com.oskarkraak.gulag.world.Overworld;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.SpawnLocating;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public class Gulag implements ModInitializer {

    public static final Logger LOGGER = LogManager.getLogger("Gulag");
    private static final String RESPAWN_MESSAGE_TIME = "title @a times 20 200 20";
    private static final String RESPAWN_MESSAGE_TITLE = "title @a title {\"text\":\"Gulag\",\"color\":\"dark_red\"}";
    private static final String RESPAWN_MESSAGE_SUBTITLE = "title @a subtitle {\"text\":\"Defeat the ender dragon!\"}";
    private static final String RESPAWN_MESSAGE_CHAT =
            "Welcome to the Gulag! Fight your way back to your home world by defeating the ender dragon.";

    public static MinecraftServer server;
    public static Overworld overworld;
    public static Nether nether;
    public static End end;
    public static boolean isLoaded;

    private static void loadGulag(long seed) {
        overworld = new Overworld(server, "gulag", seed, Difficulty.HARD);
        nether = new Nether(server, "gulag", seed, Difficulty.HARD);
        end = new End(server, "gulag", seed, Difficulty.HARD);
        Gulag.isLoaded = true;
        LOGGER.info("Loaded gulag with seed " + seed);
    }

    private static long getRandomSeed() {
        return new Random().nextLong();
    }

    private static void respawnInGulag(ServerPlayerEntity player) {
        BlockPos bestSpawnPos = overworld.asWorld().getChunkManager().getNoiseConfig().getMultiNoiseSampler()
                .findBestSpawnPosition();
        BlockPos spawnPos = SpawnLocating.findServerSpawnPoint(overworld.asWorld(), new ChunkPos(bestSpawnPos));
        TeleportTarget target = new TeleportTarget(spawnPos.toCenterPos(), Vec3d.ZERO, 0.0f, 0.0f);
        FabricDimensions.teleport(player, overworld.asWorld(), target);
    }

    private static void sendInfoMessage(ServerPlayerEntity player) {
        executeCommand(RESPAWN_MESSAGE_TIME);
        executeCommand(RESPAWN_MESSAGE_TITLE);
        executeCommand(RESPAWN_MESSAGE_SUBTITLE);
        player.sendMessage(Text.of(RESPAWN_MESSAGE_CHAT));
    }

    private static void executeCommand(String command) {
        server.getCommandManager().executeWithPrefix(server.getCommandSource(), command);
    }

    private static boolean shouldDeleteGulag() {
        boolean isEmtpy = countPlayersInGulag() == 0;
        boolean enderDragonDefeated = end.asWorld().getEnderDragonFight().hasPreviouslyKilled();
        return isEmtpy && enderDragonDefeated;
    }

    private static int countPlayersInGulag() {
        int playersInOverworld = overworld.asWorld().getPlayers().size();
        int playersInNether = nether.asWorld().getPlayers().size();
        int playersInEnd = end.asWorld().getPlayers().size();
        return playersInOverworld + playersInNether + playersInEnd;
    }

    private static void deleteGulag() {
        Gulag.isLoaded = false;
        long seed = overworld.getSeed();
        overworld.delete();
        nether.delete();
        end.delete();
        LOGGER.info("Deleted gulag with seed " + seed);
    }

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStart);
        ServerPlayerEvents.AFTER_RESPAWN.register(this::onRespawn);
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register(this::onPlayerChangeWorld);
    }

    private void onServerStart(MinecraftServer server) {
        Gulag.server = server;
        loadGulag(getRandomSeed());
    }

    private void onRespawn(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
        if (!alive) {
            if (!Gulag.isLoaded) {
                loadGulag(getRandomSeed());
            }
            respawnInGulag(newPlayer);
            sendInfoMessage(newPlayer);
        }
    }

    private void onPlayerChangeWorld(ServerPlayerEntity player, ServerWorld origin, ServerWorld destination) {
        if (shouldDeleteGulag()) {
            deleteGulag();
        }
    }

    /**
     * Finds the correct destination world for an entity coming from a gulag world.
     *
     * @param entity              is the entity that is moving between worlds
     * @param originalDestination is the original destination of the entity
     * @return the correct destination
     */
    public static ServerWorld getCorrectDestination(Entity entity, ServerWorld origin, ServerWorld originalDestination) {
        boolean originIsGulagOverworld = Gulag.isGulagOverworld(origin);
        boolean originIsGulagNether = Gulag.isGulagNether(origin);
        boolean originIsGulagEnd = Gulag.isGulagEnd(origin);
        boolean destinationIsNether = originalDestination.getRegistryKey() == World.NETHER;
        boolean destinationIsEnd = originalDestination.getRegistryKey() == World.END;
        ServerWorld destination = originalDestination;
        if (originIsGulagOverworld && destinationIsNether) {
            // gulag:overworld -> gulag:the_nether
            destination = Gulag.nether.asWorld();
        } else if (originIsGulagNether && destinationIsNether) {
            // gulag:the_nether -> gulag:overworld
            destination = Gulag.overworld.asWorld();
        } else if (originIsGulagOverworld && destinationIsEnd) {
            // gulag:overworld -> gulag:the_end
            destination = Gulag.end.asWorld();
            // Because Minecraft does this only for the World.END, we have to create the spawn platform ourselves
            if (entity instanceof ServerPlayerEntity player) {
                ((ServerPlayerEntityInvoker) player).invokeCreateEndSpawnPlatform(
                        destination, new BlockPos(ServerWorld.END_SPAWN_POS));
            } else {
                ServerWorld.createEndSpawnPlatform(destination);
            }
        } else if (originIsGulagEnd && destinationIsEnd) {
            // gulag:the_end -> minecraft:overworld
            destination = Gulag.server.getOverworld();
        }
        return destination;
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
