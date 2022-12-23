package io.github.haykam821.werewolf.game.timecycle;

import net.minecraft.text.Text;

public enum TimeCycle {
	NIGHT("night", 18000),
	DAY("day", 6000);

	private final Text name;
	private final Text warnText;
	private final Text endText;
	private final int timeOfDay;

	private TimeCycle(String name, int timeOfDay) {
		this.name = Text.translatable("timeCycle." + name);
		this.warnText = Text.translatable("timeCycle." + name + ".warn");
		this.endText = Text.translatable("timeCycle." + name + ".end");

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