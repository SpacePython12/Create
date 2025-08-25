package com.simibubi.create.content.logistics.packagePort;

import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.foundation.item.ItemHandlerWrapper;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public class PackagePortAutomationInventoryWrapper extends ItemHandlerWrapper {
	private final PackagePortBlockEntity ppbe;

	public PackagePortAutomationInventoryWrapper(Storage<ItemVariant> wrapped, PackagePortBlockEntity ppbe) {
		super(wrapped);
		this.ppbe = ppbe;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		ItemStack preview = super.extractItem(slot, 64, true);

		if (!PackageItem.isPackage(preview))
			return ItemStack.EMPTY;

		String filterString = ppbe.getFilterString();
		if (filterString == null || !PackageItem.matchAddress(preview, filterString))
			return ItemStack.EMPTY;

		return simulate ? preview : super.extractItem(slot, amount, false);
	}

	@Override
	public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		if (!PackageItem.isPackage(resource))
			return 0;
		String filterString = ppbe.getFilterString();
		if (filterString != null && PackageItem.matchAddress(stack, filterString))
			return stack;
		return super.insertItem(slot, stack, simulate);
	}
}
