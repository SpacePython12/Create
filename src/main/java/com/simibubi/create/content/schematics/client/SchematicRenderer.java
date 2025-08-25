package com.simibubi.create.content.schematics.client;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.Iterators;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.render.BlockEntityRenderHelper;
import com.simibubi.create.foundation.utility.fabric.SingleRenderTypeSbbBuilder;

import net.createmod.catnip.client.render.model.BakedModelBufferer;
import net.createmod.catnip.levelWrappers.SchematicLevel;
import net.createmod.catnip.render.SuperByteBuffer;
import net.createmod.catnip.render.SuperRenderTypeBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class SchematicRenderer {

	private static final ThreadLocal<ThreadLocalObjects> THREAD_LOCAL_OBJECTS = ThreadLocal.withInitial(ThreadLocalObjects::new);

	private final Map<RenderType, SuperByteBuffer> bufferCache = new LinkedHashMap<>();
	private boolean active;
	private boolean changed;
	protected SchematicLevel schematic;
	private BlockPos anchor;

	public SchematicRenderer() {
		changed = false;
	}

	public void display(SchematicLevel world) {
		this.anchor = world.anchor;
		this.schematic = world;
		this.active = true;
		this.changed = true;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void update() {
		changed = true;
	}

	public void render(PoseStack ms, SuperRenderTypeBuffer buffers) {
		if (!active)
			return;

		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null || mc.player == null)
			return;
		if (changed)
			redraw();
		changed = false;

		bufferCache.forEach((layer, buffer) -> {
			buffer.renderInto(ms, buffers.getBuffer(layer));
		});
		BlockEntityRenderHelper.renderBlockEntities(schematic, schematic.getRenderedBlockEntities(), ms, buffers);
	}

	protected void redraw() {
		bufferCache.clear();

		for (RenderType layer : RenderType.chunkBufferLayers()) {
			SuperByteBuffer buffer = drawLayer(layer);
			if (!buffer.isEmpty())
				bufferCache.put(layer, buffer);
		}
	}

	protected SuperByteBuffer drawLayer(RenderType layer) {
		ThreadLocalObjects objects = THREAD_LOCAL_OBJECTS.get();

		SingleRenderTypeSbbBuilder sbbBuilder = objects.sbbBuilder;
		PoseStack poseStack = objects.poseStack;
		MutableBlockPos reusedPos = objects.mutableBlockPos;
		BoundingBox bounds = schematic.getBounds();

		sbbBuilder.prepare(layer);
		schematic.renderMode = true;

		Iterator<BlockPos> positions = Iterators.transform(
			BlockPos.betweenClosedStream(bounds).iterator(),
			pos -> reusedPos.setWithOffset(Objects.requireNonNull(pos), this.anchor)
		);

		BakedModelBufferer.bufferBlocks(positions, schematic, poseStack, true, sbbBuilder);

		schematic.renderMode = false;

		return sbbBuilder.build();
	}

	// fabric: calling chunkBufferLayers early causes issues (#612), let the map handle its size on its own
//	private static int getLayerCount() {
//		return RenderType.chunkBufferLayers()
//			.size();
//	}

	private static class ThreadLocalObjects {
		public final PoseStack poseStack = new PoseStack();
		public final BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
		public final SingleRenderTypeSbbBuilder sbbBuilder = new SingleRenderTypeSbbBuilder();
	}

}
