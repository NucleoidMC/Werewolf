package io.github.haykam821.werewolf.game.player;

import java.util.UUID;

import io.github.haykam821.werewolf.game.phase.WerewolfActivePhase;
import io.github.haykam821.werewolf.game.role.Role;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import xyz.nucleoid.map_templates.BlockBounds;

public class FakePlayerEntry extends AbstractPlayerEntry {
	private final UUID uuid = UUID.randomUUID();
	
	public FakePlayerEntry(WerewolfActivePhase phase, Role role, boolean cursed) {
		super(phase, role, cursed);
	}
	
	@Override
	public void sendDirectMessage(String key, Object... args) {
		System.out.println("Direct message to " + this.uuid.toString() + " with key: " + key);
	}

	@Override
	public void spawn(ServerWorld world, BlockBounds spawnBounds) {
		this.sendDirectMessage("text.werewolf.role.initial");
	}

	@Override
	public void changeRole(Role role) {
		this.role.update(this);
		super.changeRole(role);
	}

	@Override
	public Text getName() {
		return Text.translatable("text.werewolf.fake_player", this.uuid.toString());
	}

	@Override
	public String toString() {
		return "FakePlayerEntry{uuid=" + this.uuid + ", role=" + this.role + ", remainingActions=" + this.remainingActions + "}";
	}
}
