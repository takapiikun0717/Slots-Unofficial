package com.ketheroth.slots.client.screen;

import com.ketheroth.slots.Slots;
import com.ketheroth.slots.common.inventory.container.SlotsMenu;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.client.gui.components.Button;
import com.ketheroth.slots.common.network.UnlockSlotPacket;
import com.ketheroth.slots.common.networking.SlotsPacketHandler;

public class SlotsInventoryScreen extends AbstractContainerScreen<SlotsMenu> {

	private final ResourceLocation GUI =
        new ResourceLocation(Slots.MOD_ID, "textures/gui/slots_inventory.png");

    private final int slotAmount;

	public SlotsInventoryScreen(SlotsMenu menu, Inventory playerInventory, Component title) {
    super(menu, playerInventory, title);

    slotAmount = menu.getSlotAmount();

        int rows = (slotAmount + 8) / 9;

        this.imageWidth = 176;
        this.imageHeight = 114 + rows * 18;

        int playerInventoryY = 18 + rows * 18 + 14;
        this.inventoryLabelY = playerInventoryY - 10;
    
}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		this.renderBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, partialTick);
		this.renderTooltip(guiGraphics, mouseX, mouseY);
		// TODO: @ketheroth render next slot to unlock "unlock this slot by having x xp levels"
	}
    @Override
    protected void init() {
        super.init();

        this.addRenderableWidget(
            Button.builder(Component.literal("+"), button -> {

                SlotsPacketHandler.INSTANCE.sendToServer(
            new UnlockSlotPacket());

            }).bounds(
                this.leftPos + 157,
                this.topPos + 3,
                12,
                12
            ).build()
        );
    }

	@Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {

    int rows = Math.min(6, (slotAmount + 8) / 9);

    int left = (this.width - this.imageWidth) / 2;
    int top = (this.height - this.imageHeight) / 2;

    // 上側（背景）
    guiGraphics.blit(
            GUI,
            left,
            top,
            0,
            0,
            176,
            17 + rows * 18
    );

    // 下側（プレイヤーインベントリ）
    guiGraphics.blit(
            GUI,
            left,
            top + 17 + rows * 18,
            0,
            126,
            176,
            96
    );

    // 解放済みスロットだけ枠を描画
    for (int i = 0; i < slotAmount; i++) {

        int x = i % 9;
        int y = i / 9;

        guiGraphics.blit(
                GUI,
                left + 7 + x * 18,
                top + 17 + y * 18,
                0,
                238,
                18,
                18
        );
    }
}
}