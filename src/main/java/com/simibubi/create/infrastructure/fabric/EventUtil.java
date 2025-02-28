package com.simibubi.create.infrastructure.fabric;

import java.util.function.Function;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class EventUtil {
	@SuppressWarnings("unchecked")
	static <T> Event<T> create(Function<T[], T> invoker) {
		T probe = invoker.apply((T[]) new Object[0]);
		Class<?>[] interfaces = probe.getClass().getInterfaces();
		if (interfaces.length != 1) {
			throw new IllegalArgumentException("Cannot create event from probe " + probe);
		}
		Class<T> clazz = (Class<T>) interfaces[0];
		return EventFactory.createArrayBacked(clazz, invoker);
	}
}
