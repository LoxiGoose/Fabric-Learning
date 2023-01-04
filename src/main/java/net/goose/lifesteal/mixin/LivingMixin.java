package net.goose.lifesteal.mixin;

import net.goose.lifesteal.LifeSteal;
import net.goose.lifesteal.advancement.ModCriteria;
import net.goose.lifesteal.api.IHealthComponent;
import net.goose.lifesteal.component.ComponentRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingMixin {
    @Shadow
    @Nullable
    public abstract LivingEntity getAttacker();

    @Inject(method = "tryUseTotem", at = @At("HEAD"))
    private void totemUsed(final DamageSource source, final CallbackInfoReturnable<Boolean> cir) {
        if (!cir.isCancelled()) {
            LivingEntity livingEntity = ((LivingEntity) (Object) this);
            if (livingEntity instanceof ServerPlayerEntity serverPlayer) {
                IHealthComponent healthComponent = serverPlayer.getComponent(ComponentRegistry.HEALTH_DATA);
                if (healthComponent.getHeartDifference() >= 20) {
                    ModCriteria.USE_TOTEM_WHILE_MAX_20_HEARTS.trigger(serverPlayer);
                }
            }
        }
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onDeath(final CallbackInfo ci) {
        if (LifeSteal.config.shouldAllMobsGiveHearts.get()) {
            LivingEntity entity = this.getAttacker();
            if (entity instanceof ServerPlayerEntity serverPlayer) {
                IHealthComponent healthComponent = serverPlayer.getComponent(ComponentRegistry.HEALTH_DATA);
                healthComponent.setHeartDifference(healthComponent.getHeartDifference() + LifeSteal.config.amountOfHealthLostUponLoss.get());
                healthComponent.refreshHearts(false);
            }
        }
    }
}
