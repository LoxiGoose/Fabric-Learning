package net.goose.lifesteal.advancement;

import net.goose.lifesteal.LifeSteal;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.util.Identifier;

public class ModCriteria {
    public static LSAdvancementTrigger GET_10_MAX_HEARTS = new LSAdvancementTrigger(new Identifier(LifeSteal.MOD_ID, "get_10_max_hearts"));
    public static LSAdvancementTrigger USE_TOTEM_WHILE_MAX_20_HEARTS = new LSAdvancementTrigger(new Identifier(LifeSteal.MOD_ID, "use_totem_while_20_max_hearts"));

    public static void register() {
        LifeSteal.LOGGER.debug("Registering ModCriteria for " + LifeSteal.MOD_ID);
        Criteria.register(GET_10_MAX_HEARTS);
        Criteria.register(USE_TOTEM_WHILE_MAX_20_HEARTS);
    }
}
