package com.simibubi.create.foundation.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import it.unimi.dsi.fastutil.Hash.Strategy;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackLinkedSet;

@Mixin(ItemStackLinkedSet.class)
public interface ItemStackLinkedSetAccessor {
	@Accessor
	static Strategy<? super ItemStack> getTYPE_AND_TAG() {
		throw new AbstractMethodError();
	}
}
