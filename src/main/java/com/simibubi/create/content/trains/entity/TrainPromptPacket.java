package com.simibubi.create.content.trains.entity;

import com.simibubi.create.content.trains.TrainHUD;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import com.tterrag.registrate.fabric.EnvExecutor;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class TrainPromptPacket extends SimplePacketBase {

	private Component text;
	private boolean shadow;

	public TrainPromptPacket(Component text, boolean shadow) {
		this.text = text;
		this.shadow = shadow;
	}

	public TrainPromptPacket(FriendlyByteBuf buffer) {
		text = buffer.readComponent();
		shadow = buffer.readBoolean();
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeComponent(text);
		buffer.writeBoolean(shadow);
	}

	@Override
	public boolean handle(Context context) {
		context.enqueueWork(() -> EnvExecutor.runWhenOn(EnvType.CLIENT, () -> this::apply));
		return true;
	}

	@Environment(EnvType.CLIENT)
	public void apply() {
		TrainHUD.currentPrompt = text;
		TrainHUD.currentPromptShadow = shadow;
		TrainHUD.promptKeepAlive = 30;
	}

}
