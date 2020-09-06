package io.github.haykam821.werewolf.game.channel;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import io.github.haykam821.werewolf.game.PlayerEntry;
import net.minecraft.server.network.ServerPlayerEntity;

public abstract class FilteredChannel extends Channel {
	private final List<PlayerEntry> players;
	private final Predicate<PlayerEntry> predicate;

	public FilteredChannel(List<PlayerEntry> players, Predicate<PlayerEntry> predicate) {
		this.players = players;
		this.predicate = predicate;
	}

	@Override
	public List<ServerPlayerEntity> getTargets() {
		return this.players.stream()
			.filter(this.predicate)
			.map(entry -> entry.getPlayer())
			.collect(Collectors.toList());
	}
}