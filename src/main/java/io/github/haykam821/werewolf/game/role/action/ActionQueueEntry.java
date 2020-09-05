package io.github.haykam821.werewolf.game.role.action;

import io.github.haykam821.werewolf.game.PlayerEntry;

public class ActionQueueEntry implements Comparable<ActionQueueEntry> {
	private final Action action;
	private final PlayerEntry user;

	public ActionQueueEntry(Action action, PlayerEntry user) {
		this.action = action;
		this.user = user;
	}

	public void execute() {
		this.action.execute(this.user);
	}

	@Override
	public int compareTo(ActionQueueEntry otherEntry) {
		return this.action.getPriority() - otherEntry.action.getPriority();
	}

	@Override
	public String toString() {
		return "ActionEntry{action=" + this.action + ", user=" + this.user + "}";
	}
}