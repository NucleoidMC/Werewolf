package io.github.haykam821.werewolf.game.role.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public enum Totem {
	PACIFISM("pacifism");

	private static final List<Totem> VALUES = new ArrayList<>();
	private final Text name;

	private Totem(String key) {
		this.name = new TranslatableText("totem." + key);
	}

	public Text getName() {
		return this.name;
	}

	public static Totem getRandom(Random random) {
		return VALUES.get(random.nextInt(VALUES.size()));
	}

	static {
		for (Totem totem : Totem.values()) {
			VALUES.add(totem);
		}
	}
}