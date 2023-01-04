package net.goose.lifesteal.api;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public interface IHealthComponent extends ComponentV3 {
    void revivedTeleport(ServerWorld level, ILevelComponent levelComponent, boolean synchronize);

    void revivedTeleport(ServerWorld level, ILevelComponent levelComponent);

    void spawnPlayerHead(ServerPlayerEntity serverPlayer);

    int getHeartDifference();

    void setHeartDifference(int hearts);

    void refreshHearts(boolean healtoMax);
}
