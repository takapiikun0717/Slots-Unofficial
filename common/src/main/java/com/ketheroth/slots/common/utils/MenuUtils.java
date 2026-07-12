package com.ketheroth.slots.common.utils;

import com.ketheroth.slots.common.inventory.container.SlotsMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class MenuUtils {

    public static void openSlotsMenu(ServerPlayer player) {

        player.openMenu(new MenuProvider() {

            @Override
            public Component getDisplayName() {
                return Component.translatable("screen.slots.slots_inventory");
            }

            @Override
            public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
                return new SlotsMenu(id, inventory, player);
            }
        });

    }

}