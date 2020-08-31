package io.github.haykam821.werewolf.game.role;

import io.github.haykam821.werewolf.game.PlayerEntry;

public class CultistRole extends Role {
	@Override
	public Alignment getAlignment() {
		return Alignment.WOLF;
	}

	@Override
	public Role getSeenRole(PlayerEntry entry) {
		return Roles.VILLAGER.getRole();
	}
}