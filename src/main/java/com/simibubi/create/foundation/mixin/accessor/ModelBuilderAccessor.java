package com.simibubi.create.foundation.mixin.accessor;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import io.github.fabricators_of_create.porting_lib.models.generators.ModelBuilder;

@Mixin(ModelBuilder.class)
public interface ModelBuilderAccessor {
	@Accessor(value = "textures", remap = false)
	Map<String, String> create$getTextures();
}
