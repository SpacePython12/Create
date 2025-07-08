package com.simibubi.create.content.logistics.packager;

import com.simibubi.create.content.logistics.box.PackageItem;

import net.minecraft.world.item.ItemStack;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext.Result;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import io.github.fabricators_of_create.porting_lib.transfer.callbacks.TransactionCallback;

public class PackagerItemHandler extends SnapshotParticipant<ItemStack> implements SingleSlotStorage<ItemVariant> {

	private final PackagerBlockEntity blockEntity;

	private boolean unpackSuccessful = false;

	public PackagerItemHandler(PackagerBlockEntity blockEntity) {
		this.blockEntity = blockEntity;
	}

	@Override
	public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		StoragePreconditions.notBlankNotNegative(resource, maxAmount);
		if (!blockEntity.heldBox.isEmpty() || !blockEntity.queuedExitingPackages.isEmpty())
			return 0;
		if (!PackageItem.isPackage(resource))
			return 0;
		ItemStack stack = resource.toStack(1);
		this.updateSnapshots(transaction);
		if (blockEntity.unwrapBox(stack, transaction)) {
			this.unpackSuccessful = true;
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		StoragePreconditions.notBlankNotNegative(resource, maxAmount);
		if (blockEntity.animationTicks != 0)
			return 0;
		ItemStack box = blockEntity.heldBox;
		if (!resource.matches(box))
			return 0;
		this.updateSnapshots(transaction);
		blockEntity.heldBox = ItemStack.EMPTY;
		return box.getCount();
	}

	@Override
	public boolean isResourceBlank() {
		return blockEntity.heldBox.isEmpty();
	}

	@Override
	public ItemVariant getResource() {
		return ItemVariant.of(blockEntity.heldBox);
	}

	@Override
	public long getAmount() {
		return blockEntity.heldBox.getCount();
	}

	@Override
	public long getCapacity() {
		return 1;
	}

	@Override
	protected ItemStack createSnapshot() {
		return blockEntity.heldBox.copy();
	}

	@Override
	protected void readSnapshot(ItemStack snapshot) {
		blockEntity.heldBox = snapshot;
	}

	@Override
	protected void onFinalCommit() {
		if (this.unpackSuccessful) {
			blockEntity.scheduleStockCheck();
			this.unpackSuccessful = false;
		}
		blockEntity.notifyUpdate();
	}
}
