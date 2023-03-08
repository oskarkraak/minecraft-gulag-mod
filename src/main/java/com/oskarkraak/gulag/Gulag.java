package com.oskarkraak.gulag;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Difficulty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Gulag implements DedicatedServerModInitializer {

    public static final Logger LOGGER = LogManager.getLogger("Gulag");

    public static MinecraftServer server;

    @Override
    public void onInitializeServer() {
        ServerLifecycleEvents.SERVER_STARTED.register(this::loadGulag);
    }

    private void loadGulag(MinecraftServer server) {
        this.server = server;
        new Overworld(server, "gulag", 100L, Difficulty.HARD);
        LOGGER.info("Loaded dimension gulag:overworld");
    }

}
