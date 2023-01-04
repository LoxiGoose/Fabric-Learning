package net.goose.lifesteal.component.components;

import net.goose.lifesteal.api.ILevelComponent;
import net.goose.lifesteal.component.ComponentRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

public class LevelComponent implements ILevelComponent {
    private final World level;

    public LevelComponent(@Nullable final World level) {
        this.level = level;
    }

    private final HashMap<UUID, BlockPos> bannedMap = new HashMap<>();

    @Override
    public HashMap getMap() {
        return bannedMap;
    }

    @Override
    public void setUUIDanditsBlockPos(UUID uuid, BlockPos blockPos) {
        if (!bannedMap.containsKey(uuid)) {
            bannedMap.put(uuid, blockPos);
            ComponentRegistry.UUID_AND_BLOCKPOS_MAP.sync(this.level);
        }
    }

    @Override
    public void removeUUIDanditsBlockPos(UUID uuid, BlockPos blockPos) {
        if (bannedMap.containsKey(uuid)) {
            bannedMap.remove(uuid, blockPos);
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        NbtList listTag = new NbtList();

        for (UUID uuid : bannedMap.keySet()) {

            NbtCompound playerCompoundTag = new NbtCompound();
            BlockPos blockPos = bannedMap.get(uuid);
            playerCompoundTag.putUuid("Key", uuid);
            playerCompoundTag.put("Value", NbtHelper.fromBlockPos(blockPos));

            listTag.add(playerCompoundTag);
        }

        tag.put("Map", listTag);
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        NbtList listTag = (NbtList) tag.get("Map");
        listTag.forEach((tag1) -> {
            NbtCompound compoundTag = (NbtCompound) tag1;
            UUID uuid = compoundTag.getUuid("Key");
            BlockPos blockPos = NbtHelper.toBlockPos(compoundTag.getCompound("Value"));

            setUUIDanditsBlockPos(uuid, blockPos);
        });
    }
}
