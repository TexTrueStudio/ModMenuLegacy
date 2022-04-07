package io.github.prospector.modmenu.util;


import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class ScreenTexts {
	public static final Text ON = new TranslatableText("options.on");
	public static final Text OFF = new TranslatableText("options.off");
	public static final Text DONE = new TranslatableText("gui.done");

	private ScreenTexts() { }

	public static Text composeGenericOptionText(Text text, Text value) {
		return new TranslatableText( "options.generic_value", text, value );
	}
}

