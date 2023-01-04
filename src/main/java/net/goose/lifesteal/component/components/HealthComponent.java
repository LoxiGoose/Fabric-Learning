package net.goose.lifesteal.component.components;

import com.mojang.authlib.GameProfile;
import net.goose.lifesteal.LifeSteal;
import net.goose.lifesteal.advancement.ModCriteria;
import net.goose.lifesteal.api.IHealthComponent;
import net.goose.lifesteal.api.ILevelComponent;
import net.goose.lifesteal.component.ComponentRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.BannedPlayerEntry;
import net.minecraft.server.BannedPlayerList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class HealthComponent implements IHealthComponent {
    private final LivingEntity livingEntity;
    private int heartDifference = LifeSteal.config.startingHeartDifference.get();

    public HealthComponent(@Nullable final PlayerEntity playerEntity) {
        this.livingEntity = playerEntity;
    }

    @Override
    public void revivedTeleport(ServerWorld level, ILevelComponent levelComponent, boolean synchronize) {
        if (livingEntity instanceof ServerPlayerEntity serverPlayer) {
            if (!serverPlayer.getWorld().isClient) {
                HashMap hashMap = levelComponent.getMap();
                BlockPos blockPos = (BlockPos) hashMap.get(serverPlayer.getUuid());

                if (blockPos != null) {
                    levelComponent.removeUUIDanditsBlockPos(serverPlayer.getUuid(), blockPos);
                    if (serverPlayer.getWorld() == level) {
                        serverPlayer.networkHandler.requestTeleport(blockPos.getX(), blockPos.getY(), blockPos.getZ(), serverPlayer.getPitch(), serverPlayer.getYaw());
                    } else {
                        serverPlayer.teleport(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), serverPlayer.getPitch(), serverPlayer.getYaw());
                    }
                    if (serverPlayer.isSpectator()) {
                        serverPlayer.changeGameMode(GameMode.SURVIVAL);
                    }
                    int tickTime = 600;
                    livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, tickTime, 3));
                    livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, tickTime, 3));
                    livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, tickTime, 3));
                    if (synchronize) {
                        serverPlayer.jump();
                        serverPlayer.updateTrackedPosition(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                    }
                    ModCriteria.REVIVED.trigger(serverPlayer);
                }
            }
        }
    }

    @Override
    public void revivedTeleport(ServerWorld level, ILevelComponent levelComponent) {
        revivedTeleport(level, levelComponent, true);
    }

    @Override
    public void spawnPlayerHead(ServerPlayerEntity serverPlayer) {
        World level = serverPlayer.world;
        BlockPos blockPos = serverPlayer.getBlockPos();
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity != null) {
            blockEntity.markRemoved();
        }
        BlockState playerHeadState = Blocks.PLAYER_HEAD.getDefaultState().with(Properties.ROTATION, Integer.valueOf(MathHelper.floor((double)((180.0F + serverPlayer.getYaw()) * 16.0F / 360.0F) + 0.5) & 15));
        level.setBlockState(blockPos, playerHeadState);
        SkullBlockEntity playerHeadEntity = new SkullBlockEntity(blockPos, playerHeadState);
        playerHeadEntity.setOwner(serverPlayer.getGameProfile());
        level.addBlockEntity(playerHeadEntity);
    }

    @Override
    public int getHeartDifference() {
        return this.heartDifference;
    }

    @Override
    public void setHeartDifference(int hearts) {
        if (!livingEntity.world.isClient) {
            this.heartDifference = hearts;
            ComponentRegistry.HEALTH_DATA.sync(this.livingEntity);
        }
    }

    @Override
    public void refreshHearts(boolean healtoMax) {
        if(!livingEntity.world.isClient){
            final int maximumheartsGainable = LifeSteal.config.maximumamountofheartsGainable.get();
            final int maximumheartsLoseable = LifeSteal.config.maximumamountofheartsLoseable.get();
            final int defaultheartDifference = LifeSteal.config.startingHeartDifference.get();

            EntityAttributeInstance attributeInstance = livingEntity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
            Set<EntityAttributeModifier> attributeModifiers = attributeInstance.getModifiers();

            if (maximumheartsGainable > 0) {
                if (this.heartDifference - defaultheartDifference >= maximumheartsGainable) {
                    this.heartDifference = maximumheartsGainable + defaultheartDifference;

                    if (LifeSteal.config.tellPlayersIfReachedMaxHearts.get()) {
                        livingEntity.sendMessage(Text.translatable("chat.message.lifesteal.reached_max_hearts"));
                    }
                }
            }

            if (maximumheartsLoseable >= 0) {
                if (this.heartDifference < defaultheartDifference - maximumheartsLoseable) {
                    this.heartDifference = defaultheartDifference - maximumheartsLoseable;
                }
            }

            if (!attributeModifiers.isEmpty()) {

                Iterator<EntityAttributeModifier> attributeModifierIterator = attributeModifiers.iterator();

                boolean FoundAttribute = false;

                while (attributeModifierIterator.hasNext()) {
                    EntityAttributeModifier attributeModifier = attributeModifierIterator.next();
                    if (attributeModifier != null && attributeModifier.getName().equals("LifeStealHealthModifier")) {
                        FoundAttribute = true;

                        attributeInstance.removeModifier(attributeModifier);

                        EntityAttributeModifier newmodifier = new EntityAttributeModifier("LifeStealHealthModifier", this.heartDifference, EntityAttributeModifier.Operation.ADDITION);
                        attributeInstance.addPersistentModifier(newmodifier);
                    }
                }

                if (!FoundAttribute) {
                    EntityAttributeModifier attributeModifier = new EntityAttributeModifier("LifeStealHealthModifier", this.heartDifference, EntityAttributeModifier.Operation.ADDITION);
                    attributeInstance.addPersistentModifier(attributeModifier);
                }
            } else {
                EntityAttributeModifier attributeModifier = new EntityAttributeModifier("LifeStealHealthModifier", this.heartDifference, EntityAttributeModifier.Operation.ADDITION);
                attributeInstance.addPersistentModifier(attributeModifier);
            }

            if (this.heartDifference >= 20 && livingEntity instanceof ServerPlayerEntity serverPlayer) {
                ModCriteria.GET_10_MAX_HEARTS.trigger(serverPlayer);
            }

            if (livingEntity.getHealth() > livingEntity.getMaxHealth() || healtoMax) {
                livingEntity.setHealth(livingEntity.getMaxHealth());
            }

            if (livingEntity.getMaxHealth() <= 1 && this.heartDifference <= -20) {

                if (livingEntity instanceof ServerPlayerEntity serverPlayer) {

                    this.heartDifference = defaultheartDifference;

                    refreshHearts(true);

                    MinecraftServer server = livingEntity.world.getServer();

                    if (!server.isSingleplayer()) {

                        if (LifeSteal.config.playersSpawnHeadUponDeath.get()) {
                            spawnPlayerHead(serverPlayer);
                        }
                        serverPlayer.getInventory().dropAll();

                        @Nullable Text text = Text.translatable("bannedmessage.lifesteal.lost_max_hearts");

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

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putInt("heartdifference", getHeartDifference());
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        setHeartDifference(tag.getInt("heartdifference"));
    }
}
