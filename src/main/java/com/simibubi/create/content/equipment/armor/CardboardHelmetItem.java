package com.simibubi.create.content.equipment.armor;

import java.util.function.Consumer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class CardboardHelmetItem extends CardboardArmorItem {

	public CardboardHelmetItem(Type type, Properties properties) {
		super(type, properties);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		super.initializeClient(consumer);
		consumer.accept(new CardboardArmorStealthOverlay());
	}

}
