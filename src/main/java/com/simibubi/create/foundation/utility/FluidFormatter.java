package com.simibubi.create.foundation.utility;

import net.createmod.catnip.utility.Couple;
import net.createmod.catnip.utility.lang.Components;
import net.minecraft.network.chat.MutableComponent;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;

import io.github.fabricators_of_create.porting_lib.util.FluidTextUtil;
import io.github.fabricators_of_create.porting_lib.util.FluidUnit;

public class FluidFormatter {

	public static String asString(long amount, boolean shorten, FluidUnit unit) {
		Couple<MutableComponent> couple = asComponents(amount, shorten, unit);
		return couple.getFirst().getString() + " " + couple.getSecond().getString();
	}

	public static Couple<MutableComponent> asComponents(long amount, boolean shorten, FluidUnit unit) {
		if (shorten && amount >= FluidConstants.BUCKET && unit == FluidUnit.MILLIBUCKETS) {
			return Couple.create(
					Components.literal(String.format("%.1f" , amount / (double) FluidConstants.BUCKET)),
					CreateLang.translateDirect("generic.unit.buckets")
			);
		}

		String amountToDisplay = FluidTextUtil.getUnicodeMillibuckets(amount, unit, true);
		return Couple.create(
				Components.literal(amountToDisplay),
				CreateLang.translateDirect(unit.getTranslationKey())
		);
	}

}
