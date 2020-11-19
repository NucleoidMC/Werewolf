package io.github.haykam821.werewolf.game.phase;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.haykam821.werewolf.game.WerewolfConfig;
import io.github.haykam821.werewolf.game.channel.ChannelManager;
import io.github.haykam821.werewolf.game.map.WerewolfMap;
import io.github.haykam821.werewolf.game.player.AbstractPlayerEntry;
import io.github.haykam821.werewolf.game.player.PlayerEntry;
import io.github.haykam821.werewolf.game.role.Alignment;
import io.github.haykam821.werewolf.game.role.Role;
import io.github.haykam821.werewolf.game.role.Roles;
import io.github.haykam821.werewolf.game.role.action.Action;
import io.github.haykam821.werewolf.game.role.action.ActionQueueEntry;
import io.github.haykam821.werewolf.game.role.action.Totem;
import io.github.haykam821.werewolf.game.timecycle.TimeCycle;
import io.github.haykam821.werewolf.game.timecycle.TimeCycleBar;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
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
import xyz.nucleoid.plasmid.game.event.PlayerChatListener;
import xyz.nucleoid.plasmid.game.event.PlayerDamageListener;
import xyz.nucleoid.plasmid.game.event.PlayerDeathListener;
import xyz.nucleoid.plasmid.game.event.UseItemListener;
import xyz.nucleoid.plasmid.game.rule.GameRule;
import xyz.nucleoid.plasmid.game.rule.RuleResult;

public class WerewolfActivePhase {
	private static final DecimalFormat FORMAT = new DecimalFormat("###,###");

	private final GameWorld gameWorld;
	private final ServerWorld world;
	private final WerewolfMap map;
	private final WerewolfConfig config;
	private final List<AbstractPlayerEntry> players = new ArrayList<>();
	private int ticksUntilSwitch;
	private TimeCycle timeCycle = TimeCycle.NIGHT;
	private final List<ActionQueueEntry> actionQueue = new ArrayList<>();
	private TimeCycleBar bar;
	private final ChannelManager channelManager;
	private final VoteManager voteManager;

