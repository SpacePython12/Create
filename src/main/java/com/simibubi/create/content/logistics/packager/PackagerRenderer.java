package com.simibubi.create.content.logistics.packager;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.createmod.catnip.utility.math.AngleHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class PackagerRenderer extends SmartBlockEntityRenderer<PackagerBlockEntity> {

	public PackagerRenderer(Context context) {
		super(context);
	}

	@Override
	protected void renderSafe(PackagerBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
		int light, int overlay) {
		super.renderSafe(be, partialTicks, ms, buffer, light, overlay);

		ItemStack renderedBox = be.getRenderedBox();
		float trayOffset = be.getTrayOffset(partialTicks);
		boolean hatchOpen = be.animationTicks > (be.animationInward ? 1 : 5)
			&& be.animationTicks < PackagerBlockEntity.CYCLE - (be.animationInward ? 5 : 1);
		BlockState blockState = be.getBlockState();
		Direction facing = blockState.getValue(PackagerBlock.FACING)
			.getOpposite();

		PartialModel hatchModel =
			hatchOpen ? AllPartialModels.PACKAGER_HATCH_OPEN : AllPartialModels.PACKAGER_HATCH_CLOSED;

		SuperByteBuffer sbb = CachedBuffers.partial(hatchModel, blockState);
		sbb.translate(Vec3.atLowerCornerOf(facing.getNormal())
			.scale(.49999f))
			.rotateCentered(AngleHelper.rad(AngleHelper.horizontalAngle(facing)), Direction.UP)
			.rotateCentered(AngleHelper.rad(AngleHelper.verticalAngle(facing)), Direction.EAST)
			.light(light)
			.renderInto(ms, buffer.getBuffer(RenderType.solid()));

		ms.pushPose();
		var msr = TransformStack.of(ms);
		msr.translate(Vec3.atLowerCornerOf(facing.getNormal())
			.scale(trayOffset));

		sbb = CachedBuffers.partial(AllBlocks.PACKAGER.has(blockState) ? AllPartialModels.PACKAGER_TRAY_REGULAR
			: AllPartialModels.PACKAGER_TRAY_DEFRAG, blockState);
		sbb.rotateCentered(AngleHelper.rad(facing.toYRot()), Direction.UP)
			.light(light)
			.renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()));

		if (!renderedBox.isEmpty()) {
			msr.translate(.5f, .5f, .5f)
				.rotateYDegrees(facing.toYRot())
				.translate(0, 2 / 16f, 0)
				.scale(1.49f, 1.49f, 1.49f);
			Minecraft.getInstance()
				.getItemRenderer()
				.renderStatic(null, renderedBox, ItemDisplayContext.FIXED, false, ms, buffer, be.getLevel(), light,
					overlay, 0);
		}

		ms.popPose();
	}

}
