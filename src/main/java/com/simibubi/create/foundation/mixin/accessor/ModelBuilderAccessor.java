package com.simibubi.create.foundation.mixin.accessor;

import io.github.fabricators_of_create.porting_lib.models.generators.ModelBuilder;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ModelBuilder.class)
public interface ModelBuilderAccessor {
	@Accessor(value = "textures", remap = false)
	Map<String, String> create$getTextures();
}
