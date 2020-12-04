package io.github.haykam821.werewolf.game.channel;

import java.util.List;

import io.github.haykam821.werewolf.game.player.AbstractPlayerEntry;
import xyz.nucleoid.plasmid.game.GameSpace;

public class ChannelManager {
	private final Channel gameChannel;
	private final Channel wolfChannel;

	public ChannelManager(GameSpace gameSpace, List<AbstractPlayerEntry> players) {
		this.gameChannel = new GameChannel(gameSpace.getPlayers());
		this.wolfChannel = new WolfChannel(players);
	}

	public Channel getGameChannel() {
		return this.gameChannel;
	}

	public Channel getWolfChannel() {
		return this.wolfChannel;
	}
}
