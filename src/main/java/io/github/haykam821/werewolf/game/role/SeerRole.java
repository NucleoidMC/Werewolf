package io.github.haykam821.werewolf.game.role;

import java.util.ArrayList;
import java.util.List;

import io.github.haykam821.werewolf.game.PlayerEntry;
import io.github.haykam821.werewolf.game.role.action.Action;
import io.github.haykam821.werewolf.game.role.action.SeeAction;

public class SeerRole extends VillagerRole {
	@Override
	public List<Action> getNightActions(PlayerEntry user) {
		List<Action> actions = new ArrayList<>();

		for (PlayerEntry entry : user.getPhase().getPlayers()) {
			if (!user.equals(entry)) {
				actions.add(new SeeAction(entry));
			}
		}

		return actions;
	}
}