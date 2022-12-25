package net.goose.lifesteal;

import net.fabricmc.api.ModInitializer;
import net.goose.lifesteal.advancement.ModCriteria;
import net.goose.lifesteal.block.ModBlocks;
import net.goose.lifesteal.command.ModCommands;
import net.goose.lifesteal.configuration.Config;
import net.goose.lifesteal.configuration.ConfigHolder;
import net.goose.lifesteal.event.ModEvents;
import net.goose.lifesteal.item.ModItems;
import net.goose.lifesteal.world.feature.ModConfiguredFeatures;
import net.goose.lifesteal.world.gen.ModBiomeModifier;
import net.minecraftforge.api.ModLoadingContext;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LifeSteal implements ModInitializer {
	public static final String MOD_ID = "lifesteal";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static Config config;

	@Override
	public void onInitialize() {
		LOGGER.info("Lifestealers are on the loose!");
		ModLoadingContext.registerConfig("lifesteal", ModConfig.Type.COMMON, ConfigHolder.SERVER_SPEC);
		config = ConfigHolder.SERVER;
		ModItems.register();
		ModBlocks.register();

		ModEvents.register();
		ModCommands.register();

		ModConfiguredFeatures.register();
		ModBiomeModifier.register();

		ModCriteria.register();
	}
}
