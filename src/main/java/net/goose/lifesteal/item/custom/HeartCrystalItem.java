package net.goose.lifesteal.item.custom;

import net.goose.lifesteal.LifeSteal;
import net.goose.lifesteal.api.IHealthComponent;
import net.goose.lifesteal.component.ComponentRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class HeartCrystalItem extends Item {
    public static final FoodComponent HeartCrystal = (new FoodComponent.Builder()).alwaysEdible().build();

    public HeartCrystalItem(Settings settings) {
        super(settings);
    }

    public void applyCrystalEffect(LivingEntity entity) {
        // Formula, for every hit point, increase duration of the regeneration by 50 ticks: TickDuration = MaxHealth * 50
        int tickTime = (int) (entity.getMaxHealth() * 50) / 4;
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, tickTime, 3));
    }

    @Override
    public ItemStack finishUsing(ItemStack item, World world, LivingEntity entity) {

        if (!world.isClient() && entity instanceof ServerPlayerEntity serverPlayer) {

            if (!LifeSteal.config.disableHeartCrystals.get()) {
                IHealthComponent healthComponent = entity.getComponent(ComponentRegistry.HEALTH_DATA);
                int currentheartDifference = healthComponent.getHeartDifference();

                if (LifeSteal.config.maximumamountofheartsGainable.get() > -1 && LifeSteal.config.preventFromUsingCrystalIfMax.get()) {
                    int maximumheartDifference = LifeSteal.config.startingHeartDifference.get() + LifeSteal.config.maximumamountofheartsGainable.get();
                    if (currentheartDifference == maximumheartDifference) {
                        serverPlayer.sendMessageToClient(Text.translatable("gui.lifesteal.heart_crystal_reaching_max"), true);
                        item.setCount(item.getCount() + 1);
                        serverPlayer.currentScreenHandler.syncState();
                        return super.finishUsing(item, world, entity);
                    }
                }

                int newheartDifference = currentheartDifference + LifeSteal.config.heartCrystalAmountGain.get();

                healthComponent.setHeartDifference(newheartDifference);
                healthComponent.refreshHearts(false);

                // Formula, for every hit point, increase duration of the regeneration by 50 ticks: TickDuration = MaxHealth * 50
                NbtCompound nbt = item.getNbt();
                if (nbt != null) {
                    NbtCompound compound = nbt.getCompound("lifesteal");
                    if (compound == null) {
                        applyCrystalEffect(entity);
                    } else if (compound.getBoolean("Fresh")) {
                        applyCrystalEffect(entity);
                    }
                } else {
                    applyCrystalEffect(entity);
                }

            } else {
                serverPlayer.sendMessageToClient(Text.translatable("gui.lifesteal.heart_crystal_disabled"), true);
                item.increment(1);
                serverPlayer.currentScreenHandler.syncState();
            }
        }
        return super.finishUsing(item, world, entity);
    }
}
