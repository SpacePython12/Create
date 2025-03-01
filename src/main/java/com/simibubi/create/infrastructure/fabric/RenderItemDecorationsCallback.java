package com.simibubi.create.infrastructure.fabric;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

import net.fabricmc.fabric.api.event.Event;

/**
 * Called when item decorations are rendered, like an item's count.
 */
@FunctionalInterface
public interface RenderItemDecorationsCallback {
	Event<RenderItemDecorationsCallback> EVENT = EventUtil.create(callbacks -> (graphics, font, stack, x, y) -> {
		for (RenderItemDecorationsCallback callback : callbacks) {
			callback.render(graphics, font, stack, x, y);
		}
	});

	/**
	 * Render additional item decorations for the given item.
	 */
	void render(GuiGraphics graphics, Font font, ItemStack stack, int x, int y);
}
