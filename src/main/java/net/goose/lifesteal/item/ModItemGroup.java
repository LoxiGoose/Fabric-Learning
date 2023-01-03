package net.goose.lifesteal.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.goose.lifesteal.LifeSteal;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroup {
    public static final ItemGroup LIFESTEAL = FabricItemGroup.builder(new Identifier(LifeSteal.MOD_ID))
            .displayName(Text.translatable("itemGroup.lifesteal"))
            .icon(() -> new ItemStack(ModItems.HEART_CRYSTAL))
            .build();
}
