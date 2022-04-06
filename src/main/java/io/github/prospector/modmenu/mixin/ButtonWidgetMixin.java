package io.github.prospector.modmenu.mixin;

import io.github.prospector.modmenu.imixin.ButtonAcessor;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ButtonWidget.class)
public class ButtonWidgetMixin implements ButtonAcessor {
	@Shadow
	protected int height;

	@Override
	public int modmenu$getHeight() {
		return this.height;
	}
}
