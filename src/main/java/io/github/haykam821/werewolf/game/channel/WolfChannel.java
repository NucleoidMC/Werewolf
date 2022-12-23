package io.github.haykam821.werewolf.game.channel;

import java.util.List;

import io.github.haykam821.werewolf.Main;
import io.github.haykam821.werewolf.game.player.AbstractPlayerEntry;
import net.minecraft.network.message.MessageType;
import net.minecraft.util.Formatting;
import net.minecraft.util.registry.RegistryKey;

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

	@Override
	public RegistryKey<MessageType> getMessageType() {
		return Main.WOLF_CHANNEL_KEY;
	}
}