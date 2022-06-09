package io.github.prospector.modmenu.util;


import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public final class ScreenTexts {
	public static final Text GRAY_DEFAULT_SEPARATOR_TEXT = new LiteralText( ", " ).setStyle( new Style().setFormatting( Formatting.GRAY ) );
	public static final Text ON = new TranslatableText( "options.on" );
	public static final Text OFF = new TranslatableText( "options.off" );
	public static final Text DONE = new TranslatableText( "gui.done" );
	public static final Text CANCEL = new TranslatableText( "gui.cancel" );
	public static final Text YES = new TranslatableText( "gui.yes" );
	public static final Text NO = new TranslatableText( "gui.no" );
	public static final Text PROCEED = new TranslatableText( "gui.proceed" );
	public static final Text BACK = new TranslatableText( "gui.back" );
	public static final Text CONNECT_FAILED = new TranslatableText( "connect.failed" );
	public static final Text LINE_BREAK = new LiteralText( "\n" );
	public static final Text SENTENCE_SEPARATOR = new LiteralText( ". " );

	private ScreenTexts() { }

	public static Text onOrOff( boolean on ) {
		return on ? ON : OFF;
	}

	public static Text composeToggleText( Text text, boolean value ) {
		return new TranslatableText( value ? "options.on.composed" : "options.off.composed", text );
	}

	public static Text composeGenericOptionText( Text text, Text value ) {
		return new TranslatableText( "options.generic_value", text, value );
	}

	public static Text joinSentences( Text first, Text second ) {
		return new LiteralText( "" ).append( first ).append( SENTENCE_SEPARATOR ).append( second );
	}

	public static Text joinLines( Text... texts ) {
		return joinLines( Arrays.asList( texts ) );
	}

	public static Text joinLines( Collection<? extends Text> texts ) {
		return join( texts, LINE_BREAK );
	}

	public static <T> Text join( Collection<? extends T> elements, Function<T, Text> transformer ) {
		return join( elements, GRAY_DEFAULT_SEPARATOR_TEXT, transformer );
	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public static <T> Text join( Collection<? extends T> elements, Optional<Text> separator, Function<T, Text> transformer ) {
		return join( elements, separator.orElse( GRAY_DEFAULT_SEPARATOR_TEXT ), transformer );
	}

	public static Text join( Collection<? extends Text> texts, Text separator ) {
		return join( texts, separator, Function.identity() );
	}

	public static <T> Text join( Collection<? extends T> elements, Text separator, Function<T, Text> transformer ) {
		if ( elements.isEmpty() )
			return new LiteralText( "" );
		else if ( elements.size() == 1 )
			return transformer.apply( elements.iterator().next() ).copy();
		else {
			Text mutableText = new LiteralText( "" );
			boolean bl = true;

			for ( T object : elements ) {
				if ( !bl )
					mutableText.append( separator );

				mutableText.append( transformer.apply( object ) );
				bl = false;
			}

			return mutableText;
		}
	}
}

