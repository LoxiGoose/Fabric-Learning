package net.goose.lifesteal.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.goose.lifesteal.LifeSteal;
import net.goose.lifesteal.util.HealthData;
import net.goose.lifesteal.util.IEntityDataSaver;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class GetHitPointDifferenceCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment){
        dispatcher.register(
                CommandManager.literal("getHitPointDifference")
                        .requires((commandSource) -> {return commandSource.hasPermissionLevel(2);})
                        .then(CommandManager.argument("Player", EntityArgumentType.entity()).executes((command) -> {
                            return getHitPoint(command.getSource(), EntityArgumentType.getEntity(command, "Player"));}
                        )));
    }

    public static int getHitPoint(ServerCommandSource source, Entity chosenentity) throws CommandSyntaxException {

        LivingEntity playerthatsentcommand = source.getPlayer();

        if(!source.isExecutedByPlayer()){
            LifeSteal.LOGGER.info(chosenentity.getName().getString() +"'s HitPoint difference is "+ HealthData.retrieveHeartDifference((IEntityDataSaver) chosenentity) + ".");
        }else{
            playerthatsentcommand.sendMessage(Text.translatable(chosenentity.getName().getString() +"'s HitPoint difference is "+ HealthData.retrieveHeartDifference((IEntityDataSaver) chosenentity) + "."));
        }

        return 1;
    }
}
