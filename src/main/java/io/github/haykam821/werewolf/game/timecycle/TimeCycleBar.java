package io.github.haykam821.werewolf.game.timecycle;

import io.github.haykam821.werewolf.game.phase.WerewolfActivePhase;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.text.Text;
import net.minecraft.util.Tickable;
import xyz.nucleoid.plasmid.widget.BossBarWidget;

public class TimeCycleBar implements Tickable {
	private static final BossBar.Style STYLE = BossBar.Style.PROGRESS;
	
	private final WerewolfActivePhase phase;
	private final BossBarWidget widget;

	public TimeCycleBar(WerewolfActivePhase phase) {
		this.phase = phase;

		this.widget = BossBarWidget.open(this.phase.getGameWorld().getPlayerSet(), this.getTitle(), this.getColor(), STYLE);
		this.phase.getGameWorld().addResource(this.widget);
	}

	@Override
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