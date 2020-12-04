package io.github.haykam821.werewolf.game.map;

import java.util.Set;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import xyz.nucleoid.plasmid.map.template.MapTemplate;
import xyz.nucleoid.plasmid.map.template.TemplateChunkGenerator;
import xyz.nucleoid.plasmid.util.BlockBounds;

public class WerewolfMap {
	private final MapTemplate template;
	private final BlockBounds spawn;
	private final Set<BlockBounds> campfires;

	public WerewolfMap(MapTemplate template, BlockBounds spawn, Set<BlockBounds> campfires) {
		this.template = template;
		this.spawn = spawn;
		this.campfires = campfires;
	}

	public BlockBounds getSpawn() {
		return this.spawn;
	}

	public Set<BlockBounds> getCampfires() {
		return this.campfires;
	}

	public ChunkGenerator createGenerator(MinecraftServer server) {
		return new TemplateChunkGenerator(server, this.template);
	}
}