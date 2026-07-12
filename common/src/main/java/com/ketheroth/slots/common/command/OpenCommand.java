package com.ketheroth.slots.common.command;

import java.util.Collection;

import com.ketheroth.slots.common.utils.MenuUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;

public class OpenCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> register() {

        return Commands.literal("open")
            .requires(source -> source.hasPermission(2))
            // /slots open
            .executes(context -> {

                ServerPlayer player = context.getSource().getPlayerOrException();
                MenuUtils.openSlotsMenu(player);

                    context.getSource().sendSuccess(
                        () -> Component.translatable("commands.slots.open.self"),
                        true
                    );
                    return 1;
            })

        // /slots open <player>
            .then(
                Commands.argument("player", EntityArgument.players())
            .executes(context -> {

                Collection<ServerPlayer> players =
                EntityArgument.getPlayers(context, "player");

                    for (ServerPlayer player : players) {
                        MenuUtils.openSlotsMenu(player);
                    }
                        context.getSource().sendSuccess(
                        () -> Component.translatable(
                            "commands.slots.open.success", 
                            players.size()
                        ),
                        true
                     );

                     return players.size();
               }
              )
            );
    }
}