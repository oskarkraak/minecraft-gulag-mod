package com.oskarkraak.gulag;

import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.chunk.placement.StructurePlacementCalculator;
import net.minecraft.world.gen.noise.NoiseConfig;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * A NoiseChunkGenerator with a custom seed.
 * Instead of the world seed, any seed can be given as a parameter.
 */
public class SeededNoiseChunkGenerator extends NoiseChunkGenerator {

    private long seed;
    private NoiseConfig noiseConfig;

    public SeededNoiseChunkGenerator(long seed, BiomeSource biomeSource, RegistryEntry<ChunkGeneratorSettings> settings) {
        super(biomeSource, settings);
        setSeed(seed);
    }

    public void setSeed(long seed) {
        this.seed = seed;
        noiseConfig = NoiseConfig.create(
                super.getSettings().value(),
                Gulag.server.getOverworld().getRegistryManager().getWrapperOrThrow(RegistryKeys.NOISE_PARAMETERS),
                seed);
    }

    @Override
    public StructurePlacementCalculator createStructurePlacementCalculator(RegistryWrapper<StructureSet> structureSetRegistry, NoiseConfig noiseConfig, long seed) {
        seed = this.seed;
        noiseConfig = this.noiseConfig;
        return super.createStructurePlacementCalculator(structureSetRegistry, noiseConfig, seed);
    }

    @Override
    public void carve(ChunkRegion chunkRegion, long seed, NoiseConfig noiseConfig, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk, GenerationStep.Carver carverStep) {
        seed = this.seed;
        noiseConfig = this.noiseConfig;
        super.carve(chunkRegion, seed, noiseConfig, biomeAccess, structureAccessor, chunk, carverStep);
    }

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk) {
        noiseConfig = this.noiseConfig;
        super.buildSurface(region, structures, noiseConfig, chunk);
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk) {
        noiseConfig = this.noiseConfig;
        return super.populateNoise(executor, blender, noiseConfig, structureAccessor, chunk);
    }

    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
        noiseConfig = this.noiseConfig;
        return super.getHeight(x, z, heightmap, world, noiseConfig);
    }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig) {
        noiseConfig = this.noiseConfig;
        return super.getColumnSample(x, z, world, noiseConfig);
    }

    @Override
    public void getDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos pos) {
        noiseConfig = this.noiseConfig;
        super.getDebugHudText(text, noiseConfig, pos);
    }

    @Override
    public CompletableFuture<Chunk> populateBiomes(Executor executor, NoiseConfig noiseConfig, Blender blender, StructureAccessor structureAccessor, Chunk chunk) {
        noiseConfig = this.noiseConfig;
        return super.populateBiomes(executor, noiseConfig, blender, structureAccessor, chunk);
    }

    @Override
    public int getHeightOnGround(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
        noiseConfig = this.noiseConfig;
        return super.getHeightOnGround(x, z, heightmap, world, noiseConfig);
    }

    @Override
    public int getHeightInGround(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
        noiseConfig = this.noiseConfig;
        return super.getHeightInGround(x, z, heightmap, world, noiseConfig);
    }

}
