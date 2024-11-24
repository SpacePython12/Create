package com.simibubi.create.compat.rei.category;

import org.jetbrains.annotations.NotNull;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.rei.category.animations.AnimatedKinetics;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import net.createmod.catnip.gui.element.GuiGameElement;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.level.material.Fluids;

public class FanBlastingCategory extends ProcessingViaFanCategory<AbstractCookingRecipe> {

	public FanBlastingCategory(Info<AbstractCookingRecipe> info) {
		super(info);
	}

	@Override
	protected AllGuiTextures getBlockShadow() {
		return AllGuiTextures.JEI_LIGHT;
	}

	@Override
	protected void renderAttachedBlock(@NotNull GuiGraphics graphics) {
		GuiGameElement.of(Fluids.LAVA)
			.scale(SCALE)
			.atLocal(0, 0, 2)
			.lighting(AnimatedKinetics.DEFAULT_LIGHTING)
			.render(graphics);
	}

}
