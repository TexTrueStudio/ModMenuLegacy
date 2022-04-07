package io.github.prospector.modmenu.gui.widget;

import com.mojang.text2speech.Narrator;
import io.github.prospector.modmenu.ModMenu;
import io.github.prospector.modmenu.api.Mod;
import io.github.prospector.modmenu.config.ModMenuConfig;
import io.github.prospector.modmenu.gui.ModsScreen;
import io.github.prospector.modmenu.gui.widget.entries.ChildEntry;
import io.github.prospector.modmenu.gui.widget.entries.IndependentEntry;
import io.github.prospector.modmenu.gui.widget.entries.ModListEntry;
import io.github.prospector.modmenu.gui.widget.entries.ParentEntry;
import io.github.prospector.modmenu.util.mod.ModIconHandler;
import io.github.prospector.modmenu.util.mod.ModSearch;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.TranslatableText;

import java.util.*;
import java.util.stream.Collectors;

public class ModListWidget extends BetterEntryListWidget<ModListEntry> {
	public static final boolean DEBUG = Boolean.getBoolean("modmenu.debug");

	private final ModIconHandler iconHandler = new ModIconHandler();
	private final Set<Mod> addedMods = new HashSet<>();
	private String selectedModId = null;
	private final ModsScreen parent;
	private List<Mod> mods = null;

	public ModListWidget(MinecraftClient client, int width, int height, int y1, int y2, int entryHeight, String searchTerm, ModListWidget list, ModsScreen parent) {
		super(client, width, height, y1, y2, entryHeight);
		this.parent = parent;
		if (list != null) {
			this.mods = list.mods;
		}
		this.filter(searchTerm, false);
		setScrollAmount( parent.getScrollPercent() * Math.max( 0, this.getMaxPosition() - ( this.yEnd - this.yStart - 4 ) ) );
	}

	public void setScrollAmount(double amount) {
		scrollAmount = (float) amount;
		int denominator = Math.max( 0, this.getMaxPosition() - ( this.yEnd - this.yStart - 4 ) );
		if ( denominator <= 0 )
			parent.updateScrollPercent(0);
		else
			parent.updateScrollPercent( (float) getScrollAmount() / Math.max(0, this.getMaxPosition() - ( this.yEnd - this.yStart - 4 ) ) );
	}

	public void select(ModListEntry entry) {
		this.setSelected(entry);
		if ( entry != null ) {
			Mod mod = entry.getMod();
			Narrator.getNarrator().say( new TranslatableText( "narrator.select", mod.getName() ).getString() );
		}
	}

	@Override
	public void setSelected(ModListEntry entry) {
		super.setSelected( entry );
		this.selectedModId = entry.getMod().getId();
		this.parent.updateSelectedEntry( this.getSelectedOrNull() );
	}

	@Override
	protected int getEntryCount() {
		return children().size();
	}

	@Override
	protected boolean isEntrySelected(int index) {
		ModListEntry selected = getSelectedOrNull();
		return selected != null && selected.getMod().getId().equals( getEntry(index).getMod().getId() );
	}

	@Override
	public int addEntry(ModListEntry entry) {
		if ( addedMods.contains( entry.getMod() ) ) {
			return 0;
		}
		addedMods.add( entry.getMod() );
		int i = super.addEntry( entry );
		if ( entry.getMod().getId().equals(selectedModId) ) {
			setSelected(entry);
		}
		return i;
	}

	@Override
	protected boolean removeEntry(ModListEntry entry) {
		addedMods.remove( entry.getMod() );
		return super.removeEntry(entry);
	}

	@Override
	public ModListEntry remove(int index) {
		addedMods.remove( getEntry(index).getMod() );
		return super.remove(index);
	}

	public void reloadFilters() {
		filter(parent.getSearchInput(), true);
	}


	public void filter(String searchTerm, boolean refresh) {
		this.clearEntries();
		addedMods.clear();
		Collection<Mod> mods = ModMenu.MODS.values()
				.stream()
				.filter( mod -> !ModMenuConfig.HIDDEN_MODS.getValue().contains( mod.getId() ) )
				.collect( Collectors.toList() );

		if ( DEBUG ) {
			mods = new ArrayList<>(mods);
		}

		if ( this.mods == null || refresh ) {
			this.mods = new ArrayList<>();
			this.mods.addAll(mods);
			this.mods.sort( ModMenuConfig.SORTING.getValue().getComparator() );
		}

		List<Mod> matched = ModSearch.search( this.parent, searchTerm, this.mods );

		for ( Mod mod : matched ) {
			String modId = mod.getId();

			// Hide parent lib mods when not searching, and the config is set to hide
			if( mod.getBadges().contains(Mod.Badge.LIBRARY) && !ModMenuConfig.SHOW_LIBRARIES.getValue() )
				continue;

			if (! ModMenu.PARENT_MAP.values().contains(mod) ) {
				if ( ModMenu.PARENT_MAP.keySet().contains(mod) ) {
					// A parent mod with children

					List<Mod> children = ModMenu.PARENT_MAP.get(mod);
					children.sort( ModMenuConfig.SORTING.getValue().getComparator() );
					ParentEntry parent = new ParentEntry( mod, children, this );
					this.addEntry(parent);

					// Add all the child mods when not searching
					if ( this.parent.showModChildren.contains(modId) ) {
						List<Mod> validChildren = ModSearch.search( this.parent, searchTerm, children );
						for ( Mod child : validChildren )
							this.addEntry( new ChildEntry( child, parent, this, children.indexOf(child) == children.size() - 1 ) );
					}
				} else {
					// A mod with no children
					this.addEntry( new IndependentEntry( mod, this ) );
				}
			}
		}

		if (
				parent.getSelectedEntry() != null &&
				!children().isEmpty() ||
				this.getSelectedOrNull() != null &&
				this.getSelectedOrNull().getMod() != parent.getSelectedEntry().getMod()
		) {
			for ( ModListEntry entry : children() ) {
				if ( entry.getMod().equals( parent.getSelectedEntry().getMod() ) )
					setSelected(entry);
			}
		} else {
			if ( getSelectedOrNull() == null && !children().isEmpty() && getEntry(0) != null ) {
				setSelected( getEntry(0) );
			}
		}

		if ( getScrollAmount() > Math.max( 0, this.getMaxPosition() - ( this.yEnd - this.yStart - 4 ) ) ) {
			setScrollAmount( Math.max( 0, this.getMaxPosition() - ( this.yEnd - this.yStart - 4 ) ) );
		}
	}

	@Override
	protected int getScrollbarPosition() {
		return this.width - 6;
	}

	@Override
	public int getRowWidth() {
		return this.width - (Math.max(0, this.getMaxPosition() - (this.yEnd - this.yStart - 4)) > 0 ? 18 : 12);
	}

	public int getWidth() {
		return width;
	}

	public ModsScreen getParent() {
		return parent;
	}

	@Override
	protected int getMaxPosition() {
		return super.getMaxPosition() + 4;
	}

	public int getDisplayedCountFor(Set<String> set) {
		int count = 0;
		for (ModListEntry c : children()) {
			if (set.contains(c.getMod().getId())) {
				count++;
			}
		}
		return count;
	}

	public ModIconHandler getIconHandler() {
		return this.iconHandler;
	}
}
