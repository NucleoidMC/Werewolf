package io.github.haykam821.werewolf.game.channel;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.network.ServerPlayerEntity;
import xyz.nucleoid.plasmid.game.player.PlayerSet;

public class GameChannel extends Channel {
	private final PlayerSet playerSet;
	
	public GameChannel(PlayerSet playerSet) {
		this.playerSet = playerSet;
	}

	@Override
	public String getTranslationKey() {
		return "channel.game";
	}

	@Override
	public List<ServerPlayerEntity> getTargets() {
		List<ServerPlayerEntity> targets = new ArrayList<>();
		for (ServerPlayerEntity player : this.playerSet) {
			targets.add(player);
		}
		return targets;
	}
}