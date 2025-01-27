package com.simibubi.create.foundation.utility;

import java.util.Comparator;
import java.util.function.Function;

import net.createmod.catnip.data.IntAttached;
import net.createmod.catnip.data.Pair;
import net.minecraft.nbt.CompoundTag;

/**
 * Fabric: {@link IntAttached} but elongated, for use with transfer and related things.
 */
public class LongAttached<V> extends Pair<Long, V> {

	protected LongAttached(Long first, V second) {
		super(first, second);
	}

	public static <V> LongAttached<V> with(long number, V value) {
		return new LongAttached<>(number, value);
	}

	public static <V> LongAttached<V> withZero(V value) {
		return new LongAttached<>(0L, value);
	}

	public void setFirst(long first) {
		super.setFirst(first);
	}

	public boolean isZero() {
		return getFirst() == 0;
	}

	public boolean exceeds(long value) {
		return getFirst() > value;
	}

	public boolean isOrBelowZero() {
		return getFirst() <= 0;
	}

	public void increment() {
		setFirst(getFirst() + 1);
	}

	public void decrement() {
		setFirst(getFirst() - 1);
	}

	public V getValue() {
		return getSecond();
	}

	public CompoundTag serializeNBT(Function<V, CompoundTag> serializer) {
		CompoundTag nbt = new CompoundTag();
		nbt.put("Item", serializer.apply(getValue()));
		nbt.putLong("Location", getFirst());
		return nbt;
	}

	public static Comparator<? super LongAttached<?>> comparator() {
		return (i1, i2) -> Long.compare(i2.getFirst(), i1.getFirst());
	}

	public static <T> LongAttached<T> read(CompoundTag nbt, Function<CompoundTag, T> deserializer) {
		return LongAttached.with(nbt.getLong("Location"), deserializer.apply(nbt.getCompound("Item")));
	}

}
