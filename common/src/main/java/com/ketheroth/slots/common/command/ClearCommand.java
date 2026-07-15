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

public class ClearCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> register(CommandBuildContext buildContext) {

    return Commands.literal("clear")
            .requires(source -> source.hasPermission(2))

            .then(
                Commands.argument("player", EntityArgument.players())

                // /slots clear <player>
                .executes(command -> {

                    Collection<ServerPlayer> players =
                            EntityArgument.getPlayers(command, "player");

                    int removed = 0;

                    for (ServerPlayer player : players) {
                        removed += clearAll(player);
                    }

                    if (removed == 0) {

                        command.getSource().sendFailure(
                                Component.translatable(
                                        "commands.slots.clear.failed"));
                        return 0;
                    }

                    final int result = removed;

                    command.getSource().sendSuccess(
                            () -> Component.translatable(
                                    "commands.slots.clear.success",
                                    result),
                            true);

                    return removed;
                })

                .then(
                    Commands.argument("item", ItemArgument.item(buildContext))

                    // /slots clear <player> <item>
                    .executes(command -> {

                        Collection<ServerPlayer> players =
                                EntityArgument.getPlayers(command, "player");

                        ItemInput input =
                                ItemArgument.getItem(command, "item");

                        ItemStack stack =
                                input.createItemStack(1, false);

                        int removed = 0;

                        for (ServerPlayer player : players) {
                            removed += clearPlayer(player, stack, Integer.MAX_VALUE);
                        }

                        if (removed == 0) {

                            command.getSource().sendFailure(
                                    Component.translatable(
                                            "commands.slots.clear.failed"));
                            return 0;
                        }

                        final int result = removed;

                        command.getSource().sendSuccess(
                                () -> Component.translatable(
                                        "commands.slots.clear.success",
                                        result),
                                true);

                        return removed;
                    })

                    .then(
                        Commands.argument("maxCount", IntegerArgumentType.integer(1))

                        // /slots clear <player> <item> <maxCount>
                        .executes(command -> {

                            Collection<ServerPlayer> players =
                                    EntityArgument.getPlayers(command, "player");

                            ItemInput input =
                                    ItemArgument.getItem(command, "item");

                            int maxCount =
                                    IntegerArgumentType.getInteger(command, "maxCount");

                            ItemStack stack =
                                    input.createItemStack(1, false);

                            int removed = 0;

                            for (ServerPlayer player : players) {
                                removed += clearPlayer(player, stack, maxCount);
                            }

                            if (removed == 0) {

                                command.getSource().sendFailure(
                                        Component.translatable(
                                                "commands.slots.clear.failed"));
                                return 0;
                            }

                            final int result = removed;

                            command.getSource().sendSuccess(
                                    () -> Component.translatable(
                                            "commands.slots.clear.success",
                                            result),
                                    true);

                            return removed;
                        })
                    )
                )
            );
    }
    private static int clearAll(ServerPlayer player) {

        SlotsSavedData.PlayerData playerData =
            SlotsSavedData.getPlayerUnlockedSlots(player);

        int removed = 0;

            for (int i = 0; i < playerData.getTotalUnlockedSlots(); i++) {

            ItemStack stack = playerData.inventory.getItem(i);

                if (stack.isEmpty()) {
                    continue;
                }

                removed += stack.getCount();

               playerData.inventory.setItem(i, ItemStack.EMPTY);
            }

            if (removed > 0) {
                PlatformUtils.syncPlayerData(player);
            }

            return removed;
    }
    private static int clearPlayer(ServerPlayer player, ItemStack targetStack, int maxCount) {

        SlotsSavedData.PlayerData playerData =
            SlotsSavedData.getPlayerUnlockedSlots(player);

        int removed = 0;

            for (int i = 0; i < playerData.getTotalUnlockedSlots(); i++) {

                if (removed >= maxCount) {
                    break;
                }

            ItemStack slotStack = playerData.inventory.getItem(i);

                if (slotStack.isEmpty()) {
                    continue;
                }

                if (!ItemStack.isSameItemSameTags(slotStack, targetStack)) {
                   continue;
                }

            int remove = Math.min(
                slotStack.getCount(),
                maxCount - removed);

             slotStack.shrink(remove);

                if (slotStack.isEmpty()) {
                    playerData.inventory.setItem(i, ItemStack.EMPTY);
                }

                removed += remove;
            }

            if (removed > 0) {
                PlatformUtils.syncPlayerData(player);
            }
        return removed;
    }
}