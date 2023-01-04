package net.goose.lifesteal.command.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.goose.lifesteal.LifeSteal;
import net.goose.lifesteal.api.IHealthComponent;
import net.goose.lifesteal.component.ComponentRegistry;
import net.goose.lifesteal.item.ModItems;
import net.minecraft.advancement.Advancement;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class lifestealCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(
                CommandManager.literal("ls")
                        .then(CommandManager.literal("withdraw")
                                .requires((commandSource) -> commandSource.hasPermissionLevel(CommandManager.field_31837))
                                .executes((command) -> withdraw(command.getSource(), 1))
                                .then(CommandManager.argument("Amount", IntegerArgumentType.integer())
                                        .executes((command) -> withdraw(command.getSource(), IntegerArgumentType.getInteger(command, "Amount")))))
                        .then(CommandManager.literal("get-hitpoints")
                                .requires((commandSource) -> commandSource.hasPermissionLevel(CommandManager.field_31839))
                                .executes((command) -> getHitPoint(command.getSource()))
                                .then(CommandManager.argument("Player", EntityArgumentType.entity())
                                        .executes((command) -> getHitPoint(command.getSource(), EntityArgumentType.getEntity(command, "Player"))))
                        )
                        .then(CommandManager.literal("set-hitpoints")
                                .requires((commandSource) -> commandSource.hasPermissionLevel(CommandManager.field_31839))
                                .then(CommandManager.argument("Amount", IntegerArgumentType.integer())
                                        .executes((command) -> setHitPoint(command.getSource(), IntegerArgumentType.getInteger(command, "Amount"))))
                                .then(CommandManager.argument("Player", EntityArgumentType.entity())
                                        .then(CommandManager.argument("Amount", IntegerArgumentType.integer())
                                                .executes((command) -> setHitPoint(command.getSource(), EntityArgumentType.getEntity(command, "Player"), IntegerArgumentType.getInteger(command, "Amount")))))));
    }

    public static int withdraw(ServerCommandSource source, int amount) throws CommandSyntaxException {
        if (source.isExecutedByPlayer()) {
            final int maximumheartsLoseable = LifeSteal.config.maximumamountofheartsLoseable.get();
            final int startingHitPointDifference = LifeSteal.config.startingHeartDifference.get();

            LivingEntity playerthatsentcommand = source.getPlayer();
            if (playerthatsentcommand instanceof ServerPlayerEntity player) {
                String advancementUsed = (String) LifeSteal.config.advancementUsedForWithdrawing.get();

                if (source.getPlayer().getAdvancementTracker().getProgress(Advancement.Builder.create().build(new Identifier(advancementUsed))).isDone() || advancementUsed.isEmpty()) {
                    IHealthComponent healthComponent = player.getComponent(ComponentRegistry.HEALTH_DATA);
                    int heartDifference = healthComponent.getHeartDifference() - (LifeSteal.config.heartCrystalAmountGain.get() * amount);
                    if (maximumheartsLoseable >= 0) {
                        if (heartDifference < startingHitPointDifference - maximumheartsLoseable) {
                            player.sendMessageToClient(Text.translatable("gui.lifesteal.can't_withdraw_less_than_minimum"), true);
                            return 1;
                        }
                    }
                    healthComponent.setHeartDifference(heartDifference);
                    healthComponent.refreshHearts(false);

                    ItemStack heartCrystal = new ItemStack(ModItems.HEART_CRYSTAL, amount);
                    NbtCompound compoundTag = heartCrystal.getOrCreateNbt().getCompound("lifesteal");
                    compoundTag.putBoolean("Fresh", false);
                    heartCrystal.setCustomName(Text.translatable("item.lifesteal.heart_crystal.unnatural"));
                    if (player.getInventory().getEmptySlot() == -1) {
                        player.dropItem(heartCrystal, true);
                    } else {
                        player.getInventory().insertStack(heartCrystal);
                    }
                } else {
                    String text = (String) LifeSteal.config.textUsedForRequirementOnWithdrawing.get();
                    if (!text.isEmpty()) {
                        player.sendMessageToClient(Text.literal((String) LifeSteal.config.textUsedForRequirementOnWithdrawing.get()), true);
                    }
                }
            }
        }
        return 1;
    }

    public static int getHitPoint(ServerCommandSource source, Entity chosenentity) throws CommandSyntaxException {
        LivingEntity playerthatsentcommand = source.getPlayer();
        IHealthComponent healthComponent = chosenentity.getComponent(ComponentRegistry.HEALTH_DATA);
        if (!source.isExecutedByPlayer()) {
            LifeSteal.LOGGER.info(chosenentity.getName().getString() + "'s HitPoint difference is " + healthComponent.getHeartDifference() + ".");
        } else {
            playerthatsentcommand.sendMessage(Text.translatable(chosenentity.getName().getString() + "'s HitPoint difference is " + healthComponent.getHeartDifference() + "."));
        }
        return 1;
    }

    public static int getHitPoint(ServerCommandSource source) throws CommandSyntaxException {
        if (source.isExecutedByPlayer()) {
            LivingEntity playerthatsentcommand = source.getPlayer();
            IHealthComponent healthComponent = playerthatsentcommand.getComponent(ComponentRegistry.HEALTH_DATA);
            playerthatsentcommand.sendMessage(Text.translatable("Your HitPoint difference is " + healthComponent.getHeartDifference() + "."));
        }
        return 1;
    }

    public static int setHitPoint(ServerCommandSource source, int amount) throws CommandSyntaxException {
        if (source.isExecutedByPlayer()) {
            LivingEntity playerthatsentcommand = source.getPlayer();

            IHealthComponent healthComponent = playerthatsentcommand.getComponent(ComponentRegistry.HEALTH_DATA);
            healthComponent.setHeartDifference(amount);
            healthComponent.refreshHearts(false);

            playerthatsentcommand.sendMessage(Text.translatable("Your HitPoint difference has been set to " + amount));
        }
        return 1;
    }

    public static int setHitPoint(ServerCommandSource source, Entity chosenentity, int amount) throws CommandSyntaxException {
        LivingEntity playerthatsentcommand = source.getPlayer();
        IHealthComponent healthComponent = chosenentity.getComponent(ComponentRegistry.HEALTH_DATA);
        healthComponent.setHeartDifference(amount);
        healthComponent.refreshHearts(false);
        if (chosenentity != playerthatsentcommand && source.isExecutedByPlayer()) {
            playerthatsentcommand.sendMessage(Text.translatable("Set " + chosenentity.getName().getString() + "'s HitPoint difference to " + amount));
        } else if (!source.isExecutedByPlayer()) {
            LifeSteal.LOGGER.info("Set " + chosenentity.getName().getString() + "'s HitPoint difference to " + amount);
        }

        if (LifeSteal.config.tellPlayersIfHitPointChanged.get()) {
            chosenentity.sendMessage(Text.translatable("Your HitPoint difference has been set to " + amount));
        }
        return 1;
    }
}
