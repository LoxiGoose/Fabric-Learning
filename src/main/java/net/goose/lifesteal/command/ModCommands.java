package net.goose.lifesteal.command;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.goose.lifesteal.LifeSteal;
import net.goose.lifesteal.command.commands.lifestealCommand;

public class ModCommands {
    public static void register() {
        LifeSteal.LOGGER.debug("Registering ModCommands for " + LifeSteal.MOD_ID);
        CommandRegistrationCallback.EVENT.register(lifestealCommand::register);
    }
}
