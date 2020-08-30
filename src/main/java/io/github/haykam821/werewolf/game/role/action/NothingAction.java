package io.github.haykam821.werewolf.game.role.action;

import io.github.haykam821.werewolf.game.PlayerEntry;

public abstract class NothingAction extends Action {
	@Override
	public void execute(PlayerEntry user) {
		return;
	}
}