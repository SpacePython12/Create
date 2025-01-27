package com.simibubi.create.api.behaviour;

import com.simibubi.create.Create;
import com.simibubi.create.compat.botania.ApothecaryFilling;
import com.simibubi.create.compat.tconstruct.SpoutCasting;
import com.simibubi.create.content.fluids.spout.SpoutBlockEntity;
import com.simibubi.create.impl.behaviour.BlockSpoutingBehaviourImpl;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;

public abstract class BlockSpoutingBehaviour {
	/**
	 * Register a new custom spout interaction
	 *
	 * @param resourceLocation  The interaction id
	 * @param spoutingBehaviour An instance of your behaviour class
	 */
	public static void addCustomSpoutInteraction(ResourceLocation resourceLocation, BlockSpoutingBehaviour spoutingBehaviour) {
		BlockSpoutingBehaviourImpl.addCustomSpoutInteraction(resourceLocation, spoutingBehaviour);
	}

	public static void registerDefaults() {
		addCustomSpoutInteraction(Create.asResource("ticon_casting"), new SpoutCasting());
		addCustomSpoutInteraction(Create.asResource("botania_apothecary_filling"), new ApothecaryFilling());
	}

	/**
	 * While idle, Spouts will call this every tick with simulate == true <br>
	 * When fillBlock returns &gt; 0, the Spout will start its animation cycle <br>
	 * <br>
	 * During this animation cycle, fillBlock is called once again with simulate == false but only on the relevant SpoutingBehaviour <br>
	 * When fillBlock returns &gt; 0 once again, the Spout will drain its content by the returned amount of units <br>
	 * Perform any other side effects in this method <br>
	 * This method is called server-side only (except in ponder) <br>
	 *
	 * @param level          The current level
	 * @param pos            The position of the affected block
	 * @param spout          The spout block entity that is calling this
	 * @param availableFluid A copy of the fluidStack that is available, modifying this will do nothing, return the amount to be subtracted instead
	 * @param simulate       Whether the spout is testing or actually performing this behaviour
	 * @return The amount filled into the block, 0 to idle/cancel
	 */
	public abstract long fillBlock(Level level, BlockPos pos, SpoutBlockEntity spout, FluidStack availableFluid, boolean simulate);

}
