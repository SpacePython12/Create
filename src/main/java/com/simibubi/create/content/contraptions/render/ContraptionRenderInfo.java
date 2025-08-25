package com.simibubi.create.content.contraptions.render;

import org.apache.commons.lang3.tuple.Pair;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.Contraption.RenderedBlocks;
import com.simibubi.create.content.contraptions.ContraptionWorld;
import com.simibubi.create.foundation.utility.fabric.SingleRenderTypeSbbBuilder;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;

import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.createmod.catnip.client.render.model.BakedModelBufferer;
import net.createmod.catnip.render.SuperByteBuffer;
import net.createmod.catnip.render.SuperByteBufferCache;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class ContraptionRenderInfo {
	public static final SuperByteBufferCache.Compartment<Pair<Contraption, RenderType>> CONTRAPTION = new SuperByteBufferCache.Compartment<>();
	private static final ThreadLocal<ThreadLocalObjects> THREAD_LOCAL_OBJECTS = ThreadLocal.withInitial(ThreadLocalObjects::new);

	private final Contraption contraption;
	private final VirtualRenderWorld renderWorld;
	private final ContraptionMatrices matrices = new ContraptionMatrices();

	ContraptionRenderInfo(Level level, Contraption contraption) {
		this.contraption = contraption;
		this.renderWorld = setupRenderWorld(level, contraption);
	}

	public static ContraptionRenderInfo get(Contraption contraption) {
		return ContraptionRenderInfoManager.MANAGERS.get(contraption.entity.level()).getRenderInfo(contraption);
	}

	/**
	 * Reset a contraption's renderer.
	 *
	 * @param contraption The contraption to invalidate.
	 * @return true if there was a renderer associated with the given contraption.
	 */
	public static boolean invalidate(Contraption contraption) {
		return ContraptionRenderInfoManager.MANAGERS.get(contraption.entity.level()).invalidate(contraption);
	}

	public boolean isDead() {
		return !contraption.entity.isAliveOrStale();
	}

	public Contraption getContraption() {
		return contraption;
	}

	public VirtualRenderWorld getRenderWorld() {
		return renderWorld;
	}

	public ContraptionMatrices getMatrices() {
		return matrices;
	}

	public SuperByteBuffer getBuffer(RenderType renderType) {
		return SuperByteBufferCache.getInstance().get(CONTRAPTION, Pair.of(contraption, renderType), () -> buildStructureBuffer(renderType));
	}

	public void invalidate() {
		for (RenderType renderType : RenderType.chunkBufferLayers()) {
			SuperByteBufferCache.getInstance().invalidate(CONTRAPTION, Pair.of(contraption, renderType));
		}
	}

	public static VirtualRenderWorld setupRenderWorld(Level level, Contraption c) {
		ContraptionWorld contraptionWorld = c.getContraptionWorld();

		BlockPos origin = c.anchor;
		int minBuildHeight = contraptionWorld.getMinBuildHeight();
		int height = contraptionWorld.getHeight();
		VirtualRenderWorld renderWorld = new VirtualRenderWorld(level, minBuildHeight, height, origin) {
			@Override
			public boolean supportsVisualization() {
				return VisualizationManager.supportsVisualization(level);
			}
		};

		renderWorld.setBlockEntities(c.presentBlockEntities.values());
		for (StructureTemplate.StructureBlockInfo info : c.getBlocks()
			.values())
			renderWorld.setBlock(info.pos(), info.state(), 0);

		renderWorld.runLightEngine();
		return renderWorld;
	}

	private SuperByteBuffer buildStructureBuffer(RenderType layer) {
		ThreadLocalObjects objects = THREAD_LOCAL_OBJECTS.get();


		PoseStack poseStack = objects.poseStack;
		SingleRenderTypeSbbBuilder sbbBuilder = objects.sbbBuilder;

		RenderedBlocks blocks = contraption.getRenderedBlocks();
		sbbBuilder.prepare(layer);

		BakedModelBufferer.bufferBlocks(blocks.positions().iterator(), this.renderWorld, poseStack, true, sbbBuilder);

		return objects.sbbBuilder.build();
	}

	private static class ThreadLocalObjects {
		public final PoseStack poseStack = new PoseStack();
		public final SingleRenderTypeSbbBuilder sbbBuilder = new SingleRenderTypeSbbBuilder();
	}
}
