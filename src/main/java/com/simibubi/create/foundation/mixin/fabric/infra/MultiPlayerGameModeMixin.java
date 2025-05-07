package com.simibubi.create.foundation.mixin.fabric.infra;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.infrastructure.fabric.ItemExtras;
import com.simibubi.create.infrastructure.fabric.SecondaryUseBypassingBlock;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import net.fabricmc.fabric.api.util.TriState;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
	@ModifyExpressionValue(
		method = "performUseItemOn",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/player/LocalPlayer;isSecondaryUseActive()Z"
		)
	)
	private boolean maybeBypassSecondaryUse(boolean original, LocalPlayer player, InteractionHand hand, BlockHitResult hit) {
		if (!original)
			return false;

		BlockState state = player.level().getBlockState(hit.getBlockPos());
		if (state.getBlock() instanceof SecondaryUseBypassingBlock block && block.shouldBypassSecondaryUse(player, hand, state)) {
			return false;
		}

		return true;
	}

	@WrapOperation(
		method = "performUseItemOn",
		at = {
			@At(
				value = "INVOKE",
				target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z",
				ordinal = 0
			),
			@At(
				value = "INVOKE",
				target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z",
				ordinal = 1
			)
		}
	)
	private boolean customHasSecondaryUse(ItemStack stack, Operation<Boolean> original,
										  @Local(argsOnly = true) LocalPlayer player, @Local BlockPos pos) {
		if (stack.getItem() instanceof ItemExtras ex) {
			TriState result = ex.hasSecondaryUse(stack, player.clientLevel, pos, player);
			if (result != TriState.DEFAULT) {
				// negate since the result of the target is negated
				return !result.get();
			}
		}

		return original.call(stack);
	}
}
