package io.github.prospector.modmenu.gui;

import io.github.prospector.modmenu.gui.widget.BetterEntryListWidget;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.LabelWidget;
import net.minecraft.client.gui.widget.ListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractScreen extends Screen {
	private final List<DrawableHelper> drawables = new ArrayList<>();
	private final List<Object> selectables = new ArrayList<>();
	private final List<Object> children = new ArrayList<>();

	protected void addDrawableChild(DrawableHelper child) {
		this.drawables.add( child );
	}

	protected void addSelectableChild(DrawableHelper child) {
		this.selectables.add( child );
	}

	protected void addSelectableChild(ListWidget child) {
		this.selectables.add( child );
	}

	protected void addChild(Object searchBox) {
		this.children.add( searchBox );
	}

	public void render(int mouseX, int mouseY, float tickDelta) {
		for( ButtonWidget widget : this.buttons ) {
			widget.method_891( this.client, mouseX, mouseY, tickDelta );
		}

		for( LabelWidget widget : this.labels ) {
			widget.render( this.client, mouseX, mouseY );
		}

	}
}
