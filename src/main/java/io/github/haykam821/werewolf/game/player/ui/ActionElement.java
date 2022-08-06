package io.github.haykam821.werewolf.game.player.ui;

import eu.pb4.sgui.api.GuiHelpers;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementInterface.ClickCallback;
import io.github.haykam821.werewolf.game.player.PlayerEntry;
import io.github.haykam821.werewolf.game.role.action.Action;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public final class ActionElement {
	private ActionElement() {
		return;
	}

	protected static GuiElement of(Action action, PlayerEntry entry) {
		GuiElementBuilder builder = GuiElementBuilder.from(action.getDisplayStack(entry))
			.setName(action.getName())
			.setCallback(ActionElement.createCallback(action, entry));

		for (Text lore : action.getLore()) {
			builder.addLoreLine(lore);
		}

		return builder.build();
	}

	private static ClickCallback createCallback(Action action, PlayerEntry entry) {
		return (index, type, slotAction, guiInterface) -> {
			action.use(entry);
			ActionElement.playClickSound(entry);
			GuiHelpers.sendPlayerScreenHandler(entry.getPlayer());
		};
	}

	protected static void playClickSound(PlayerEntry entry) {
		entry.getPlayer().playSound(SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.MASTER, 1, 1);
	}
}
