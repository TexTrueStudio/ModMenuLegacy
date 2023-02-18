package org.thinkingstudio.legacycore.modmenu.config.option;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.option.GameOptions;
import org.thinkingstudio.legacycore.modmenu.gui.widget.AbstractButtonWidget;
import org.thinkingstudio.legacycore.modmenu.gui.widget.CyclingButtonWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class CyclingOption<T> extends Option {
	private final CyclingOption.Setter<T> setter;
	private final Function<GameOptions, T> getter;
	private final Supplier<CyclingButtonWidget.Builder<T>> buttonBuilderFactory;
	private Function<MinecraftClient, CyclingButtonWidget.TooltipFactory<T>> tooltips = client -> value -> ImmutableList.of();

	private CyclingOption(
		String key,
		Function<GameOptions, T> getter,
		Setter<T> setter,
		Supplier<CyclingButtonWidget.Builder<T>> buttonBuilderFactory
	) {
		super( key );
		this.getter = getter;
		this.setter = setter;
		this.buttonBuilderFactory = buttonBuilderFactory;
	}

	public static <T> CyclingOption<T> create(
		String key,
		List<T> values,
		Function<T, Text> valueToText,
		Function<GameOptions, T> getter,
		Setter<T> setter
	) {
		return new CyclingOption<>( key, getter, setter, () -> CyclingButtonWidget.builder( valueToText ).values( values ) );
	}

	public static <T> CyclingOption<T> create(
		String key,
		Supplier<List<T>> valuesSupplier,
		Function<T, Text> valueToText,
		Function<GameOptions, T> getter,
		Setter<T> setter
	) {
		return new CyclingOption<>(
			key,
			getter,
			setter,
			() -> CyclingButtonWidget.builder( valueToText ).values( valuesSupplier.get() )
		);
	}

	public static <T> CyclingOption<T> create(
		String key,
		List<T> defaults,
		List<T> alternatives,
		BooleanSupplier alternativeToggle,
		Function<T, Text> valueToText,
		Function<GameOptions, T> getter,
		Setter<T> setter
	) {
		return new CyclingOption<>(
			key,
			getter,
			setter,
			() -> CyclingButtonWidget.builder( valueToText ).values( alternativeToggle, defaults, alternatives )
		);
	}

	public static <T> CyclingOption<T> create(
		String key,
		T[] values,
		Function<T, Text> valueToText,
		Function<GameOptions, T> getter,
		Setter<T> setter
	) {
		return new CyclingOption<>( key, getter, setter, () -> CyclingButtonWidget.builder( valueToText ).values( values ) );
	}

	public static CyclingOption<Boolean> create(
		String key,
		Text on,
		Text off,
		Function<GameOptions, Boolean> getter,
		Setter<Boolean> setter
	) {
		return new CyclingOption<>( key, getter, setter, () -> CyclingButtonWidget.onOffBuilder( on, off ) );
	}

	public static CyclingOption<Boolean> create(
		String key,
		Function<GameOptions, Boolean> getter,
		Setter<Boolean> setter
	) {
		return new CyclingOption<>( key, getter, setter, CyclingButtonWidget::onOffBuilder );
	}

	public static CyclingOption<Boolean> create(
		String key,
		Text tooltip,
		Function<GameOptions, Boolean> getter,
		Setter<Boolean> setter
	) {
		return create( key, getter, setter ).tooltip(
			client -> value -> client.textRenderer
				.wrapLines( tooltip.asFormattedString(), 200 )
				.stream()
				.map( LiteralText::new )
				.collect( Collectors.toList() )
		);
	}

	public CyclingOption<T> tooltip( Function<MinecraftClient, CyclingButtonWidget.TooltipFactory<T>> tooltips ) {
		this.tooltips = tooltips;
		return this;
	}

	public AbstractButtonWidget createButton(GameOptions options, int id, int x, int y, int width ) {
		CyclingButtonWidget.TooltipFactory<T> tooltipFactory = this.tooltips.apply( MinecraftClient.getInstance() );
		return this.buttonBuilderFactory.get()
			.tooltip( tooltipFactory )
			.initially( this.getter.apply( options ) )
			.build(
				id,
				x,
				y,
				width,
				20,
				this.getDisplayPrefix(),
				( button, value ) -> {
					this.setter.accept( options, this, value );
					options.save();
				}
			);
	}

	@FunctionalInterface
	@Environment(EnvType.CLIENT)
	public interface Setter<T> {
		void accept( GameOptions gameOptions, Option option, T value );
	}
}

