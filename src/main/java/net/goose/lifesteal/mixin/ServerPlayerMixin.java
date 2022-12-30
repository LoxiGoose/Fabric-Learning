package net.goose.lifesteal.mixin;

import net.goose.lifesteal.component.ComponentRegistry;
import net.goose.lifesteal.util.HealthData;
import net.goose.lifesteal.util.IEntityDataSaver;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerMixin extends LivingEntity {

    @Shadow public abstract World getWorld();
    @Shadow public ServerPlayNetworkHandler networkHandler;

    @Shadow public abstract void teleport(ServerWorld targetWorld, double x, double y, double z, float yaw, float pitch);

    protected ServerPlayerMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }
    @Inject(method = "onSpawn", at = @At("HEAD"))
    private void onSpawn(final CallbackInfo info) {
        HealthData.refreshHearts((IEntityDataSaver) this, this, false);
        this.getServer().getWorlds().forEach((level) -> {
            if(!this.getWorld().isClient){
                HashMap hashMap = level.getComponent(ComponentRegistry.BANNED_MAP).getMap();
                BlockPos blockPos = (BlockPos) hashMap.get(this.getUuid());

                if(blockPos != null){
                    level.getComponent(ComponentRegistry.BANNED_MAP).removeBannedUUIDanditsBlockPos(this.getUuid(), blockPos);
                    if(this.getWorld() == level){
                        this.networkHandler.requestTeleport(blockPos.getX(), blockPos.getY(), blockPos.getZ(), this.getPitch(), this.getYaw());
                    }else{
                        this.teleport(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), this.getPitch(), this.getYaw());
                    }
                    this.jump();
                    this.updateTrackedPosition(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                }
            }
        });
    }
}
