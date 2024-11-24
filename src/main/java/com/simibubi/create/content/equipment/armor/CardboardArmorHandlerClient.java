package com.simibubi.create.content.equipment.armor;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.logistics.box.PackageRenderer;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.utility.AnimationTickHolder;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class CardboardArmorHandlerClient {

	private static final Cache<UUID, Integer> BOXES_PLAYERS_ARE_HIDING_AS = CacheBuilder.newBuilder()
		.expireAfterAccess(1, TimeUnit.SECONDS)
		.build();

	@SubscribeEvent
	public static void keepCacheAliveDesignDespiteNotRendering(PlayerTickEvent event) {
		if (event.phase == Phase.START)
			return;
		Player player = event.player;
		if (!CardboardArmorHandler.testForStealth(player))
			return;
		try {
			getCurrentBoxIndex(player);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void playerRendersAsBoxWhenSneaking(RenderPlayerEvent.Pre event) {
		Player player = event.getEntity();
		if (!CardboardArmorHandler.testForStealth(player))
			return;

		event.setCanceled(true);

		if (player == Minecraft.getInstance().player
			&& Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON)
			return;

		PoseStack ms = event.getPoseStack();
		ms.pushPose();
		ms.translate(0, 2 / 16f, 0);

		float movement = (float) player.position()
			.subtract(player.xo, player.yo, player.zo)
			.length();

		if (player.onGround())
			ms.translate(0,
				Math.min(Math.abs(Mth.cos((AnimationTickHolder.getRenderTime() % 256) / 2.0f)) * 2 / 16f, movement * 5),
				0);

		float interpolatedYaw = Mth.lerp(event.getPartialTick(), player.yRotO, player.getYRot());

		try {
			PartialModel model = AllPartialModels.PACKAGES_AS_LIST.get(getCurrentBoxIndex(player));
			PackageRenderer.renderBox(player, -interpolatedYaw + -90, ms, event.getMultiBufferSource(),
				event.getPackedLight(), model);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		ms.popPose();
	}

	private static Integer getCurrentBoxIndex(Player player) throws ExecutionException {
		return BOXES_PLAYERS_ARE_HIDING_AS.get(player.getUUID(),
			() -> player.level().random.nextInt(AllPartialModels.PACKAGES_AS_LIST.size()));
	}

}
