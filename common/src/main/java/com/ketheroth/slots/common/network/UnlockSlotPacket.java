package com.ketheroth.slots.common.network;

import com.ketheroth.slots.common.command.UnlockCommand;
import com.ketheroth.slots.common.utils.PlatformUtils;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class UnlockSlotPacket {

    public UnlockSlotPacket() {
    }

    public void write(FriendlyByteBuf buf) {
        // データは送らない
    }

    public void read(FriendlyByteBuf buf) {
        // データは受け取らない
    }

    public void handle(ServerPlayer player) {

        if (player == null) {
            return;
        }

        int unlocked = UnlockCommand.unlockPlayer(player, 1);

        if (unlocked > 0) {
            PlatformUtils.syncPlayerData(player);
        }
    }
}