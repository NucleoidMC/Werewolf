package io.github.haykam821.werewolf.game.channel;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;

public class DirectChannel extends Channel {
	private final ServerPlayerEntity player;

	public DirectChannel(ServerPlayerEntity player) {
		this.player = player;
	}

	@Override
	public String getTranslationKey() {
		return "channel.direct";
	}

	@Override
	public Formatting getPrefixColor() {
		return Formatting.DARK_AQUA;
	}

	@Override
	public List<ServerPlayerEntity> getTargets() {
		return Lists.newArrayList(this.player);
	}
}