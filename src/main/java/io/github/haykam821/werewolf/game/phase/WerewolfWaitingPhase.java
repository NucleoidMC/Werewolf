package io.github.haykam821.werewolf.game.phase;

import java.util.concurrent.CompletableFuture;

import io.github.haykam821.werewolf.game.WerewolfConfig;
import io.github.haykam821.werewolf.game.map.WerewolfMap;
import io.github.haykam821.werewolf.game.map.WerewolfMapBuilder;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import xyz.nucleoid.plasmid.game.GameOpenContext;
import xyz.nucleoid.plasmid.game.GameWorld;
import xyz.nucleoid.plasmid.game.StartResult;
import xyz.nucleoid.plasmid.game.config.PlayerConfig;
import xyz.nucleoid.plasmid.game.event.OfferPlayerListener;
import xyz.nucleoid.plasmid.game.event.PlayerAddListener;
import xyz.nucleoid.plasmid.game.event.PlayerDamageListener;
import xyz.nucleoid.plasmid.game.event.PlayerDeathListener;
import xyz.nucleoid.plasmid.game.event.RequestStartListener;
import xyz.nucleoid.plasmid.game.player.JoinResult;
import xyz.nucleoid.plasmid.world.bubble.BubbleWorldConfig;

public class WerewolfWaitingPhase {
	private final GameWorld gameWorld;
	private final WerewolfMap map;
	private final WerewolfConfig config;

	public WerewolfWaitingPhase(GameWorld gameWorld, WerewolfMap map, WerewolfConfig config) {
		this.gameWorld = gameWorld;
		this.map = map;
		this.config = config;
	}

	public static CompletableFuture<GameWorld> open(GameOpenContext<WerewolfConfig> context) {
		WerewolfMapBuilder mapBuilder = new WerewolfMapBuilder(context.getConfig());

		return mapBuilder.create().thenCompose(map -> {
			BubbleWorldConfig worldConfig = new BubbleWorldConfig()
				.setGenerator(map.createGenerator(context.getServer()))
				.setDefaultGameMode(GameMode.ADVENTURE)
				.setTimeOfDay(10000);

			return context.openWorld(worldConfig).thenApply(gameWorld -> {
				WerewolfWaitingPhase phase = new WerewolfWaitingPhase(gameWorld, map, context.getConfig());

				gameWorld.openGame(game -> {
					WerewolfActivePhase.setRules(game);

					// Listeners
					game.on(PlayerAddListener.EVENT, phase::addPlayer);
					game.on(PlayerDamageListener.EVENT, phase::onPlayerDamage);
					game.on(PlayerDeathListener.EVENT, phase::onPlayerDeath);
					game.on(OfferPlayerListener.EVENT, phase::offerPlayer);
					game.on(RequestStartListener.EVENT, phase::requestStart);
				});

				return gameWorld;
			});
		});
	}

	private boolean isFull() {
		return this.gameWorld.getPlayerCount() >= this.config.getPlayerConfig().getMaxPlayers();
	}

	public JoinResult offerPlayer(ServerPlayerEntity player) {
		return this.isFull() ? JoinResult.gameFull() : JoinResult.ok();
	}

	public StartResult requestStart() {
		PlayerConfig playerConfig = this.config.getPlayerConfig();
		if (this.gameWorld.getPlayerCount() < playerConfig.getMinPlayers()) {
			return StartResult.NOT_ENOUGH_PLAYERS;
		}

		WerewolfActivePhase.open(this.gameWorld, this.map, this.config);
		return StartResult.OK;
	}

	public void addPlayer(ServerPlayerEntity player) {
		this.spawn(player);
	}

	private boolean onPlayerDamage(ServerPlayerEntity player, DamageSource source, float amount) {
		return true;
	}

	public ActionResult onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
		// Respawn player
		this.spawn(player);
		return ActionResult.SUCCESS;
	}

	private void spawn(ServerPlayerEntity player) {
		Vec3d spawn = this.map.getSpawn().getCenter();
		player.teleport(this.gameWorld.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ(), 0, 0);
	}
}