package com.simibubi.create.compat.trinkets;

import java.util.Optional;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.equipment.goggles.GogglesItem;

import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import dev.emi.trinkets.api.client.TrinketRendererRegistry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class Trinkets {
	public static void init() {
		GogglesItem.addIsWearingPredicate(player -> {
			Optional<TrinketComponent> optional = TrinketsApi.getTrinketComponent(player);
			if (optional.isPresent()) {
				TrinketComponent component = optional.get();
				if (component.isEquipped(AllItems.GOGGLES.get())) {
					return true;
				}
			}
			return false;
		});
	}

	@Environment(EnvType.CLIENT)
	public static void clientInit() {
		TrinketRendererRegistry.registerRenderer(AllItems.GOGGLES.get(), new GoggleTrinketRenderer());
	}
}
