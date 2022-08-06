package io.github.haykam821.werewolf.game.player;

import io.github.haykam821.werewolf.game.channel.Channel;
import io.github.haykam821.werewolf.game.channel.DirectChannel;
import io.github.haykam821.werewolf.game.phase.WerewolfActivePhase;
import io.github.haykam821.werewolf.game.player.ui.ActionUi;
import io.github.haykam821.werewolf.game.role.Role;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import xyz.nucleoid.map_templates.BlockBounds;

public class PlayerEntry extends AbstractPlayerEntry {
	private final ServerPlayerEntity player;
	private final Channel channel;
	private final ActionUi ui;
	
	public PlayerEntry(WerewolfActivePhase phase, ServerPlayerEntity player, Role role, boolean cursed) {
		super(phase, role, cursed);

		this.player = player;
		this.channel = new DirectChannel(player);
		this.ui = new ActionUi(this);
	}

	public ServerPlayerEntity getPlayer() {
		return this.player;
	}

	public ActionUi getUi() {
		return this.ui;
	}
	
	@Override
	public void sendDirectMessage(String key, Object... args) {
		this.channel.sendMessage(key, args);
	}

	@Override
	public void spawn(ServerWorld world, BlockBounds spawnBounds) {
		this.player.changeGameMode(GameMode.ADVENTURE);
		this.role.update(this);
		this.ui.open();

		Vec3d spawn = spawnBounds.center();
		this.player.teleport(world, spawn.getX(), spawn.getY(), spawn.getZ(), 0, 0);

		this.sendDirectMessage("text.werewolf.role.initial", this.role.getName());
	}

	@Override
	public void changeRole(Role role) {
		this.role.update(this);
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
