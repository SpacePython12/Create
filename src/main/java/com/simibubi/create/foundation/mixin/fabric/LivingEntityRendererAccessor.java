package com.simibubi.create.foundation.mixin.fabric;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;

@Mixin(LivingEntityRenderer.class)
public interface LivingEntityRendererAccessor {
	@Invoker("addLayer")
	<T extends LivingEntity, M extends EntityModel<T>> boolean create$addLayer(RenderLayer<T, M> layer);
}
