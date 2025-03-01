package com.simibubi.create.foundation.data.recipe;

import static com.simibubi.create.foundation.data.recipe.Mods.ALG;
import static com.simibubi.create.foundation.data.recipe.Mods.BWT;
import static com.simibubi.create.foundation.data.recipe.Mods.GS;
import static com.simibubi.create.foundation.data.recipe.Mods.IC2;
import static com.simibubi.create.foundation.data.recipe.Mods.IE;
import static com.simibubi.create.foundation.data.recipe.Mods.IF;
import static com.simibubi.create.foundation.data.recipe.Mods.MEK;
import static com.simibubi.create.foundation.data.recipe.Mods.MI;
import static com.simibubi.create.foundation.data.recipe.Mods.MTM;
import static com.simibubi.create.foundation.data.recipe.Mods.OREGANIZED;
import static com.simibubi.create.foundation.data.recipe.Mods.TH;
import static com.simibubi.create.foundation.data.recipe.Mods.TR;

import net.createmod.catnip.lang.Lang;

public enum CompatMetals {
	ALUMINUM(IE, IC2),
	LEAD(MEK, TH, IE, OREGANIZED, TR, MI),
	NICKEL(TH, IE, MI, ALG),
	OSMIUM(MEK, MTM),
	PLATINUM(MTM, MI),
	QUICKSILVER(),
	SILVER(TH, IE, IC2, OREGANIZED, GS, IF, TR, MI, MTM, BWT),
	TIN(TH, MEK, IC2, TR, MI, ALG, MTM),
	URANIUM(MEK, IE, IC2, MI);

	private final Mods[] mods;
	private final String name;

	CompatMetals(Mods... mods) {
		this.name = Lang.asId(name());
		this.mods = mods;
	}

	public String getName() {
		return name;
	}

	public String getName(Mods mod) {
		if (this == ALUMINUM && mod == IC2) // include in mods.builder if this happens again
			return "aluminium";
		return name;
	}

	/**
	 * These mods must provide an ingot and nugget variant of the corresponding metal.
	 */
	public Mods[] getMods() {
		return mods;
	}
}
