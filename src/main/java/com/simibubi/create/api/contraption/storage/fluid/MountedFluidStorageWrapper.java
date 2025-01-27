package com.simibubi.create.api.contraption.storage.fluid;

import com.google.common.collect.ImmutableMap;

import net.minecraft.core.BlockPos;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;

/**
 * Wrapper around many MountedFluidStorages, providing access to all of them as one storage.
 * They can still be accessed individually through the map.
 */
public class MountedFluidStorageWrapper extends CombinedStorage<FluidVariant, MountedFluidStorage> {
	public final ImmutableMap<BlockPos, MountedFluidStorage> storages;

	public MountedFluidStorageWrapper(ImmutableMap<BlockPos, MountedFluidStorage> storages) {
		super(storages.values().stream().toList());
		this.storages = storages;
	}
}
