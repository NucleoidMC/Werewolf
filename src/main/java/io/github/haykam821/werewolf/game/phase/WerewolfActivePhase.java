package io.github.haykam821.werewolf.game.phase;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.haykam821.werewolf.game.PlayerEntry;
import io.github.haykam821.werewolf.game.WerewolfConfig;
import io.github.haykam821.werewolf.game.map.WerewolfMap;
import io.github.haykam821.werewolf.game.role.Alignment;
import io.github.haykam821.werewolf.game.role.Role;
import io.github.haykam821.werewolf.game.role.Roles;
import io.github.haykam821.werewolf.game.role.action.Action;
import io.github.haykam821.werewolf.game.role.action.ActionQueueEntry;
import io.github.haykam821.werewolf.game.timecycle.TimeCycle;
import io.github.haykam821.werewolf.game.timecycle.TimeCycleBar;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import xyz.nucleoid.plasmid.game.Game;
import xyz.nucleoid.plasmid.game.GameWorld;
import xyz.nucleoid.plasmid.game.event.GameOpenListener;
import xyz.nucleoid.plasmid.game.event.GameTickListener;
import xyz.nucleoid.plasmid.game.event.PlayerAddListener;
import xyz.nucleoid.plasmid.game.event.PlayerDamageListener;
import xyz.nucleoid.plasmid.game.event.PlayerDeathListener;
import xyz.nucleoid.plasmid.game.event.UseItemListener;
import xyz.nucleoid.plasmid.game.rule.GameRule;
import xyz.nucleoid.plasmid.game.rule.RuleResult;

public class WerewolfActivePhase {
	private static final DecimalFormat FORMAT = new DecimalFormat("0.##");

	private final GameWorld gameWorld;
	private final ServerWorld world;
	private final WerewolfMap map;
	private final WerewolfConfig config;
	private final List<PlayerEntry> players = new ArrayList<>();
	private int ticksUntilSwitch;
	private TimeCycle timeCycle = TimeCycle.NIGHT;
	private final List<ActionQueueEntry> actionQueue = new ArrayList<>();
	private final Object2IntLinkedOpenHashMap<PlayerEntry> votes = new Object2IntLinkedOpenHashMap<>();
	private int abstainVotes = 0;
	private TimeCycleBar bar;

	public WerewolfActivePhase(GameWorld gameWorld, WerewolfMap map, WerewolfConfig config) {
		this.gameWorld = gameWorld;
		this.world = gameWorld.getWorld();
		this.map = map;
		this.config = config;
		this.ticksUntilSwitch = this.config.getMaxTimeCycleLength();

		this.votes.defaultReturnValue(0);

		this.bar = new TimeCycleBar(this);
	}

	public static void setRules(Game game) {
		game.setRule(GameRule.BLOCK_DROPS, RuleResult.DENY);
		game.setRule(GameRule.CRAFTING, RuleResult.DENY);
		game.setRule(GameRule.FALL_DAMAGE, RuleResult.DENY);
		game.setRule(GameRule.HUNGER, RuleResult.DENY);
		game.setRule(GameRule.PORTALS, RuleResult.DENY);
		game.setRule(GameRule.THROW_ITEMS, RuleResult.DENY);
	}

	public static void open(GameWorld gameWorld, WerewolfMap map, WerewolfConfig config) {
		WerewolfActivePhase phase = new WerewolfActivePhase(gameWorld, map, config);

		gameWorld.openGame(game -> {
			WerewolfActivePhase.setRules(game);

			// Listeners
			game.on(GameOpenListener.EVENT, phase::open);
			game.on(GameTickListener.EVENT, phase::tick);
			game.on(PlayerAddListener.EVENT, phase::addPlayer);
			game.on(PlayerDamageListener.EVENT, phase::onPlayerDamage);
			game.on(PlayerDeathListener.EVENT, phase::onPlayerDeath);
			game.on(UseItemListener.EVENT, phase::onUseItem);
		});
	}

	private Role getRoleByIndex(int index) {
		if (index == 0) return Roles.WOLF.getRole();
		if (index == 1) return Roles.SEER.getRole();
		return Roles.VILLAGER.getRole();
	}

	private List<ServerPlayerEntity> getShuffledPlayers() {
		List<ServerPlayerEntity> players = new ArrayList<>();
		for (ServerPlayerEntity player : this.gameWorld.getPlayerSet()) {
			players.add(player);
		}

		Collections.shuffle(players);
		return players;
	}

	private void open() {
		Object2IntLinkedOpenHashMap<Role> roleCounts = new Object2IntLinkedOpenHashMap<>();
		roleCounts.defaultReturnValue(0);

		int index = 0;
 		for (ServerPlayerEntity player : this.getShuffledPlayers()) {
			Role role = this.getRoleByIndex(index);
			roleCounts.addTo(role, 1);

			PlayerEntry entry = new PlayerEntry(this, player, role);
			this.players.add(entry);

			entry.resetRemainingActions();
			entry.spawn(world, this.map.getSpawn());

			index += 1;
		}

		MutableText breakdown = new TranslatableText("text.werewolf.role.breakdown.header");
		for (Object2IntMap.Entry<Role> entry : roleCounts.object2IntEntrySet()) {
			Role role = entry.getKey();
			int count = entry.getIntValue();

			if (count > 0) {
				breakdown.append(new TranslatableText("text.werewolf.role.breakdown." + (count == 1 ? "single" : "plural"), role.getName(), count));
			}
		}
		this.gameWorld.getPlayerSet().sendMessage(breakdown.formatted(Formatting.GOLD));
	}

