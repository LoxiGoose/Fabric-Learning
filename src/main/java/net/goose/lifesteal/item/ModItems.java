package net.goose.lifesteal.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.goose.lifesteal.LifeSteal;
import net.goose.lifesteal.item.custom.HeartCoreItem;
import net.goose.lifesteal.item.custom.HeartCrystalItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item HEART_FRAGMENT = registerItem("heart_fragment",
            new Item(new Item.Settings()), ModItemGroup.LIFESTEAL);
    public static final Item HEART_CORE = registerItem("heart_core",
            new HeartCoreItem(new Item.Settings().food(HeartCoreItem.HeartCore)), ModItemGroup.LIFESTEAL);
    public static final Item HEART_CRYSTAL = registerItem("heart_crystal",
            new HeartCrystalItem(new Item.Settings().maxCount(1).fireproof().food(HeartCrystalItem.HeartCrystal)), ModItemGroup.LIFESTEAL);

    public static Item registerItem(String name, Item item, ItemGroup itemGroup){
        ItemGroupEvents.modifyEntriesEvent(itemGroup).register(entries -> entries.add(item));
        return Registry.register(Registries.ITEM, new Identifier(LifeSteal.MOD_ID, name), item);
    }
    public static void register(){
        LifeSteal.LOGGER.debug("Registering ModItems for "+ LifeSteal.MOD_ID);
    }
}
