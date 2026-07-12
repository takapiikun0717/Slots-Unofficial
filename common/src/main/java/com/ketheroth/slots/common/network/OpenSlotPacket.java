package com.ketheroth.slots.common.network;

import com.ketheroth.slots.common.utils.MenuUtils;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

// from client to server
public class OpenSlotPacket implements HandledPacket {

	@Override
	public void write(FriendlyByteBuf buf) {

	}

	@Override
	public void read(FriendlyByteBuf buf) {

	}

	@Override
    public void handle(Player player) {

        if (player instanceof ServerPlayer serverPlayer) {
            MenuUtils.openSlotsMenu(serverPlayer);
        }
    }

}
