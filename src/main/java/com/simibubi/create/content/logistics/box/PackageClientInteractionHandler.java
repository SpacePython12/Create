package com.simibubi.create.content.logistics.box;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class PackageClientInteractionHandler {

	// In vanilla, punching an entity doesnt reset the attack timer. This leads to
	// accidentally breaking blocks behind an armorstand or package when punching it
	// in creative mode

	@SubscribeEvent
	@Environment(EnvType.CLIENT)
	public static void onPlayerPunchPackage(AttackEntityEvent event) {
		Player attacker = event.getEntity();
		if (!attacker.level()
			.isClientSide())
			return;
		Minecraft mc = Minecraft.getInstance();
		if (attacker != mc.player)
			return;
		if (!(event.getTarget() instanceof PackageEntity))
			return;
		ObfuscationReflectionHelper.setPrivateValue(Minecraft.class, mc, 10, "f_91078_");
	}

}
