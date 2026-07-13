package com.ketheroth.slots.common.command;

import java.util.Collection;

import com.ketheroth.slots.common.config.SlotsConfig;
import com.ketheroth.slots.common.utils.PlatformUtils;
import com.ketheroth.slots.common.world.SlotsSavedData;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.commands.arguments.EntityArgument;

public class UnlockCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> register() {

        return Commands.literal("unlock")
                .requires(source -> source.hasPermission(0))

                // /slots unlock
                .executes(command -> {

                    ServerPlayer player = command.getSource().getPlayerOrException();

                    SlotsSavedData.PlayerData playerData =
                    SlotsSavedData.getPlayerUnlockedSlots(player);

                        if (playerData.getTotalUnlockedSlots() >= SlotsConfig.getMaxUnlockedSlots()) {

                            command.getSource().sendFailure(
                                Component.translatable("commands.slots.unlock.max_slots"));

                                return 0;
                        }

                            int unlocked = unlockPlayer(player, 1);

                        if (unlocked == 0) {

                            command.getSource().sendFailure(
                                Component.translatable("commands.slots.unlock.not_enough_xp"));
                                return 0;
                        }
                    PlatformUtils.syncPlayerData(player);

                    command.getSource().sendSuccess(
                            () -> Component.translatable("commands.slots.unlock.success"),
                            true);

                    return unlocked;
                })

                // /slots unlock <count>
                .then(
                        Commands.argument("count", IntegerArgumentType.integer(1))
                                .executes(command -> {

                                    ServerPlayer player = command.getSource().getPlayerOrException();

                                    int count = IntegerArgumentType.getInteger(command, "count");

                                    int unlocked = unlockPlayer(player, count);
                                    
                                    if (unlocked == 0) {

                                        SlotsSavedData.PlayerData playerData =
                                                SlotsSavedData.getPlayerUnlockedSlots(player);

                                        if (playerData.getTotalUnlockedSlots() >= SlotsConfig.getMaxUnlockedSlots()) {

                                            command.getSource().sendFailure(
                                                    Component.translatable("commands.slots.unlock.max_slots"));
                                        } else {

                                            command.getSource().sendFailure(
                                                    Component.translatable("commands.slots.unlock.not_enough_xp"));
                                        }

                                        return 0;
                                    }
                                    PlatformUtils.syncPlayerData(player);
                                    final int unlockedCount = unlocked;

                                    command.getSource().sendSuccess(
                                            () -> Component.translatable(
                                                    "commands.slots.unlock.success_multiple",
                                                    unlockedCount),
                                            true);

                                    return unlocked;
                                })
                )
                .then(
    Commands.argument("player", EntityArgument.players())
        .requires(source -> source.hasPermission(2))

        // /slots unlock <player>
        .executes(command -> {

            Collection<ServerPlayer> targets =
                    EntityArgument.getPlayers(command, "player");

            int success = 0;

            for (ServerPlayer target : targets) {

                success += unlockPlayerAdmin(target, 1);
                PlatformUtils.syncPlayerData(target);
            }

            if (success == 0) {

                command.getSource().sendFailure(
                        Component.translatable("commands.slots.unlock.max_slots"));

                return 0;
            }

            // 対象が1人ならプレイヤー名を表示
            if (targets.size() == 1) {

                ServerPlayer target = targets.iterator().next();

                command.getSource().sendSuccess(
                        () -> Component.translatable(
                                "commands.slots.unlock.admin.success",
                                target.getDisplayName()),
                        true);

            } else {

                // 複数人なら人数を表示
                final int playerCount = targets.size();

                command.getSource().sendSuccess(
                        () -> Component.translatable(
                                "commands.slots.unlock.admin.success_players",
                                playerCount),
                        true);
            }

            return success;
        })

        // /slots unlock <player> <count>
        .then(
            Commands.argument("count", IntegerArgumentType.integer(1))
                .executes(command -> {

                    Collection<ServerPlayer> targets =
                            EntityArgument.getPlayers(command, "player");

                    int count =
                            IntegerArgumentType.getInteger(command, "count");

                    int success = 0;

                    for (ServerPlayer target : targets) {

                        success += unlockPlayerAdmin(target, count);
                        PlatformUtils.syncPlayerData(target);
                    }

                    if (success == 0) {

                        command.getSource().sendFailure(
                                Component.translatable("commands.slots.unlock.max_slots"));

                        return 0;
                    }

                    if (targets.size() == 1) {

                        ServerPlayer target = targets.iterator().next();

                        command.getSource().sendSuccess(
                                () -> Component.translatable(
                                        "commands.slots.unlock.admin.success_multiple",
                                        target.getDisplayName(),
                                        count),
                                true);

                    } else {

                        final int playerCount = targets.size();

                        command.getSource().sendSuccess(
                                () -> Component.translatable(
                                        "commands.slots.unlock.admin.success_multiple_players",
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
     * 指定した数だけスロットを解放する
     * @return 実際に解放できた数
     */
    public static int unlockPlayer(ServerPlayer player, int count) {

        SlotsSavedData.PlayerData playerData =
            SlotsSavedData.getPlayerUnlockedSlots(player);

        int unlocked = 0;

        for (int i = 0; i < count; i++) {

            if (playerData.getTotalUnlockedSlots() >= SlotsConfig.getMaxUnlockedSlots()) {
                break;
            }
            if (player.experienceLevel < SlotsConfig.levelPerSlot) {
                break;
            }
            player.giveExperienceLevels(-SlotsConfig.levelPerSlot);

            playerData.addSlot();

            unlocked++;
        }
        return unlocked;
    }
    public static int unlockPlayerAdmin(ServerPlayer player, int count) {

        SlotsSavedData.PlayerData playerData =
            SlotsSavedData.getPlayerUnlockedSlots(player);

        int unlocked = 0;

            for (int i = 0; i < count; i++) {

                if (playerData.getTotalUnlockedSlots() >= SlotsConfig.getMaxUnlockedSlots()) {
                    break;
                } 
                playerData.addSlot();

                unlocked++;
            }
        return unlocked;
    }
}