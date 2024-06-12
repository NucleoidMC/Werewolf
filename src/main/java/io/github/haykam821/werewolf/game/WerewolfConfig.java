package io.github.haykam821.werewolf.game;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.Identifier;
import xyz.nucleoid.plasmid.game.common.config.PlayerConfig;

public class WerewolfConfig {
	public static final Codec<WerewolfConfig> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
			Identifier.CODEC.fieldOf("map").forGetter(WerewolfConfig::getMap),
			Codec.INT.optionalFieldOf("guide_ticks", 20 * 30).forGetter(WerewolfConfig::getGuideTicks),
			Codec.INT.optionalFieldOf("max_time_cycle_length", 12 * 60 * 20).forGetter(WerewolfConfig::getMaxTimeCycleLength),
			PlayerConfig.CODEC.fieldOf("players").forGetter(WerewolfConfig::getPlayerConfig)
		).apply(instance, WerewolfConfig::new);
	});

	private final Identifier map;
	private final int guideTicks;
	private final int maxTimeCycleLength;
	private final PlayerConfig playerConfig;

	public WerewolfConfig(Identifier map, int guideTicks, int maxTimeCycleLength, PlayerConfig playerConfig) {
		this.map = map;
		this.guideTicks = guideTicks;
		this.maxTimeCycleLength = maxTimeCycleLength;
		this.playerConfig = playerConfig;
	}

	public Identifier getMap() {
		return this.map;
	}

	public int getGuideTicks() {
		return this.guideTicks;
	}

	public int getMaxTimeCycleLength() {
		return this.maxTimeCycleLength;
	}

	public PlayerConfig getPlayerConfig() {
		return this.playerConfig;
	}
}