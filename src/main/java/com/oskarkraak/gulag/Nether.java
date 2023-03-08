package com.oskarkraak.gulag;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;

public class Nether extends VanillaWorld {

    private static final String DIMENSION_TYPE = "the_nether";
    private static final RegistryKey<World> WORLD_KEY = World.NETHER;
    private static final RegistryKey<ChunkGeneratorSettings> CHUNK_GENERATOR_SETTINGS_KEY = ChunkGeneratorSettings.NETHER;

    public Nether(MinecraftServer server, String namespace, long seed, Difficulty difficulty) {
        super(server, namespace, DIMENSION_TYPE, seed, WORLD_KEY, CHUNK_GENERATOR_SETTINGS_KEY, difficulty);
    }

}
