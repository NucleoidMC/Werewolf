package io.github.haykam821.werewolf.game.role;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public enum Alignment {
	VILLAGER("villager"),
	WOLF("wolf");

	private Text name;

	private Alignment(String name) {
		this.name = new TranslatableText("alignment." + name);
	}

	public Text getName() {
		return this.name;
	}
}