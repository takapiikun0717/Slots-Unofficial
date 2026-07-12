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

public class GiveCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> register(CommandBuildContext buildContext) {

        return Commands.literal("give")
                .requires(source -> source.hasPermission(2))

                .then(
                    Commands.argument("player", EntityArgument.players())

                .then(
                    Commands.argument("slot", IntegerArgumentType.integer(0))

                .then(
                    Commands.argument("item", ItemArgument.item(buildContext))

                // /slots give <player> <slot> <item>
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

                        for (ServerPlayer player : players) {

                    SlotsSavedData.PlayerData playerData =
                    SlotsSavedData.getPlayerUnlockedSlots(player);

                        if (slot < 0 || slot >= playerData.getTotalUnlockedSlots()) {
                                continue;
                        }

                        if (!playerData.inventory.getItem(slot).isEmpty()) {
                                continue;
                        }

                                playerData.inventory.setItem(slot, stack.copy());
                                PlatformUtils.syncPlayerData(player);
                                success++;
                        }

                        if (success == 0) {
                                command.getSource().sendFailure(
                                Component.translatable("commands.slots.give.failed")
                                
                           );
                           return 0;
                        }

                                final int result = success;

                                command.getSource().sendSuccess(
                                        () -> Component.translatable(
                                        "commands.slots.give.success",
                                        result
                                   ),
                                   true
                                );

                                return success;
                        })

                // /slots give <player> <slot> <item> <count>
                .then(
                    Commands.argument("count",
                    IntegerArgumentType.integer(1))

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

                        for (ServerPlayer player : players) {

                                SlotsSavedData.PlayerData playerData =
                                SlotsSavedData.getPlayerUnlockedSlots(player);

                        if (slot < 0 || slot >= playerData.getTotalUnlockedSlots()) {
                                continue;
                        }

                                                                                        
                        if (!playerData.inventory.getItem(slot).isEmpty()) {
                                continue;
                        }

                                playerData.inventory.setItem(slot, stack.copy()); 
                                PlatformUtils.syncPlayerData(player);
                                success++;
                                                                                        
                        }

                        if (success == 0) {
                                command.getSource().sendFailure(
                                Component.translatable("commands.slots.give.failed")
                            );
                            return 0;
                        }

                        final int result = success;

                                command.getSource().sendSuccess(
                                () -> Component.translatable(
                                "commands.slots.give.success",
                                result
                             ),
                             true
                          );

                          return success;
                })
              )
            )
          )
       );
    }
}