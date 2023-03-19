package com.oskarkraak.gulag;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import xyz.nucleoid.fantasy.Fantasy;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;
import xyz.nucleoid.fantasy.RuntimeWorldHandle;

public class VanillaWorld {

    private final RuntimeWorldHandle worldHandle;
    private final SeededNoiseChunkGenerator chunkGenerator;

    public VanillaWorld(MinecraftServer server, String namespace, String dimensionType, long seed, RegistryKey<World> worldKey,
                        RegistryKey<ChunkGeneratorSettings> cgsKey, Difficulty difficulty) {
        this(server, namespace, dimensionType, seed, worldKey, cgsKey, difficulty, false);
    }

    public VanillaWorld(MinecraftServer server, String namespace, String dimensionType, long seed, RegistryKey<World> worldKey,
                        RegistryKey<ChunkGeneratorSettings> cgsKey, Difficulty difficulty, boolean shouldTickTime) {
        chunkGenerator = getChunkGenerator(server, seed, worldKey, cgsKey);
        RuntimeWorldConfig worldConfig = new RuntimeWorldConfig()
                .setDimensionType(getDimensionTypeKey(dimensionType))
                .setDifficulty(difficulty)
                .setGenerator(chunkGenerator)
                .setSeed(seed)
                .setShouldTickTime(shouldTickTime);
        Identifier id = new Identifier(namespace, dimensionType);
        worldHandle = Fantasy.get(server).getOrOpenPersistentWorld(id, worldConfig);
    }

    private RegistryKey<DimensionType> getDimensionTypeKey(String dimensionType) {
        return RegistryKey.of(RegistryKeys.DIMENSION_TYPE, new Identifier("minecraft", dimensionType));
    }

    private SeededNoiseChunkGenerator getChunkGenerator(MinecraftServer server, long seed, RegistryKey<World> worldKey,
                                                        RegistryKey<ChunkGeneratorSettings> cgsKey) {
        RegistryEntry<World> worldEntry =
                server.getRegistryManager().get(RegistryKeys.WORLD).getEntry(worldKey).orElseThrow();
        ServerWorld world = server.getWorld(worldEntry.getKey().orElseThrow());
        BiomeSource bs = world.getChunkManager().getChunkGenerator().getBiomeSource();
        RegistryEntry<ChunkGeneratorSettings> cgsEntry =
                server.getRegistryManager().get(RegistryKeys.CHUNK_GENERATOR_SETTINGS).getEntry(cgsKey).orElseThrow();
        return new SeededNoiseChunkGenerator(seed, bs, cgsEntry);
    }

    public void delete() {
        worldHandle.delete();
    }

    public ServerWorld asWorld() {
        return worldHandle.asWorld();
    }

    public RegistryKey<World> getRegistryKey() {
        return worldHandle.getRegistryKey();
    }

    public long getSeed() {
        return chunkGenerator.getSeed();
    }

}
