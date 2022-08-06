package io.github.haykam821.werewolf.game.role;

import java.util.ArrayList;
import java.util.List;

import io.github.haykam821.werewolf.game.player.AbstractPlayerEntry;
import io.github.haykam821.werewolf.game.player.PlayerEntry;
import io.github.haykam821.werewolf.game.player.ui.ActionUi;
import io.github.haykam821.werewolf.game.role.action.AbstainAction;
import io.github.haykam821.werewolf.game.role.action.Action;
import io.github.haykam821.werewolf.game.role.action.LynchAction;
import io.github.haykam821.werewolf.game.timecycle.TimeCycle;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import xyz.nucleoid.plasmid.registry.TinyRegistry;

public abstract class Role {
	public static final TinyRegistry<Role> REGISTRY = TinyRegistry.create();

	private String translationKey;

	private String getTranslationKey() {
		if (this.translationKey == null) {
			this.translationKey = Util.createTranslationKey("role", REGISTRY.getIdentifier(this));
		}
		return this.translationKey;
	}

	public Text getName() {
		return new TranslatableText(this.getTranslationKey());
	}

	public void clear(AbstractPlayerEntry entry) {
		entry.clearActions();
	}

	private List<Action> getActions(AbstractPlayerEntry entry) {
		TimeCycle timeCycle = entry.getPhase().getTimeCycle();
		return timeCycle == TimeCycle.NIGHT ? this.getNightActions(entry) : this.getDayActions(entry);
	}

	public void update(AbstractPlayerEntry entry) {
		this.clear(entry);

		if (entry.getRemainingActions() > 0) {
			List<Action> actions = this.getActions(entry);
			if (actions.size() < entry.getRemainingActions()) {
				entry.setRemainingActions(actions.size());
			}

			for (Action action : actions) {
				entry.putAction(action);
			}
		}

		if (entry instanceof PlayerEntry) {
			ActionUi ui = ((PlayerEntry) entry).getUi();
			ui.update();
		}
	}

	public int getMaxDayActions(AbstractPlayerEntry user) {
		return 1;
	}

	public List<Action> getDayActions(AbstractPlayerEntry user) {
		List<Action> actions = new ArrayList<>();
		actions.add(new AbstainAction());

		for (AbstractPlayerEntry entry : user.getPhase().getPlayers()) {
			if (!user.equals(entry)) {
				actions.add(new LynchAction(entry));
			}
		}

		return actions;
	}

	public int getMaxNightActions(AbstractPlayerEntry user) {
		return 1;
	}

	public List<Action> getNightActions(AbstractPlayerEntry user) {
		return new ArrayList<>();
	}

	public abstract Alignment getAlignment();

	public Role getSeenRole(AbstractPlayerEntry entry) {
		return entry.isCursed() ? Roles.WOLF.getRole() : this;
	}

	public boolean canBeCursed() {
		return this.getAlignment() != Alignment.WOLF;
	}

	public boolean canUseWolfChannel() {
		return false;
	}
}