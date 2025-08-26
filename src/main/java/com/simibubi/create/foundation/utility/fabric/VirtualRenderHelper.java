package com.simibubi.create.foundation.utility.fabric;

import dev.engine_room.flywheel.lib.model.baked.VirtualBlockGetter;
import net.minecraft.world.level.BlockGetter;

/**
 * Fabric parallel to the forge-specific class of the same name provided by Ponder.
 */
public final class VirtualRenderHelper {
	private VirtualRenderHelper() {
	}

	public static boolean isVirtual(BlockGetter level) {
		return level instanceof VirtualBlockGetter;
	}
}
