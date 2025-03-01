package com.simibubi.create.compat.rei;

import java.util.function.Supplier;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

public class DoubleItemIcon implements Renderer {

	private Supplier<ItemStack> primarySupplier;
	private Supplier<ItemStack> secondarySupplier;
	private ItemStack primaryStack;
	private ItemStack secondaryStack;
	private Point pos;

	public DoubleItemIcon(Supplier<ItemStack> primary, Supplier<ItemStack> secondary) {
		this.primarySupplier = primary;
		this.secondarySupplier = secondary;
	}

	public DoubleItemIcon setPos(Point pos) {
		this.pos = pos;
		return this;
	}

	@Override
	public void render(GuiGraphics graphics, Rectangle bounds, int mouseX, int mouseY, float delta) {
		if (primaryStack == null) {
			primaryStack = primarySupplier.get();
			secondaryStack = secondarySupplier.get();
		}

		PoseStack matrixStack = graphics.pose();
		RenderSystem.enableDepthTest();
		matrixStack.pushPose();
		if(pos == null)
			matrixStack.translate(bounds.getCenterX() - 9, bounds.getCenterY() - 9, 0);
		else
			matrixStack.translate(pos.getX(), pos.getY(), 0);

		matrixStack.pushPose();
		matrixStack.translate(1, 1, 0);
		GuiGameElement.of(primaryStack)
			.render(graphics);
		matrixStack.popPose();

		matrixStack.pushPose();
		matrixStack.translate(10, 10, 100);
		matrixStack.scale(.5f, .5f, .5f);
		GuiGameElement.of(secondaryStack)
			.render(graphics);
		matrixStack.popPose();

		matrixStack.popPose();
	}
}
