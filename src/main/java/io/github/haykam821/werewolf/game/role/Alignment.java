package io.github.haykam821.werewolf.game.role;

import net.minecraft.text.Text;

public enum Alignment {
	VILLAGER("villager"),
	WOLF("wolf");

	private Text name;

	private Alignment(String name) {
		this.name = Text.translatable("alignment." + name);
	}

	public Text getName() {
		return this.name;
	}
}