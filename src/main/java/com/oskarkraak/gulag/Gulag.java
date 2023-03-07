package com.oskarkraak.gulag;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.world.Difficulty;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.nucleoid.fantasy.Fantasy;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;
import xyz.nucleoid.fantasy.RuntimeWorldHandle;

public class Gulag implements DedicatedServerModInitializer {

    public static final Logger LOGGER = LogManager.getLogger("Gulag");

    public static MinecraftServer server;

    @Override
    public void onInitializeServer() {
        ServerLifecycleEvents.SERVER_STARTED.register(this::loadGulag);
    }

    private void loadGulag(MinecraftServer server) {
        this.server = server;

        RegistryKey<DimensionType> overworldDimensionTypeKey = RegistryKey.of(
                RegistryKeys.DIMENSION_TYPE, new Identifier("minecraft", "overworld"));
        BiomeSource bs = server.getOverworld().getChunkManager().getChunkGenerator().getBiomeSource();
        RegistryKey<ChunkGeneratorSettings> cgsKey = ChunkGeneratorSettings.OVERWORLD;
        RegistryEntry<ChunkGeneratorSettings> cgsEntry =
                server.getRegistryManager().get(RegistryKeys.CHUNK_GENERATOR_SETTINGS).getEntry(cgsKey).orElseThrow();
        ChunkGenerator cg = new NoiseChunkGenerator(bs, cgsEntry);
        RuntimeWorldConfig worldConfig = new RuntimeWorldConfig()
                .setDimensionType(overworldDimensionTypeKey)
                .setDifficulty(Difficulty.HARD)
                .setGenerator(cg)
                .setSeed(100L);
        RuntimeWorldHandle h = Fantasy.get(Gulag.server).openTemporaryWorld(worldConfig);
//        Identifier id = new Identifier("gulag", "overworld");
//        RuntimeWorldHandle worldHandle = Fantasy.get(server).getOrOpenPersistentWorld(id, worldConfig);
//        LOGGER.info("Loaded dimension gulag:overworld");
    }

}
