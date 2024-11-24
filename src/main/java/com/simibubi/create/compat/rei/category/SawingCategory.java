package com.simibubi.create.compat.rei.category;

import java.util.ArrayList;
import java.util.List;

import com.simibubi.create.compat.rei.category.animations.AnimatedSaw;
import com.simibubi.create.compat.rei.display.CreateDisplay;
import com.simibubi.create.content.kinetics.saw.CuttingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.foundation.utility.Lang;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class SawingCategory extends CreateRecipeCategory<CuttingRecipe> {

	public SawingCategory(Info<CuttingRecipe> info) {
		super(info);
	}

	@Override
	public List<Widget> setupDisplay(CreateDisplay<CuttingRecipe> display, Rectangle bounds) {
		Point origin = new Point(bounds.getX(), bounds.getY() + 4);
		List<Widget> widgets = new ArrayList<>();
		List<ProcessingOutput> results = display.getRecipe().getRollableResults();
		widgets.add(Widgets.createRecipeBase(bounds));
		widgets.add(Widgets.createSlot(new Point(origin.x + 44, origin.y + 5)).disableBackground().markInput().entries(display.getInputEntries().get(0)));
		widgets.add(WidgetUtil.textured(AllGuiTextures.JEI_SLOT, origin.x + 43, origin.y + 4));
		widgets.add(WidgetUtil.textured(AllGuiTextures.JEI_DOWN_ARROW, origin.x + 70, origin.y + 6));
		widgets.add(WidgetUtil.textured(AllGuiTextures.JEI_SHADOW, origin.x + 72 - 17, origin.y + 42 + 13));

		for (int outputIndex = 0; outputIndex < results.size(); outputIndex++) {
			int xOffset = outputIndex % 2 == 0 ? 0 : 19;
			int yOffset = (outputIndex / 2) * -19;
			List<Component> tooltip = new ArrayList<>();
			if (results.get(outputIndex).getChance() != 1)
				tooltip.add(CreateLang.translateDirect("recipe.processing.chance", results.get(outputIndex).getChance() < 0.01 ? "<1" : (int) (results.get(outputIndex).getChance() * 100))
						.withStyle(ChatFormatting.GOLD));
			widgets.add(Widgets.createSlot(new Point((origin.x + 117 + xOffset) + 1, (origin.y + 47 + yOffset) + 1)).disableBackground().markOutput().entry(EntryStack.of(VanillaEntryTypes.ITEM, results.get(outputIndex).getStack()).tooltip(tooltip)));
			widgets.add(WidgetUtil.textured(getRenderedSlot(display.getRecipe(), outputIndex), origin.x + 117 + xOffset, origin.y + 47 + yOffset));
		}

		AnimatedSaw saw = new AnimatedSaw();
		saw.setPos(new Point(origin.x + 72, origin.y + 42));
		widgets.add(saw);

		return widgets;
	}
}
