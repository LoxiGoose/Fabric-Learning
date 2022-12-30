package net.goose.lifesteal.component;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import net.goose.lifesteal.util.IWorldComponent;
import net.goose.lifesteal.util.WorldComponent;
import net.minecraft.util.Identifier;

public final class ComponentRegistry implements WorldComponentInitializer {
    public static final ComponentKey<IWorldComponent> BANNED_MAP =
            ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier("lifesteal:bannedmap"), IWorldComponent.class);

    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
        // Add the component to every World instance
        registry.register(BANNED_MAP, world -> new WorldComponent(world));
    }
}