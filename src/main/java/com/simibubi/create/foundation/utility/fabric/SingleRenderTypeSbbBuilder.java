package com.simibubi.create.foundation.utility.fabric;

import com.mojang.blaze3d.vertex.BufferBuilder;

import net.createmod.catnip.client.render.model.ShadeSeparatedResultConsumer;
import net.createmod.catnip.render.SuperByteBufferBuilder;
import net.minecraft.client.renderer.RenderType;

/**
 * Originally from Ponder, <a href="https://github.com/Creators-of-Create/Ponder/blob/056046594f5e50a1689752315c3d2582e3ae460c/Common/src/main/java/net/createmod/ponder/foundation/element/WorldSectionElementImpl.java#L454">here</a>
 */
public class SingleRenderTypeSbbBuilder extends SuperByteBufferBuilder implements ShadeSeparatedResultConsumer {
	private RenderType renderType;

	public void prepare(RenderType renderType) {
		prepare();
		this.renderType = renderType;
	}

	@Override
	public void accept(RenderType renderType, boolean shaded, BufferBuilder.RenderedBuffer data) {
		if (renderType != this.renderType) {
			return;
		}

		add(data, shaded);
	}
}
