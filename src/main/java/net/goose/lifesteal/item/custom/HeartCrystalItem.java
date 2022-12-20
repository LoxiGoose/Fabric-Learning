package net.goose.lifesteal.item.custom;

import net.goose.lifesteal.LifeSteal;
import net.goose.lifesteal.util.HealthData;
import net.goose.lifesteal.util.IEntityDataSaver;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class HeartCrystalItem extends Item{
    public static final FoodComponent HeartCrystal = (new FoodComponent.Builder()).alwaysEdible().build();

    public HeartCrystalItem(Settings settings){
        super(settings);
    }
    @Override
    public ItemStack finishUsing(ItemStack item, World world, LivingEntity entity) {

        if(!world.isClient() && entity instanceof ServerPlayerEntity serverPlayer){

            if(!LifeSteal.config.ItemsAndEnchantments.DisableHeartCrystals){

                HealthData.setData((IEntityDataSaver) serverPlayer, HealthData.retrieveHeartDifference((IEntityDataSaver) serverPlayer) + LifeSteal.config.ItemsAndEnchantments.AmountOfHitPointsCrystalGives);
                HealthData.refreshHearts((IEntityDataSaver) serverPlayer, entity);

                // Formula, for every hit point, increase duration of the regeneration by 50 ticks: TickDuration = MaxHealth * 50
                int tickTime = (int) (serverPlayer.getMaxHealth() * 50) / 4;
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, tickTime, 3));

            }else{
                serverPlayer.sendMessageToClient(Text.translatable("Heart Crystals have been disabled in the configurations"), true);
                item.setCount(item.getCount() + 1);
                serverPlayer.currentScreenHandler.syncState();

            }
        }
        return super.finishUsing(item, world, entity);
    }
}
