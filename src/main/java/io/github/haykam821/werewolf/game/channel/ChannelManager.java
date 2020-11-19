package io.github.haykam821.werewolf.game.channel;

import java.util.List;

import io.github.haykam821.werewolf.game.player.AbstractPlayerEntry;
import xyz.nucleoid.plasmid.game.GameWorld;

public class ChannelManager {
	private final Channel gameChannel;
	private final Channel wolfChannel;

	public ChannelManager(GameWorld gameWorld, List<AbstractPlayerEntry> players) {
		this.gameChannel = new GameChannel(gameWorld.getPlayerSet());
		this.wolfChannel = new WolfChannel(players);
	}

	public Channel getGameChannel() {
		return this.gameChannel;
	}

	public Channel getWolfChannel() {
		return this.wolfChannel;
	}
}
