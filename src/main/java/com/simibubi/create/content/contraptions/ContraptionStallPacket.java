package com.simibubi.create.content.contraptions;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import com.tterrag.registrate.fabric.EnvExecutor;

import net.minecraft.network.FriendlyByteBuf;

import net.fabricmc.api.EnvType;

public class ContraptionStallPacket extends SimplePacketBase {

	int entityID;
	double x;
	double y;
	double z;
	float angle;

	public ContraptionStallPacket(int entityID, double posX, double posY, double posZ, float angle) {
		this.entityID = entityID;
		this.x = posX;
		this.y = posY;
		this.z = posZ;
		this.angle = angle;
	}

	public ContraptionStallPacket(FriendlyByteBuf buffer) {
		entityID = buffer.readInt();
		x = buffer.readDouble();
		y = buffer.readDouble();
		z = buffer.readDouble();
		angle = buffer.readFloat();
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeInt(entityID);
		writeAll(buffer, x, y, z);
		buffer.writeFloat(angle);
	}

	@Override
	public boolean handle(Context context) {
		context.enqueueWork(
			() -> EnvExecutor.runWhenOn(EnvType.CLIENT, () -> () -> AbstractContraptionEntity.handleStallPacket(this)));
		return true;
	}

	private void writeAll(FriendlyByteBuf buffer, double... doubles) {
		for (double d : doubles)
			buffer.writeDouble(d);
	}

}
