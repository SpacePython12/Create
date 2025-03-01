package com.simibubi.create.infrastructure.fabric;

import com.simibubi.create.api.registry.SimpleRegistry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public abstract class HelmetOverlay {
	public static final SimpleRegistry<Item, HelmetOverlay> REGISTRY = SimpleRegistry.create();

	public final ResourceLocation texture;

	protected HelmetOverlay(ResourceLocation texture) {
		this.texture = texture;
	}

	public abstract float calculateOpacity(ItemStack stack, Player player, float partialTicks);
}
