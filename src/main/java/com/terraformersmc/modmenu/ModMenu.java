package com.terraformersmc.modmenu;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.LinkedListMultimap;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.util.mod.Mod;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.terraformersmc.modmenu.config.ModMenuConfig;
import com.terraformersmc.modmenu.config.ModMenuConfigManager;
import com.terraformersmc.modmenu.util.mod.fabric.FabricDummyParentMod;
import com.terraformersmc.modmenu.util.mod.fabric.FabricMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static com.terraformersmc.modmenu.util.TranslationUtil.hasTranslation;

public class ModMenu implements ClientModInitializer {
	public static final String MOD_ID = "modmenu";
	public static final Logger LOGGER = LogManager.getLogger( "Mod Menu" );
	public static final Gson GSON = new GsonBuilder()
		.setFieldNamingPolicy( FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES )
		.setPrettyPrinting()
		.create();

	public static final Map<String, Mod> MODS = new HashMap<>();
	public static final Map<String, Mod> ROOT_MODS = new HashMap<>();
	public static final LinkedListMultimap<Mod, Mod> PARENT_MAP = LinkedListMultimap.create();

	private static final List<Supplier<Map<String, ConfigScreenFactory<?>>>> dynamicScreenFactories = new ArrayList<>();
	private static ImmutableMap<String, ConfigScreenFactory<?>> configScreenFactories = ImmutableMap.of();

	private static int cachedDisplayedModCount = -1;

	public static boolean hasFactory( String modid ) {
		return configScreenFactories.containsKey( modid );
	}

	public static Screen getConfigScreen( String modid, Screen menuScreen ) {
		ConfigScreenFactory<?> factory = configScreenFactories.get( modid );
		if ( factory != null )
			return factory.create( menuScreen );
		for ( Supplier<Map<String, ConfigScreenFactory<?>>> factorySupplier : dynamicScreenFactories ) {
			factory = factorySupplier.get().get( modid );
			if ( factory != null )
				return factory.create( menuScreen );
		}
		return null;
	}

	@Override
	public void onInitializeClient() {
		ModMenuConfigManager.initializeConfig();
		Map<String, ConfigScreenFactory<?>> factories = new HashMap<>();
		Map<String, String> additionalParents = new HashMap<>();
		// find all entrypoints
		List<EntrypointContainer<ModMenuApi>> entrypoints = FabricLoader.getInstance().getEntrypointContainers( "modmenu", ModMenuApi.class );
		// badges should be loaded first, as the other things depend on them
		entrypoints.forEach( entrypoint -> {
			try {
				entrypoint.getEntrypoint().onSetupBadges();
			} catch ( Throwable err ) {
				LOGGER.error(
					"Failed to setup badges from mod '{}': ",
					entrypoint.getProvider().getMetadata().getId(),
					err
				);
			}
		} );
		// load everything else
		entrypoints.forEach( entrypoint -> {
			ModMetadata meta = entrypoint.getProvider().getMetadata();
			String modId = meta.getId();
			try {
				ModMenuApi api = entrypoint.getEntrypoint();
				factories.put( modId, api.getModConfigScreenFactory() );
				api.getAdditionalMods().forEach( mod -> MODS.put( mod.getId(), mod ) );
				additionalParents.putAll( api.getAdditionalParents() );
				dynamicScreenFactories.add( api::getProvidedConfigScreenFactories );
			} catch ( Throwable e ) {
				LOGGER.error( "Mod {} provides a broken implementation of ModMenuApi", modId, e );
			}
		} );
		configScreenFactories = ImmutableMap.copyOf( factories );

		// fill mods map
		for ( ModContainer modContainer : FabricLoader.getInstance().getAllMods() ) {
			if ( !ModMenuConfig.HIDDEN_MODS.getValue().contains( modContainer.getMetadata().getId() ) ) {
				Mod mod = new FabricMod( modContainer );
				MODS.put( mod.getId(), mod );
			}
		}

		Map<String, Mod> dummyParents = new HashMap<>();
		// Init parents map
		for ( Mod mod : MODS.values() ) {
			String parentId = mod.getParent();
			if ( parentId != null ) {
				Mod parent = MODS.getOrDefault( parentId, dummyParents.get( parentId ) );
				if ( parent != null ) {
					if ( mod instanceof FabricMod ) {
						parent = new FabricDummyParentMod( (FabricMod) mod, parentId );
						dummyParents.put( parentId, parent );
					}
				}
				PARENT_MAP.put( parent, mod );
			} else {
				ROOT_MODS.put( mod.getId(), mod );
			}
		}
		for ( Map.Entry<String, String> entry : additionalParents.entrySet() ) {
			// get both the parentId and the child mod object
			String parentId = entry.getValue();
			Mod mod = MODS.get( entry.getKey() );
			// if neither of them are null
			if ( parentId != null && mod != null ) {
				// get the parent mod obj
				Mod parent = MODS.getOrDefault( parentId, dummyParents.get( parentId ) );
				if ( parent != null ) {
					// if it isn't null, add the child to it
					if ( mod instanceof FabricMod ) {
						parent = new FabricDummyParentMod( (FabricMod) entry, parentId );
						dummyParents.put( parentId, parent );
					}
				}
				PARENT_MAP.put( parent, MODS.get( entry.getKey() ) );
				ROOT_MODS.remove( entry.getKey() );
			}
		}

		MODS.putAll( dummyParents );
	}

	public static void clearModCountCache() {
		cachedDisplayedModCount = -1;
	}

	public static String getDisplayedModCount() {
		if ( cachedDisplayedModCount == -1 ) {
			// listen, if you have >= 2^32 mods then that's on you
			cachedDisplayedModCount = Math.toIntExact(
				MODS.values().stream().filter( mod ->
					( ModMenuConfig.COUNT_CHILDREN.getValue() || mod.getParent() == null ) &&
						( ModMenuConfig.COUNT_LIBRARIES.getValue() || !mod.getBadges().contains( Mod.Badge.LIBRARY ) ) &&
						( ModMenuConfig.COUNT_HIDDEN_MODS.getValue() || !ModMenuConfig.HIDDEN_MODS.getValue().contains( mod.getId() ) )
				).count()
			);
		}
		return NumberFormat.getInstance().format( cachedDisplayedModCount );
	}

	public static Text createModsButtonText() {
		TranslatableText modsText = new TranslatableText( "modmenu.title" );
		if ( ModMenuConfig.MOD_COUNT_LOCATION.getValue().isOnModsButton() && ModMenuConfig.MODS_BUTTON_STYLE.getValue() != ModMenuConfig.ModsButtonStyle.ICON ) {
			String count = ModMenu.getDisplayedModCount();
			if ( ModMenuConfig.MODS_BUTTON_STYLE.getValue() == ModMenuConfig.ModsButtonStyle.SHRINK ) {
				modsText.append( new LiteralText( " " ) ).append( new TranslatableText( "modmenu.loaded.short", count ) );
			} else {
				String specificKey = "modmenu.loaded." + count;
				String key = hasTranslation( specificKey ) ? specificKey : "modmenu.loaded";
				if ( ModMenuConfig.EASTER_EGGS.getValue() && hasTranslation( specificKey + ".secret" ) )
					key = specificKey + ".secret";
				modsText.append( new LiteralText( " " ) ).append( new TranslatableText( key, count ) );
			}
		}
		return modsText;
	}

}
