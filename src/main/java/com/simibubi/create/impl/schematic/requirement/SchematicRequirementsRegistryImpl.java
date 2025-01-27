package com.simibubi.create.impl.schematic.requirement;

import org.jetbrains.annotations.ApiStatus;

import com.simibubi.create.api.schematic.requirement.SchematicRequirementsRegistry;
import com.simibubi.create.foundation.utility.AttachedRegistry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

@ApiStatus.Internal
public class SchematicRequirementsRegistryImpl {
	private static final AttachedRegistry<Block, SchematicRequirementsRegistry.BlockRequirement> BLOCK_REQUIREMENTS = new AttachedRegistry<>(BuiltInRegistries.BLOCK);
	private static final AttachedRegistry<BlockEntityType<?>, SchematicRequirementsRegistry.BlockEntityRequirement> BLOCK_ENTITY_REQUIREMENTS = new AttachedRegistry<>(BuiltInRegistries.BLOCK_ENTITY_TYPE);
	private static final AttachedRegistry<EntityType<?>, SchematicRequirementsRegistry.EntityRequirement> ENTITY_REQUIREMENTS = new AttachedRegistry<>(BuiltInRegistries.ENTITY_TYPE);

	public static void registerForBlock(Block block, SchematicRequirementsRegistry.BlockRequirement requirement) {
		BLOCK_REQUIREMENTS.register(block, requirement);
	}

	public static void registerForBlock(ResourceLocation block, SchematicRequirementsRegistry.BlockRequirement requirement) {
		BLOCK_REQUIREMENTS.register(block, requirement);
	}

	public static void registerForBlockEntity(BlockEntityType<BlockEntity> blockEntityType, SchematicRequirementsRegistry.BlockEntityRequirement requirement) {
		BLOCK_ENTITY_REQUIREMENTS.register(blockEntityType, requirement);
	}

	public static void registerForBlockEntity(ResourceLocation blockEntityType, SchematicRequirementsRegistry.BlockEntityRequirement requirement) {
		BLOCK_ENTITY_REQUIREMENTS.register(blockEntityType, requirement);
	}

	public static void registerForEntity(EntityType<Entity> entityType, SchematicRequirementsRegistry.EntityRequirement requirement) {
		ENTITY_REQUIREMENTS.register(entityType, requirement);
	}

	// ---

	public static void registerForEntity(ResourceLocation entityType, SchematicRequirementsRegistry.EntityRequirement requirement) {
		ENTITY_REQUIREMENTS.register(entityType, requirement);
	}

	public static SchematicRequirementsRegistry.BlockRequirement getRequirementForBlock(Block block) {
		return BLOCK_REQUIREMENTS.get(block);
	}

	public static SchematicRequirementsRegistry.BlockEntityRequirement getRequirementForBlockEntityType(BlockEntityType<? extends BlockEntity> blockEntityType) {
		return BLOCK_ENTITY_REQUIREMENTS.get(blockEntityType);
	}

	public static SchematicRequirementsRegistry.EntityRequirement getRequirementForEntityType(EntityType<? extends Entity> entityType) {
		return ENTITY_REQUIREMENTS.get(entityType);
	}
}
