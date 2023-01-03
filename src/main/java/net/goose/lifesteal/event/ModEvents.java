package net.goose.lifesteal.event;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.goose.lifesteal.LifeSteal;
import net.goose.lifesteal.api.IHealthComponent;
import net.goose.lifesteal.component.ComponentRegistry;
import net.minecraft.server.network.ServerPlayerEntity;

public class ModEvents {
    public static void COPY_FROM(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
        IHealthComponent oldComponent = oldPlayer.getComponent(ComponentRegistry.HEALTH_DATA);
        IHealthComponent newComponent = newPlayer.getComponent(ComponentRegistry.HEALTH_DATA);
        newComponent.setHeartDifference(oldComponent.getHeartDifference());
        newComponent.refreshHearts(false);
        newPlayer.heal(newPlayer.getMaxHealth());
    }

    public static void register() {
        LifeSteal.LOGGER.debug("Registering ModEvents for " + LifeSteal.MOD_ID);
        ServerPlayerEvents.COPY_FROM.register(((oldPlayer, newPlayer, alive) -> COPY_FROM(oldPlayer, newPlayer, alive)));
    }
}
