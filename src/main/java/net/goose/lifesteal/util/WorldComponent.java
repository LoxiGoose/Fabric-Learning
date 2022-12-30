package net.goose.lifesteal.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

public class WorldComponent implements IWorldComponent {
    private final World level;

    public WorldComponent(@Nullable final World level) {
        this.level = level;
    }

    private HashMap<UUID, BlockPos> bannedMap = new HashMap<>();

    @Override
    public HashMap getMap() {
        return bannedMap;
    }
    @Override
    public void setBannedUUIDanditsBlockPos(UUID uuid, BlockPos blockPos) {
        bannedMap.put(uuid, blockPos);
    }

    @Override
    public void removeBannedUUIDanditsBlockPos(UUID uuid, BlockPos blockPos) {
        bannedMap.remove(uuid, blockPos);
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        NbtList listTag = new NbtList();

        for(UUID uuid: bannedMap.keySet()){

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
        listTag.forEach( (tag1) -> {
            NbtCompound compoundTag = (NbtCompound) tag1;
            UUID uuid = compoundTag.getUuid("Key");
            BlockPos blockPos = NbtHelper.toBlockPos(compoundTag.getCompound("Value"));

            setBannedUUIDanditsBlockPos(uuid, blockPos);
        });
    }
}
