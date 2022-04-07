package io.github.prospector.modmenu.gui.widget;

import com.google.common.collect.Lists;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.EntryListWidget;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractList;
import java.util.List;
import java.util.Objects;

public abstract class BetterEntryListWidget< E extends BetterEntryListWidget.Entry<E> > extends EntryListWidget implements Renderable {
	private final List<E> children = new Entries();
	@Nullable
	private E selected;

	public BetterEntryListWidget( MinecraftClient minecraftClient, int width, int height, int top, int bottom, int itemHeight ) {
		super(minecraftClient, width, height, top, bottom, itemHeight);
	}

	public void setSelected( @Nullable E entry) {
		this.selected = entry;
	}

	@Nullable
	public E getSelectedOrNull() {
		return this.selected;
	}

	public final List<E> children() {
		return this.children;
	}

	protected final void clearEntries() {
		this.children.clear();
	}

	@Override
	public E getEntry(int index) {
		return this.children().get(index);
	}

	protected int addEntry(E entry) {
		this.children.add(entry);
		return this.children.size() - 1;
	}

	protected int getEntryCount() {
		return this.children().size();
	}

	@Nullable
	public E remove(int index) {
		E entry = this.children.get(index);
		return this.removeEntry( this.children.get(index) ) ? entry : null;
	}

	protected boolean removeEntry(E entry) {
		boolean bl = this.children.remove(entry);
		if ( bl && entry == this.getSelectedOrNull() ) {
			this.setSelected( null );
		}
		return bl;
	}

	@SuppressWarnings("unused")
	public boolean isMouseOver(double mouseX, double mouseY) {
		return mouseY >= (double) this.yStart &&
				mouseY <= (double) this.yEnd &&
				mouseX >= (double) this.xStart &&
				mouseX <= (double) this.xStart;
	}

	void setEntryParentList(Entry<E> entry ) {
		entry.parentList = this;
	}

	class Entries extends AbstractList<E> {
		private final List<E> entries = Lists.newArrayList();

		Entries() { }

		public E get(int i) {
			return this.entries.get(i);
		}

		public int size() {
			return this.entries.size();
		}

		@Override
		public E set(int i, E entry) {
			E entry2 = this.entries.set(i, entry);
			BetterEntryListWidget.this.setEntryParentList(entry);
			return entry2;
		}

		@Override
		public void add(int i, E entry) {
			this.entries.add(i, entry);
			BetterEntryListWidget.this.setEntryParentList(entry);
		}

		@Override
		public E remove(int i) {
			return this.entries.remove(i);
		}
	}

	public static abstract class Entry< E extends Entry<E> > implements EntryListWidget.Entry {
		BetterEntryListWidget<E> parentList;

		public Entry() { }

		public abstract void render( int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta );

		public boolean isMouseOver( int mouseX, int mouseY ) {
			//noinspection EqualsBetweenInconvertibleTypes
			return Objects.equals( this.parentList.getEntryAt( mouseX, mouseY ), this );
		}
	}

}
