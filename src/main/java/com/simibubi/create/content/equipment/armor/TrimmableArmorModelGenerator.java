package com.simibubi.create.content.equipment.armor;

import com.simibubi.create.Create;
import com.simibubi.create.foundation.mixin.accessor.ItemModelGeneratorsAccessor;
import com.simibubi.create.foundation.mixin.accessor.ModelBuilderAccessor;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;

import io.github.fabricators_of_create.porting_lib.models.generators.item.ItemModelBuilder;

import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;

public class TrimmableArmorModelGenerator {
	public static <T extends ArmorItem> void generate(DataGenContext<Item, T> c, RegistrateItemModelProvider p) {
		T item = c.get();
		ItemModelBuilder builder = p.generated(c);
		for (ItemModelGenerators.TrimModelData data : ItemModelGeneratorsAccessor.create$getGENERATED_TRIM_MODELS()) {
			ResourceLocation modelLoc = ModelLocationUtils.getModelLocation(item);
			ResourceLocation textureLoc = TextureMapping.getItemTexture(item);
			String trimId = data.name(item.getMaterial());
			ResourceLocation trimModelLoc = modelLoc.withSuffix("_" + trimId + "_trim");
			ResourceLocation trimLoc =
					new ResourceLocation("trims/items/" + item.getType().getName() + "_trim_" + trimId);
			String parent = "item/generated";
			if (item.getMaterial() == AllArmorMaterials.CARDBOARD) {
				trimLoc = Create.asResource("trims/items/card_" + item.getType().getName() + "_trim_" + trimId);
			}
			ItemModelBuilder itemModel = p.withExistingParent(trimModelLoc.getPath(), parent)
					.texture("layer0", textureLoc);
			((ModelBuilderAccessor) itemModel).create$getTextures().put("layer1", trimLoc.toString());
			builder.override()
					.predicate(ItemModelGenerators.TRIM_TYPE_PREDICATE_ID, data.itemModelIndex())
					.model(itemModel)
					.end();
		}
	}
}
