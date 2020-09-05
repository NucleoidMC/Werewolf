package io.github.haykam821.werewolf.game.role;

import java.util.ArrayList;
import java.util.List;

import io.github.haykam821.werewolf.game.PlayerEntry;
import io.github.haykam821.werewolf.game.role.action.Action;
import io.github.haykam821.werewolf.game.role.action.GiveTotemAction;
import io.github.haykam821.werewolf.game.role.action.Totem;

public class ShamanRole extends VillagerRole {
	@Override
	public List<Action> getNightActions(PlayerEntry user) {
		List<Action> actions = new ArrayList<>();
		Totem totem = Totem.getRandom(user.getPhase().getGameWorld().getWorld().getRandom());

		for (PlayerEntry entry : user.getPhase().getPlayers()) {
			if (!user.equals(entry)) {
				actions.add(new GiveTotemAction(entry, totem));
			}
		}

		return actions;
	}
}