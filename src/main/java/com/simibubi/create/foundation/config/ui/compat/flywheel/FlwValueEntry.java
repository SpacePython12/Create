package com.simibubi.create.foundation.config.ui.compat.flywheel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;

import com.google.common.base.Predicates;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.item.TooltipHelper;

import net.createmod.catnip.config.ui.ConfigAnnotations;
import net.createmod.catnip.config.ui.ConfigHelper;
import net.createmod.catnip.config.ui.ConfigScreen;
import net.createmod.catnip.config.ui.entries.ValueEntry;
import net.createmod.catnip.gui.element.DelegatedStencilElement;
import net.createmod.catnip.gui.widget.BoxWidget;
import net.createmod.catnip.utility.FontHelper.Palette;
import net.createmod.catnip.utility.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class FlwValueEntry<T> extends ValueEntry<T> {
	protected Supplier<T> getter;
	protected Consumer<T> option;

	public FlwValueEntry(String key, Supplier<T> getter, Consumer<T> option) {
		super(ConfigScreen.toHumanReadable(key));
		this.getter = getter;
		this.option = option;
		this.path = String.join(".", key);

		resetButton = new BoxWidget(0, 0, resetWidth - 12, 16)
				.showingElement(AllIcons.I_CONFIG_RESET.asStencil())
				.withCallback(() -> {
					setValue(getter.get());
					this.onReset();
				});
		resetButton.modifyElement(e -> ((DelegatedStencilElement) e).withElementRenderer(BoxWidget.gradientFactory.apply(resetButton)));

		listeners.add(resetButton);

        labelTooltip.add(Component.literal(ConfigScreen.toHumanReadable(key)).withStyle(ChatFormatting.WHITE));
		String comment = null;//option.getComment();
		if (comment == null || comment.isEmpty())
			return;

		List<String> commentLines = new ArrayList<>(Arrays.asList(comment.split("\n")));


		Pair<String, Map<String, String>> metadata = ConfigHelper.readMetadataFromComment(commentLines);
		if (metadata.getFirst() != null) {
			unit = metadata.getFirst();
		}
		if (metadata.getSecond() != null && !metadata.getSecond().isEmpty()) {
			annotations.putAll(metadata.getSecond());
		}
		// add comment to tooltip
		labelTooltip.addAll(commentLines.stream()
				.filter(Predicates.not(s -> s.startsWith("Range")))
				.map(Component::literal)
				.flatMap(stc -> TooltipHelper.cutTextComponent(stc, Palette.ALL_GRAY)
						.stream())
				.toList());

		if (annotations.containsKey(ConfigAnnotations.RequiresRelog.TRUE.getName()))
			labelTooltip.addAll(TooltipHelper.cutTextComponent(Component.literal("Changing this value will require a _relog_ to take full effect"), Palette.GRAY_AND_GOLD));

		if (annotations.containsKey(ConfigAnnotations.RequiresRestart.CLIENT.getName()))
			labelTooltip.addAll(TooltipHelper.cutTextComponent(Component.literal("Changing this value will require a _restart_ to take full effect"), Palette.GRAY_AND_RED));

		labelTooltip.add(Component.literal(ConfigScreen.modID + ":" + key).withStyle(ChatFormatting.DARK_GRAY));
	}

	@Override
	public void setValue(@NotNull T value) {
		option.accept(value);
		onValueChange(value);
	}

	@NotNull
	@Override
	public T getValue() {
		return getter.get();
	}

	@Override
	protected boolean isCurrentValueDefault() {
		return false;
	}
}
