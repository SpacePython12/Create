package com.simibubi.create.foundation.config.ui.compat.flywheel;

import java.util.Locale;

import org.jetbrains.annotations.NotNull;

import com.simibubi.create.foundation.gui.AllIcons;

import dev.engine_room.flywheel.impl.FabricFlwConfig;
import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.createmod.catnip.config.ui.ConfigModListScreen;
import net.createmod.catnip.config.ui.ConfigScreen;
import net.createmod.catnip.gui.UIRenderHelper;
import net.createmod.catnip.gui.element.TextStencilElement;
import net.createmod.catnip.gui.widget.BoxWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.config.ModConfig;

public class FlwConfigScreen extends BaseConfigScreen {

	private final FabricFlwConfig flwConfig = FabricFlwConfig.INSTANCE;

	public FlwConfigScreen(Screen parent, @NotNull String modID) {
		super(parent, modID);
	}

	@Override
	protected void init() {
		guiLeft = (width - windowWidth) / 2;
		guiTop = (height - windowHeight) / 2;
		guiLeft += windowXOffset;
		guiTop += windowYOffset;
		returnOnClose = true;

		TextStencilElement clientText = new TextStencilElement(minecraft.font, Component.literal(clientTitle)).centered(true, true);
		addRenderableWidget(clientConfigWidget = new BoxWidget(width / 2 - 100, height / 2 - 15 - 30, 200, 16).showingElement(clientText));

		if (flwConfig != null) {
			clientConfigWidget.withCallback(() -> linkTo(new FlwSubMenuConfigScreen(this, ModConfig.Type.CLIENT, flwConfig)));
			clientText.withElementRenderer(BoxWidget.gradientFactory.apply(clientConfigWidget));
		} else {
			clientConfigWidget.active = false;
			clientConfigWidget.updateColorsFromState();
			clientText.withElementRenderer(DISABLED_RENDERER);
		}

		TextStencilElement commonText = new TextStencilElement(minecraft.font, Component.literal(commonTitle)).centered(true, true);
		addRenderableWidget(commonConfigWidget = new BoxWidget(width / 2 - 100, height / 2 - 15, 200, 16).showingElement(commonText));

		commonConfigWidget.active = false;
		commonConfigWidget.updateColorsFromState();
		commonText.withElementRenderer(DISABLED_RENDERER);


		TextStencilElement serverText = new TextStencilElement(minecraft.font, Component.literal(serverTitle)).centered(true, true);
		addRenderableWidget(serverConfigWidget = new BoxWidget(width / 2 - 100, height / 2 - 15 + 30, 200, 16).showingElement(serverText));

		serverConfigWidget.active = false;
		serverConfigWidget.updateColorsFromState();
		serverText.withElementRenderer(DISABLED_RENDERER);

		TextStencilElement titleText = new TextStencilElement(minecraft.font, modID.toUpperCase(Locale.ROOT))
				.centered(true, true)
				.withElementRenderer((ms, w, h, alpha) -> {
					UIRenderHelper.angledGradient(ms, 0, 0, h / 2, h, w / 2, Theme.p(Theme.Key.CONFIG_TITLE_A));
					UIRenderHelper.angledGradient(ms, 0, w / 2, h / 2, h, w / 2, Theme.p(Theme.Key.CONFIG_TITLE_B));
				});
		int boxWidth = width + 10;
		int boxHeight = 39;
		int boxPadding = 4;
		title = new BoxWidget(-5, height / 2 - 110, boxWidth, boxHeight)
				//.withCustomBackground(new Color(0x20_000000, true))
				.withBorderColors(Theme.p(Theme.Key.BUTTON_IDLE))
				.withPadding(0, boxPadding)
				.rescaleElement(boxWidth / 2f, (boxHeight - 2 * boxPadding) / 2f)//double the text size by telling it the element is only half as big as the available space
				.showingElement(titleText.at(0, 7));
		title.active = false;

		addRenderableWidget(title);


		ConfigScreen.modID = this.modID;

		goBack = new BoxWidget(width / 2 - 134, height / 2, 20, 20).withPadding(2, 2)
				.withCallback(this::onClose);
		goBack.showingElement(AllIcons.I_CONFIG_BACK.asStencil()
				.withElementRenderer(BoxWidget.gradientFactory.apply(goBack)));
		goBack.getToolTip()
				.add(Component.literal("Go Back"));
		addRenderableWidget(goBack);

		TextStencilElement othersText = new TextStencilElement(minecraft.font, Component.literal("Access Configs of other Mods")).centered(true, true);
		others = new BoxWidget(width / 2 - 100, height / 2 - 15 + 90, 200, 16).showingElement(othersText);
		othersText.withElementRenderer(BoxWidget.gradientFactory.apply(others));
		others.withCallback(() -> linkTo(new ConfigModListScreen(this)));
		addRenderableWidget(others);
	}
}