	public WerewolfActivePhase(GameWorld gameWorld, WerewolfMap map, WerewolfConfig config) {
		this.gameWorld = gameWorld;
		this.world = gameWorld.getWorld();
		this.map = map;
		this.config = config;
		this.ticksUntilSwitch = this.config.getMaxTimeCycleLength();


		this.bar = new TimeCycleBar(this);
		this.voteManager = new VoteManager(this);
		this.channelManager = new ChannelManager(this.gameWorld, this.players);
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
			game.on(PlayerChatListener.EVENT, phase::onPlayerChat);
			game.on(PlayerDamageListener.EVENT, phase::onPlayerDamage);
			game.on(PlayerDeathListener.EVENT, phase::onPlayerDeath);
			game.on(UseItemListener.EVENT, phase::onUseItem);
		});
	}

	private Role getRoleByIndex(int index) {
		if (index == 0) return Roles.WOLF.getRole();
		if (index == 1) return Roles.SEER.getRole();
		if (index == 5) return Roles.SHAMAN.getRole();
		if (index == 6) return Roles.CULTIST.getRole();
		return Roles.VILLAGER.getRole();
	}

	private boolean isCursedByIndex(int index) {
		return index == 5 || index == 19;
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
		this.world.setTimeOfDay(this.timeCycle.getTimeOfDay());

		Object2IntLinkedOpenHashMap<Role> roleCounts = new Object2IntLinkedOpenHashMap<>();
		roleCounts.defaultReturnValue(0);

		int index = 0;
 		for (ServerPlayerEntity player : this.getShuffledPlayers()) {
			Role role = this.getRoleByIndex(index);
			roleCounts.addTo(role, 1);

			boolean cursed = role.canBeCursed() && this.isCursedByIndex(index);
			this.players.add(new PlayerEntry(this, player, role, cursed));

			index += 1;
		}

		for (AbstractPlayerEntry entry : this.players) { 
			entry.resetRemainingActions();
			entry.spawn(world, this.map.getSpawn());
		}

		this.sendBreakdown(roleCounts);
		this.sendWolfMessage("channel.wolf.hint");
	}

	private void sendBreakdown(Object2IntLinkedOpenHashMap<Role> roleCounts) {
		MutableText breakdown = new TranslatableText("text.werewolf.role.breakdown.header");
		for (Object2IntMap.Entry<Role> entry : roleCounts.object2IntEntrySet()) {
			Role role = entry.getKey();
			int count = entry.getIntValue();

			if (count > 0) {
				String translationKey = "text.werewolf.role.breakdown." + (count == 1 ? "single" : "plural");
				breakdown.append(new LiteralText("\n")).append(new TranslatableText(translationKey, role.getName(), FORMAT.format(count)));
			}
		}
		this.sendGameMessage(breakdown);
	}

	public void eliminate(AbstractPlayerEntry entry) {
		if (entry instanceof PlayerEntry) {
			entry.getRole().unapply((PlayerEntry) entry);
			this.setSpectator(((PlayerEntry) entry).getPlayer());
		}
		this.players.remove(entry);
	}

	private void reapplyAll() {
		for (AbstractPlayerEntry entry : this.players) {
			entry.resetRemainingActions();
			if (entry instanceof PlayerEntry) {
				entry.getRole().reapply((PlayerEntry) entry);
			}

			if (this.timeCycle == TimeCycle.NIGHT) {
				entry.clearTotems();
			} else if (entry.hasTotem(Totem.DEATH)) {
				this.sendGameMessage("totem.death.activate", entry.getName(), entry.getLynchRoleName());
				this.eliminate(entry);
			}
		}
	}

	private void resetAndCycleTime() {
		this.voteManager.reset();
		if (this.timeCycle == TimeCycle.DAY) {
			this.voteManager.lynch();
		}

		this.actionQueue.sort(null);
		for (ActionQueueEntry entry : this.actionQueue) {
			entry.execute();
		}
		this.actionQueue.clear();

		// Switch time cycle
		this.timeCycle = this.timeCycle == TimeCycle.NIGHT ? TimeCycle.DAY : TimeCycle.NIGHT;
		this.reapplyAll();

		this.bar.changeTimeCycle();
		this.world.setTimeOfDay(this.timeCycle.getTimeOfDay());

		this.ticksUntilSwitch = this.config.getMaxTimeCycleLength();
	}

	private void tick() {
		if (this.ticksUntilSwitch <= 0) {
			this.resetAndCycleTime();
		}
		this.ticksUntilSwitch -= 1;
		this.bar.tick();

		Object2IntLinkedOpenHashMap<Alignment> alignmentCounts = new Object2IntLinkedOpenHashMap<>();
		alignmentCounts.defaultReturnValue(0);

		boolean finished = true;

		for (AbstractPlayerEntry entry : this.players) {
			if (entry.getRemainingActions() > 0) {
				finished = false;
			}
			alignmentCounts.addTo(entry.getRole().getAlignment(), 1);
		}

		if (alignmentCounts.getInt(Alignment.WOLF) == 0) {
			this.endGameWithWinner(Alignment.VILLAGER);
		} else if (alignmentCounts.getInt(Alignment.VILLAGER) == 0) {
			this.endGameWithWinner(Alignment.WOLF);
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
		this.sendGameMessage("text.werewolf.end", alignment.getName());
		this.endGame();
	}

	private void setSpectator(PlayerEntity player) {
		player.setGameMode(GameMode.SPECTATOR);
	}

	private AbstractPlayerEntry getEntryFromPlayer(ServerPlayerEntity player) {
		for (AbstractPlayerEntry entry : this.players) {
			if (entry instanceof PlayerEntry && player.equals(((PlayerEntry) entry).getPlayer())) {
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

	private ActionResult handleMessage(String message, ServerPlayerEntity sender) {
		if (message.length() == 0) return ActionResult.SUCCESS;
		if (message.charAt(0) != '#') return ActionResult.SUCCESS;
		
		AbstractPlayerEntry entry = this.getEntryFromPlayer(sender);
		if (entry == null) return ActionResult.SUCCESS;

		if (!entry.getRole().canUseWolfChannel()) {
			entry.sendDirectMessage("channel.wolf.denied");
			return ActionResult.FAIL;
		}

		this.sendWolfMessage(new TranslatableText("chat.type.text", sender.getDisplayName(), message.substring(1)).formatted(Formatting.WHITE));
		return ActionResult.FAIL;
	}

	private ActionResult onPlayerChat(Text text, ServerPlayerEntity sender) {
		if (text instanceof TranslatableText) {
			TranslatableText translatableText = (TranslatableText) text;
			return this.handleMessage((String) translatableText.getArgs()[1], sender);
		} else if (text instanceof LiteralText) {
			LiteralText literalText = (LiteralText) text;
			return this.handleMessage((String) literalText.asString(), sender);
		}
		return ActionResult.SUCCESS;
	}

	private boolean onPlayerDamage(ServerPlayerEntity player, DamageSource source, float amount) {
		return true;
	}

	private ActionResult onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
		AbstractPlayerEntry entry = this.getEntryFromPlayer(player);
		if (entry != null) {
			entry.spawn(this.world, this.map.getSpawn());
		}

		return ActionResult.SUCCESS;
	}

	private TypedActionResult<ItemStack> onUseItem(ServerPlayerEntity player, Hand hand) {
		ItemStack stack = player.getStackInHand(hand);

		AbstractPlayerEntry entry = this.getEntryFromPlayer(player);
		if (entry != null) {
			Action action = entry.getAction(stack.getTag().getInt("ActionIndex"));
			if (action != null) {
				action.use(entry);
				return TypedActionResult.success(stack);
			}
		}

		return TypedActionResult.pass(stack);
	}

	public void queueAction(Action action, AbstractPlayerEntry user) {
		this.actionQueue.add(new ActionQueueEntry(action, user));
		if (user instanceof PlayerEntry) {
			user.getRole().reapply((PlayerEntry) user);
		}
	}

	public GameWorld getGameWorld() {
		return this.gameWorld;
	}

	public List<AbstractPlayerEntry> getPlayers() {
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

	public VoteManager getVoteManager() {
		return this.voteManager;
	}

	private void sendGameMessage(Text message) {
		this.channelManager.getGameChannel().sendMessage(message);
	}

	public void sendGameMessage(String key, Object... args) {
		this.channelManager.getGameChannel().sendMessage(key, args);
	}

	private void sendWolfMessage(Text message) {
		this.channelManager.getWolfChannel().sendMessage(message, true);
	}

	public void sendWolfMessage(String key, Object... args) {
		this.channelManager.getWolfChannel().sendMessage(key, args);
	}
}