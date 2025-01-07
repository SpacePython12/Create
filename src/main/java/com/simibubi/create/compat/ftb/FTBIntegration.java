package com.simibubi.create.compat.ftb;

import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;

import dev.ftb.mods.ftblibrary.FTBLibraryClient;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.minecraft.client.gui.screens.Screen;

public class FTBIntegration {

	private static int buttonStatePreviously = 0;

	public static void init(IEventBus modEventBus, IEventBus forgeEventBus) {
		forgeEventBus.addListener(EventPriority.HIGH, FTBIntegration::removeGUIClutterOpen);
		forgeEventBus.addListener(EventPriority.LOW, FTBIntegration::removeGUIClutterClose);
	}

	private static void removeGUIClutterOpen(ScreenEvent.Opening event) {
		if (isCreate(event.getCurrentScreen()))
			return;
		if (!isCreate(event.getNewScreen()))
			return;
		buttonStatePreviously = FTBLibraryClient.showButtons;
		FTBLibraryClient.showButtons = 0;
	}

	private static void removeGUIClutterClose(ScreenEvent.Closing event) {
		if (!isCreate(event.getScreen()))
			return;
		FTBLibraryClient.showButtons = buttonStatePreviously;
	}

	private static boolean isCreate(Screen screen) {
		return screen instanceof AbstractSimiContainerScreen<?> || screen instanceof AbstractSimiScreen;
	}

}
