package io.github.prospector.modmenu.util;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.prospector.modmenu.api.Mod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;

import java.util.Random;

@Environment(EnvType.CLIENT)
public class DrawingUtil {
	private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

	public static void drawRandomVersionBackground( Mod mod, int x, int y, int width, int height ){
		int seed = mod.getName().hashCode() + mod.getVersion().hashCode();
		int color = 0xFF000000 + new Random(seed).nextInt(0xFFFFFF);
		float a = (float) ( color >> 24 & 0xFF ) / 0xFF;
		float r = (float) ( color >> 16 & 0xFF ) / 0xFF;
		float g = (float) ( color >>  8 & 0xFF ) / 0xFF;
		float b = (float) ( color       & 0xFF ) / 0xFF;
		GlStateManager.color4f( r, g, b, a );
		DrawableHelper.fill( x, y, x + width, y + height, color );
	}

	public static void drawWrappedString( String string, int x, int y, int wrapWidth, int lines, int color ) {
		while (string != null && string.endsWith("\n")) {
			string = string.substring(0, string.length() - 1);
		}
		List<StringVisitable> strings = ScreenTexts.wrapLines( new LiteralText(string), wrapWidth, new Style() );
		for (int i = 0; i < strings.size(); i++) {
			if (i >= lines) {
				break;
			}
			StringVisitable renderable = strings.get(i);
			if (i == lines - 1 && strings.size() > lines) {
				renderable = StringVisitable.concat(strings.get(i), StringVisitable.plain("..."));
			}
			int x1 = x;
			if (CLIENT.textRenderer.isRightToLeft()) {
				int width = CLIENT.textRenderer.getWidth(line);
				x1 += (float) (wrapWidth - width);
			}
			CLIENT.textRenderer.draw( line, x1, y + i * CLIENT.textRenderer.fontHeight, color);
		}
	}

	public static void drawBadge( int x, int y, int tagWidth, String text, int outlineColor, int fillColor, int textColor ) {
		DrawableHelper.fill( x + 1, y - 1, x + tagWidth, y, outlineColor );
		DrawableHelper.fill( x, y, x + 1, y + CLIENT.textRenderer.fontHeight, outlineColor );
		DrawableHelper.fill( x + 1, y + 1 + CLIENT.textRenderer.fontHeight - 1, x + tagWidth, y + CLIENT.textRenderer.fontHeight + 1, outlineColor );
		DrawableHelper.fill( x + tagWidth, y, x + tagWidth + 1, y + CLIENT.textRenderer.fontHeight, outlineColor );
		DrawableHelper.fill( x + 1, y, x + tagWidth, y + CLIENT.textRenderer.fontHeight, fillColor );
		CLIENT.textRenderer.draw( text, (int) ( x + 1 + ( tagWidth - CLIENT.textRenderer.getStringWidth(text) ) / (float) 2 ), y + 1, textColor );
	}
}
