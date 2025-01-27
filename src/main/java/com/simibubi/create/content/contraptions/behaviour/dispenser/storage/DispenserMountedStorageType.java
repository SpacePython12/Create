package com.simibubi.create.content.contraptions.behaviour.dispenser.storage;

import com.simibubi.create.api.contraption.storage.item.simple.SimpleMountedStorage;
import com.simibubi.create.api.contraption.storage.item.simple.SimpleMountedStorageType;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;

public class DispenserMountedStorageType extends SimpleMountedStorageType<DispenserMountedStorage> {
	public DispenserMountedStorageType() {
		super(DispenserMountedStorage.CODEC);
	}

	@Override
	protected SimpleMountedStorage createStorage(SlottedStorage<ItemVariant> storage) {
		return new DispenserMountedStorage(storage);
	}
}
