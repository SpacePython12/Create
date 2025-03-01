package com.simibubi.create.compat.rei.category;

import java.util.Collections;
import java.util.List;

import com.simibubi.create.foundation.gui.AllGuiTextures;

import me.shedaniel.rei.api.client.gui.widgets.Widget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;

public class WidgetUtil {
	public static Widget textured(AllGuiTextures texture, int x, int y) {
		return new Widget() {
			@Override
			public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
				texture.render(graphics, x, y);
			}

			@Override
			public List<? extends GuiEventListener> children() {
				return Collections.emptyList();
			}
		};
	}
}
