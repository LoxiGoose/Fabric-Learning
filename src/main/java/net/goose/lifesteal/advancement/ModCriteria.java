package net.goose.lifesteal.advancement;

import net.goose.lifesteal.LifeSteal;
import net.minecraft.advancement.criterion.Criteria;

public class ModCriteria {
    public static LSAdvancementTrigger GET_10_MAX_HEARTS;
    public static void register(){
        LifeSteal.LOGGER.debug("Registering ModCriteria for "+LifeSteal.MOD_ID);
        GET_10_MAX_HEARTS = Criteria.register(new LSAdvancementTrigger());
    }
}
