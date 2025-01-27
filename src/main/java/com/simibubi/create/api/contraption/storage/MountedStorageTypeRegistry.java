package com.simibubi.create.api.contraption.storage;

import com.simibubi.create.Create;
import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageType;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import com.simibubi.create.api.lookup.BlockLookup;
import com.simibubi.create.impl.contraption.storage.MountedStorageTypeRegistryImpl;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

// fabric: registries no longer lazy
public class MountedStorageTypeRegistry {
	public static final ResourceKey<Registry<MountedItemStorageType<?>>> ITEMS = ResourceKey.createRegistryKey(
		Create.asResource("mounted_item_storage_type")
	);
	public static final Registry<MountedItemStorageType<?>> ITEMS_REGISTRY = MountedStorageTypeRegistryImpl.ITEMS_REGISTRY;

	public static final ResourceKey<Registry<MountedFluidStorageType<?>>> FLUIDS = ResourceKey.createRegistryKey(
		Create.asResource("mounted_fluid_storage_type")
	);
	public static final Registry<MountedFluidStorageType<?>> FLUIDS_REGISTRY = MountedStorageTypeRegistryImpl.FLUIDS_REGISTRY;

	/**
	 * Lookup used for finding the item storage type associated with a block.
	 * @see BlockLookup
	 */
	public static final BlockLookup<MountedItemStorageType<?>> ITEM_LOOKUP = MountedStorageTypeRegistryImpl.ITEM_LOOKUP;
	/**
	 * Lookup used for finding the fluid storage type associated with a block.
	 * @see BlockLookup
	 */
	public static final BlockLookup<MountedFluidStorageType<?>> FLUID_LOOKUP = MountedStorageTypeRegistryImpl.FLUID_LOOKUP;

	/**
	 * Utility for use with Registrate builders. Creates a builder transformer
	 * that will register the given MountedItemStorageType to a block when ready.
	 */
	public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> mountedItemStorage(RegistryEntry<? extends MountedItemStorageType<?>> type) {
		return builder -> builder.onRegisterAfter(ITEMS, block -> ITEM_LOOKUP.register(block, type.get()));
	}

	/**
	 * Utility for use with Registrate builders. Creates a builder transformer
	 * that will register the given MountedFluidStorageType to a block when ready.
	 */
	public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> mountedFluidStorage(RegistryEntry<? extends MountedFluidStorageType<?>> type) {
		return builder -> builder.onRegisterAfter(ITEMS, block -> FLUID_LOOKUP.register(block, type.get()));
	}
}
