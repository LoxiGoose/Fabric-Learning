package net.goose.lifesteal.item.custom;

import net.goose.lifesteal.LifeSteal;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class HeartCoreItem extends Item {
    public static final FoodComponent HeartCore = (new FoodComponent.Builder().alwaysEdible().build());

    public HeartCoreItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack finishUsing(ItemStack itemStack, World world, LivingEntity entity) {
        if (!world.isClient() && entity instanceof ServerPlayerEntity serverPlayer) {
            if (!LifeSteal.config.disableHeartCores.get()) {
                if (entity.getHealth() < entity.getMaxHealth() || !LifeSteal.config.preventFromUsingCoreIfMax.get()) {
                    float maxHealth = entity.getMaxHealth();
                    float amountThatWillBeHealed = (float) (maxHealth * LifeSteal.config.HeartCoreHeal.get());
                    float differenceInHealth = entity.getMaxHealth() - entity.getHealth();
                    if (differenceInHealth <= amountThatWillBeHealed) {
                        amountThatWillBeHealed = differenceInHealth;
                    }

                    int oldDuration = 0;
                    if (entity.hasStatusEffect(StatusEffects.REGENERATION)) {
                        StatusEffectInstance mobEffect = entity.getStatusEffect(StatusEffects.REGENERATION);

                        oldDuration = mobEffect.getDuration();
                    }

                    int tickTime = (int) ((amountThatWillBeHealed * 50) / 2) + oldDuration;
                    entity.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, tickTime, 1));
                } else {
                    serverPlayer.sendMessageToClient(Text.translatable("gui.lifesteal.heart_core_at_max_health"), true);
                    itemStack.increment(1);
                    serverPlayer.currentScreenHandler.syncState();
                }
            } else {
                serverPlayer.sendMessageToClient(Text.translatable("gui.lifesteal.heart_core_disabled"), true);
                itemStack.increment(1);
                serverPlayer.currentScreenHandler.syncState();
            }
        }
        return super.finishUsing(itemStack, world, entity);
    }
}
