package com.simibubi.create.content.fluids;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.content.fluids.FluidTransportBehaviour.AttachmentTypes;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.createmod.catnip.utility.Iterate;
import net.minecraft.world.level.block.entity.BlockEntity;

import net.fabricmc.fabric.api.blockview.v2.RenderDataBlockEntity;

/**
 * Fabric: Implement this on any BlockEntity that uses {@link PipeAttachmentModel} and call {@link #getAttachments(BlockEntity)}
 */
public interface PipeAttachmentBlockEntity extends RenderDataBlockEntity {
	@Nullable
	static AttachmentTypes[] getAttachments(BlockEntity be) {
		FluidTransportBehaviour behavior = BlockEntityBehaviour.get(be, FluidTransportBehaviour.TYPE);
		if (behavior == null)
			return null;
		AttachmentTypes[] attachments = new AttachmentTypes[6];
		for (int i = 0; i < Iterate.directions.length; i++) {
			attachments[i] = behavior.getRenderedRimAttachment(
					be.getLevel(), be.getBlockPos(), be.getBlockState(), Iterate.directions[i]
			);
		}
		return attachments;
	}
}
