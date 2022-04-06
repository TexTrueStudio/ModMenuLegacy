package io.github.prospector.modmenu.gui;

import com.google.common.collect.ImmutableList;
import io.github.prospector.modmenu.config.ModMenuConfig;
import io.github.prospector.modmenu.config.ModMenuConfigManager;
import io.github.prospector.modmenu.gui.widget.AbstractButtonWidget;
import io.github.prospector.modmenu.util.ScreenTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Optional;

public class ModMenuOptionsScreen extends AbstractScreen {

	private Screen previous;
	private ButtonListWidget list;

	@SuppressWarnings("resource")
	public ModMenuOptionsScreen(Screen previous) {
		super();
		this.previous = previous;
	}


	public void init() {
		this.list = new ButtonListWidget(this.client, this.width, this.height, 32, this.height - 32, 25);
		this.list.addAll( ModMenuConfig.asOptions() );
		this.method_13411(this.list);
		this.method_13411( new AbstractButtonWidget(
			999,
			this.width / 2 - 100,
			this.height - 27,
			200,
			20,
			ScreenTexts.DONE,
			(button) -> {
				ModMenuConfigManager.save();
				this.client.openScreen(this.previous);
			}
		) { } );
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.renderBackground();
		this.list.render( mouseX, mouseY, delta );
		drawCenteredText( this.textRenderer, this.title, this.width / 2, 5, 0xffffff );
		super.render( mouseX, mouseY, delta );
		List<String> list = getHoveredButtonTooltip(this.list, mouseX, mouseY);
		if ( list != null )
			this.renderTooltip( list, mouseX, mouseY );
	}

	@Override
	public void removed() {
		ModMenuConfigManager.save();
	}

	public static List<String> getHoveredButtonTooltip(ButtonListWidget buttonList, int mouseX, int mouseY) {
		Optional<ButtonWidget> optional = buttonList.getHoveredButton( (double) mouseX, (double) mouseY );
		return (List<OrderedText>) (
				optional.isPresent() && optional.get() instanceof OrderableTooltip ?
						( (OrderableTooltip) optional.get() ).getOrderedTooltip()
						: ImmutableList.of());
	}
}
