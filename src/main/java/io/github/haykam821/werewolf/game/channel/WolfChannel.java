package io.github.haykam821.werewolf.game.channel;

import java.util.List;

import io.github.haykam821.werewolf.game.player.AbstractPlayerEntry;
import net.minecraft.util.Formatting;

public class WolfChannel extends FilteredChannel {
	public WolfChannel(List<AbstractPlayerEntry> players) {
		super(players, entry -> {
			return entry.getRole().canUseWolfChannel();
		});
	}

	@Override
	public String getTranslationKey() {
		return "channel.wolf";
	}

	@Override
	public Formatting getPrefixColor() {
		return Formatting.DARK_GRAY;
	}
}