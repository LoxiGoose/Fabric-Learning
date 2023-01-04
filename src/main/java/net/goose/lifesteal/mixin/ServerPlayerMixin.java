package net.goose.lifesteal.mixin;

import net.goose.lifesteal.api.IHealthComponent;
import net.goose.lifesteal.component.ComponentRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerMixin extends LivingEntity {

    @Shadow
    public abstract World getWorld();

    protected ServerPlayerMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "onSpawn", at = @At("HEAD"))
    private void onSpawn(final CallbackInfo info) {
        final IHealthComponent healthComponent = this.getComponent(ComponentRegistry.HEALTH_DATA);
        healthComponent.refreshHearts(false);
        this.getServer().getWorlds().forEach((level) ->
                healthComponent.revivedTeleport(level, level.getComponent(ComponentRegistry.UUID_AND_BLOCKPOS_MAP))
        );
    }
}
