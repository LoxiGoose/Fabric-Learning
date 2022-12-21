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
        LivingEntity killedEntity = this;

        if(killedEntity instanceof ServerPlayerEntity) {
            if(!killedEntity.isAlive()){
                int HeartDifference = HealthData.retrieveHeartDifference((IEntityDataSaver) this);

                LivingEntity killerEntity = killedEntity.getAttacker();

                int amountOfHealthLostUponLoss;

                if(LifeSteal.config.Maximums.MaximumHitPointsPlayerCanLose < 0 ){
                    if(20 + HeartDifference - LifeSteal.config.StartingConfigurations.AmountOfHitPointsGivenorTaken >= 0 || LifeSteal.config.LifestealRelated.PlayersGainHeartsFromNoHeartPlayers){
                        amountOfHealthLostUponLoss = LifeSteal.config.StartingConfigurations.AmountOfHitPointsGivenorTaken;
                    }else{
                        amountOfHealthLostUponLoss = 20 + HeartDifference;
                    }
                }else {
                    if (20 + HeartDifference - LifeSteal.config.StartingConfigurations.AmountOfHitPointsGivenorTaken >= (20 + LifeSteal.config.StartingConfigurations.StartingHitPointDifference) - LifeSteal.config.Maximums.MaximumHitPointsPlayerCanLose || LifeSteal.config.LifestealRelated.PlayersGainHeartsFromNoHeartPlayers) {
                        amountOfHealthLostUponLoss = LifeSteal.config.StartingConfigurations.AmountOfHitPointsGivenorTaken;
                    } else {
                        amountOfHealthLostUponLoss = HeartDifference + LifeSteal.config.Maximums.MaximumHitPointsPlayerCanLose;
                    }
                }

                if (killerEntity != null) {
                    if(killerEntity != killedEntity){
                        if (killerEntity instanceof ServerPlayerEntity serverPlayer && !LifeSteal.config.LifestealRelated.DisableLifesteal) {
                            if (LifeSteal.config.LifestealRelated.PlayersGainHeartsFromNoHeartPlayers) {
                                HealthData.setData((IEntityDataSaver) killerEntity, HealthData.retrieveHeartDifference((IEntityDataSaver) killerEntity) + amountOfHealthLostUponLoss);
                                HealthData.refreshHearts((IEntityDataSaver) killerEntity, killerEntity);
                            } else {

                                if (!LifeSteal.config.StartingConfigurations.DisableHeartLoss) {
                                    if (LifeSteal.config.Maximums.MaximumHitPointsPlayerCanLose > -1) {
                                        if (LifeSteal.config.StartingConfigurations.StartingHitPointDifference + HeartDifference > -LifeSteal.config.Maximums.MaximumHitPointsPlayerCanLose) {
                                            HealthData.setData((IEntityDataSaver) killerEntity, HealthData.retrieveHeartDifference((IEntityDataSaver) killerEntity) + amountOfHealthLostUponLoss);
                                            HealthData.refreshHearts((IEntityDataSaver) killerEntity, killerEntity);
                                        } else {
                                            serverPlayer.sendMessage(Text.translatable("This player doesn't have any hearts you can steal."));
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

                        if(!LifeSteal.config.LifestealRelated.DisableLifesteal){
                            if(!LifeSteal.config.StartingConfigurations.LoseHeartsOnlyWhenKilledByMob && LifeSteal.config.StartingConfigurations.LoseHeartsOnlyWhenKilledByPlayer){
                                if(killerEntity instanceof PlayerEntity){
                                    HealthData.setData((IEntityDataSaver) killedEntity, HealthData.retrieveHeartDifference((IEntityDataSaver) killedEntity) - amountOfHealthLostUponLoss);
                                    HealthData.refreshHearts((IEntityDataSaver) killedEntity, killedEntity);
                                }
                            }else{
                                HealthData.setData((IEntityDataSaver) killedEntity, HealthData.retrieveHeartDifference((IEntityDataSaver) killedEntity) - amountOfHealthLostUponLoss);
                                HealthData.refreshHearts((IEntityDataSaver) killedEntity, killedEntity);
                            }
                        }
                    }else {
                        HealthData.setData((IEntityDataSaver) killedEntity, HealthData.retrieveHeartDifference((IEntityDataSaver) killedEntity) - amountOfHealthLostUponLoss);
                        HealthData.refreshHearts((IEntityDataSaver) killedEntity, killedEntity);
                    }
                }else{
                    if (!LifeSteal.config.StartingConfigurations.LoseHeartsOnlyWhenKilledByMob && !LifeSteal.config.StartingConfigurations.LoseHeartsOnlyWhenKilledByPlayer) {
                        HealthData.setData((IEntityDataSaver) killedEntity, HealthData.retrieveHeartDifference((IEntityDataSaver) killedEntity) - amountOfHealthLostUponLoss);
                        HealthData.refreshHearts((IEntityDataSaver) killedEntity, killedEntity);
                    }
                }

            }
        }
    }
}
