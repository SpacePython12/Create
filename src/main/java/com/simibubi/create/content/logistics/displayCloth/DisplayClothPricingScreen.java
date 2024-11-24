package com.simibubi.create.content.logistics.displayCloth;

import static com.simibubi.create.foundation.gui.AllGuiTextures.PLAYER_INVENTORY;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.utility.CreateLang;

import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.utility.Iterate;
import net.createmod.catnip.utility.lang.Components;
import net.createmod.catnip.utility.theme.Color;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class DisplayClothPricingScreen extends AbstractSimiContainerScreen<DisplayClothPricingMenu> {

	private ScrollInput scrollInput;

	public DisplayClothPricingScreen(DisplayClothPricingMenu container, Inventory inv, Component title) {
		super(container, inv, title);
	}

	@Override
	protected void init() {
		setWindowSize(PLAYER_INVENTORY.getWidth() + 50, PLAYER_INVENTORY.getHeight() + 100);
		super.init();
		clearWidgets();

		int x = leftPos;
		int y = topPos;

		scrollInput = new ScrollInput(x + 33, y - 4 + 72, 100, 20).withRange(1, 513)
			.withShiftStep(10)
			.calling(s -> menu.pricingAmount = s);
		scrollInput.setState(menu.contentHolder.paymentAmount);
		addRenderableWidget(scrollInput);
	}

	final int cols = 8;
	final int rowHeight = 18;
	final int colWidth = 26;

	@Override
	protected void renderBg(GuiGraphics graphics, float pPartialTick, int mouseX, int mouseY) {
		int x = leftPos;
		int y = topPos;

		AllGuiTextures.PLAYER_INVENTORY.render(graphics, x + 25, y + 100);
		graphics.drawString(font, playerInventoryTitle, x + 8 + 25, y + 6 + 100, 0x404040, false);

		Color color = new Color(255, 255, 255, 50);
		int hoveredSlot = getHoveredSlot(mouseX, mouseY);

		// Render some boxes
		graphics.renderOutline(x - 2, y - 4 + 20, cols * colWidth + 6, rowHeight + 8, color.getRGB());
		graphics.renderOutline(x - 2, y - 4 + 72, 20, 20, color.getRGB());
		graphics.renderOutline(scrollInput.getX(), scrollInput.getY(), scrollInput.getWidth(), scrollInput.getHeight(),
			color.getRGB());

		graphics.drawString(font, Components.literal("Price per Order:"), x, y + 52, 0x88dddddd);
		graphics.drawString(font, Components.literal("x"), scrollInput.getX() - 10, scrollInput.getY() + 6, 0x88dddddd,
			true);
		graphics.drawString(font, Components.literal(scrollInput.getState() + ""), scrollInput.getX() + 6,
			scrollInput.getY() + 6, 0x88dddddd, true);

		PoseStack ms = graphics.pose();

		for (int i = 0; i < currentStacks().size(); i++) {
			BigItemStack entry = currentStacks().get(i);
			ms.pushPose();

			ms.translate(x + i % cols * colWidth, y + 20 + i / cols * rowHeight, 200);

			int customCount = currentStacks().get(i).count;
			drawItemCount(graphics, customCount, customCount);
			ms.translate(0, 0, -200);

			float scaleFromHover = hoveredSlot == i ? 1.075f : 1;
			ms.translate((colWidth - 18) / 2.0, (rowHeight - 18) / 2.0, 0);
			ms.translate(18 / 2.0, 18 / 2.0, 0);
			ms.scale(scaleFromHover, scaleFromHover, scaleFromHover);
			ms.translate(-18 / 2.0, -18 / 2.0, 0);
			GuiGameElement.of(entry.stack)
				.render(graphics);
			ms.popPose();
		}

		if (hoveredSlot != -1)
			graphics.renderTooltip(font, currentStacks().get(hoveredSlot).stack, mouseX, mouseY);
	}

	private List<BigItemStack> currentStacks() {
		return menu.contentHolder.requestData.encodedRequest.stacks();
	}

	private int getHoveredSlot(int mouseX, int mouseY) {
		if (mouseY < topPos + 20 || mouseY > topPos + 20 + rowHeight)
			return -1;
		int slot = (mouseX - leftPos) / colWidth;
		if (slot >= currentStacks().size())
			return -1;
		return Math.max(-1, slot);
	}

	private void drawItemCount(GuiGraphics graphics, int count, int customCount) { // TODO same methods in stock ticker
																					// screens
		boolean special = customCount != count;
		if (!special && count == 1)
			return;

		count = customCount;
		String text = count >= 1000000 ? (count / 1000000) + "m"
			: count >= 10000 ? (count / 1000) + "k"
				: count >= 1000 ? ((count * 10) / 1000) / 10f + "k"
					: count >= 100 ? count + "" : count > 0 ? " " + count : " \u2714";

		int lightOutline = 0x444444;
		int darkOutline = 0x222222;
		int middleColor = special ? 0xaaffaa : 0xdddddd;

		for (int xi : Iterate.positiveAndNegative)
			graphics.drawString(font, CreateLang.text(text)
				.component(), 11 + xi, 10, xi < 0 ? lightOutline : darkOutline, false);
		for (int yi : Iterate.positiveAndNegative)
			graphics.drawString(font, CreateLang.text(text)
				.component(), 11, 10 + yi, yi < 0 ? lightOutline : darkOutline, false);
		graphics.drawString(font, CreateLang.text(text)
			.component(), 11, 10, middleColor, false);
	}

}
