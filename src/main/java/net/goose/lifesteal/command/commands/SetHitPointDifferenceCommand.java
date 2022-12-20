package net.goose.lifesteal.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
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

public class SetHitPointDifferenceCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment){
        dispatcher.register(
                CommandManager.literal("setHitPointDifference")
                        .requires((commandSource) -> {return commandSource.hasPermissionLevel(2);})
                        .then(CommandManager.argument("Player", EntityArgumentType.entity())
                                .then(CommandManager.argument("Amount", IntegerArgumentType.integer()).executes((command) -> {
                                    return setHitPoint(command.getSource(), EntityArgumentType.getEntity(command, "Player"), IntegerArgumentType.getInteger(command, "Amount"));}
                                ))));
    }

    public static int setHitPoint(ServerCommandSource source, Entity chosenentity, int amount) throws CommandSyntaxException{

        LivingEntity playerthatsentcommand = source.getPlayer();

        HealthData.setData((IEntityDataSaver) chosenentity, amount);
        HealthData.refreshHearts((IEntityDataSaver) chosenentity, (LivingEntity) chosenentity);

        if(chosenentity != playerthatsentcommand && source.isExecutedByPlayer()){
            playerthatsentcommand.sendMessage(Text.translatable("Set "+ chosenentity.getName().getString() +"'s HitPoint difference to "+amount));
        }else if(!source.isExecutedByPlayer()){
            LifeSteal.LOGGER.info("Set "+ chosenentity.getName().getString() +"'s HitPoint difference to "+amount);
        }

        chosenentity.sendMessage(Text.translatable("Your HitPoint difference has been set to "+amount));
        return 1;
    }
}
