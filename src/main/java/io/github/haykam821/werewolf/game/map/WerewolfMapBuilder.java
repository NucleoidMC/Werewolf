package io.github.haykam821.werewolf.game.map;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import io.github.haykam821.werewolf.game.WerewolfConfig;
import net.minecraft.util.math.BlockPos;
import xyz.nucleoid.plasmid.game.map.template.MapTemplateSerializer;
import xyz.nucleoid.plasmid.util.BlockBounds;

public class WerewolfMapBuilder {
	private final WerewolfConfig config;

	public WerewolfMapBuilder(WerewolfConfig config) {
		this.config = config;
	}

	public CompletableFuture<WerewolfMap> create() {
		return MapTemplateSerializer.INSTANCE.load(this.config.getMap()).thenApply(template -> {
			BlockBounds spawn = template.getFirstRegion("spawn");
			Set<BlockBounds> campfires = template.getRegions("campfire").collect(Collectors.toSet());

			if (spawn == null) {
				return new WerewolfMap(template, BlockBounds.of(new BlockPos(0, 0, 0)), campfires);
			}

			return new WerewolfMap(template, spawn, campfires);
		});
	}
}