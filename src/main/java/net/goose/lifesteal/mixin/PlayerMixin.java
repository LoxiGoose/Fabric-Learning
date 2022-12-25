package net.goose.lifesteal.mixin;

import net.goose.lifesteal.LifeSteal;
import net.goose.lifesteal.util.HealthData;
import net.goose.lifesteal.util.IEntityDataSaver;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerMixin extends LivingEntity {
    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "dropInventory", at = @At("HEAD"))
    private void onDeath(final CallbackInfo info) {

        final int maximumheartsGainable = LifeSteal.config.maximumamountofheartsGainable.get();
        final int maximumheartsLoseable = LifeSteal.config.maximumamountofheartsLoseable.get();
        final int startingHitPointDifference = LifeSteal.config.startingHeartDifference.get();
        final int amountOfHealthLostUponLossConfig = LifeSteal.config.amountOfHealthLostUponLoss.get();
        final boolean playersGainHeartsifKillednoHeart = LifeSteal.config.playersGainHeartsifKillednoHeart.get();
        final boolean disableLifesteal = LifeSteal.config.disableLifesteal.get();
        final boolean disableHeartLoss = LifeSteal.config.disableHeartLoss.get();
        final boolean loseHeartsOnlyWhenKilledByMob = LifeSteal.config.loseHeartsOnlyWhenKilledByMob.get();
        final boolean loseHeartsOnlyWhenKilledByPlayer = LifeSteal.config.loseHeartsOnlyWhenKilledByPlayer.get();

        LivingEntity killedEntity = this;

        if (killedEntity instanceof ServerPlayerEntity) {
            if (!killedEntity.isAlive()) {
                int HeartDifference = HealthData.retrieveHeartDifference((IEntityDataSaver) this);

                LivingEntity killerEntity = killedEntity.getAttacker();

                int amountOfHealthLostUponLoss;

                if (maximumheartsLoseable < 0) {
                    if (20 + HeartDifference - amountOfHealthLostUponLossConfig >= 0 || playersGainHeartsifKillednoHeart) {
                        amountOfHealthLostUponLoss = amountOfHealthLostUponLossConfig;
                    } else {
                        amountOfHealthLostUponLoss = 20 + HeartDifference;
                    }
                } else {
                    if (20 + HeartDifference - amountOfHealthLostUponLossConfig >= (20 + startingHitPointDifference) - maximumheartsLoseable || playersGainHeartsifKillednoHeart) {
                        amountOfHealthLostUponLoss = amountOfHealthLostUponLossConfig;
                    } else {
                        amountOfHealthLostUponLoss = HeartDifference + maximumheartsLoseable;
                    }
                }

                if (killerEntity != null) {
                    if (killerEntity != killedEntity) {
                        if (killerEntity instanceof ServerPlayerEntity serverPlayer && !disableLifesteal) {

                            if (playersGainHeartsifKillednoHeart) {
                                HealthData.setData((IEntityDataSaver) killerEntity, HealthData.retrieveHeartDifference((IEntityDataSaver) killerEntity) + amountOfHealthLostUponLoss);
                                HealthData.refreshHearts((IEntityDataSaver) killerEntity, killerEntity);

                            } else {

                                if (!disableHeartLoss) {
                                    if (maximumheartsLoseable > -1) {
                                        if (startingHitPointDifference + HeartDifference > -maximumheartsLoseable) {
                                            HealthData.setData((IEntityDataSaver) killerEntity, HealthData.retrieveHeartDifference((IEntityDataSaver) killerEntity) + amountOfHealthLostUponLoss);
                                            HealthData.refreshHearts((IEntityDataSaver) killerEntity, killerEntity);
                                        } else {
                                            serverPlayer.sendMessage(Text.translatable("chat.message.lifesteal.no_more_hearts_to_steal"));
                                        }

                                    } else {
                                        HealthData.setData((IEntityDataSaver) killerEntity, HealthData.retrieveHeartDifference((IEntityDataSaver) killerEntity) + amountOfHealthLostUponLoss);
                                        HealthData.refreshHearts((IEntityDataSaver) killerEntity, killerEntity);
                                    }
                                } else {
                                    HealthData.setData((IEntityDataSaver) killerEntity, HealthData.retrieveHeartDifference((IEntityDataSaver) killerEntity) + amountOfHealthLostUponLoss);
                                    HealthData.refreshHearts((IEntityDataSaver) killerEntity, killerEntity);
                                }
                            }

                        }

                        if (!disableLifesteal) {
                            if (!loseHeartsOnlyWhenKilledByMob && loseHeartsOnlyWhenKilledByPlayer) {
                                if (killerEntity instanceof PlayerEntity) {
                                    HealthData.setData((IEntityDataSaver) killedEntity, HealthData.retrieveHeartDifference((IEntityDataSaver) killedEntity) - amountOfHealthLostUponLoss);
                                    HealthData.refreshHearts((IEntityDataSaver) killedEntity, killedEntity);
                                }
                            } else {
                                HealthData.setData((IEntityDataSaver) killedEntity, HealthData.retrieveHeartDifference((IEntityDataSaver) killedEntity) - amountOfHealthLostUponLoss);
                                HealthData.refreshHearts((IEntityDataSaver) killedEntity, killedEntity);
                            }
                        }
                    } else {
                        HealthData.setData((IEntityDataSaver) killedEntity, HealthData.retrieveHeartDifference((IEntityDataSaver) killedEntity) - amountOfHealthLostUponLoss);
                        HealthData.refreshHearts((IEntityDataSaver) killedEntity, killedEntity);
                    }
                } else {
                    if (!loseHeartsOnlyWhenKilledByMob && !loseHeartsOnlyWhenKilledByPlayer) {
                        HealthData.setData((IEntityDataSaver) killedEntity, HealthData.retrieveHeartDifference((IEntityDataSaver) killedEntity) - amountOfHealthLostUponLoss);
                        HealthData.refreshHearts((IEntityDataSaver) killedEntity, killedEntity);
                    }
                }

            }
        }
    }
}
