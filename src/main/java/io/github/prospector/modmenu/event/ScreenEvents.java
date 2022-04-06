package io.github.prospector.modmenu.event;

import net.legacyfabric.fabric.api.event.Event;
import net.legacyfabric.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

public class ScreenEvents {
	public static final Event<ScreenInitEvent> AFTER_INIT = EventFactory.createArrayBacked(
			ScreenInitEvent.class,
			listeners -> (MinecraftClient client, Screen screen, int scaledWidth, int scaledHeight) -> {
				for ( ScreenInitEvent listener : listeners )
					listener.afterInit( client, screen, scaledWidth, scaledHeight );
			}
	);

	@FunctionalInterface
	public interface ScreenInitEvent {
		void afterInit(MinecraftClient client, Screen screen, int scaledWidth, int scaledHeight);
	}
}
