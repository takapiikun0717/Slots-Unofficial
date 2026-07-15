package com.ketheroth.slots.common.command;

import java.util.Collection;

import com.ketheroth.slots.common.utils.PlatformUtils;
import com.ketheroth.slots.common.world.SlotsSavedData;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class SetCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> register(CommandBuildContext buildContext) {

    return Commands.literal("set")
            .requires(source -> source.hasPermission(2))

            .then(
                Commands.argument("player", EntityArgument.players())

                .then(
                    Commands.argument("slot", IntegerArgumentType.integer(0))

                    .then(
                        Commands.argument("item", ItemArgument.item(buildContext))

                        // /slots set <player> <slot> <item>
                        .executes(command -> {

                            Collection<ServerPlayer> players =
                                    EntityArgument.getPlayers(command, "player");

                            int slot =
                                    IntegerArgumentType.getInteger(command, "slot");

                            ItemInput input =
                                    ItemArgument.getItem(command, "item");

                            ItemStack stack =
                                    input.createItemStack(1, false);

                            int success = 0;
                            int error = 0;

                            for (ServerPlayer player : players) {

                                int result = setPlayer(player, slot, stack.copy());

                                if (result == 0) {
                                    success++;
                                } else {
                                    error = result;
                                }
                            }

                            if (success == 0) {

                                switch (error) {

                                    case 1:
                                        command.getSource().sendFailure(
                                                Component.translatable("commands.slots.set.invalid_slot"));
                                        break;

                                    case 2:
                                        command.getSource().sendFailure(
                                                Component.translatable("commands.slots.set.stack_too_large"));
                                        break;

                                    default:
                                        command.getSource().sendFailure(
                                                Component.translatable("commands.slots.set.failed"));
                                }

                                return 0;
                            }

                            final int result = success;

                            command.getSource().sendSuccess(
                                    () -> Component.translatable(
                                            "commands.slots.set.success",
                                            result),
                                    true);

                            return success;
                        })

                        // /slots set <player> <slot> <item> <count>
                        .then(
                            Commands.argument("count", IntegerArgumentType.integer(1))

                            .executes(command -> {

                                Collection<ServerPlayer> players =
                                        EntityArgument.getPlayers(command, "player");

                                int slot =
                                        IntegerArgumentType.getInteger(command, "slot");

                                ItemInput input =
                                        ItemArgument.getItem(command, "item");

                                int count =
                                        IntegerArgumentType.getInteger(command, "count");

                                ItemStack stack =
                                        input.createItemStack(count, false);

                                int success = 0;
                                int error = 0;

                                for (ServerPlayer player : players) {

                                    int result = setPlayer(player, slot, stack.copy());

                                    if (result == 0) {
                                        success++;
                                    } else {
                                        error = result;
                                    }
                                }

                                if (success == 0) {

                                    switch (error) {

                                        case 1:
                                            command.getSource().sendFailure(
                                                    Component.translatable("commands.slots.set.invalid_slot"));
                                            break;

                                        case 2:
                                            command.getSource().sendFailure(
                                                    Component.translatable("commands.slots.set.stack_too_large"));
                                            break;

                                        default:
                                            command.getSource().sendFailure(
                                                    Component.translatable("commands.slots.set.failed"));
                                    }

                                    return 0;
                                }

                                final int result = success;

                                command.getSource().sendSuccess(
                                        () -> Component.translatable(
                                                "commands.slots.set.success",
                                                result),
                                        true);

                                return success;
                            })
                        )
                    )
                )
            );
    }
    private static int setPlayer(ServerPlayer player, int slot, ItemStack stack) {

        SlotsSavedData.PlayerData playerData =
            SlotsSavedData.getPlayerUnlockedSlots(player);

        // 存在しないスロット
        if (slot < 0 || slot >= playerData.getTotalUnlockedSlots()) {
            return 1;
        }

        // スタック数オーバー
        if (stack.getCount() > stack.getMaxStackSize()) {
            return 2;
        }

        playerData.inventory.setItem(slot, stack.copy());

        PlatformUtils.syncPlayerData(player);

        return 0;
    }
}