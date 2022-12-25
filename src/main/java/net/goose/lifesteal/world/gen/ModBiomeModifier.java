package net.goose.lifesteal.world.gen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.goose.lifesteal.LifeSteal;
import net.goose.lifesteal.world.feature.ModPlacedFeatures;
import net.minecraft.world.gen.GenerationStep;

public class ModBiomeModifier {
    public static void register() {
        LifeSteal.LOGGER.debug("Registering world gen for " + LifeSteal.MOD_ID);
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES, ModPlacedFeatures.HEART_ORE_PLACED.getKey().get());
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES, ModPlacedFeatures.DEEPSLATE_HEART_GEODE_PLACED.getKey().get());
        BiomeModifications.addFeature(BiomeSelectors.foundInTheNether(),
                GenerationStep.Feature.UNDERGROUND_ORES, ModPlacedFeatures.NETHER_HEART_GEODE_PLACED.getKey().get());
        BiomeModifications.addFeature(BiomeSelectors.foundInTheNether(),
                GenerationStep.Feature.UNDERGROUND_ORES, ModPlacedFeatures.NETHER_HEART_ORE_PLACED.getKey().get());
    }
}
