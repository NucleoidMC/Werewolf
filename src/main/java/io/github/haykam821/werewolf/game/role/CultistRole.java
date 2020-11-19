package io.github.haykam821.werewolf.game.role;

import io.github.haykam821.werewolf.game.player.AbstractPlayerEntry;

public class CultistRole extends Role {
	@Override
	public Alignment getAlignment() {
		return Alignment.WOLF;
	}

	@Override
	public Role getSeenRole(AbstractPlayerEntry entry) {
		return Roles.VILLAGER.getRole();
	}
}