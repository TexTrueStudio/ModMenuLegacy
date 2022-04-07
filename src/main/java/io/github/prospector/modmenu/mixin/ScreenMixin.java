package io.github.prospector.modmenu.mixin;

import io.github.prospector.modmenu.event.ModMenuEventHandler;
import io.github.prospector.modmenu.event.ScreenEvents;
import io.github.prospector.modmenu.imixin.ScreenAcessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Screen.class)
public class ScreenMixin implements ScreenAcessor {
	@Shadow
	protected List<ButtonWidget> buttons;

	@Override
	public List<ButtonWidget> modmenu$getButtons() {
		return this.buttons;
	}

	@Inject( method = "init(Lnet/minecraft/client/MinecraftClient;II)V", at = @At("TAIL") )
	public void onInit(MinecraftClient client, int width, int height, CallbackInfo ci) {
		ModMenuEventHandler.afterScreenInit( client, (Screen) (Object) this, width, height );
	}
}
