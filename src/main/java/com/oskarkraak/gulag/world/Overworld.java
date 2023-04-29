package com.oskarkraak.gulag.world;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Difficulty;

public class Overworld extends VanillaWorld {

    private static final String DIMENSION_TYPE = "overworld";

    public Overworld(MinecraftServer server, String namespace, long seed, Difficulty difficulty) {
        super(server, namespace, DIMENSION_TYPE, seed, difficulty, true);
    }

}
