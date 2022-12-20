package net.goose.lifesteal.event;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.goose.lifesteal.LifeSteal;
import net.goose.lifesteal.util.HealthData;
import net.goose.lifesteal.util.IEntityDataSaver;

public class ModEvents {
    public static void register(){
        LifeSteal.LOGGER.debug("Registering ModEvents for "+LifeSteal.MOD_ID);
        ServerPlayerEvents.COPY_FROM.register(((oldPlayer, newPlayer, alive) -> {
            HealthData.setData((IEntityDataSaver) newPlayer, HealthData.retrieveHeartDifference((IEntityDataSaver) oldPlayer));
            HealthData.refreshHearts((IEntityDataSaver) newPlayer, newPlayer);
            newPlayer.heal(newPlayer.getMaxHealth());
        }));
    }
}
