package com.ketheroth.slots.client;

import com.ketheroth.slots.Slots;
import com.ketheroth.slots.common.network.OpenSlotPacket;
import com.ketheroth.slots.common.networking.SlotsPacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;

public class SlotsInventoryButton extends ImageButton {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(Slots.MOD_ID, "textures/gui/button.png");

    public SlotsInventoryButton(int x, int y) {
        super(
                x,
                y,
                15,
                15,
                0,
                0,
                15,
                TEXTURE,
                15,
                30,
                button -> {

                    LocalPlayer player = Minecraft.getInstance().player;

                    if (player != null) {
                        SlotsPacketHandler.INSTANCE.sendTo(
                                new OpenSlotPacket(),
                                player.connection.getConnection(),
                                NetworkDirection.PLAY_TO_SERVER
                        );
                    }

                },
                Component.literal("Open Slots")
        );
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
    }
    @Override
    public void onPress() {
        super.onPress();
    }

}