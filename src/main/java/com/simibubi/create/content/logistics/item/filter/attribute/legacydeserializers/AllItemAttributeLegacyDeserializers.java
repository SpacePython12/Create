package com.simibubi.create.content.logistics.item.filter.attribute.legacydeserializers;

import java.util.function.Function;
import java.util.function.Supplier;

import org.jetbrains.annotations.ApiStatus;

import com.simibubi.create.content.logistics.item.filter.attribute.AllItemAttributeTypes;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;
import com.simibubi.create.content.logistics.item.filter.attribute.attributes.EnchantAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.attributes.InTagAttribute;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

@SuppressWarnings("deprecation")
public class AllItemAttributeLegacyDeserializers {
	@ApiStatus.Internal
	public static void register() {
		addLegacyDeserializer(new StandardTraitsLegacyDeserializer());
		createLegacyDeserializer("in_tag", tag ->
			new InTagAttribute(TagKey.create(Registries.ITEM, new ResourceLocation(
				tag.getString("space"),
				tag.getString("path")
			)))
		);
		createLegacyDeserializer("in_item_group", AllItemAttributeTypes.IN_ITEM_GROUP);
		createLegacyDeserializer("added_by", AllItemAttributeTypes.ADDED_BY);
		createLegacyDeserializer("has_enchant", tag ->
			new EnchantAttribute(BuiltInRegistries.ENCHANTMENT.get(ResourceLocation.tryParse(tag.getString("id")))));
		createLegacyDeserializer("shulker_fill_level", AllItemAttributeTypes.SHULKER_FILL_LEVEL);
		createLegacyDeserializer("has_color", AllItemAttributeTypes.HAS_COLOR);
		createLegacyDeserializer("has_fluid", AllItemAttributeTypes.HAS_FLUID);
		createLegacyDeserializer("has_name", AllItemAttributeTypes.HAS_NAME);
		createLegacyDeserializer("book_author", AllItemAttributeTypes.BOOK_AUTHOR);
		createLegacyDeserializer("book_copy", AllItemAttributeTypes.BOOK_COPY);
		createLegacyDeserializer("astralsorcery_amulet", AllItemAttributeTypes.ASTRAL_AMULET);
		createLegacyDeserializer("astralsorcery_constellation", AllItemAttributeTypes.ASTRAL_ATTUNMENT);
		createLegacyDeserializer("astralsorcery_crystal", AllItemAttributeTypes.ASTRAL_CRYSTAL);
		createLegacyDeserializer("astralsorcery_perk_gem", AllItemAttributeTypes.ASTRAL_PERK_GEM);
	}

	private static void createLegacyDeserializer(String nbtKey, Supplier<ItemAttributeType> type) {
		createLegacyDeserializer(nbtKey, tag -> {
			ItemAttribute attribute = type.get().createAttribute();
			attribute.load(tag);
			return attribute;
		});
	}

	private static void createLegacyDeserializer(String nbtKey, Function<CompoundTag, ItemAttribute> func) {
		addLegacyDeserializer(new ItemAttribute.LegacyDeserializer() {
			@Override
			public String getNBTKey() {
				return nbtKey;
			}

			@Override
			public ItemAttribute readNBT(CompoundTag nbt) {
				return func.apply(nbt);
			}
		});
	}

	private static void addLegacyDeserializer(ItemAttribute.LegacyDeserializer legacyDeserializer) {
		ItemAttribute.LegacyDeserializer.ALL.add(legacyDeserializer);
	}
}
