package net.goose.lifesteal.api;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.UUID;

public interface ILevelComponent extends ComponentV3 {
    HashMap getMap();

    void setUUIDanditsBlockPos(UUID uuid, BlockPos blockPos);

    void removeUUIDanditsBlockPos(UUID uuid, BlockPos blockPos);
}