	public void addVote(PlayerEntry target) {
		this.votes.addTo(target, 1);
	}

	public void addAbstainVote() {
		this.abstainVotes += 1;
	}

	public void eliminate(PlayerEntry entry) {
		entry.getRole().unapply(entry);
		this.players.remove(entry);
		this.setSpectator(entry.getPlayer());
	}

	private void reapplyAll() {
		for (PlayerEntry entry : this.players) {
			entry.resetRemainingActions();
			entry.getRole().reapply(entry);
		}
	}

	private void lynch() {
		int maxVotes = Collections.max(this.votes.values());

		if (maxVotes >= this.abstainVotes) {
			this.sendMessage(new TranslatableText("action.lynch.announce.abstain"));
			return;
		}

		List<PlayerEntry> possibleLynches = new ArrayList<>();
		for (Object2IntMap.Entry<PlayerEntry> entry : this.votes.object2IntEntrySet()) {
			if (entry.getIntValue() == maxVotes) {
				possibleLynches.add(entry.getKey());
			}
		}

		if (possibleLynches.size() == 0) {
			this.sendMessage(new TranslatableText("action.lynch.announce.none"));
		} else if (possibleLynches.size() == 1) {
			PlayerEntry toLynch = possibleLynches.get(0);
			this.eliminate(toLynch);

			this.sendMessage(new TranslatableText("action.lynch.announce", toLynch));
		} else {
			this.sendMessage(new TranslatableText("action.lynch.announce.tie"));
		}
	}

	private void tick() {
		if (this.ticksUntilSwitch <= 0) {
			this.abstainVotes = 0;
			this.votes.clear();

			for (ActionQueueEntry entry : this.actionQueue) {
				entry.execute();
			}
			this.actionQueue.clear();

			this.lynch();
	
			// Switch time cycle
			this.timeCycle = this.timeCycle == TimeCycle.NIGHT ? TimeCycle.DAY : TimeCycle.NIGHT;
			this.reapplyAll();

			this.bar.changeTimeCycle();
			this.ticksUntilSwitch = this.config.getMaxTimeCycleLength();
		}
		this.ticksUntilSwitch -= 1;

		this.bar.tick();

		Object2IntLinkedOpenHashMap<Alignment> alignmentCounts = new Object2IntLinkedOpenHashMap<>();
		alignmentCounts.defaultReturnValue(0);

		boolean finished = true;

		for (PlayerEntry entry : this.players) {
			if (entry.getRemainingActions() > 0) {
				finished = false;
			}
			alignmentCounts.addTo(entry.getRole().getAlignment(), 1);
		}

		if (alignmentCounts.getInt(Alignment.WOLF) == 0) {
			this.endGameWithWinner(Alignment.WOLF);
		} else if (alignmentCounts.getInt(Alignment.VILLAGER) == 0) {
			this.endGameWithWinner(Alignment.VILLAGER);
		}

		// Switch early when all players have no more actions
		if (finished && this.ticksUntilSwitch > 60) {
			this.ticksUntilSwitch = 60;
		}
	}

	private void endGame() {
		this.gameWorld.close();
	}

	private void endGameWithWinner(Alignment alignment) {
		Text text = new TranslatableText("text.werewolf.end", alignment.getName());
		this.gameWorld.getPlayerSet().sendMessage(text);
	
		this.endGame();
	}

	private void setSpectator(PlayerEntity player) {
		player.setGameMode(GameMode.SPECTATOR);
	}

	private PlayerEntry getEntryFromPlayer(ServerPlayerEntity player) {
		for (PlayerEntry entry : this.players) {
			if (player.equals(entry.getPlayer())) {
				return entry;
			}
		}
		return null;
	}

	private void addPlayer(ServerPlayerEntity player) {
		if (this.getEntryFromPlayer(player) == null) {
			this.setSpectator(player);

			Vec3d spawn = this.map.getSpawn().getCenter();
			player.teleport(this.world, spawn.getX(), spawn.getY(), spawn.getZ(), 0, 0);
		}
	}

	private boolean onPlayerDamage(ServerPlayerEntity player, DamageSource source, float amount) {
		return true;
	}

	private ActionResult onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
		PlayerEntry entry = this.getEntryFromPlayer(player);
		if (entry != null) {
			entry.spawn(this.world, this.map.getSpawn());
		}

		return ActionResult.SUCCESS;
	}

	private TypedActionResult<ItemStack> onUseItem(ServerPlayerEntity player, Hand hand) {
		ItemStack stack = player.getStackInHand(hand);

		PlayerEntry entry = this.getEntryFromPlayer(player);
		if (entry != null) {
			Action action = entry.getActionStacks().get(stack);
			if (action != null) {
				action.use(entry);
			}
		}

		return TypedActionResult.success(stack);
	}

	public void queueAction(Action action, PlayerEntry user) {
		this.actionQueue.add(new ActionQueueEntry(action, user));
		user.getRole().reapply(user);
	}

	public GameWorld getGameWorld() {
		return this.gameWorld;
	}

	public List<PlayerEntry> getPlayers() {
		return this.players;
	}

	public TimeCycle getTimeCycle() {
		return this.timeCycle;
	}

	public int getTicksUntilSwitch() {
		return this.ticksUntilSwitch;
	}

	public WerewolfConfig getConfig() {
		return this.config;
	}

	public void sendMessage(Text message) {
		this.gameWorld.getPlayerSet().sendMessage(message);
	}

	static {
		FORMAT.setRoundingMode(RoundingMode.DOWN);
	}
}