package io.github.haykam821.werewolf.game.player;

import io.github.haykam821.werewolf.game.channel.Channel;
import io.github.haykam821.werewolf.game.channel.DirectChannel;
import io.github.haykam821.werewolf.game.phase.WerewolfActivePhase;
import io.github.haykam821.werewolf.game.role.Role;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import xyz.nucleoid.plasmid.util.BlockBounds;

public class PlayerEntry extends AbstractPlayerEntry {
	private final ServerPlayerEntity player;
	private final Channel channel;
	
	public PlayerEntry(WerewolfActivePhase phase, ServerPlayerEntity player, Role role, boolean cursed) {
		super(phase, role, cursed);

		this.player = player;
		this.channel = new DirectChannel(player);
	}

	public ServerPlayerEntity getPlayer() {
		return this.player;
	}
	
	@Override
	public void sendDirectMessage(String key, Object... args) {
		this.channel.sendMessage(key, args);
	}

	@Override
	public void spawn(ServerWorld world, BlockBounds spawnBounds) {
		this.player.setGameMode(GameMode.ADVENTURE);
		this.role.reapply(this);

		Vec3d spawn = spawnBounds.getCenter();
		this.player.teleport(world, spawn.getX(), spawn.getY(), spawn.getZ(), 0, 0);

		this.sendDirectMessage("text.werewolf.role.initial", this.role.getName());
	}

	@Override
	public void changeRole(Role role) {
		this.role.reapply(this);
		super.changeRole(role);
	}

	@Override
	public Text getName() {
		return this.getPlayer().getDisplayName();
	}

	@Override
	public String toString() {
		return "PlayerEntry{player=" + this.player + ", role=" + this.role + ", remainingActions=" + this.remainingActions + "}";
	}
}
