package io.github.prospector.modmenu.api;

import com.google.common.collect.ImmutableMap;
import io.github.prospector.modmenu.ModMenu;
import io.github.prospector.modmenu.gui.ModsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public interface ModMenuApi {
	/**
	 * Used for creating a {@link Screen} instance of the Mod Menu
	 * "Mods" screen
	 *
	 * @param previous The screen before opening
	 * @return A "Mods" Screen
	 */
	static Screen createModsScreen( Screen previous ) {
		return new ModsScreen( previous );
	}

	/**
	 * Used for creating a {@link Text} just like what would appear
	 * on a Mod Menu Mods button
	 *
	 * @return The text that would be displayed on a Mods button
	 */
	static Text createModsButtonText() {
		return ModMenu.createModsButtonText();
	}

	/**
	 * Used to construct a new config screen instance when your mod's
	 * configuration button is selected on the mod menu screen. The
	 * screen instance parameter is the active mod menu screen.
	 *
	 * @return A factory for constructing config screen instances.
	 */
	default ConfigScreenFactory<?> getModConfigScreenFactory() {
		return screen -> null;
	}

	/**
	 * Used to provide config screen factories for other mods.
	 * This takes second priority to a mod's own config screen factory provider.
	 * For example, if mod `xyz` supplies a config screen factory, mod `abc` providing a config
	 * screen to `xyz` will be pointless, as the one provided by `xyz` will be used.
	 * <p>
	 * This method is NOT meant to be used to add a config screen factory to your own mod.
	 *
	 * @return a map of mod ids to screen factories.
	 */
	default Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
		return ImmutableMap.of();
	}

	/**
	 * Used to provide additional mods to the mods menu
	 *
	 * @return a collection of {@link Mod} objects
	 */
	default Collection<Mod> getAdditionalMods() {
		return Collections.emptyList();
	}

	/**
	 * Used to provide additional parents to the mods menu.
	 *
	 * @return a {@link Map} of child id-parent id keyvalues
	 */
	default Map<String, String> getAdditionalParents() {
		return Collections.emptyMap();
	}

	/**
	 * Called before mod list initialization to find all possible badges
	 */
	default void onSetupBadges() {
	}
}
