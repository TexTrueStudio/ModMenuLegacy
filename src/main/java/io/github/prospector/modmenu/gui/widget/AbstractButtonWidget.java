package io.github.prospector.modmenu.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public abstract class AbstractButtonWidget extends ButtonWidget {
	public static final TooltipSupplier EMPTY = ( button, mouseX, mouseY ) -> { };

	private final PressAction onPress;
	private final TooltipSupplier tooltipSupplier;
	private Text text;

	public AbstractButtonWidget(int id, int x, int y, int width, int height, Text text, PressAction onPress ) {
		this( id, x, y, width, height, text, onPress, EMPTY );
	}

	public AbstractButtonWidget(int id, int x, int y, int width, int height, Text text, PressAction onPress, TooltipSupplier tooltipSupplier ) {
		super( id, x, y, width, height, text.asFormattedString() );
		this.onPress = onPress;
		this.tooltipSupplier = tooltipSupplier;
		this.text = text;
	}

	// renderButton
	@Override
	public void method_891( MinecraftClient client, int mouseX, int mouseY, float tickDelta ) {
		super.method_891( client, mouseX, mouseY, tickDelta );
		if ( this.isHovered() )
			this.renderToolTip( mouseX, mouseY );
	}

	@Override
	public void renderToolTip(int mouseX, int mouseY) {
		this.tooltipSupplier.onTooltip( this, mouseX, mouseY );
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getHeight() {
		return this.height;
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY) {
		this.onPress.onPress( this );
	}

	public Text getMessage() {
		return this.text;
	}

	public void setMessage( Text text ) {
		this.text = text;
	}

	@FunctionalInterface
	public interface PressAction {
		void onPress( AbstractButtonWidget button );
	}

	@FunctionalInterface
	public interface TooltipSupplier {
		void onTooltip( AbstractButtonWidget button, int mouseX, int mouseY );

		default void supply( Consumer<Text> consumer ) { }
	}
}
