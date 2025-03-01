package com.simibubi.create.content.logistics.factoryBoard;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBlockItem;
import com.simibubi.create.foundation.utility.CreateLang;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;

public class FactoryPanelBlockItem extends LogisticallyLinkedBlockItem {

	public FactoryPanelBlockItem(Block pBlock, Properties pProperties) {
		super(pBlock, pProperties);
	}

	@Override
	public InteractionResult place(BlockPlaceContext pContext) {
		ItemStack stack = pContext.getItemInHand();

		if (!isTuned(stack)) {
			AllSoundEvents.DENY.playOnServer(pContext.getLevel(), pContext.getClickedPos());
			pContext.getPlayer()
				.displayClientMessage(CreateLang.translate("factory_panel.tune_before_placing")
					.component(), true);
			return InteractionResult.FAIL;
		}

		return super.place(pContext);
	}

}
