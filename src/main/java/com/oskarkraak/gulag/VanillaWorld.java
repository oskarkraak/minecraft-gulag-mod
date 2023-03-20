package com.oskarkraak.gulag;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import xyz.nucleoid.fantasy.Fantasy;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;
import xyz.nucleoid.fantasy.RuntimeWorldHandle;

public class VanillaWorld {

    private final RuntimeWorldHandle worldHandle;

    public VanillaWorld(MinecraftServer server, String namespace, String dimensionType, long seed,
                        Difficulty difficulty) {
        this(server, namespace, dimensionType, seed, difficulty, false);
    }

    public VanillaWorld(MinecraftServer server, String namespace, String dimensionType, long seed,
                        Difficulty difficulty, boolean shouldTickTime) {
        RegistryKey<World> minecraftWorldKey =
                RegistryKey.of(RegistryKeys.WORLD, new Identifier("minecraft", dimensionType));
        RuntimeWorldConfig worldConfig = new RuntimeWorldConfig()
                .setDimensionType(getDimensionTypeKey(dimensionType))
                .setDifficulty(difficulty)
                .setGenerator(server.getWorld(minecraftWorldKey).getChunkManager().getChunkGenerator())
                .setSeed(seed)
                .setShouldTickTime(shouldTickTime);
        Identifier id = new Identifier(namespace, dimensionType);
        worldHandle = Fantasy.get(server).getOrOpenPersistentWorld(id, worldConfig);
    }

    private RegistryKey<DimensionType> getDimensionTypeKey(String dimensionType) {
        return RegistryKey.of(RegistryKeys.DIMENSION_TYPE, new Identifier("minecraft", dimensionType));
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
        return worldHandle.asWorld().getSeed();
    }

}
