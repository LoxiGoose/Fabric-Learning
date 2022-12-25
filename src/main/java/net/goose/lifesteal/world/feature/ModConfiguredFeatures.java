package net.goose.lifesteal.world.feature;

import net.goose.lifesteal.LifeSteal;
import net.goose.lifesteal.block.ModBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

import java.util.List;

public class ModConfiguredFeatures {
    public static final List<OreFeatureConfig.Target> OVERWORLD_HEART_ORES = List.of(
            OreFeatureConfig.createTarget(OreConfiguredFeatures.STONE_ORE_REPLACEABLES, ModBlocks.HEART_ORE.getDefaultState()),
            OreFeatureConfig.createTarget(OreConfiguredFeatures.DEEPSLATE_ORE_REPLACEABLES, ModBlocks.DEEPSLATE_HEART_ORE.getDefaultState()));

    public static final List<OreFeatureConfig.Target> NETHER_HEART_ORES = List.of(
            OreFeatureConfig.createTarget(OreConfiguredFeatures.BASE_STONE_NETHER, ModBlocks.NETHERRACK_HEART_ORE.getDefaultState()));

    public static final RegistryEntry<ConfiguredFeature<OreFeatureConfig, ?>> HEART_ORE =
            ConfiguredFeatures.register("heart_ore", Feature.SCATTERED_ORE, new OreFeatureConfig(OVERWORLD_HEART_ORES, 12));

    public static final RegistryEntry<ConfiguredFeature<OreFeatureConfig, ?>> NETHER_HEART_ORE =
            ConfiguredFeatures.register("nether_heart_ore", Feature.SCATTERED_ORE, new OreFeatureConfig(NETHER_HEART_ORES, 14));
    public static final RegistryEntry<ConfiguredFeature<GeodeFeatureConfig, ?>> DEEPSLATE_HEART_GEODE =
            ConfiguredFeatures.register("deepslate_heart_geode", Feature.GEODE,
                    new GeodeFeatureConfig(new GeodeLayerConfig(
                            BlockStateProvider.of(Blocks.AIR),
                            BlockStateProvider.of(Blocks.DEEPSLATE),
                            BlockStateProvider.of(ModBlocks.DEEPSLATE_HEART_ORE),
                            BlockStateProvider.of(Blocks.DIRT),
                            BlockStateProvider.of(Blocks.SMOOTH_BASALT),
                            List.of(Blocks.CALCITE.getDefaultState()),
                            BlockTags.FEATURES_CANNOT_REPLACE, BlockTags.GEODE_INVALID_BLOCKS),
                            new GeodeLayerThicknessConfig(1.7D, 2.2D, 3.2D, 4.2D),
                            new GeodeCrackConfig(0.95D, 2.0D, 2),
                            0.35D, 0.083D,
                            true, UniformIntProvider.create(4, 6),
                            UniformIntProvider.create(3, 4), UniformIntProvider.create(1, 2),
                            -16, 16, 0.05D, 1));
    public static final RegistryEntry<ConfiguredFeature<GeodeFeatureConfig, ?>> NETHER_HEART_GEODE =
            ConfiguredFeatures.register("nether_heart_geode", Feature.GEODE,
                    new GeodeFeatureConfig(new GeodeLayerConfig(
                            BlockStateProvider.of(Blocks.AIR),
                            BlockStateProvider.of(Blocks.NETHERRACK),
                            BlockStateProvider.of(ModBlocks.NETHERRACK_HEART_ORE),
                            BlockStateProvider.of(Blocks.MAGMA_BLOCK),
                            BlockStateProvider.of(Blocks.BLACKSTONE),
                            List.of(Blocks.NETHER_GOLD_ORE.getDefaultState(), Blocks.SOUL_SAND.getDefaultState(), Blocks.GRAVEL.getDefaultState()),
                            BlockTags.FEATURES_CANNOT_REPLACE, BlockTags.GEODE_INVALID_BLOCKS),
                            new GeodeLayerThicknessConfig(1.7D, 2.2D, 3.2D, 4.2D),
                            new GeodeCrackConfig(0.95D, 2.0D, 2),
                            0.35D, 0.083D,
                            true, UniformIntProvider.create(4, 6),
                            UniformIntProvider.create(3, 4), UniformIntProvider.create(1, 2),
                            -16, 16, 0.05D, 1));

    public static void register() {
        LifeSteal.LOGGER.debug("Registering the ModConfiguredFeatures for " + LifeSteal.MOD_ID);
    }
}
