package com.simibubi.create.foundation.mixin.fabric;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.simibubi.create.foundation.utility.AttachedRegistry;

import net.minecraft.core.registries.BuiltInRegistries;

@Mixin(BuiltInRegistries.class)
public class RegistryMixin {
	@Inject(method = "freeze", at = @At("TAIL"))
	private static void unwrapAttached(CallbackInfo ci) {
		AttachedRegistry.unwrapAll();
	}
}
