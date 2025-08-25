package com.simibubi.create.compat.computercraft;

import static com.simibubi.create.compat.computercraft.implementation.ComputerBehaviour.peripheralProvider;

import java.util.function.Function;

import com.simibubi.create.compat.Mods;
import com.simibubi.create.compat.computercraft.implementation.ComputerBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;

import dan200.computercraft.api.peripheral.PeripheralLookup;

public class ComputerCraftProxy {

	public static void register() {
		fallbackFactory = FallbackComputerBehaviour::new;
		Mods.COMPUTERCRAFT.executeIfInstalled(() -> ComputerCraftProxy::registerWithDependency);
	}

	private static void registerWithDependency() {
		/* Comment if computercraft.implementation is not in the source set */
		 computerFactory = ComputerBehaviour::new;
     ComputerBehaviour.registerItemDetailProviders();

		// fabric: register the provider
		PeripheralLookup.get().registerFallback((level, pos, state, be, face) -> peripheralProvider(level, pos));
	}

	private static Function<SmartBlockEntity, ? extends AbstractComputerBehaviour> fallbackFactory;
	private static Function<SmartBlockEntity, ? extends AbstractComputerBehaviour> computerFactory;

	public static AbstractComputerBehaviour behaviour(SmartBlockEntity sbe) {
		if (computerFactory == null)
			return fallbackFactory.apply(sbe);
		return computerFactory.apply(sbe);
	}
}
