package com.simibubi.create.impl.contraption.storage;

import org.jetbrains.annotations.ApiStatus;

import com.simibubi.create.api.contraption.storage.MountedStorageTypeRegistry;
import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageType;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import com.simibubi.create.api.lookup.BlockLookup;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;

// fabric: registries no longer lazy, fallback removed
@ApiStatus.Internal
public class MountedStorageTypeRegistryImpl {
	public static final Registry<MountedItemStorageType<?>> ITEMS_REGISTRY = registry(MountedStorageTypeRegistry.ITEMS);
	public static final Registry<MountedFluidStorageType<?>> FLUIDS_REGISTRY = registry(MountedStorageTypeRegistry.FLUIDS);

	public static final BlockLookup<MountedItemStorageType<?>> ITEM_LOOKUP = BlockLookup.create();
	public static final BlockLookup<MountedFluidStorageType<?>> FLUID_LOOKUP = BlockLookup.create();

	private static <T> Registry<T> registry(ResourceKey<Registry<T>> key) {
		return FabricRegistryBuilder.createSimple(key).buildAndRegister();
	}

	public static void init() {
	}
}
