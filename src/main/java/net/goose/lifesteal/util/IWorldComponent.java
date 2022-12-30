package net.goose.lifesteal.util;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.UUID;

public interface IWorldComponent extends ComponentV3 {
    HashMap getMap();
    void setBannedUUIDanditsBlockPos(UUID uuid, BlockPos blockPos);
    void removeBannedUUIDanditsBlockPos(UUID uuid, BlockPos blockPos);
}
