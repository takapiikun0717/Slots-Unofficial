package com.ketheroth.slots.common.command; 
import com.mojang.brigadier.CommandDispatcher; 
import net.minecraft.commands.CommandBuildContext; 
import net.minecraft.commands.CommandSourceStack; 
import net.minecraft.commands.Commands; 

public class SlotsCommand { 
    public static void register( 
        CommandDispatcher<CommandSourceStack> dispatcher, 
        CommandBuildContext context ) { 
            dispatcher.register( 
                Commands.literal("slots") 
                    .requires(source -> source.hasPermission(2)) 
                    .then(OpenCommand.register()) 
                    .then(GiveCommand.register(context)) 
                    .then(UnlockCommand.register())
            ); 
        } 
}