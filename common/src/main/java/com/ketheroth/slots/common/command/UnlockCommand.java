package com.ketheroth.slots.common.command;

import com.ketheroth.slots.common.config.SlotsConfig;
import com.ketheroth.slots.common.utils.PlatformUtils;
import com.ketheroth.slots.common.world.SlotsSavedData;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class UnlockCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> register() {

        return Commands.literal("unlock")
                .requires(source -> source.hasPermission(0))

                // /slots unlock
                .executes(command -> {

                    ServerPlayer player = command.getSource().getPlayerOrException();

                    int unlocked = unlockPlayer(player, 1);

                    if (unlocked == 0) {
                        command.getSource().sendFailure(
                                Component.translatable("commands.slots.unlock.not_enough_xp"));
                        return 0;
                    }

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

                                    final int unlockedCount = unlocked;

                                    command.getSource().sendSuccess(
                                            () -> Component.translatable(
                                                    "commands.slots.unlock.success_multiple",
                                                    unlockedCount),
                                            true);

                                    return unlocked;
                                })
                );
    }

    /**
     * 指定した数だけスロットを解放する
     * @return 実際に解放できた数
     */
    private static int unlockPlayer(ServerPlayer player, int count) {

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

        PlatformUtils.syncPlayerData(player);

        return unlocked;
    }
}