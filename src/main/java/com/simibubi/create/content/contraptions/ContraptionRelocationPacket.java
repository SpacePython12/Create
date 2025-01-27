package com.simibubi.create.content.contraptions;

import com.simibubi.create.foundation.networking.SimplePacketBase;

import net.minecraft.network.FriendlyByteBuf;

import net.fabricmc.api.EnvType;

import io.github.fabricators_of_create.porting_lib.util.EnvExecutor;

public class ContraptionRelocationPacket extends SimplePacketBase {

	int entityID;

	public ContraptionRelocationPacket(int entityID) {
		this.entityID = entityID;
	}

	public ContraptionRelocationPacket(FriendlyByteBuf buffer) {
		entityID = buffer.readInt();
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeInt(entityID);
	}

	@Override
	public boolean handle(Context context) {
		context.enqueueWork(() -> EnvExecutor.runWhenOn(EnvType.CLIENT,
			() -> () -> OrientedContraptionEntity.handleRelocationPacket(this)));
		return true;
	}

}
