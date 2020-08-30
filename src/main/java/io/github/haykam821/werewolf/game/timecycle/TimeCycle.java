package io.github.haykam821.werewolf.game.timecycle;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public enum TimeCycle {
	NIGHT("night"),
	DAY("day");

	private Text name;
	private Text warnText;
	private Text endText;

	private TimeCycle(String name) {
		this.name = new TranslatableText("timeCycle." + name);
		this.warnText = new TranslatableText("timeCycle." + name + ".warn");
		this.endText = new TranslatableText("timeCycle." + name + ".end");
	}

	public Text getName() {
		return this.name;
	}

	public Text getWarnText() {
		return this.warnText;
	}

	public Text getEndText() {
		return this.endText;
	}
}