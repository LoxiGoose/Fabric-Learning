package net.goose.lifesteal.world.feature;

import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.placementmodifier.*;

import java.util.List;

public class ModPlacedFeatures {
    public static final RegistryEntry<PlacedFeature> HEART_ORE_PLACED = PlacedFeatures.register("heart_ore_placed",
            ModConfiguredFeatures.HEART_ORE, modifiersWithCount(6,
                    HeightRangePlacementModifier.trapezoid(YOffset.fixed(-50), YOffset.fixed(70))));

    public static final RegistryEntry<PlacedFeature> NETHER_HEART_ORE_PLACED = PlacedFeatures.register("nether_heart_ore_placed",
            ModConfiguredFeatures.NETHER_HEART_ORE, modifiersWithCount(6,
                    HeightRangePlacementModifier.uniform(YOffset.fixed(20), YOffset.fixed(100))));
    public static final RegistryEntry<PlacedFeature> DEEPSLATE_HEART_GEODE_PLACED = PlacedFeatures.register("deepslate_heart_geode_placed",
            ModConfiguredFeatures.DEEPSLATE_HEART_GEODE, RarityFilterPlacementModifier.of(50),
            SquarePlacementModifier.of(),
            HeightRangePlacementModifier.uniform(YOffset.aboveBottom(0), YOffset.aboveBottom(-45)),
            BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> NETHER_HEART_GEODE_PLACED = PlacedFeatures.register("nether_heart_geode_placed",
            ModConfiguredFeatures.DEEPSLATE_HEART_GEODE, RarityFilterPlacementModifier.of(30),
            SquarePlacementModifier.of(),
            HeightRangePlacementModifier.uniform(YOffset.aboveBottom(10), YOffset.aboveBottom(30)),
            BiomePlacementModifier.of());

    private static List<PlacementModifier> modifiers(PlacementModifier countModifier, PlacementModifier heightModifier) {
        return List.of(countModifier, SquarePlacementModifier.of(), heightModifier, BiomePlacementModifier.of());
    }

    private static List<PlacementModifier> modifiersWithCount(int count, PlacementModifier heightModifier) {
        return modifiers(CountPlacementModifier.of(count), heightModifier);
    }

    private static List<PlacementModifier> modifiersWithRarity(int chance, PlacementModifier heightModifier) {
        return modifiers(RarityFilterPlacementModifier.of(chance), heightModifier);
    }
}
