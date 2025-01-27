package com.simibubi.create.compat.thresholdSwitch;

import net.minecraft.world.level.block.entity.BlockEntity;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;

public interface ThresholdSwitchCompat {

	boolean isFromThisMod(BlockEntity blockEntity);

	long getSpaceInSlot(StorageView<ItemVariant> inv);

}
