package net.goose.lifesteal.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.goose.lifesteal.LifeSteal;
import net.goose.lifesteal.item.custom.HeartCoreItem;
import net.goose.lifesteal.item.custom.HeartCrystalItem;
import net.goose.lifesteal.item.custom.ReviveCrystalItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item HEART_FRAGMENT = registerItem("heart_fragment",
            new Item(new Item.Settings()), ModItemGroup.LIFESTEAL, ItemGroups.INGREDIENTS);
    public static final Item HEART_CORE = registerItem("heart_core",
            new HeartCoreItem(new Item.Settings().food(HeartCoreItem.HeartCore)), ModItemGroup.LIFESTEAL, ItemGroups.INGREDIENTS, ItemGroups.FOOD_AND_DRINK);
    public static final Item HEART_CRYSTAL = registerItem("heart_crystal",
            new HeartCrystalItem(new Item.Settings().maxCount(1).fireproof().food(HeartCrystalItem.HeartCrystal)), ModItemGroup.LIFESTEAL, ItemGroups.FOOD_AND_DRINK);
    public static final Item REVIVE_CRYSTAL = registerItem("revive_crystal",
            new ReviveCrystalItem(new Item.Settings().maxCount(1).fireproof()), ModItemGroup.LIFESTEAL);
    public static Item registerItem(String name, Item item, ItemGroup... itemGroupList){
        for(ItemGroup itemGroup: itemGroupList){
            ItemGroupEvents.modifyEntriesEvent(itemGroup).register(entries -> entries.add(item));
        }
        return Registry.register(Registries.ITEM, new Identifier(LifeSteal.MOD_ID, name), item);
    }
    public static void register(){
        LifeSteal.LOGGER.debug("Registering ModItems for "+ LifeSteal.MOD_ID);
    }
}
