package com.oskarkraak.gulag;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureSet;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.*;
import net.minecraft.world.gen.chunk.placement.StructurePlacementCalculator;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.structure.Structure;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * A NoiseChunkGenerator with a custom seed.
 * Instead of the world seed, any seed can be given as a parameter.
 */
public class SeededNoiseChunkGenerator extends ChunkGenerator {

    private long seed;
    private NoiseChunkGenerator chunkGenerator;
    private NoiseConfig noiseConfig;

    public SeededNoiseChunkGenerator(long seed, BiomeSource biomeSource, RegistryEntry<ChunkGeneratorSettings> settings) {
        super(biomeSource);
        this.seed = seed;
        chunkGenerator = new NoiseChunkGenerator(biomeSource, settings);
        noiseConfig = NoiseConfig.create(
                settings.value(),
                Gulag.server.getOverworld().getRegistryManager().getWrapperOrThrow(RegistryKeys.NOISE_PARAMETERS),
                seed);
    }

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return NoiseChunkGenerator.CODEC;
    }

    @Override
    public Pair<BlockPos, RegistryEntry<Structure>> locateStructure(ServerWorld world, RegistryEntryList<Structure> structures, BlockPos center, int radius, boolean skipReferencedStructures) {
        return chunkGenerator.locateStructure(world, structures, center, radius, skipReferencedStructures);
    }

    @Override
    public void generateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor) {
        chunkGenerator.generateFeatures(world, chunk, structureAccessor);
    }

    public int getSpawnHeight(HeightLimitView world) {
        return chunkGenerator.getSpawnHeight(world);
    }

    @Override
    public StructurePlacementCalculator createStructurePlacementCalculator(RegistryWrapper<StructureSet> structureSetRegistry, NoiseConfig noiseConfig, long seed) {
        seed = this.seed;
        noiseConfig = this.noiseConfig;
        return chunkGenerator.createStructurePlacementCalculator(structureSetRegistry, noiseConfig, seed);
    }

    @Override
    public void carve(ChunkRegion chunkRegion, long seed, NoiseConfig noiseConfig, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk, GenerationStep.Carver carverStep) {
        seed = this.seed;
        noiseConfig = this.noiseConfig;
        chunkGenerator.carve(chunkRegion, seed, noiseConfig, biomeAccess, structureAccessor, chunk, carverStep);
    }

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk) {
        noiseConfig = this.noiseConfig;
        chunkGenerator.buildSurface(region, structures, noiseConfig, chunk);
    }

    @Override
    public void populateEntities(ChunkRegion region) {
        chunkGenerator.populateEntities(region);
    }

    @Override
    public int getWorldHeight() {
        return chunkGenerator.getWorldHeight();
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk) {
        noiseConfig = this.noiseConfig;
        return chunkGenerator.populateNoise(executor, blender, noiseConfig, structureAccessor, chunk);
    }

    @Override
    public int getSeaLevel() {
        return chunkGenerator.getSeaLevel();
    }

    @Override
    public int getMinimumY() {
        return chunkGenerator.getMinimumY();
    }

    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
        noiseConfig = this.noiseConfig;
        return chunkGenerator.getHeight(x, z, heightmap, world, noiseConfig);
    }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig) {
        noiseConfig = this.noiseConfig;
        return chunkGenerator.getColumnSample(x, z, world, noiseConfig);
    }

    @Override
    public void getDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos pos) {
        noiseConfig = this.noiseConfig;
        chunkGenerator.getDebugHudText(text, noiseConfig, pos);
    }

    @Override
    public CompletableFuture<Chunk> populateBiomes(Executor executor, NoiseConfig noiseConfig, Blender blender, StructureAccessor structureAccessor, Chunk chunk) {
        noiseConfig = this.noiseConfig;
        return chunkGenerator.populateBiomes(executor, noiseConfig, blender, structureAccessor, chunk);
    }

    @Override
    public Pool<SpawnSettings.SpawnEntry> getEntitySpawnList(RegistryEntry<Biome> biome, StructureAccessor accessor, SpawnGroup group, BlockPos pos) {
        return chunkGenerator.getEntitySpawnList(biome, accessor, group, pos);
    }

    @Override
    public void setStructureStarts(DynamicRegistryManager registryManager, StructurePlacementCalculator placementCalculator, StructureAccessor structureAccessor, Chunk chunk, StructureTemplateManager structureTemplateManager) {
        chunkGenerator.setStructureStarts(registryManager, placementCalculator, structureAccessor, chunk, structureTemplateManager);
    }

    @Override
    public void addStructureReferences(StructureWorldAccess world, StructureAccessor structureAccessor, Chunk chunk) {
        chunkGenerator.addStructureReferences(world, structureAccessor, chunk);
    }

    @Override
    public int getHeightOnGround(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
        noiseConfig = this.noiseConfig;
        return chunkGenerator.getHeightOnGround(x, z, heightmap, world, noiseConfig);
    }

    @Override
    public int getHeightInGround(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
        noiseConfig = this.noiseConfig;
        return chunkGenerator.getHeightInGround(x, z, heightmap, world, noiseConfig);
    }

    @Override
    public GenerationSettings getGenerationSettings(RegistryEntry<Biome> biomeEntry) {
        return chunkGenerator.getGenerationSettings(biomeEntry);
    }

}
