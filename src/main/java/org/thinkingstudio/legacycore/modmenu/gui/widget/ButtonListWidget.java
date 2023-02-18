package org.thinkingstudio.legacycore.modmenu.gui.widget;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.class_1803;
import net.minecraft.client.option.GameOptions;
import org.thinkingstudio.legacycore.modmenu.config.option.Option;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class ButtonListWidget extends BetterEntryListWidget<ButtonListWidget.ButtonEntry> {
	public ButtonListWidget( MinecraftClient minecraftClient, int width, int height, int top, int bottom, int itemHeight ) {
		super( minecraftClient, width, height, top, bottom, itemHeight );
	}

	@Override
	public class_1803 method_6697(int i) {
		return null;
	}

	public int addSingleOptionEntry( Option option, int id ) {
		return this.addEntry( ButtonListWidget.ButtonEntry.create( this.client.options, id, this.width, option ) );
	}

	public void addOptionEntry( Option firstOption, @Nullable Option secondOption, int id ) {
		this.addEntry( ButtonListWidget.ButtonEntry.create(
			this.client.options,
			id,
			this.width,
			firstOption,
			secondOption
		) );
	}

	public void addAll( Option[] options, int id ) {
		for ( int i = 0; i < options.length; i += 2 )
			this.addOptionEntry(
				options[i],
				i < options.length - 1 ? options[i + 1] : null,
				id - i
			);
	}

	public int getRowWidth() {
		return 400;
	}

	protected int getScrollbarPositionX() {
		return super.getScrollbarPosition() + 32;
	}

	@Nullable
	public ButtonWidget getButtonFor( Option option ) {
		for ( ButtonListWidget.ButtonEntry buttonEntry : this.children() ) {
			ButtonWidget clickableWidget = buttonEntry.optionsToButtons.get( option );
			if ( clickableWidget != null )
				return clickableWidget;
		}

		return null;
	}

	public Optional<AbstractButtonWidget> getHoveredButton( double mouseX, double mouseY ) {
		for ( ButtonEntry buttonEntry : this.children() )
			for ( AbstractButtonWidget clickableWidget : buttonEntry.buttons )
				if ( clickableWidget.isMouseOver( client, (int) mouseX, (int) mouseY ) )
					return Optional.of( clickableWidget );

		return Optional.empty();
	}

	@Override
	public void method_9529() {
		super.method_9529();
		for ( ButtonEntry entry : this.children() )
			entry.buttons.forEach( AbstractButtonWidget::handleMouse );
	}

	@Environment(EnvType.CLIENT)
	protected static class ButtonEntry extends BetterEntryListWidget.Entry<ButtonListWidget.ButtonEntry> {
		final Map<Option, AbstractButtonWidget> optionsToButtons;
		final List<AbstractButtonWidget> buttons;

		private ButtonEntry( Map<Option, AbstractButtonWidget> optionsToButtons ) {
			this.optionsToButtons = optionsToButtons;
			this.buttons = ImmutableList.copyOf( optionsToButtons.values() );
		}

		public static ButtonListWidget.ButtonEntry create(GameOptions options, int id, int width, Option option ) {
			return new ButtonListWidget.ButtonEntry( ImmutableMap.of(
				option, option.createButton( options, id, width / 2 - 155, 0, 310 )
			) );
		}

		public static ButtonListWidget.ButtonEntry create( GameOptions options, int id, int width, Option firstOption, @Nullable Option secondOption ) {
			AbstractButtonWidget clickableWidget = firstOption.createButton( options, id, width / 2 - 155, 0, 150 );
			return secondOption == null
				? new ButtonListWidget.ButtonEntry( ImmutableMap.of( firstOption, clickableWidget ) )
				: new ButtonListWidget.ButtonEntry(
				ImmutableMap.of(
					firstOption, clickableWidget,
					secondOption, secondOption.createButton( options, id, width / 2 - 155 + 160, 0, 150 )
				)
			);
		}

		@Override
		public void render( int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta ) {
			this.render( index, x, y, entryWidth, entryHeight, mouseX, mouseY, hovered );
		}

		@Override
		public void updatePosition( int index, int x, int y ) { }

		@Override
		public void render( int index, int x, int y, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered ) {
			this.buttons.forEach( button -> {
				button.y = y;
				button.render( MinecraftClient.getInstance(), mouseX, mouseY );
			} );
		}

		@Override
		public boolean mouseClicked( int index, int mouseX, int mouseY, int button, int x, int y ) {
			this.buttons.forEach( buttonWidget -> {
				if ( buttonWidget.isMouseOver( MinecraftClient.getInstance(), mouseX, mouseY ) )
					buttonWidget.mouseReleased( mouseX, mouseY );
			} );
			return false;
		}

		@Override
		public void mouseReleased( int index, int mouseX, int mouseY, int button, int x, int y ) {
		}

		public List<AbstractButtonWidget> children() {
			return this.buttons;
		}
	}
}

