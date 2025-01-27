package com.simibubi.create.content.contraptions.minecart.capability;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import com.tterrag.registrate.fabric.EnvExecutor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class MinecartControllerUpdatePacket extends SimplePacketBase {

	int entityID;
	CompoundTag nbt;

	public MinecartControllerUpdatePacket(MinecartController controller) {
		entityID = controller.cart()
			.getId();
		nbt = controller.serializeNBT();
	}

	public MinecartControllerUpdatePacket(FriendlyByteBuf buffer) {
		entityID = buffer.readInt();
		nbt = buffer.readNbt();
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
 		buffer.writeInt(entityID);
		buffer.writeNbt(nbt);
	}

	@Override
	public boolean handle(Context context) {
		context.enqueueWork(() -> EnvExecutor.runWhenOn(EnvType.CLIENT, () -> this::handleCL));
		return true;
	}

	@Environment(EnvType.CLIENT)
	private void handleCL() {
		ClientLevel world = Minecraft.getInstance().level;
		if (world == null)
			return;
		Entity entityByID = world.getEntity(entityID);
		if (entityByID == null)
			return;
		((AbstractMinecart) entityByID).create$getController().deserializeNBT(nbt);
	}

}
