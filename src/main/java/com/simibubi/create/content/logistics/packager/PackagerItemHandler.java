package com.simibubi.create.content.logistics.packager;

import com.simibubi.create.content.logistics.box.PackageItem;

import io.github.fabricators_of_create.porting_lib.transfer.item.ItemHandlerHelper;
import net.minecraft.world.item.ItemStack;

public class PackagerItemHandler implements IItemHandlerModifiable {

	private PackagerBlockEntity blockEntity;

	public PackagerItemHandler(PackagerBlockEntity blockEntity) {
		this.blockEntity = blockEntity;
	}

	@Override
	public int getSlots() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return blockEntity.heldBox;
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		if (slot != 0)
			return;
		blockEntity.heldBox = stack;
		blockEntity.notifyUpdate();
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if (!blockEntity.heldBox.isEmpty() || !blockEntity.queuedExitingPackages.isEmpty())
			return stack;
		if (!isItemValid(slot, stack))
			return stack;
		if (!blockEntity.unwrapBox(stack, true))
			return stack;
		if (!simulate) {
			blockEntity.unwrapBox(stack, false);
			blockEntity.triggerStockCheck();
		}
		return ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - 1);
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (blockEntity.animationTicks != 0)
			return ItemStack.EMPTY;
		ItemStack box = blockEntity.heldBox;
		if (!simulate)
			setStackInSlot(slot, ItemStack.EMPTY);
		return box;
	}

	@Override
	public int getSlotLimit(int slot) {
		return 1;
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		return stack.getItem() instanceof PackageItem;
	}

}
