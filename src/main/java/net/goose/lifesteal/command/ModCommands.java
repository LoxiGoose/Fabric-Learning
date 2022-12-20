package net.goose.lifesteal.command;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.goose.lifesteal.LifeSteal;
import net.goose.lifesteal.command.commands.GetHitPointDifferenceCommand;
import net.goose.lifesteal.command.commands.SetHitPointDifferenceCommand;

public class ModCommands {
    public static void register(){
        LifeSteal.LOGGER.debug("Registering ModCommands for "+LifeSteal.MOD_ID);
        CommandRegistrationCallback.EVENT.register(GetHitPointDifferenceCommand::register);
        CommandRegistrationCallback.EVENT.register(SetHitPointDifferenceCommand::register);
    }
}
