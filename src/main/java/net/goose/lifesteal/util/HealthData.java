package net.goose.lifesteal.util;

import blue.endless.jankson.annotation.Nullable;
import com.mojang.authlib.GameProfile;
import net.goose.lifesteal.LifeSteal;
import net.goose.lifesteal.advancement.ModCriteria;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.BannedPlayerEntry;
import net.minecraft.server.BannedPlayerList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;

import java.util.Iterator;
import java.util.Set;

public class HealthData {
    public static int retrieveHeartDifference(IEntityDataSaver player){
        NbtCompound nbt = player.getPersistentData();
        return nbt.getInt("heartdifference");
    }
    public static void setData(IEntityDataSaver player, int hearts) {

        NbtCompound nbt = player.getPersistentData();
        int heartDifference;

        heartDifference = hearts;

        nbt.putInt("heartdifference", heartDifference);
    }
    public static int maximumheartsGainable = LifeSteal.config.Maximums.MaximumHitPointsPlayerCanGet;
    public static int maximumheartsLoseable = LifeSteal.config.Maximums.MaximumHitPointsPlayerCanLose;
    public static int startingHitPointDifference = LifeSteal.config.StartingConfigurations.StartingHitPointDifference;
    public static void refreshHearts(IEntityDataSaver player, LivingEntity livingEntity){
        NbtCompound nbt = player.getPersistentData();
        int heartDifference = nbt.getInt("heartdifference");

        EntityAttributeInstance attributeInstance = livingEntity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        Set<EntityAttributeModifier> attributeModifiers = attributeInstance.getModifiers();

        if(maximumheartsGainable > 0){
            if(heartDifference - startingHitPointDifference >= maximumheartsGainable ) {
                heartDifference = maximumheartsGainable + startingHitPointDifference;

                livingEntity.sendMessage(Text.translatable("You have reached max hearts."));
            }
        }

        if(maximumheartsLoseable >= 0){
            if(heartDifference < startingHitPointDifference - maximumheartsLoseable){
                heartDifference = startingHitPointDifference - maximumheartsLoseable;
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

        if(livingEntity.getHealth() > livingEntity.getMaxHealth()){
            livingEntity.setHealth(livingEntity.getMaxHealth());
        }

        if(livingEntity.getMaxHealth() <= 1 && heartDifference <= -20){
            setData(player, LifeSteal.config.StartingConfigurations.StartingHitPointDifference);
            refreshHearts(player, livingEntity);

            if (livingEntity instanceof ServerPlayerEntity serverPlayer) {

                if (LifeSteal.config.StartingConfigurations.BannedUponLosingAllHearts) {

                    @Nullable Text text = Text.translatable("You have lost all your lives and max hearts, you are now permanently banned till further notice.");

                    BannedPlayerList userbanlist = serverPlayer.getServer().getPlayerManager().getUserBanList();
                    serverPlayer.getGameProfile();
                    GameProfile gameprofile = serverPlayer.getGameProfile();
                    BannedPlayerEntry userbanlistentry = new BannedPlayerEntry(gameprofile, null, "Lifesteal", null, text == null ? null : text.getString());
                    userbanlist.add(userbanlistentry);

                    if (serverPlayer != null) {
                        serverPlayer.networkHandler.disconnect(Text.translatable("You have lost all your max hearts, you are now permanently banned till further notice."));
                    }
                } else if (!serverPlayer.isSpectator()) {
                    serverPlayer.changeGameMode(GameMode.SPECTATOR);
                    livingEntity.sendMessage(Text.translatable("You have lost all your max hearts. You are now permanently dead."));
                }
            }
        }
    }
}
