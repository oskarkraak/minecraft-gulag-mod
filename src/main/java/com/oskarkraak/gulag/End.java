package com.oskarkraak.gulag;

import com.oskarkraak.gulag.mixin.ServerWorldAccessor;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;

public class End extends VanillaWorld {

    private static final String DIMENSION_TYPE = "the_end";
    private static final RegistryKey<World> WORLD_KEY = World.END;
    private static final RegistryKey<ChunkGeneratorSettings> CHUNK_GENERATOR_SETTINGS_KEY = ChunkGeneratorSettings.END;

    public End(MinecraftServer server, String namespace, long seed, Difficulty difficulty) {
        super(server, namespace, DIMENSION_TYPE, seed, WORLD_KEY, CHUNK_GENERATOR_SETTINGS_KEY, difficulty);
        EnderDragonFight enderDragonFight = new EnderDragonFight(this.asWorld(), seed, getNewNbtCompound());
        ((ServerWorldAccessor) this.asWorld()).setEnderDragonFight(enderDragonFight);
    }

    private NbtCompound getNewNbtCompound() {
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putBoolean("NeedsStateScanning", false);
        nbtCompound.putBoolean("DragonKilled", false);
        nbtCompound.putBoolean("PreviouslyKilled", false);
        return nbtCompound;
    }

}
