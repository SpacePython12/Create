package com.simibubi.create.infrastructure.fabric;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;

import net.fabricmc.fabric.api.util.TriState;

/**
 * New methods implementable by Item classes to change their behavior.
 * @implNote This class is not called ItemExtensions since Extensions usually means an injected interface, which this is not.
 */
public interface ItemExtras {
	/**
	 * Determine if this item has a secondary use or not.
	 * <p>
	 * By default, all items do, and therefore sneak-using a block will always use the item, not the block.
	 * <p>
	 * Override this method to make that behavior conditional.
	 * <p>
	 * This method is a parallel to Forge's {@code doesSneakBypassUse}.
	 * @param pos the clicked block
	 * @param player the player holding the item
	 * @return {@link TriState#FALSE} to avoid triggering the secondary use, allowing sneak-clicks to hit the block
	 */
	default TriState hasSecondaryUse(ItemStack stack, LevelReader level, BlockPos pos, Player player) {
		return TriState.DEFAULT;
	}
}
