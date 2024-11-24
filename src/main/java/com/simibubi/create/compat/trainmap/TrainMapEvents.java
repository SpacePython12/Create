package com.simibubi.create.compat.trainmap;

import com.mojang.blaze3d.platform.InputConstants;
import com.simibubi.create.compat.Mods;

import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.client.Minecraft;

public class TrainMapEvents {

	@SubscribeEvent
	public static void tick(ClientTickEvent event) {
		if (event.phase == Phase.START)
			return;
		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null)
			return;

		if (Mods.FTBCHUNKS.isLoaded())
			FTBChunksTrainMap.tick();
		if (Mods.JOURNEYMAP.isLoaded())
			JourneyTrainMap.tick();
	}

	@SubscribeEvent
	public static void mouseClick(InputEvent.MouseButton.Pre event) {
		if (event.getAction() != InputConstants.PRESS)
			return;

		if (Mods.FTBCHUNKS.isLoaded())
			FTBChunksTrainMap.mouseClick(event);
		if (Mods.JOURNEYMAP.isLoaded())
			JourneyTrainMap.mouseClick(event);
	}

	@SubscribeEvent
	public static void cancelTooltips(RenderTooltipEvent.Pre event) {
		if (Mods.FTBCHUNKS.isLoaded())
			FTBChunksTrainMap.cancelTooltips(event);
	}

	@SubscribeEvent
	public static void renderGui(ScreenEvent.Render.Post event) {
		if (Mods.FTBCHUNKS.isLoaded())
			FTBChunksTrainMap.renderGui(event);
	}

}
