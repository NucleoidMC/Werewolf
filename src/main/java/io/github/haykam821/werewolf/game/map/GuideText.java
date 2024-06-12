package io.github.haykam821.werewolf.game.map;

import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.elements.TextDisplayElement;
import net.minecraft.entity.decoration.DisplayEntity.BillboardMode;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public final class GuideText {	
	private static final Formatting FORMATTING = Formatting.GOLD;

	private static final Text TEXT = Text.empty()
		.append(Text.translatable("gameType.werewolf.werewolf").formatted(Formatting.BOLD))
		.append(ScreenTexts.LINE_BREAK)
		.append(Text.translatable("text.werewolf.guide.villagers_eliminate_wolves"))
		.append(ScreenTexts.LINE_BREAK)
		.append(Text.translatable("text.werewolf.guide.wolves_hunt_villagers"))
		.append(ScreenTexts.LINE_BREAK)
		.append(Text.translatable("text.werewolf.guide.villagers_have_special_powers"))
		.formatted(FORMATTING);

	private GuideText() {
		return;
	}

	public static ElementHolder createElementHolder() {
		TextDisplayElement element = new TextDisplayElement(TEXT);

		element.setBillboardMode(BillboardMode.CENTER);
		element.setLineWidth(400);

		ElementHolder holder = new ElementHolder();
		holder.addElement(element);

		return holder;
	}
}
