package net.goose.lifesteal.mixin;

import com.mojang.authlib.GameProfile;
import net.goose.lifesteal.LifeSteal;
import net.goose.lifesteal.advancement.ModCriteria;
import net.goose.lifesteal.util.HealthData;
import net.goose.lifesteal.util.IEntityDataSaver;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingMixin{
    @Shadow @Nullable public abstract LivingEntity getAttacker();

    @Inject(method = "tryUseTotem", at = @At("HEAD"))
    private void totemUsed(final DamageSource source, final CallbackInfoReturnable<Boolean> cir) {
        if(this instanceof IEntityDataSaver dataSaver){
            if(dataSaver instanceof ServerPlayerEntity serverPlayer){
                if(HealthData.retrieveHeartDifference(dataSaver) >= 20){
                    ModCriteria.USE_TOTEM_WHILE_MAX_20_HEARTS.trigger(serverPlayer);
                }
            }
        }
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onDeath(final CallbackInfo ci){
        if(LifeSteal.config.shouldAllMobsGiveHearts.get()){
            LivingEntity entity = this.getAttacker();
            if(entity instanceof ServerPlayerEntity serverPlayer){
                HealthData.setData((IEntityDataSaver) serverPlayer, HealthData.retrieveHeartDifference((IEntityDataSaver) serverPlayer) + LifeSteal.config.amountOfHealthLostUponLoss.get());
                HealthData.refreshHearts((IEntityDataSaver) serverPlayer, entity);
            }
        }
    }
}
