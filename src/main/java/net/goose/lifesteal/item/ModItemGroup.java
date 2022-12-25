package net.goose.lifesteal.item;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.goose.lifesteal.LifeSteal;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class ModItemGroup {
    public static final ItemGroup LIFESTEAL = FabricItemGroupBuilder.build(new Identifier(LifeSteal.MOD_ID),
            () -> new ItemStack(ModItems.HEART_CRYSTAL));
}