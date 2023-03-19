package com.oskarkraak.gulag;

import com.oskarkraak.gulag.event.ServerWorldEvents;
import com.oskarkraak.gulag.mixin.ServerWorldAccessor;
import com.oskarkraak.gulag.util.NbtCompoundUtils;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;

public class End extends VanillaWorld {

    private static final String DIMENSION_TYPE = "the_end";
    private static final RegistryKey<World> WORLD_KEY = World.END;
    private static final RegistryKey<ChunkGeneratorSettings> CHUNK_GENERATOR_SETTINGS_KEY = ChunkGeneratorSettings.END;

    public End(MinecraftServer server, String namespace, long seed, Difficulty difficulty) {
        super(server, namespace, DIMENSION_TYPE, seed, WORLD_KEY, CHUNK_GENERATOR_SETTINGS_KEY, difficulty);
        ServerWorldEvents.LEVEL_SAVED.register(this::onSave);
        startEnderDragonFight();
    }

    public void startEnderDragonFight() {
        NbtCompound nbtCompound = this.getGulagDragonFightNbtCompound();
        if (!nbtCompound.contains("PreviouslyKilled")) {
            nbtCompound.putBoolean("NeedsStateScanning", true);
            nbtCompound.putBoolean("DragonKilled", false);
            nbtCompound.putBoolean("PreviouslyKilled", false);
        }
        EnderDragonFight enderDragonFight = new EnderDragonFight(this.asWorld(), this.getSeed(), nbtCompound);
        ((ServerWorldAccessor) this.asWorld()).setEnderDragonFight(enderDragonFight);
    }

    private NbtCompound getMinecraftDragonFightNbtCompound() {
        return this.asWorld().getServer().getSaveProperties().getDragonFight();
    }

    /**
     * Loads the dragon fight data from the save properties and filters out the data from minecraft:the_end.
     *
     * @return A NbtCompound containing only the dragon fight data from this world
     */
    private NbtCompound getGulagDragonFightNbtCompound() {
        NbtCompound minecraftNbtCompound = getMinecraftDragonFightNbtCompound();
        NbtCompound gulagNbtCompound = minecraftNbtCompound.copy();
        String prefix = getNbtCompoundKeyPrefix();
        NbtCompoundUtils.removeAllElementsWithoutPrefix(prefix, gulagNbtCompound);
        NbtCompoundUtils.removePrefix(prefix, gulagNbtCompound);
        return gulagNbtCompound;
    }

    private String getNbtCompoundKeyPrefix() {
        return this.asWorld().getRegistryKey().getValue().toString() + "-";
    }

    /**
     * Merges the dragon fight into the Minecraft dragon fight.
     * Only saves after another dragon fight has been saved.
     */
    public void onSave(ServerWorld world) {
        boolean isThis = this.getRegistryKey() == world.getRegistryKey();
        boolean worldHasEnderDragonFight = world.getEnderDragonFight() != null;
        if (!isThis && worldHasEnderDragonFight) {
            saveEnderDragonFight();
        }
    }

    private void saveEnderDragonFight() {
        NbtCompound minecraftDragonFightNbtCompound = getMinecraftDragonFightNbtCompound();
        NbtCompoundUtils.removeAllElementsWithPrefix(this.getNbtCompoundKeyPrefix(), minecraftDragonFightNbtCompound);
        NbtCompound gulagDragonFightNbtCompound = this.asWorld().getEnderDragonFight().toNbt();
        NbtCompoundUtils.addPrefix(this.getNbtCompoundKeyPrefix(), gulagDragonFightNbtCompound);
        NbtCompound dragonFightNbtCompound =
                NbtCompoundUtils.merge(minecraftDragonFightNbtCompound, gulagDragonFightNbtCompound);
        this.asWorld().getServer().getSaveProperties().setDragonFight(dragonFightNbtCompound);
    }

    @Override
    public void delete() {
        super.delete();
        NbtCompound dragonFightNbtCompound = getMinecraftDragonFightNbtCompound();
        NbtCompoundUtils.removeAllElementsWithPrefix(this.getNbtCompoundKeyPrefix(), dragonFightNbtCompound);
    }

}
