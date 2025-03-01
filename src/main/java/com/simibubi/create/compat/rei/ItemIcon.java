package com.simibubi.create.compat.rei;

import java.util.function.Supplier;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

public class ItemIcon implements Renderer {

	private Supplier<ItemStack> supplier;
	private ItemStack stack;

	public ItemIcon(Supplier<ItemStack> stack) {
		this.supplier = stack;
	}

	@Override
	public void render(GuiGraphics graphics, Rectangle bounds, int mouseX, int mouseY, float delta) {
		if (stack == null) {
			stack = supplier.get();
		}

		int xOffset = bounds.x;
		int yOffset = bounds.y;

		PoseStack matrixStack = graphics.pose();
		RenderSystem.enableDepthTest();
		matrixStack.pushPose();
		matrixStack.translate(xOffset, yOffset, 0);

		GuiGameElement.of(stack)
				.render(graphics);

		matrixStack.popPose();
	}
}
