package io.github.prospector.modmenu.gui.widget.entries;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.prospector.modmenu.ModMenu;
import io.github.prospector.modmenu.api.Mod;
import io.github.prospector.modmenu.gui.widget.ModListWidget;
import io.github.prospector.modmenu.util.mod.ModSearch;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.util.Identifier;

import java.util.List;

public class ParentEntry extends ModListEntry {
	private static final Identifier PARENT_MOD_TEXTURE = new Identifier(ModMenu.MOD_ID, "textures/gui/parent_mod.png");
	protected List<Mod> children;
	protected ModListWidget list;
	protected boolean hoveringIcon = false;

	public ParentEntry( Mod parent, List<Mod> children, ModListWidget list ) {
		super(parent, list);
		this.children = children;
		this.list = list;
	}

	// render()
	@Override
	public void method_6700(int index, int y, int x, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
		super.method_6700(index, x, y, rowWidth, rowHeight, mouseX, mouseY, hovered, tickDelta);
		TextRenderer font = this.client.textRenderer;
		int childrenBadgeHeight = font.fontHeight;
		//noinspection SuspiciousNameCombination
		int childrenBadgeWidth = font.fontHeight;
		int children = ModSearch.search( list.getParent(), list.getParent().getSearchInput(), this.children ).size();
		int childrenWidth = font.getStringWidth( Integer.toString(children) ) - 1;

		if (childrenBadgeWidth < childrenWidth + 4)
			childrenBadgeWidth = childrenWidth + 4;

		int childrenBadgeX = x + 32 - childrenBadgeWidth;
		int childrenBadgeY = y + 32 - childrenBadgeHeight;
		int childrenOutlineColor = 0x8810d098;
		int childrenFillColor = 0x88046146;
		DrawableHelper.fill(childrenBadgeX + 1, childrenBadgeY, childrenBadgeX + childrenBadgeWidth - 1, childrenBadgeY + 1, childrenOutlineColor);
		DrawableHelper.fill(childrenBadgeX, childrenBadgeY + 1, childrenBadgeX + 1, childrenBadgeY + childrenBadgeHeight - 1, childrenOutlineColor);
		DrawableHelper.fill(childrenBadgeX + childrenBadgeWidth - 1, childrenBadgeY + 1, childrenBadgeX + childrenBadgeWidth, childrenBadgeY + childrenBadgeHeight - 1, childrenOutlineColor);
		DrawableHelper.fill(childrenBadgeX + 1, childrenBadgeY + 1, childrenBadgeX + childrenBadgeWidth - 1, childrenBadgeY + childrenBadgeHeight - 1, childrenFillColor);
		DrawableHelper.fill(childrenBadgeX + 1, childrenBadgeY + childrenBadgeHeight - 1, childrenBadgeX + childrenBadgeWidth - 1, childrenBadgeY + childrenBadgeHeight, childrenOutlineColor);
		font.draw(Integer.toString(children), childrenBadgeX + childrenBadgeWidth / 2 - childrenWidth / 2, childrenBadgeY + 1, 0xCACACA);
		this.hoveringIcon = mouseX >= x - 1 && mouseX <= x - 1 + 32 && mouseY >= y - 1 && mouseY <= y - 1 + 32;
		if ( this.isMouseOver(mouseX, mouseY) ) {
			DrawableHelper.fill(x, y, x + 32, y + 32, 0xA0909090);
			this.client.getTextureManager().bindTexture(PARENT_MOD_TEXTURE);
			int xOffset = this.list.getParent().showModChildren.contains( getMod().getId() ) ? 32 : 0;
			int yOffset = this.hoveringIcon ? 32 : 0;
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			DrawableHelper.drawTexture(x, y, xOffset, yOffset, 32 + xOffset, 32 + yOffset, 256, 256);
		}
	}

	@Override
	public boolean mouseClicked(int index, int mouseX, int mouseY, int button, int x, int y) {
		if (hoveringIcon) {
			String id = getMod().getId();
			if (this.list.getParent().showModChildren.contains(id)) {
				this.list.getParent().showModChildren.remove(id);
			} else {
				this.list.getParent().showModChildren.add(id);
			}
			this.list.filter(this.list.getParent().getSearchInput(), false);
		}
		return super.mouseClicked(index, mouseX, mouseY, button, x, y);
	}

	public void addChildren(List<Mod> children) {
		this.children.addAll(children);
	}
}
