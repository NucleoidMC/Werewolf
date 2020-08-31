package io.github.haykam821.werewolf.game;

import java.util.HashMap;
import java.util.Map;

import io.github.haykam821.werewolf.game.phase.WerewolfActivePhase;
import io.github.haykam821.werewolf.game.role.Role;
import io.github.haykam821.werewolf.game.role.action.Action;
import io.github.haykam821.werewolf.game.timecycle.TimeCycle;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import xyz.nucleoid.plasmid.util.BlockBounds;

public class PlayerEntry {
	private final WerewolfActivePhase phase;
	private final ServerPlayerEntity player;
	private int remainingActions;
	private Role role;
	private boolean cursed;
	private Map<ItemStack, Action> actionStacks = new HashMap<>();

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
		this.sendMessage(new TranslatableText("text.werewolf.role.change", this.role.getName()));
	}

	public boolean isCursed() {
		return this.cursed;
	}

	public Role getSeenRole() {
		return this.getRole().getSeenRole(this);
	}

	public Map<ItemStack, Action> getActionStacks() {
		return this.actionStacks;
	}

	public void putActionStack(ItemStack stack, Action action) {
		this.actionStacks.put(stack, action);
	}

	public void sendMessage(Text message) {
		this.getPlayer().sendMessage(message, false);
	}

	public void spawn(ServerWorld world, BlockBounds spawnBounds) {
		this.player.setGameMode(GameMode.ADVENTURE);
		this.role.reapply(this);

		Vec3d spawn = spawnBounds.getCenter();
		this.player.teleport(world, spawn.getX(), spawn.getY(), spawn.getZ(), 0, 0);

		this.sendMessage(new TranslatableText("text.werewolf.role.initial", this.role.getName()));
	}

	public Text getLynchRoleName() {
		Text roleName = this.getRole().getName();

		if (this.isCursed()) {
			return new TranslatableText("text.werewolf.cursed").append(" ").append(roleName);
		}
		return roleName;
	}

	@Override
	public String toString() {
		return "PlayerEntry{player=" + this.player + ", role=" + this.role + ", remainingActions=" + this.remainingActions + "}";
	}
}