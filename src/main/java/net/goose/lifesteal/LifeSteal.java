package net.goose.lifesteal;

import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.ModInitializer;
import net.goose.lifesteal.advancement.ModCriteria;
import net.goose.lifesteal.block.ModBlocks;
import net.goose.lifesteal.command.ModCommands;
import net.goose.lifesteal.config.ModConfig;
import net.goose.lifesteal.event.ModEvents;
import net.goose.lifesteal.item.ModItems;
import net.goose.lifesteal.world.feature.ModConfiguredFeatures;
import net.goose.lifesteal.world.gen.ModBiomeModifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LifeSteal implements ModInitializer {
	public static final String MOD_ID = "lifesteal";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static ModConfig config;

	@Override
	public void onInitialize() {
		LOGGER.info("Lifestealers are on the loose!");
		ModConfig.register();
		config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
		ModItems.register();
		ModBlocks.register();

		ModEvents.register();
		ModCommands.register();

		ModConfiguredFeatures.register();
		ModBiomeModifier.register();

		ModCriteria.register();
	}
}
