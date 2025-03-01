package com.simibubi.create.content.logistics.packager.fabric;

import java.util.Set;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.api.registry.SimpleRegistry;

import net.createmod.catnip.math.BlockFace;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Used to uniquely identify a single inventory in the world, irrelevant of how many blocks it takes up.
 */
@FunctionalInterface
public interface InventoryIdentifier {
	SimpleRegistry<Block, ByBlockFinder> BY_BLOCK = SimpleRegistry.create();
	SimpleRegistry<BlockEntityType<?>, ByBlockEntityFinder> BY_BLOCK_ENTITY = SimpleRegistry.create();

	boolean contains(BlockFace face);

	static InventoryIdentifier get(Level level, BlockPos pos, Direction side) {
		BlockState state = level.getBlockState(pos);
		ByBlockFinder byBlock = BY_BLOCK.get(state);
		if (byBlock != null) {
			InventoryIdentifier found = byBlock.find(level, pos, state, side);
			if (found != null) {
				return found;
			}
		}

		BlockEntity be = level.getBlockEntity(pos);
		if (be != null) {
			ByBlockEntityFinder finder = BY_BLOCK_ENTITY.get(be.getType());
			if (finder != null) {
				InventoryIdentifier found = finder.find(level, pos, state, side, be);
				if (found != null) {
					return found;
				}
			}
		}

		return new SingleBlock(pos);
	}

	@FunctionalInterface
	interface ByBlockFinder {
		@Nullable
		InventoryIdentifier find(Level level, BlockPos pos, BlockState state, Direction side);
	}

	@FunctionalInterface
	interface ByBlockEntityFinder {
		@Nullable
		InventoryIdentifier find(Level level, BlockPos pos, BlockState state, Direction side, BlockEntity be);
	}

	record SingleBlock(BlockPos pos) implements InventoryIdentifier {
		@Override
		public boolean contains(BlockFace face) {
			return face.getPos().equals(this.pos);
		}
	}

	record SingleFace(BlockFace face) implements InventoryIdentifier {
		@Override
		public boolean contains(BlockFace face) {
			return face.equals(this.face);
		}
	}

	record MultiBlock(Set<BlockPos> positions) implements InventoryIdentifier {
		@Override
		public boolean contains(BlockFace face) {
			return this.positions.contains(face.getPos());
		}
	}
}
