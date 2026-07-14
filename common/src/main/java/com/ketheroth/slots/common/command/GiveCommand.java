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
                    Commands.argument("item", ItemArgument.item(buildContext))

                // /slots give <player> <slot> <item>
                .executes(command -> {

                    Collection<ServerPlayer> players =
                    EntityArgument.getPlayers(command, "player");

                    ItemInput input =
                    ItemArgument.getItem(command, "item");

                    ItemStack stack =
                        input.createItemStack(1, false);

                    int success = 0;

                        for (ServerPlayer player : players) {

                            if (givePlayer(player, stack.copy())) {
                                success++;
                            }
                        }
                        if (success == 0) {
                                command.getSource().sendFailure(
                                Component.translatable("commands.slots.give.not_enough_space")
                                
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

                    ItemInput input =
                    ItemArgument.getItem(command, "item");

                    int count =
                    IntegerArgumentType.getInteger(command, "count");

                    ItemStack stack =
                        input.createItemStack(count, false);
                    int success = 0;

                        for (ServerPlayer player : players) {

                            if (givePlayer(player, stack.copy())) {
                                success++;
                            }

                        }  

                        if (success == 0) {
                                command.getSource().sendFailure(
                                Component.translatable("commands.slots.give.not_enough_space")
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
        );
    }
    private static boolean givePlayer(ServerPlayer player, ItemStack stack) {

    SlotsSavedData.PlayerData playerData =
            SlotsSavedData.getPlayerUnlockedSlots(player);
    int remainingSpace = 0;

for (int i = 0; i < playerData.getTotalUnlockedSlots(); i++) {

    ItemStack slotStack = playerData.inventory.getItem(i);

    if (slotStack.isEmpty()) {
        remainingSpace += stack.getMaxStackSize();
    } else if (ItemStack.isSameItemSameTags(slotStack, stack)) {
        remainingSpace += slotStack.getMaxStackSize() - slotStack.getCount();
    }
    if (remainingSpace < stack.getCount()) {
    return false;
}
}

    // 渡すアイテムのコピー
    ItemStack remaining = stack.copy();

    // ① 既存スタックへ追加
    for (int i = 0; i < playerData.getTotalUnlockedSlots(); i++) {

        if (remaining.isEmpty()) {
            break;
        }

        ItemStack slotStack = playerData.inventory.getItem(i);

        if (slotStack.isEmpty()) {
            continue;
        }

        if (!ItemStack.isSameItemSameTags(slotStack, remaining)) {
            continue;
        }

        int max = slotStack.getMaxStackSize();

        if (slotStack.getCount() >= max) {
            continue;
        }

        int move = Math.min(max - slotStack.getCount(), remaining.getCount());

        slotStack.grow(move);
        remaining.shrink(move);
    }

    // ② 空きスロットへ順番に入れる
    for (int i = 0; i < playerData.getTotalUnlockedSlots(); i++) {

        if (remaining.isEmpty()) {
            break;
        }

        ItemStack slotStack = playerData.inventory.getItem(i);

        if (!slotStack.isEmpty()) {
            continue;
        }

        int move = Math.min(
                remaining.getMaxStackSize(),
                remaining.getCount());

        ItemStack copy = remaining.copy();
        copy.setCount(move);

        playerData.inventory.setItem(i, copy);

        remaining.shrink(move);
    }

    PlatformUtils.syncPlayerData(player);

    // 全部渡せたか
    return remaining.isEmpty();
    }
}