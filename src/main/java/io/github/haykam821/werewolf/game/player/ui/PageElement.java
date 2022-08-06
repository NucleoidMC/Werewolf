package io.github.haykam821.werewolf.game.player.ui;

import eu.pb4.sgui.api.GuiHelpers;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementInterface.ClickCallback;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public final class PageElement {
	private PageElement() {
		return;
	}

	protected static GuiElement ofPrevious(ActionUi ui) {
		return PageElement.of(ui, "previous", -1, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzEwODI5OGZmMmIyNjk1MWQ2ODNlNWFkZTQ2YTQyZTkwYzJmN2M3ZGQ0MWJhYTkwOGJjNTg1MmY4YzMyZTU4MyJ9fX0");
	}

	protected static GuiElement ofNext(ActionUi ui) {
		return PageElement.of(ui, "next", 1, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzg2MTg1YjFkNTE5YWRlNTg1ZjE4NGMzNGYzZjNlMjBiYjY0MWRlYjg3OWU4MTM3OGU0ZWFmMjA5Mjg3In19fQ");
	}

	private static GuiElement of(ActionUi ui, String type, int offset, String texture) {
		return new GuiElementBuilder(Items.PLAYER_HEAD)
			.setName(PageElement.getToolbarName("spectatorMenu." + type + "_page"))
			.setSkullOwner(texture)
			.setCallback(PageElement.createCallback(ui, offset))
			.build();
	}

	private static Text getToolbarName(String translationKey) {
		return new TranslatableText(translationKey).formatted(Formatting.YELLOW);
	}

	private static ClickCallback createCallback(ActionUi ui, int offset) {
		return (index, type, action, guiInterface) -> {
			ui.movePage(offset);
			PageElement.playClickSound(ui);
			GuiHelpers.sendPlayerScreenHandler(ui.getPlayer());
		};
	}

	private static void playClickSound(ActionUi ui) {
		ui.getPlayer().playSound(SoundEvents.UI_BUTTON_CLICK, SoundCategory.MASTER, 1, 1);
	}
}