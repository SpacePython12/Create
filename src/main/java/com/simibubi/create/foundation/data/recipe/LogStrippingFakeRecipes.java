package com.simibubi.create.foundation.data.recipe;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.content.kinetics.deployer.ManualApplicationRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;

import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStack.TooltipPart;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.state.BlockState;

import io.github.fabricators_of_create.porting_lib.mixin.accessors.common.accessor.AxeItemAccessor;

/**
 * Just in case players don't know about that vanilla feature
 */
public class LogStrippingFakeRecipes {

	public static List<ManualApplicationRecipe> createRecipes() {
		List<ManualApplicationRecipe> recipes = new ArrayList<>();
		if (!AllConfigs.server().recipes.displayLogStrippingRecipes.get())
			return recipes;

		ItemStack axe = new ItemStack(Items.IRON_AXE);
		axe.hideTooltipPart(TooltipPart.MODIFIERS);
		axe.setHoverName(CreateLang.translateDirect("recipe.item_application.any_axe")
			.withStyle(style -> style.withItalic(false)));
		// fabric: tag may not exist yet with JEI, #773
		BuiltInRegistries.ITEM.getTagOrEmpty(ItemTags.LOGS)
			.forEach(stack -> process(stack.value(), recipes, axe));
		return recipes;
	}

	private static void process(Item item, List<ManualApplicationRecipe> list, ItemStack axe) {
		if (!(item instanceof BlockItem blockItem))
			return;
		BlockState state = blockItem.getBlock()
			.defaultBlockState();
		BlockState strippedState = getStrippedState(state);
		if (strippedState == null)
			return;
		Item resultItem = strippedState.getBlock()
			.asItem();
		if (resultItem == null)
			return;
		list.add(create(item, resultItem, axe));
	}

	private static ManualApplicationRecipe create(Item fromItem, Item toItem, ItemStack axe) {
		ResourceLocation rn = CatnipServices.REGISTRIES.getKeyOrThrow(toItem);
		return new ProcessingRecipeBuilder<>(ManualApplicationRecipe::new,
			new ResourceLocation(rn.getNamespace(), rn.getPath() + "_via_vanilla_stripping")).require(fromItem)
				.require(Ingredient.of(axe))
				.output(toItem)
				.build();
	}

	@Nullable
	public static BlockState getStrippedState(BlockState state) {
		if (Items.IRON_AXE instanceof AxeItemAccessor axe) {
			return axe.porting_lib$getStripped(state).orElse(null);
		}
		return null;
	}
}
