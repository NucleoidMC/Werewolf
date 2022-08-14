package io.github.haykam821.werewolf.game.role.action;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;

public enum Totem {
	DEATH("death"),
	PACIFISM("pacifism"),
	REVEALING("revealing"),
	SILENCE("silence");

	private static final List<Totem> VALUES = new ArrayList<>();
	private final Text name;

	private Totem(String key) {
		this.name = Text.translatable("totem." + key);
	}

	public Text getName() {
		return this.name;
	}

	public static Totem getRandom(Random random) {
		return Util.getRandom(VALUES, random);
	}

	static {
		for (Totem totem : Totem.values()) {
			VALUES.add(totem);
		}
	}
}