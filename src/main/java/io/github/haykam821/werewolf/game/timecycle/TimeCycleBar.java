package io.github.haykam821.werewolf.game.timecycle;

import io.github.haykam821.werewolf.game.phase.WerewolfActivePhase;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.text.Text;
import xyz.nucleoid.plasmid.game.common.GlobalWidgets;
import xyz.nucleoid.plasmid.game.common.widget.BossBarWidget;

public class TimeCycleBar {
	private static final BossBar.Style STYLE = BossBar.Style.PROGRESS;
	
	private final WerewolfActivePhase phase;
	private final BossBarWidget widget;

	public TimeCycleBar(WerewolfActivePhase phase, GlobalWidgets widgets) {
		this.phase = phase;
		this.widget = widgets.addBossBar(this.getTitle(), this.getColor(), STYLE);
	}

	public void tick() {
		this.widget.setProgress(this.phase.getTicksUntilSwitch() / (float) this.phase.getConfig().getMaxTimeCycleLength());
	}

	public void changeTimeCycle() {
		this.widget.setTitle(this.getTitle());
		this.widget.setStyle(this.getColor(), STYLE);
	}

	public void remove() {
		this.widget.close();
	}

	private Text getTitle() {
		return this.phase.getTimeCycle().getName();
	}

	private BossBar.Color getColor() {
		return this.phase.getTimeCycle() == TimeCycle.NIGHT ? BossBar.Color.BLUE : BossBar.Color.YELLOW;
	}
}