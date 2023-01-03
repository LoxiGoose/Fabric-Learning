package net.goose.lifesteal.component;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import net.goose.lifesteal.LifeSteal;
import net.goose.lifesteal.api.IHealthComponent;
import net.goose.lifesteal.api.ILevelComponent;
import net.goose.lifesteal.component.components.HealthComponent;
import net.goose.lifesteal.component.components.LevelComponent;
import net.minecraft.util.Identifier;

public final class ComponentRegistry implements WorldComponentInitializer, EntityComponentInitializer {
    public static final ComponentKey<ILevelComponent> UUID_AND_BLOCKPOS_MAP =
            ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier(LifeSteal.MOD_ID, "uuidblockposmap"), ILevelComponent.class);
    public static final ComponentKey<IHealthComponent> HEALTH_DATA =
            ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier(LifeSteal.MOD_ID, "healthdata"), IHealthComponent.class);

    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
        // Add the component to every World instance
        registry.register(UUID_AND_BLOCKPOS_MAP, LevelComponent::new);
    }

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        // Add the component to every PlayerEntity instance
        registry.registerForPlayers(HEALTH_DATA, HealthComponent::new, RespawnCopyStrategy.NEVER_COPY);
    }
}