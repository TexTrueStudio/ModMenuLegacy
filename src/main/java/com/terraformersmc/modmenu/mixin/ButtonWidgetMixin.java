package com.terraformersmc.modmenu.mixin;

import org.thinkingstudio.legacycore.modmenu.imixin.ButtonAccessor;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ButtonWidget.class)
public class ButtonWidgetMixin implements ButtonAccessor {
	@Shadow
	protected int height;

	@Override
	public int modmenu$getHeight() {
		return this.height;
	}
}
