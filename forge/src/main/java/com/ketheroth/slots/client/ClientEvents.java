package com.ketheroth.slots.client;

import com.ketheroth.slots.Slots;
import com.ketheroth.slots.client.keymapping.SlotKeyMapping;
import com.ketheroth.slots.common.network.OpenSlotPacket;
import com.ketheroth.slots.common.networking.SlotsPacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.network.NetworkHooks;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.resources.ResourceLocation;


@Mod.EventBusSubscriber(
        modid = Slots.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT
)

public class ClientEvents {
    private static final ResourceLocation BUTTON =
        new ResourceLocation(Slots.MOD_ID, "textures/gui/button.png");

    @SubscribeEvent
    public static void onKeyPress(InputEvent.Key event) {
        if (SlotKeyMapping.KEY_OPEN.isDown()) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                SlotsPacketHandler.INSTANCE.sendTo(
                        new OpenSlotPacket(),
                        player.connection.getConnection(),
                        NetworkDirection.PLAY_TO_SERVER
                );
            }
        }
    }
    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {

        if (!(event.getScreen() instanceof InventoryScreen screen)) {
        return;
        }

        int left = (screen.width - 176) / 2;
        int top = (screen.height - 166) / 2;

        event.addListener(
            new SlotsInventoryButton(
                screen.getGuiLeft() + 152,
                screen.getGuiTop() + 5
            )
        );
  }
} 