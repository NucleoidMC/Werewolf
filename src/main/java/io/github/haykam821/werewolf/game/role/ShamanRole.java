package io.github.haykam821.werewolf.game.role;

import java.util.ArrayList;
import java.util.List;

import io.github.haykam821.werewolf.game.player.AbstractPlayerEntry;
import io.github.haykam821.werewolf.game.role.action.Action;
import io.github.haykam821.werewolf.game.role.action.GiveTotemAction;
import io.github.haykam821.werewolf.game.role.action.Totem;

public class ShamanRole extends VillagerRole {
	@Override
	public List<Action> getNightActions(AbstractPlayerEntry user) {
		List<Action> actions = new ArrayList<>();
		Totem totem = Totem.getRandom(user.getPhase().getWorld().getRandom());

		for (AbstractPlayerEntry entry : user.getPhase().getPlayers()) {
			if (!user.equals(entry)) {
				actions.add(new GiveTotemAction(entry, totem));
			}
		}

		return actions;
	}
}