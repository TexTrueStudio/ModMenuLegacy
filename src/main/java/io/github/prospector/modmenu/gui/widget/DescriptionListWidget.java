package io.github.prospector.modmenu.gui.widget;

import io.github.prospector.modmenu.gui.ModListScreen;
import io.github.prospector.modmenu.gui.widget.entries.ModListEntry;
import io.github.prospector.modmenu.util.HardcodedUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;

import java.util.LinkedList;
import java.util.List;

public class DescriptionListWidget extends BetterEntryListWidget<DescriptionListWidget.DescriptionEntry> {
	private final ModListScreen parent;
	private final TextRenderer textRenderer;
	private ModListEntry lastSelected = null;
	private final List<DescriptionEntry> entries = new LinkedList<>();

	public DescriptionListWidget(MinecraftClient client, int width, int height, int top, int bottom, int entryHeight, ModListScreen parent) {
		super(client, width, height, top, bottom, entryHeight);
		this.parent = parent;
		this.textRenderer = client.textRenderer;
	}

	@Override
	public int getRowWidth() {
		return this.width - 10;
	}

	@Override
	protected int getScrollbarPosition() {
		return this.width - 6 + xStart;
	}

	@Override
	protected int getEntryCount() {
		return entries.size();
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		ModListEntry selectedEntry = parent.getSelectedEntry();
		if (selectedEntry != lastSelected) {
			lastSelected = selectedEntry;
			entries.clear();
			scrollAmount = -Float.MAX_VALUE;
			String description = lastSelected.getMod().getDescription();
			String id = lastSelected.getMod().getId();
			if (description.isEmpty() && HardcodedUtil.getHardcodedDescriptions().containsKey(id)) {
				description = HardcodedUtil.getHardcodedDescription(id);
			}
			if (lastSelected != null && description != null && !description.isEmpty()) {
				for (String line : textRenderer.wrapLines(description.replaceAll("\n", "\n\n"), getRowWidth())) {
					entries.add(new DescriptionEntry(line));
				}
			}
		}
		super.render(mouseX, mouseY, delta);
	}

	@Override
	protected void renderHoleBackground(int y1, int y2, int startAlpha, int endAlpha) {
		// Awful hack but it makes the background "seamless"
		ModListScreen.overlayBackground(xStart, y1, xEnd, y2, 64, 64, 64, startAlpha, endAlpha);
	}

	@Override
	public DescriptionEntry getEntry(int index) {
		return entries.get(index);
	}

	protected static class DescriptionEntry extends BetterEntryListWidget.Entry<DescriptionEntry> {
		protected String text;

		public DescriptionEntry(String text) {
			this.text = text;
		}

		@Override
		public void render(int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			this.method_6700(index, x, y, entryWidth, entryHeight, mouseX, mouseY, hovered, tickDelta);
		}

		// render()
		@Override
		public void method_6700(int index, int x, int y, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			MinecraftClient.getInstance().textRenderer.drawWithShadow(text, x, y, 0xAAAAAA);
		}

		@Override
		public boolean mouseClicked(int index, int mouseX, int mouseY, int button, int x, int y) {
			return false;
		}

		@Override
		public void mouseReleased(int index, int mouseX, int mouseY, int button, int x, int y) { }

		// updatePosition
		@Override
		public void method_9473(int index, int x, int y, float tickDelta) { }
	}

}
