package io.github.haykam821.werewolf.game.event;

import io.github.haykam821.werewolf.game.player.AbstractPlayerEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import xyz.nucleoid.stimuli.event.StimulusEvent;

public interface PlayerEntryObtainer {
	StimulusEvent<PlayerEntryObtainer> EVENT = StimulusEvent.create(PlayerEntryObtainer.class, context -> {
		return player -> {
			for (PlayerEntryObtainer listener : context.getListeners()) {
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