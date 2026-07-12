package com.ketheroth.slots.common.networking;

import com.ketheroth.slots.common.network.SyncPlayerDataPacket;
import net.minecraft.client.Minecraft;

public class ForgeSyncPlayerDataPacket {
	public static void handle(SyncPlayerDataPacket packet) {
		packet.handle(Minecraft.getInstance().player);
	}

}
