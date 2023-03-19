package com.oskarkraak.gulag.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.world.ServerWorld;

public final class ServerWorldEvents {

    public static final Event<LevelSaved> LEVEL_SAVED =
            EventFactory.createArrayBacked(LevelSaved.class, (callbacks) -> (serverWorld) -> {
                for (LevelSaved callback : callbacks) {
                    callback.onLevelSaved(serverWorld);
                }
            });

    private ServerWorldEvents() {
    }

    @FunctionalInterface
    public interface LevelSaved {
        void onLevelSaved(ServerWorld serverWorld);
    }

}
