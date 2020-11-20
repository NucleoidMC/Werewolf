package io.github.haykam821.werewolf.game.event;

import io.github.haykam821.werewolf.game.player.AbstractPlayerEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import xyz.nucleoid.plasmid.game.event.EventType;

public interface PlayerEntryObtainer {
	EventType<PlayerEntryObtainer> EVENT = EventType.create(PlayerEntryObtainer.class, listeners -> {
		return player -> {
			for (PlayerEntryObtainer listener : listeners) {
				AbstractPlayerEntry entry = listener.obtainPlayerEntry(player);
				if (entry != null) {
					return entry;
				}
			}
			return null;
		};
	});

	AbstractPlayerEntry obtainPlayerEntry(ServerPlayerEntity player);
}