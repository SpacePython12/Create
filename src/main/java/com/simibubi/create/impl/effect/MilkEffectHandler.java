package com.simibubi.create.impl.effect;

import java.util.List;

import com.simibubi.create.api.effect.OpenPipeEffectHandler;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;

public class MilkEffectHandler implements OpenPipeEffectHandler {
	@Override
	public void apply(Level level, AABB area, FluidStack fluid) {
		if (level.getGameTime() % 5 != 0)
			return;

		List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area, LivingEntity::isAffectedByPotions);
		for (LivingEntity entity : entities) {
			entity.removeAllEffects();
		}
	}
}
