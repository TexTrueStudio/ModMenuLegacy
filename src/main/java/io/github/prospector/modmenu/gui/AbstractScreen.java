package io.github.prospector.modmenu.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.LabelWidget;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractScreen extends Screen {
	private final List<Object> children = new ArrayList<>();

	protected void addChild(Object searchBox) {
		this.children.add( searchBox );
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		for ( ButtonWidget widget : this.buttons ) {
			widget.method_891( this.client, mouseX, mouseY, tickDelta );
			if ( widget.isMouseOver( this.client, mouseX, mouseY ) ) {
				widget.renderToolTip( mouseX, mouseY );
			}
		}

		for ( LabelWidget widget : this.labels ) {
			widget.render( this.client, mouseX, mouseY );
		}
	}
}
