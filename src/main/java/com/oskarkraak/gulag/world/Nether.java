package com.oskarkraak.gulag.world;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Difficulty;

public class Nether extends VanillaWorld {

    private static final String DIMENSION_TYPE = "the_nether";

    public Nether(MinecraftServer server, String namespace, long seed, Difficulty difficulty) {
        super(server, namespace, DIMENSION_TYPE, seed, difficulty);
    }

}
