package io.github.haykam821.werewolf.game.player;

import java.util.ArrayList;
import java.util.List;

import io.github.haykam821.werewolf.game.phase.WerewolfActivePhase;
import io.github.haykam821.werewolf.game.role.Role;
import io.github.haykam821.werewolf.game.role.action.Action;
import io.github.haykam821.werewolf.game.role.action.Totem;
import io.github.haykam821.werewolf.game.timecycle.TimeCycle;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import xyz.nucleoid.map_templates.BlockBounds;

public abstract class AbstractPlayerEntry {
	private final WerewolfActivePhase phase;
	protected int remainingActions;
	protected Role role;
	private boolean cursed;
	private List<Action> actions = new ArrayList<>();
	private final List<Totem> totems = new ArrayList<>();

	public AbstractPlayerEntry(WerewolfActivePhase phase, Role role, boolean cursed) {
		this.phase = phase;
		this.role = role;
		this.cursed = cursed;
	}

	public WerewolfActivePhase getPhase() {
		return this.phase;
	}

	public List<Action> getActions() {
		return this.actions;
	}

	public int getRemainingActions() {
		return this.remainingActions;
	}

	public void decrementRemainingActions() {
		this.remainingActions -= 1;
	}

	public void setRemainingActions(int remainingActions) {
		this.remainingActions = remainingActions;
	}

	public void resetRemainingActions() {
		if (this.getPhase().getTimeCycle() == TimeCycle.NIGHT) {
			this.remainingActions = this.getRole().getMaxNightActions(this);
		} else {
			this.remainingActions = this.getRole().getMaxDayActions(this);
		}
	}

	public Role getRole() {
		return this.role;
	}

	public void changeRole(Role role) {
		if (!role.canBeCursed()) {
			this.cursed = false;
		}

		this.role = role;
		this.sendDirectMessage("text.werewolf.role.change", this.role.getName());
	}

	public boolean isCursed() {
		return this.cursed;
	}

	public Role getSeenRole() {
		return this.getRole().getSeenRole(this);
	}

	public Action getAction(int index) {
		if (index < 0 || index >= this.actions.size()) {
			return null;
		}
		return this.actions.get(index);
	}

	public void putAction(Action action) {
		if (!action.isNormal() && this.hasTotem(Totem.SILENCE)) {
			return;
		}
		this.actions.add(action);
	}

	public void clearActions() {
		this.actions.clear();
	}

	public boolean hasTotem(Totem totem) {
		return this.totems.contains(totem);
	}

	public void putTotem(Totem totem) {
		this.totems.add(totem);
	}

	public void clearTotems() {
		this.totems.clear();
	}

	public abstract void sendDirectMessage(String key, Object... args);

	public void spawn(ServerWorld world, BlockBounds spawnBounds) {
		return;
	}

	public Text getLynchRoleName() {
		Text roleName = this.getRole().getName();

		if (this.isCursed()) {
			return Text.translatable("text.werewolf.cursed").append(" ").append(roleName);
		}
		return roleName;
	}

	public abstract Text getName();

	public void onTimeCycleChanged(TimeCycle oldTimeCycle) {

	}

	public void onLynched() {
		// Prevent lynch via reveal
		if (this.hasTotem(Totem.REVEALING)) {
			this.phase.sendGameMessage("action.lynch.announce.reveal", this.getName(), this.getLynchRoleName());
			return;
		}

		this.phase.eliminate(this);
		this.phase.sendGameMessage("action.lynch.announce", this.getName(), this.getLynchRoleName());
	}

	@Override
	public String toString() {
		return "AbstractPlayerEntry{role=" + this.role + ", remainingActions=" + this.remainingActions + "}";
	}
}