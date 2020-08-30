package io.github.haykam821.werewolf.game;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.Identifier;
import xyz.nucleoid.plasmid.game.config.PlayerConfig;

public class WerewolfConfig {
	public static final Codec<WerewolfConfig> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
			Identifier.CODEC.fieldOf("map").forGetter(WerewolfConfig::getMap),
			Codec.INT.optionalFieldOf("max_time_cycle_length", 12 * 60 * 20).forGetter(WerewolfConfig::getMaxTimeCycleLength),
			PlayerConfig.CODEC.fieldOf("players").forGetter(WerewolfConfig::getPlayerConfig)
		).apply(instance, WerewolfConfig::new);
	});

	private final Identifier map;
	private final PlayerConfig playerConfig;
	private final int maxTimeCycleLength;

	public WerewolfConfig(Identifier map, int maxTimeCycleLength, PlayerConfig playerConfig) {
		this.map = map;
		this.maxTimeCycleLength = maxTimeCycleLength;
		this.playerConfig = playerConfig;
	}

	public Identifier getMap() {
		return this.map;
	}

	public int getMaxTimeCycleLength() {
		return this.maxTimeCycleLength;
	}

	public PlayerConfig getPlayerConfig() {
		return this.playerConfig;
	}
}