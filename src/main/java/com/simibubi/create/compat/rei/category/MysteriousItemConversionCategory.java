package com.simibubi.create.compat.rei.category;

import java.util.ArrayList;
import java.util.List;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.compat.rei.ConversionRecipe;
import com.simibubi.create.compat.rei.display.CreateDisplay;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.gui.AllGuiTextures;

import me.shedaniel.math.Point;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.client.gui.GuiGraphics;

public class MysteriousItemConversionCategory extends CreateRecipeCategory<ConversionRecipe> {

	public static final List<ConversionRecipe> RECIPES = new ArrayList<>();

	static {
		RECIPES.add(ConversionRecipe.create(AllItems.EMPTY_BLAZE_BURNER.asStack(), AllBlocks.BLAZE_BURNER.asStack()));
		RECIPES.add(ConversionRecipe.create(AllBlocks.PECULIAR_BELL.asStack(), AllBlocks.HAUNTED_BELL.asStack()));
		RECIPES.add(ConversionRecipe.create(AllItems.CHROMATIC_COMPOUND.asStack(), AllItems.SHADOW_STEEL.asStack()));
		RECIPES.add(ConversionRecipe.create(AllItems.CHROMATIC_COMPOUND.asStack(), AllItems.REFINED_RADIANCE.asStack()));
	}

	public MysteriousItemConversionCategory(Info<ConversionRecipe> info) {
		super(info);
	}

	@Override
	public void addWidgets(CreateDisplay<ConversionRecipe> display, List<Widget> ingredients, Point origin) {
		List<ProcessingOutput> results = display.getRecipe().getRollableResults();
		ingredients.add(basicSlot(origin.x + 27, origin.y + 17)
				.markInput()
				.entries(display.getInputEntries().get(0)));
		ingredients.add(basicSlot(origin.x + 132, origin.y + 17)
				.markOutput()
				.entries(EntryIngredients.of(results.get(0).getStack())));
	}

	@Override
	public void draw(ConversionRecipe recipe, GuiGraphics graphics, double mouseX, double mouseY) {
		AllGuiTextures.JEI_SLOT.render(graphics, 26, 16);
		AllGuiTextures.JEI_SLOT.render(graphics, 131, 16);
		AllGuiTextures.JEI_LONG_ARROW.render(graphics, 52, 20);
		AllGuiTextures.JEI_QUESTION_MARK.render(graphics, 77, 5);
	}

}
