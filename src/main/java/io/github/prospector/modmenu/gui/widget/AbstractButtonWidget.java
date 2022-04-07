package io.github.prospector.modmenu.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.lwjgl.input.Mouse;

import java.util.function.Consumer;

public class AbstractButtonWidget extends ButtonWidget {
	public static final TooltipSupplier EMPTY = ( button, mouseX, mouseY ) -> { };

	private final TooltipSupplier tooltipSupplier;
	private PressAction onPress;
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
		if ( this.onPress != null )
			this.onPress.onPress( this );
	}

	public void mouseScrolled(int mouseX, int mouseY, int amount ) { }

	public void handleMouse() {
		if ( this.isMouseOver( MinecraftClient.getInstance(), this.x, this.y ) ) {
			// scrolling
			int scroll = Mouse.getEventDWheel();
			if ( scroll != 0 ) {
				this.mouseScrolled( Mouse.getX(), Mouse.getY(), scroll > 0 ? -1 : 1 );
			}
		}
	}

	public Text getMessage() {
		return this.text;
	}

	public void setMessage( Text text ) {
		this.text = text;
	}

	protected void setOnPress( PressAction action ) {
		this.onPress = action;
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
