package net.goose.lifesteal.mixin;

import net.goose.lifesteal.LifeSteal;
import net.goose.lifesteal.api.IHealthComponent;
import net.goose.lifesteal.component.ComponentRegistry;
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
        final boolean loseHeartsOnlyWhenKilledByEntity = LifeSteal.config.loseHeartsOnlyWhenKilledByEntity.get();
        final boolean loseHeartsOnlyWhenKilledByPlayer = LifeSteal.config.loseHeartsOnlyWhenKilledByPlayer.get();
        final IHealthComponent healthComponent = this.getComponent(ComponentRegistry.HEALTH_DATA);

        LivingEntity killedEntity = this;

        if (killedEntity instanceof ServerPlayerEntity) {
            if (!killedEntity.isAlive()) {
                int HeartDifference = healthComponent.getHeartDifference();

                LivingEntity killerEntity = killedEntity.getAttacker();
                boolean killerEntityIsPlayer = killerEntity instanceof ServerPlayerEntity;
                ServerPlayerEntity serverPlayer = null;
                if (killerEntityIsPlayer) {
                    serverPlayer = (ServerPlayerEntity) killerEntity;
                }

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

                if (killerEntity != null) { // IF THERE IS A KILLER ENTITY
                    if (killerEntity != killedEntity) { // IF IT'S NOT THEMSELVES (Shooting themselves with an arrow lol)
                        // EVERYTHING BELOW THIS COMMENT IS CODE FOR MAKING THE KILLER PERSON'S HEART DIFFERENCE GO UP.
                        if (killerEntityIsPlayer && !disableLifesteal) {
                            final IHealthComponent killerComponent = killerEntity.getComponent(ComponentRegistry.HEALTH_DATA);

                            if (playersGainHeartsifKillednoHeart) {
                                killerComponent.setHeartDifference(killerComponent.getHeartDifference() + amountOfHealthLostUponLoss);
                                killerComponent.refreshHearts(false);

                            } else {

                                if (!disableHeartLoss) {
                                    if (maximumheartsLoseable > -1) {
                                        if (startingHitPointDifference + HeartDifference > -maximumheartsLoseable) {
                                            killerComponent.setHeartDifference(killerComponent.getHeartDifference() + amountOfHealthLostUponLoss);
                                            killerComponent.refreshHearts(false);
                                        } else {
                                            serverPlayer.sendMessage(Text.translatable("chat.message.lifesteal.no_more_hearts_to_steal"));
                                        }

                                    } else {
                                        killerComponent.setHeartDifference(killerComponent.getHeartDifference() + amountOfHealthLostUponLoss);
                                        killerComponent.refreshHearts(false);
                                    }
                                } else {
                                    serverPlayer.sendMessage(Text.translatable("chat.message.lifesteal.no_more_hearts_to_steal"));
                                }
                            }

                        }

                        // EVERYTHING BELOW THIS COMMENT IS CODE FOR LOWERING THE KILLED PERSON'S HEART DIFFERENCE IF THERE WAS A KILLER ENTITY
                        if (!disableHeartLoss) {
                            if (loseHeartsOnlyWhenKilledByPlayer && !loseHeartsOnlyWhenKilledByEntity) {
                                if (killerEntityIsPlayer && !disableLifesteal) {
                                    healthComponent.setHeartDifference(healthComponent.getHeartDifference() - amountOfHealthLostUponLoss);
                                    healthComponent.refreshHearts(false);
                                }
                            } else {
                                if (disableLifesteal) {
                                    if (!killerEntityIsPlayer) {
                                        healthComponent.setHeartDifference(healthComponent.getHeartDifference() - amountOfHealthLostUponLoss);
                                        healthComponent.refreshHearts(false);
                                    }
                                } else {
                                    healthComponent.setHeartDifference(healthComponent.getHeartDifference() - amountOfHealthLostUponLoss);
                                    healthComponent.refreshHearts(false);
                                }
                            }
                        }
                    } else if (!disableHeartLoss) { // IF THIS IS THEMSELVES
                        healthComponent.setHeartDifference(healthComponent.getHeartDifference() - amountOfHealthLostUponLoss);
                        healthComponent.refreshHearts(false);
                    }
                } else {
                    if (!loseHeartsOnlyWhenKilledByEntity && !loseHeartsOnlyWhenKilledByPlayer && !disableHeartLoss) {
                        healthComponent.setHeartDifference(healthComponent.getHeartDifference() - amountOfHealthLostUponLoss);
                        healthComponent.refreshHearts(false);
                    }
                }

            }
        }
    }
}
