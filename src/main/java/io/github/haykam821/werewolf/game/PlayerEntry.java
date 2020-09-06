package io.github.haykam821.werewolf.game;

import java.util.ArrayList;
import java.util.List;

import io.github.haykam821.werewolf.game.phase.WerewolfActivePhase;
import io.github.haykam821.werewolf.game.role.Role;
import io.github.haykam821.werewolf.game.role.action.Action;
import io.github.haykam821.werewolf.game.role.action.Totem;
import io.github.haykam821.werewolf.game.timecycle.TimeCycle;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import xyz.nucleoid.plasmid.util.BlockBounds;

public class PlayerEntry {
	private final WerewolfActivePhase phase;
	private final ServerPlayerEntity player;
	private int remainingActions;
	private Role role;
	private boolean cursed;
	private List<Action> actions = new ArrayList<>();
	private final List<Totem> totems = new ArrayList<>();

	public PlayerEntry(WerewolfActivePhase phase, ServerPlayerEntity player, Role role, boolean cursed) {
		this.phase = phase;
		this.player = player;
		this.role = role;
		this.cursed = cursed;
	}

	public WerewolfActivePhase getPhase() {
		return this.phase;
	}

	public ServerPlayerEntity getPlayer() {
		return this.player;
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
		this.role.reapply(this);

		if (!role.canBeCursed()) {
			this.cursed = false;
		}

		this.role = role;
		this.sendMessage(new TranslatableText("text.werewolf.role.change", this.role.getName()).formatted(Formatting.BLUE));
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

	public void sendMessage(Text message) {
		this.getPlayer().sendMessage(message, false);
	}

	public void spawn(ServerWorld world, BlockBounds spawnBounds) {
		this.player.setGameMode(GameMode.ADVENTURE);
		this.role.reapply(this);

		Vec3d spawn = spawnBounds.getCenter();
		this.player.teleport(world, spawn.getX(), spawn.getY(), spawn.getZ(), 0, 0);

		this.sendMessage(new TranslatableText("text.werewolf.role.initial", this.role.getName()).formatted(Formatting.BLUE));
	}

	public Text getLynchRoleName() {
		Text roleName = this.getRole().getName();

		if (this.isCursed()) {
			return new TranslatableText("text.werewolf.cursed").append(" ").append(roleName);
		}
		return roleName;
	}

	public Text getName() {
		return this.getPlayer().getDisplayName();
	}

	@Override
	public String toString() {
		return "PlayerEntry{player=" + this.player + ", role=" + this.role + ", remainingActions=" + this.remainingActions + "}";
	}
}