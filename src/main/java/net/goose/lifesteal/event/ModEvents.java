package net.goose.lifesteal.event;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.goose.lifesteal.LifeSteal;
import net.goose.lifesteal.util.HealthData;
import net.goose.lifesteal.util.IEntityDataSaver;
import net.minecraft.server.network.ServerPlayerEntity;

public class ModEvents {
    public static void COPY_FROM(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
        HealthData.setData((IEntityDataSaver) newPlayer, HealthData.retrieveHeartDifference((IEntityDataSaver) oldPlayer));
        HealthData.refreshHearts((IEntityDataSaver) newPlayer, newPlayer);
        newPlayer.heal(newPlayer.getMaxHealth());
    }

    public static void register() {
        LifeSteal.LOGGER.debug("Registering ModEvents for " + LifeSteal.MOD_ID);
        ServerPlayerEvents.COPY_FROM.register(((oldPlayer, newPlayer, alive) -> COPY_FROM(oldPlayer, newPlayer, alive)));
    }
}
