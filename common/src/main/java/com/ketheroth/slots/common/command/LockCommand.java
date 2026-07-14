package com.ketheroth.slots.common.command;

import java.util.Collection;

import com.ketheroth.slots.common.utils.PlatformUtils;
import com.ketheroth.slots.common.world.SlotsSavedData;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.commands.arguments.EntityArgument;

public class LockCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> register() {

        return Commands.literal("lock")
                .requires(source -> source.hasPermission(0))

                // /slots lock
                .executes(command -> {

                    ServerPlayer player = command.getSource().getPlayerOrException();

                    SlotsSavedData.PlayerData playerData =
                    SlotsSavedData.getPlayerUnlockedSlots(player);

                            int locked = lockPlayer(player, 1);

                        if (locked == 0) {

                            int lastSlot = playerData.getTotalUnlockedSlots() - 1;

                                if (lastSlot >= 0 &&
                                    !playerData.inventory.getItem(lastSlot).isEmpty()) {
                                        command.getSource().sendFailure(
                                            Component.translatable("commands.slots.lock.slot_not_empty"));
                                    } else {

                                        command.getSource().sendFailure(
                                            Component.translatable("commands.slots.lock.failed"));
                                    }
                                    return 0;
                        }
                        PlatformUtils.syncPlayerData(player);

                        command.getSource().sendSuccess(
                            () -> Component.translatable("commands.slots.lock.success"),
                            true);
                        return locked;
                })

                // /slots lock <count>
                .then(
                    Commands.argument("count", IntegerArgumentType.integer(1))
                        .executes(command -> {

                            ServerPlayer player = command.getSource().getPlayerOrException();

                            int count = IntegerArgumentType.getInteger(command, "count");

                            int locked = lockPlayer(player, count);

                                if (locked == 0) {

                                    SlotsSavedData.PlayerData playerData =
                                    SlotsSavedData.getPlayerUnlockedSlots(player);

                                    int lastSlot = playerData.getTotalUnlockedSlots() - 1;

                                        if (lastSlot >= 0 &&
                                            !playerData.inventory.getItem(lastSlot).isEmpty()) {

                                            command.getSource().sendFailure(
                                                Component.translatable("commands.slots.lock.slot_not_empty"));
                                        } else {

                                            command.getSource().sendFailure(
                                                Component.translatable("commands.slots.lock.failed"));
                                        }
                                        return 0;
                                }

                                PlatformUtils.syncPlayerData(player);

                                final int lockedCount = locked;

                                    command.getSource().sendSuccess(
                                        () -> Component.translatable(
                                            "commands.slots.lock.success_multiple",
                                            lockedCount
                                        ),
                                        true
                                    );
                                    return locked;
                        })
                )
                .then(
                    Commands.argument("player", EntityArgument.players())
                .requires(source -> source.hasPermission(2))

                // /slots lock <player>
                .executes(command -> {

                    Collection<ServerPlayer> targets =
                    EntityArgument.getPlayers(command, "player");

                    int success = 0;

                        for (ServerPlayer target : targets) {

                            success += lockPlayerAdmin(target, 1);
                            PlatformUtils.syncPlayerData(target);
                        }

                        if (success == 0) {

                            command.getSource().sendFailure(
                                Component.translatable("commands.slots.lock.failed"));
                            return 0;
                        }

                        // 対象が1人ならプレイヤー名を表示
                        if (targets.size() == 1) {

                            ServerPlayer target = targets.iterator().next();

                                command.getSource().sendSuccess(
                                    () -> Component.translatable(
                                        "commands.slots.lock.admin.success",
                                        target.getDisplayName()),
                                    true
                                );

                            } else {

                            // 複数人なら人数を表示
                            final int playerCount = targets.size();

                                command.getSource().sendSuccess(
                                    () -> Component.translatable(
                                        "commands.slots.lock.admin.success_players",
                                        playerCount),
                                    true
                                );
                            }
                            return success;
                })

                // /slots lock <player> <count>
                .then(
                    Commands.argument("count", IntegerArgumentType.integer(1))
                .executes(command -> {

                    Collection<ServerPlayer> targets =
                            EntityArgument.getPlayers(command, "player");

                    int count =
                            IntegerArgumentType.getInteger(command, "count");

                    int success = 0;

                        for (ServerPlayer target : targets) {

                            success += lockPlayerAdmin(target, count);
                            PlatformUtils.syncPlayerData(target);
                        }

                        if (success == 0) {

                            command.getSource().sendFailure(
                                Component.translatable("commands.slots.lock.failed"));
                            return 0;
                        }

                        if (targets.size() == 1) {

                        ServerPlayer target = targets.iterator().next();

                        command.getSource().sendSuccess(
                                () -> Component.translatable(
                                        "commands.slots.lock.admin.success_multiple",
                                        target.getDisplayName(),
                                        count),
                                true);
                        } else {

                        final int playerCount = targets.size();

                        command.getSource().sendSuccess(
                                () -> Component.translatable(
                                        "commands.slots.lock.admin.success_multiple_players",
                                        playerCount,
                                        count),
                                true);
                        }
                    return success;
                 })
                )
            );               
    }

    /**
     * 指定した数だけスロットをロックする
     * @return 実際にロックできた数
     */
    private static int lockPlayer(ServerPlayer player, int count) {

        SlotsSavedData.PlayerData playerData =
            SlotsSavedData.getPlayerUnlockedSlots(player);

        int locked = 0;

            for (int i = 0; i < count; i++) {

        int lastSlot = playerData.getTotalUnlockedSlots() - 1;           
            if (lastSlot < 0) {
                break;
            }
            if (!playerData.inventory.getItem(lastSlot).isEmpty()) {
                 break;
            }
        playerData.removeSlot();
        locked++;
    }

    PlatformUtils.syncPlayerData(player);

    return locked;
    }
    public static int lockPlayerAdmin(ServerPlayer player, int count) {
        return lockPlayer(player, count);
    }
}