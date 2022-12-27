package net.goose.lifesteal.mixin;

import net.goose.lifesteal.util.HealthData;
import net.goose.lifesteal.util.IEntityDataSaver;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerMixin extends LivingEntity {
    protected ServerPlayerMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "onSpawn", at = @At("HEAD"))
    private void onSpawn(final CallbackInfo info) {
        HealthData.refreshHearts((IEntityDataSaver) this, this, false);
    }
}
