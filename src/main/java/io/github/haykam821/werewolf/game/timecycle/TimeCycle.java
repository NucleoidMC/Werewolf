package io.github.haykam821.werewolf.game.timecycle;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public enum TimeCycle {
	NIGHT("night", 18000),
	DAY("day", 6000);

	private final Text name;
	private final Text warnText;
	private final Text endText;
	private final int timeOfDay;

	private TimeCycle(String name, int timeOfDay) {
		this.name = new TranslatableText("timeCycle." + name);
		this.warnText = new TranslatableText("timeCycle." + name + ".warn");
		this.endText = new TranslatableText("timeCycle." + name + ".end");

		this.timeOfDay = timeOfDay;
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

	public int getTimeOfDay() {
		return this.timeOfDay;
	}
}