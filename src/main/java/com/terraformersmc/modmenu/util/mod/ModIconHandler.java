package com.terraformersmc.modmenu.util.mod;

import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.texture.NativeImageBackedTexture;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ModIconHandler {
	private static final Logger LOGGER = LogManager.getLogger( "Mod Menu | ModIconHandler" );

	private final Map<Path, NativeImageBackedTexture> modIconCache = new HashMap<>();

	public NativeImageBackedTexture createIcon( ModContainer iconSource, String iconPath ) {
		try {
			Path foundPath = iconSource.findPath( iconPath ).orElse( null );

			if ( foundPath == null )
				throw new RuntimeException( "Path not found!" );

			NativeImageBackedTexture cachedIcon = getCachedModIcon( foundPath );
			if ( cachedIcon != null )
				return cachedIcon;

			try ( InputStream inputStream = Files.newInputStream( foundPath ) ) {
				BufferedImage image = ImageIO.read( Objects.requireNonNull( inputStream ) );
				Validate.validState( image.getHeight() == image.getWidth(), "Must be square icon" );
				NativeImageBackedTexture tex = new NativeImageBackedTexture( image );
				cacheModIcon( foundPath, tex );
				return tex;
			}

		} catch ( Throwable err ) {
			if ( !iconPath.equals( "assets/" + iconSource.getMetadata().getId() + "/icon.png" ) )
				LOGGER.error(
					"Invalid mod icon for icon source {}: {}",
					iconSource.getMetadata().getId(),
					iconPath,
					err
				);
			return null;
		}
	}

	NativeImageBackedTexture getCachedModIcon( Path path ) {
		return modIconCache.get( path );
	}

	void cacheModIcon( Path path, NativeImageBackedTexture tex ) {
		modIconCache.put( path, tex );
	}
}
