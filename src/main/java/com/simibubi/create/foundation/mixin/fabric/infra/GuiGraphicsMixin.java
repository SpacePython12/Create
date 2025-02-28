package com.simibubi.create.foundation.mixin.fabric.infra;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.simibubi.create.infrastructure.fabric.RenderItemDecorationsCallback;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {
	@Inject(
		method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V"
		)
	)
	private void renderAdditionalDecorations(Font font, ItemStack stack, int x, int y, String text, CallbackInfo ci) {
		RenderItemDecorationsCallback.EVENT.invoker().render((GuiGraphics) (Object) this, font, stack, x, y);
	}
}
