package com.simibubi.create.api.contraption.storage.fluid;

import java.util.Iterator;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

/**
 * Partial implementation of a MountedFluidStorage that wraps a fluid handler.
 */
public abstract class WrapperMountedFluidStorage<T extends Storage<FluidVariant>> extends MountedFluidStorage {
	protected final T wrapped;

	protected WrapperMountedFluidStorage(MountedFluidStorageType<?> type, T wrapped) {
		super(type);
		this.wrapped = wrapped;
	}

	@Override
	public long getVersion() {
		return this.wrapped.getVersion();
	}

	@Override
	public Iterable<StorageView<FluidVariant>> nonEmptyViews() {
		return this.wrapped.nonEmptyViews();
	}

	@Override
	public Iterator<StorageView<FluidVariant>> nonEmptyIterator() {
		return this.wrapped.nonEmptyIterator();
	}

	@Override
	public Iterator<StorageView<FluidVariant>> iterator() {
		return this.wrapped.iterator();
	}

	@Override
	public long extract(FluidVariant fluidVariant, long l, TransactionContext transactionContext) {
		return this.wrapped.extract(fluidVariant, l, transactionContext);
	}

	@Override
	public boolean supportsExtraction() {
		return this.wrapped.supportsExtraction();
	}

	@Override
	public long insert(FluidVariant fluidVariant, long l, TransactionContext transactionContext) {
		return this.wrapped.insert(fluidVariant, l, transactionContext);
	}

	@Override
	public boolean supportsInsertion() {
		return this.wrapped.supportsInsertion();
	}
}
