package net.goose.lifesteal.item;

import net.goose.lifesteal.LifeSteal;
import net.goose.lifesteal.item.custom.HeartCoreItem;
import net.goose.lifesteal.item.custom.HeartCrystalItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModItems {
    public static final Item HEART_FRAGMENT = registerItem("heart_fragment",
            new Item(new Item.Settings().group(ModItemGroup.LIFESTEAL)));
    public static final Item HEART_CORE = registerItem("heart_core",
            new HeartCoreItem(new Item.Settings().food(HeartCoreItem.HeartCore).group(ModItemGroup.LIFESTEAL)));
    public static final Item HEART_CRYSTAL = registerItem("heart_crystal",
            new HeartCrystalItem(new Item.Settings().maxCount(1).fireproof().food(HeartCrystalItem.HeartCrystal).group(ModItemGroup.LIFESTEAL)));

    public static Item registerItem(String name, Item item){
        return Registry.register(Registry.ITEM, new Identifier(LifeSteal.MOD_ID, name), item);
    }
    public static void register(){
        LifeSteal.LOGGER.debug("Registering ModItems for "+ LifeSteal.MOD_ID);
    }
}
