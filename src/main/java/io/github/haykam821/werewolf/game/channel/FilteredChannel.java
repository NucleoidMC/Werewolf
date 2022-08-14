package io.github.haykam821.werewolf.game.channel;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.base.Predicates;

import io.github.haykam821.werewolf.game.player.AbstractPlayerEntry;
import io.github.haykam821.werewolf.game.player.PlayerEntry;
import net.minecraft.server.network.ServerPlayerEntity;

public abstract class FilteredChannel extends ChatChannel {
	private final List<AbstractPlayerEntry> players;
	private final Predicate<AbstractPlayerEntry> predicate;

	public FilteredChannel(List<AbstractPlayerEntry> players, Predicate<AbstractPlayerEntry> predicate) {
		this.players = players;
		this.predicate = predicate;
	}

	@Override
	public List<ServerPlayerEntity> getTargets() {
		return this.players.stream()
			.filter(this.predicate)
			.map(entry -> {
				if (entry instanceof PlayerEntry) {
					return ((PlayerEntry) entry).getPlayer();
				}
				return null;
			})
			.filter(Predicates.notNull())
			.collect(Collectors.toList());
	}
}