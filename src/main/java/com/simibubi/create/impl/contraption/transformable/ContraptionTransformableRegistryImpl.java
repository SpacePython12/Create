package com.simibubi.create.impl.contraption.transformable;

import org.jetbrains.annotations.ApiStatus;

import com.simibubi.create.api.contraption.transformable.ContraptionTransformableRegistry.TransformableBlock;
import com.simibubi.create.api.contraption.transformable.ContraptionTransformableRegistry.TransformableBlockEntity;
import com.simibubi.create.foundation.utility.AttachedRegistry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

@ApiStatus.Internal
public class ContraptionTransformableRegistryImpl {
	private static final AttachedRegistry<Block, TransformableBlock> TRANSFORMABLE_BLOCKS = new AttachedRegistry<>(BuiltInRegistries.BLOCK);
	private static final AttachedRegistry<BlockEntityType<?>, TransformableBlockEntity> TRANSFORMABLE_BLOCK_ENTITIES = new AttachedRegistry<>(BuiltInRegistries.BLOCK_ENTITY_TYPE);

	public static void registerForBlock(Block block, TransformableBlock transformableBlock) {
		TRANSFORMABLE_BLOCKS.register(block, transformableBlock);
	}

	public static void registerForBlockEntity(BlockEntityType<?> blockEntityType, TransformableBlockEntity transformableBlockEntity) {
		TRANSFORMABLE_BLOCK_ENTITIES.register(blockEntityType, transformableBlockEntity);
	}

	public static TransformableBlock get(Block block) {
		return TRANSFORMABLE_BLOCKS.get(block);
	}

	public static TransformableBlockEntity get(BlockEntityType<?> blockEntityType) {
		return TRANSFORMABLE_BLOCK_ENTITIES.get(blockEntityType);
	}
}
