package com.terraformersmc.modmenu.gui.widget.entries;

import com.terraformersmc.modmenu.util.mod.Mod;
import com.terraformersmc.modmenu.gui.widget.ModListWidget;
import net.minecraft.client.gui.DrawableHelper;

public class ChildEntry extends ModListEntry {
	private final boolean bottomChild;
	private final ParentEntry parent;

	public ChildEntry(Mod container, ParentEntry parent, ModListWidget list, boolean bottomChild ) {
		super( container, list );
		this.bottomChild = bottomChild;
		this.parent = parent;
	}

	@Override
	public void render( int index, int x, int y, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered ) {
		super.render( index, x, y, rowWidth, rowHeight, mouseX, mouseY, hovered );
		x += 4;
		int color = 0xFFA0A0A0;
		DrawableHelper.fill( x, y - 2, x + 1, y + ( bottomChild ? rowHeight / 2 : rowHeight + 2 ), color );
		DrawableHelper.fill( x, y + rowHeight / 2, x + 7, y + rowHeight / 2 + 1, color );
	}

	@Override
	public int getXOffset() {
		return 13;
	}
}
