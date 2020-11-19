package io.github.haykam821.werewolf.game.role.action;

import io.github.haykam821.werewolf.game.player.AbstractPlayerEntry;

public abstract class NothingAction extends Action {
	@Override
	public void execute(AbstractPlayerEntry user) {
		return;
	}
}