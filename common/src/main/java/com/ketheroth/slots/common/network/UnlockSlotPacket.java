package com.ketheroth.slots.common.network;

import com.ketheroth.slots.common.command.UnlockCommand;
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

        UnlockCommand.unlockPlayer(player, 1);
    }
}