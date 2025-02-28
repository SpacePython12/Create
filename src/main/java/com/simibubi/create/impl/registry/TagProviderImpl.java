package com.simibubi.create.impl.registry;

import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.api.registry.SimpleRegistry;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.entity.BlockEntityType;

import io.github.fabricators_of_create.porting_lib.event.common.TagsUpdatedCallback;

public class TagProviderImpl<K, V> implements SimpleRegistry.Provider<K, V> {
	private final TagKey<K> tag;
	private final Function<K, Holder<K>> holderGetter;
	private final V value;

	public TagProviderImpl(TagKey<K> tag, Function<K, Holder<K>> holderGetter, V value) {
		this.tag = tag;
		this.holderGetter = holderGetter;
		this.value = value;
	}

	@Override
	@Nullable
	public V get(K object) {
		Holder<K> holder = this.holderGetter.apply(object);
		return holder.is(this.tag) ? this.value : null;
	}

	@Override
	public void onRegister(Runnable invalidate) {
		TagsUpdatedCallback.EVENT.register(registries -> invalidate.run());
	}

	// eye of the beholder? check the nametag, buddy
	public static Holder<BlockEntityType<?>> getBeHolder(BlockEntityType<?> type) {
		return BuiltInRegistries.BLOCK_ENTITY_TYPE.wrapAsHolder(type);
	}
}
