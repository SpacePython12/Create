package com.simibubi.create.compat.sodium;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.simibubi.create.Create;
import com.simibubi.create.compat.Mods;

import me.jellysquid.mods.sodium.client.render.texture.SpriteUtil;
import net.createmod.catnip.utility.lang.Components;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

/**
 * Fixes the Mechanical Saw's sprite and lets players know when Indium isn't installed.
 */
public class SodiumCompat {
	private static final MethodHandles.Lookup lookup = MethodHandles.lookup();

	public static final ResourceLocation SAW_TEXTURE = Create.asResource("block/saw_reversed");

	public static void init() {
		if (!Mods.INDIUM.isLoaded()) {
			ClientPlayConnectionEvents.JOIN.register(SodiumCompat::sendNoIndiumWarning);
		}

		boolean compatInitialized = false;
		for (SpriteUtilCompat value : SpriteUtilCompat.values()) {
			if (value.doesWork.get()) {
				Minecraft mc = Minecraft.getInstance();
				WorldRenderEvents.START.register(ctx -> {
					Function<ResourceLocation, TextureAtlasSprite> atlas = mc.getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
					TextureAtlasSprite sawSprite = atlas.apply(SAW_TEXTURE);
					value.markSpriteAsActive.accept(sawSprite);
				});
				compatInitialized = true;
				break;
			}
		}
		if (!compatInitialized) {
			Create.LOGGER.error("Create's Sodium compat errored and has been partially disabled. Report this!");
		}
	}

	public static void sendNoIndiumWarning(ClientPacketListener handler, PacketSender sender, Minecraft mc) {
		if (mc.player == null)
			return;

		MutableComponent text = ComponentUtils.wrapInSquareBrackets(Components.literal("WARN"))
				.withStyle(ChatFormatting.GOLD)
				.append(Components.literal(" Sodium is installed, but Indium is not. This will cause visual issues with Create!")
				)
				.withStyle(style -> style
						.withClickEvent(
								new ClickEvent(ClickEvent.Action.OPEN_URL, "https://modrinth.com/mod/indium")
						)
						.withHoverEvent(
								new HoverEvent(HoverEvent.Action.SHOW_TEXT, Components.literal("Click here to open Indium's mod page"))
						)
				);

		mc.player.displayClientMessage(text, false);
	}

	private enum SpriteUtilCompat {
		V0_5(() -> {
			try {
				return checkMarkSpriteActiveSignature(SpriteUtil.class);
			} catch (Throwable t) {
				return false;
			}
		}, (sawSprite) -> {
			try {
				SpriteUtil.markSpriteActive(sawSprite);
			} catch (Throwable ignored) {}
		}),
		V0_6(() -> {
			try {
				return checkMarkSpriteActiveSignature(Class.forName("net.caffeinemc.mods.sodium.client.render.texture.SpriteUtil"));
			} catch (Throwable t) {
				return false;
			}
		}, (sawSprite) -> {
			try {
				invokeMarkSpriteActive(Class.forName("net.caffeinemc.mods.sodium.client.render.texture.SpriteUtil"), sawSprite);
			} catch (Throwable ignored) {}
		}),
		V0_6_API(() -> {
			try {
				Field field = Class.forName("net.caffeinemc.mods.sodium.api.texture.SpriteUtil")
						.getDeclaredField("INSTANCE");
				return checkMarkSpriteActiveSignature((Class<?>) field.get(null));
			} catch (Throwable t) {
				return false;
			}
		}, (sawSprite) -> {
			try {
				Field field = Class.forName("net.caffeinemc.mods.sodium.api.texture.SpriteUtil")
						.getDeclaredField("INSTANCE");

				invokeMarkSpriteActive((Class<?>) field.get(null), sawSprite);
			} catch (Throwable ignored) {}
		});

		private final Supplier<Boolean> doesWork;
		private final Consumer<TextureAtlasSprite> markSpriteAsActive;

		SpriteUtilCompat(Supplier<Boolean> doesWork, Consumer<TextureAtlasSprite> markSpriteAsActive) {
			this.doesWork = doesWork;
			this.markSpriteAsActive = markSpriteAsActive;
		}

		private static boolean checkMarkSpriteActiveSignature(Class<?> clazz) throws Throwable {
			Method method = clazz.getMethod("markSpriteActive", TextureAtlasSprite.class);
			if (method.getReturnType() != Void.TYPE)
				throw new IllegalStateException("markSpriteActive's signature has changed");
			return true;
		}

		private static void invokeMarkSpriteActive(Class<?> clazz, TextureAtlasSprite sawSprite) throws Throwable {
			MethodType methodType = MethodType.methodType(Void.TYPE);
			MethodHandle handle = lookup.findVirtual(clazz, "markSpriteActive", methodType);

			handle.invoke(sawSprite);
		}
	}
}
