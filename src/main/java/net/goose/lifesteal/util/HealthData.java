package net.goose.lifesteal.util;

import com.mojang.authlib.GameProfile;
import net.goose.lifesteal.LifeSteal;
import net.goose.lifesteal.advancement.ModCriteria;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.BannedPlayerEntry;
import net.minecraft.server.BannedPlayerList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;

import java.util.Iterator;
import java.util.Set;

public class HealthData{
    public static int retrieveHeartDifference(IEntityDataSaver player){
        NbtCompound nbt = player.getPersistentData();
        return nbt.getInt("heartdifference");
    }
    public static void setData(IEntityDataSaver player, int hearts) {
        NbtCompound nbt = player.getPersistentData();

        nbt.putInt("heartdifference", hearts);
    }
    public static void refreshHearts(IEntityDataSaver player, LivingEntity livingEntity, boolean healtoMax){
        final int maximumheartsGainable = LifeSteal.config.maximumamountofheartsGainable.get();
        final int maximumheartsLoseable = LifeSteal.config.maximumamountofheartsLoseable.get();
        final int startingHitPointDifference = LifeSteal.config.startingHeartDifference.get();

        NbtCompound nbt = player.getPersistentData();
        int heartDifference = nbt.getInt("heartdifference");

        EntityAttributeInstance attributeInstance = livingEntity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        Set<EntityAttributeModifier> attributeModifiers = attributeInstance.getModifiers();

        if(maximumheartsGainable > 0){
            if(heartDifference - startingHitPointDifference >= maximumheartsGainable ) {
                heartDifference = maximumheartsGainable + startingHitPointDifference;

                if(LifeSteal.config.tellPlayersIfReachedMaxHearts.get()){
                    livingEntity.sendMessage(Text.translatable("chat.message.lifesteal.reached_max_hearts"));
                }
                HealthData.setData(player, heartDifference);
            }
        }

        if(maximumheartsLoseable >= 0){
            if(heartDifference < startingHitPointDifference - maximumheartsLoseable){
                heartDifference = startingHitPointDifference - maximumheartsLoseable;
                HealthData.setData(player, heartDifference);
            }
        }

        if(!attributeModifiers.isEmpty()){

            Iterator<EntityAttributeModifier> attributeModifierIterator = attributeModifiers.iterator();

            boolean FoundAttribute = false;

            while (attributeModifierIterator.hasNext()) {
                EntityAttributeModifier attributeModifier = attributeModifierIterator.next();
                if (attributeModifier != null && attributeModifier.getName().equals("LifeStealHealthModifier")) {
                    FoundAttribute = true;

                    attributeInstance.removeModifier(attributeModifier);

                    EntityAttributeModifier newmodifier = new EntityAttributeModifier("LifeStealHealthModifier", heartDifference, EntityAttributeModifier.Operation.ADDITION);
                    attributeInstance.addPersistentModifier(newmodifier);
                }
            }

            if(!FoundAttribute){
                EntityAttributeModifier attributeModifier = new EntityAttributeModifier("LifeStealHealthModifier", heartDifference, EntityAttributeModifier.Operation.ADDITION);
                attributeInstance.addPersistentModifier(attributeModifier);
            }
        }else{
            EntityAttributeModifier attributeModifier = new EntityAttributeModifier("LifeStealHealthModifier", heartDifference, EntityAttributeModifier.Operation.ADDITION);
            attributeInstance.addPersistentModifier(attributeModifier);
        }

        if(heartDifference >= 20 && livingEntity instanceof ServerPlayerEntity serverPlayer){
            ModCriteria.GET_10_MAX_HEARTS.trigger(serverPlayer);
        }

        if(livingEntity.getHealth() > livingEntity.getMaxHealth() || healtoMax){
            livingEntity.setHealth(livingEntity.getMaxHealth());
        }

        if(livingEntity.getMaxHealth() <= 1 && heartDifference <= -20){
            setData(player, startingHitPointDifference);
            refreshHearts(player, livingEntity, true);

            if (livingEntity instanceof ServerPlayerEntity serverPlayer) {

                if (!livingEntity.world.getServer().isSingleplayer()) {

                    ItemStack playerHead = new ItemStack(Blocks.PLAYER_HEAD);
                    NbtCompound skullOwner = new NbtCompound();
                    skullOwner.putString("Name", serverPlayer.getName().getString());
                    skullOwner.putUuid("Id", serverPlayer.getUuid());

                    NbtCompound compoundTag = new NbtCompound();
                    compoundTag.put("SkullOwner", skullOwner);
                    playerHead.setNbt(compoundTag);
                    serverPlayer.getInventory().insertStack(playerHead);
                    serverPlayer.getInventory().dropAll();

                    Text text = Text.translatable("bannedmessage.lifesteal.lost_max_hearts");

                    BannedPlayerList userbanlist = serverPlayer.getServer().getPlayerManager().getUserBanList();
                    serverPlayer.getGameProfile();
                    GameProfile gameprofile = serverPlayer.getGameProfile();
                    BannedPlayerEntry userbanlistentry = new BannedPlayerEntry(gameprofile, null, "Lifesteal", null, text == null ? null : text.getString());
                    userbanlist.add(userbanlistentry);

                    if (serverPlayer != null) {
                        serverPlayer.networkHandler.disconnect(Text.translatable("bannedmessage.lifesteal.lost_max_hearts"));
                    }
                } else if (!serverPlayer.isSpectator()) {
                    serverPlayer.changeGameMode(GameMode.SPECTATOR);
                    livingEntity.sendMessage(Text.translatable("chat.message.lifesteal.lost_max_hearts"));
                }
            }
        }
    }
}
