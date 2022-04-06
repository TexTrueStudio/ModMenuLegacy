package io.github.prospector.modmenu.gui.widget;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.GameOptions.Option;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class ButtonListWidget extends BetterEntryListWidget<ButtonListWidget.ButtonEntry> {
	public ButtonListWidget(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
		super(minecraftClient, i, j, k, l, m);
	}

	public int addSingleOptionEntry(Option option) {
		return this.addEntry(ButtonListWidget.ButtonEntry.create(this.client.options, this.width, option));
	}

	public void addOptionEntry(Option firstOption, @Nullable Option secondOption) {
		this.addEntry(ButtonListWidget.ButtonEntry.create(this.client.options, this.width, firstOption, secondOption));
	}

	public void addAll(Option[] options) {
		for(int i = 0; i < options.length; i += 2) {
			this.addOptionEntry(options[i], i < options.length - 1 ? options[i + 1] : null);
		}

	}

	public int getRowWidth() {
		return 400;
	}

	protected int getScrollbarPositionX() {
		return super.getScrollbarPosition() + 32;
	}

	@Nullable
	public ButtonWidget getButtonFor(Option option) {
		for(ButtonListWidget.ButtonEntry buttonEntry : this.children()) {
			ButtonWidget clickableWidget = buttonEntry.optionsToButtons.get(option);
			if (clickableWidget != null) {
				return clickableWidget;
			}
		}

		return null;
	}

	public Optional<ButtonWidget> getHoveredButton(double mouseX, double mouseY) {
		for(ButtonListWidget.ButtonEntry buttonEntry : this.children()) {
			for(ButtonWidget clickableWidget : buttonEntry.buttons) {
				if (clickableWidget.isMouseOver( client, (int) mouseX, (int) mouseY)) {
					return Optional.of(clickableWidget);
				}
			}
		}

		return Optional.empty();
	}

	@Environment(EnvType.CLIENT)
	protected static class ButtonEntry extends BetterEntryListWidget.Entry<ButtonListWidget.ButtonEntry> {
		final Map<Option, ButtonWidget> optionsToButtons;
		final List<ButtonWidget> buttons;

		private ButtonEntry(Map<Option, ButtonWidget> optionsToButtons) {
			this.optionsToButtons = optionsToButtons;
			this.buttons = ImmutableList.copyOf(optionsToButtons.values());
		}

		public static ButtonListWidget.ButtonEntry create(GameOptions options, int width, Option option) {
			return new ButtonListWidget.ButtonEntry( ImmutableMap.of( option, option.createButton( options, width / 2 - 155, 0, 310 ) ) );
		}

		public static ButtonListWidget.ButtonEntry create(GameOptions options, int width, Option firstOption, @Nullable Option secondOption) {
			ButtonWidget clickableWidget = firstOption.createButton( options, width / 2 - 155, 0, 150 );
			return secondOption == null
					? new ButtonListWidget.ButtonEntry(ImmutableMap.of(firstOption, clickableWidget))
					: new ButtonListWidget.ButtonEntry(
					ImmutableMap.of( firstOption, clickableWidget, secondOption, secondOption.createButton( options, width / 2 - 155 + 160, 0, 150 ) )
			);
		}

		@Override
		public void render(int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			this.method_6700(index, x, y, entryWidth, entryHeight, mouseX, mouseY, hovered, tickDelta);
		}

		@Override
		public void method_9473(int i, int j, int k, float f) { }

		// render()
		@Override
		public void method_6700(int index, int x, int y, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			this.buttons.forEach( button -> {
				button.y = y;
				button.method_891( MinecraftClient.getInstance(), mouseX, mouseY, tickDelta );
			});
		}

		@Override
		public boolean mouseClicked(int index, int mouseX, int mouseY, int button, int x, int y) {
			return false;
		}

		@Override
		public void mouseReleased(int index, int mouseX, int mouseY, int button, int x, int y) { }

		public List<ButtonWidget> children() {
			return this.buttons;
		}

		public List<ButtonWidget> selectableChildren() {
			return this.buttons;
		}
	}
}

