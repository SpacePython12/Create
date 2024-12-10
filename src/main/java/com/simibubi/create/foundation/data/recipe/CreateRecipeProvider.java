package com.simibubi.create.foundation.data.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags;
import com.simibubi.create.Create;

import io.github.fabricators_of_create.porting_lib.tags.Tags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public abstract class CreateRecipeProvider extends FabricRecipeProvider {

	protected final List<GeneratedRecipe> all = new ArrayList<>();

	public CreateRecipeProvider(FabricDataOutput output) {
		super(output);
	}

	@Override
	public void buildRecipes(Consumer<FinishedRecipe> p_200404_1_) {
		all.forEach(c -> c.register(p_200404_1_));
		Create.LOGGER.info(getName() + " registered " + all.size() + " recipe" + (all.size() == 1 ? "" : "s"));
	}

	protected GeneratedRecipe register(GeneratedRecipe recipe) {
		all.add(recipe);
		return recipe;
	}

	@FunctionalInterface
	public interface GeneratedRecipe {
		void register(Consumer<FinishedRecipe> consumer);
	}

	protected static class Marker {
	}

	protected static class I {

		static TagKey<Item> redstone() {
			return Tags.Items.DUSTS_REDSTONE;
		}

		static TagKey<Item> planks() {
			return ItemTags.PLANKS;
		}

		static TagKey<Item> woodSlab() {
			return ItemTags.WOODEN_SLABS;
		}

		static TagKey<Item> gold() {
			return Tags.Items.INGOTS_GOLD;
		}

		static TagKey<Item> goldSheet() {
			return AllTags.forgeItemTag("gold_plates");
		}

		static TagKey<Item> stone() {
			return Tags.Items.STONE;
		}

		static ItemLike andesiteAlloy() {
			return AllItems.ANDESITE_ALLOY.get();
		}

		static ItemLike shaft() {
			return AllBlocks.SHAFT.get();
		}

		static ItemLike cog() {
			return AllBlocks.COGWHEEL.get();
		}

		static ItemLike largeCog() {
			return AllBlocks.LARGE_COGWHEEL.get();
		}

		static ItemLike andesiteCasing() {
			return AllBlocks.ANDESITE_CASING.get();
		}
		
		static ItemLike vault() {
			return AllBlocks.ITEM_VAULT.get();
		}
		
		static ItemLike stockLink() {
			return AllBlocks.STOCK_LINK.get();
		}

		static TagKey<Item> brass() {
			return AllTags.forgeItemTag("brass_ingots");
		}

		static TagKey<Item> brassSheet() {
			return AllTags.forgeItemTag("brass_plates");
		}

		static TagKey<Item> iron() {
			return Tags.Items.INGOTS_IRON;
		}

		static TagKey<Item> ironNugget() {
			return Tags.Items.NUGGETS_IRON;
		}

		static TagKey<Item> zinc() {
			return AllTags.forgeItemTag("zinc_ingots");
		}

		static TagKey<Item> ironSheet() {
			return AllTags.forgeItemTag("iron_plates");
		}

		static TagKey<Item> sturdySheet() {
			return AllTags.forgeItemTag("obsidian_plates");
		}

		static ItemLike brassCasing() {
			return AllBlocks.BRASS_CASING.get();
		}
		
		static ItemLike cardboard() {
			return AllItems.CARDBOARD.get();
		}

		static ItemLike railwayCasing() {
			return AllBlocks.RAILWAY_CASING.get();
		}

		static ItemLike electronTube() {
			return AllItems.ELECTRON_TUBE.get();
		}

		static ItemLike precisionMechanism() {
			return AllItems.PRECISION_MECHANISM.get();
		}

		static TagKey<Item> brassBlock() {
			return AllTags.forgeItemTag("brass_blocks");
		}

		static TagKey<Item> zincBlock() {
			return AllTags.forgeItemTag("zinc_blocks");
		}

		static TagKey<Item> wheatFlour() {
			return AllTags.forgeItemTag("wheat_flour");
		}

		static TagKey<Item> copper() {
			return Tags.Items.INGOTS_COPPER;
		}

		static TagKey<Item> copperNugget() {
			return AllTags.forgeItemTag("copper_nuggets");
		}

		static TagKey<Item> copperBlock() {
			return Tags.Items.STORAGE_BLOCKS_COPPER;
		}

		static TagKey<Item> copperSheet() {
			return AllTags.forgeItemTag("copper_plates");
		}

		static TagKey<Item> brassNugget() {
			return AllTags.forgeItemTag("brass_nuggets");
		}

		static TagKey<Item> zincNugget() {
			return AllTags.forgeItemTag("zinc_nuggets");
		}

		static ItemLike copperCasing() {
			return AllBlocks.COPPER_CASING.get();
		}

		static ItemLike refinedRadiance() {
			return AllItems.REFINED_RADIANCE.get();
		}

		static ItemLike shadowSteel() {
			return AllItems.SHADOW_STEEL.get();
		}

		static Ingredient netherite() {
			return Ingredient.of(Tags.Items.INGOTS_NETHERITE);
		}

	}
}
