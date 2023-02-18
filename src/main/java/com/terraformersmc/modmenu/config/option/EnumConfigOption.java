package com.terraformersmc.modmenu.config.option;

import org.thinkingstudio.legacycore.modmenu.config.option.CyclingOption;
import org.thinkingstudio.legacycore.modmenu.config.option.Option;
import org.thinkingstudio.legacycore.modmenu.config.option.OptionConvertable;
import org.thinkingstudio.legacycore.modmenu.util.ScreenTexts;
import com.terraformersmc.modmenu.util.TranslationUtil;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.Locale;

public class EnumConfigOption<E extends Enum<E>> implements OptionConvertable {
	private final String key, translationKey;
	private final Class<E> enumClass;
	private final E defaultValue;

	public EnumConfigOption( String key, E defaultValue ) {
		this.translationKey = TranslationUtil.translationKeyOf( "option", key );
		this.enumClass = defaultValue.getDeclaringClass();
		ConfigOptionStorage.setEnum( key, defaultValue );
		this.defaultValue = defaultValue;
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public E getValue() {
		return ConfigOptionStorage.getEnum( key, enumClass );
	}

	public void setValue( E value ) {
		ConfigOptionStorage.setEnum( key, value );
	}

	public void cycleValue() {
		ConfigOptionStorage.cycleEnum( key, enumClass );
	}

	public void cycleValue( int amount ) {
		ConfigOptionStorage.cycleEnum( key, enumClass, amount );
	}

	public E getDefaultValue() {
		return defaultValue;
	}

	private static <E extends Enum<E>> Text getValueText( EnumConfigOption<E> option, E value ) {
		return new TranslatableText( option.translationKey + "." + value.name().toLowerCase( Locale.ROOT ) );
	}

	public Text getButtonText() {
		return ScreenTexts.composeGenericOptionText( new TranslatableText( translationKey ), getValueText( this, getValue() ) );
	}

	@Override
	public Option asOption() {
		return CyclingOption.create(
			translationKey,
			enumClass.getEnumConstants(),
			value -> getValueText( this, value ),
			ignored -> ConfigOptionStorage.getEnum( key, enumClass ),
			( ignored, option, value ) -> ConfigOptionStorage.setEnum( key, value )
		);
	}
}
