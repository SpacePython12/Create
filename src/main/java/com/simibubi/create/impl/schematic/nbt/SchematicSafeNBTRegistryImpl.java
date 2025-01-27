package com.simibubi.create.impl.schematic.nbt;

import org.jetbrains.annotations.ApiStatus;

import com.simibubi.create.api.schematic.nbt.SchematicSafeNBTRegistry;
import com.simibubi.create.foundation.utility.AttachedRegistry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

@ApiStatus.Internal
public class SchematicSafeNBTRegistryImpl {
	private static final AttachedRegistry<BlockEntityType<?>, SchematicSafeNBTRegistry.ContextProvidingPartialSafeNBT> BLOCK_ENTITY_PARTIAL_SAFE_NBT = new AttachedRegistry<>(BuiltInRegistries.BLOCK_ENTITY_TYPE);

	public static void register(BlockEntityType<? extends BlockEntity> blockEntityType, SchematicSafeNBTRegistry.ContextProvidingPartialSafeNBT safeNBT) {
		BLOCK_ENTITY_PARTIAL_SAFE_NBT.register(blockEntityType, safeNBT);
	}

	public static SchematicSafeNBTRegistry.ContextProvidingPartialSafeNBT getPartialSafeNBT(BlockEntityType<? extends BlockEntity> blockEntityType) {
		return BLOCK_ENTITY_PARTIAL_SAFE_NBT.get(blockEntityType);
	}
}
