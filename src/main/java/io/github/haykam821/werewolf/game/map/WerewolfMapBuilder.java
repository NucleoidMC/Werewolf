package io.github.haykam821.werewolf.game.map;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.haykam821.werewolf.game.WerewolfConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import xyz.nucleoid.map_templates.BlockBounds;
import xyz.nucleoid.map_templates.MapTemplate;
import xyz.nucleoid.map_templates.MapTemplateSerializer;
import xyz.nucleoid.plasmid.game.GameOpenException;

public class WerewolfMapBuilder {
	private final WerewolfConfig config;

	public WerewolfMapBuilder(WerewolfConfig config) {
		this.config = config;
	}

	private MapTemplate getTemplate(MinecraftServer server) throws GameOpenException {
		try {
			return MapTemplateSerializer.loadFromResource(server, this.config.getMap());
		} catch (IOException exception) {
			throw new GameOpenException(new TranslatableText("text.werewolf.map_load_failed", this.config.getMap().toString()));
		}
	}

	public WerewolfMap create(MinecraftServer server) throws GameOpenException {
		MapTemplate template = this.getTemplate(server);

		BlockBounds spawn = template.getMetadata().getFirstRegionBounds("spawn");
		Set<BlockBounds> campfires = template.getMetadata().getRegionBounds("campfire").collect(Collectors.toSet());

		if (spawn == null) {
			return new WerewolfMap(template, BlockBounds.ofBlock(new BlockPos(0, 0, 0)), campfires);
		}

		return new WerewolfMap(template, spawn, campfires);
	}
}