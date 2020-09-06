package io.github.haykam821.werewolf.game.role;

import java.util.ArrayList;
import java.util.List;

import io.github.haykam821.werewolf.game.PlayerEntry;
import io.github.haykam821.werewolf.game.role.action.Action;
import io.github.haykam821.werewolf.game.role.action.KillAction;

public class WolfRole extends Role {
	@Override
	public Alignment getAlignment() {
		return Alignment.WOLF;
	}

	@Override
	public List<Action> getNightActions(PlayerEntry user) {
		List<Action> actions = new ArrayList<>();

		for (PlayerEntry entry : user.getPhase().getPlayers()) {
			if (entry.getRole().getAlignment() != Alignment.WOLF) {
				actions.add(new KillAction(entry));
			}
		}

		return actions;
	}

	@Override
	public boolean canUseWolfChannel() {
		return true;
	}
}