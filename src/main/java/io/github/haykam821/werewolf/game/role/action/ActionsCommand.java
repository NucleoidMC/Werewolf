package io.github.haykam821.werewolf.game.role.action;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import io.github.haykam821.werewolf.game.event.PlayerEntryObtainer;
import io.github.haykam821.werewolf.game.player.AbstractPlayerEntry;
import io.github.haykam821.werewolf.game.player.PlayerEntry;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import xyz.nucleoid.plasmid.game.GameWorld;

public class ActionsCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(CommandManager.literal("actions")
			.then(CommandManager.literal("show")
				.then(CommandManager.argument("action", IntegerArgumentType.integer(0))
					.then(CommandManager.argument("player", EntityArgumentType.player())
						.requires(context -> {
							return context.hasPermissionLevel(2);
						})
						.executes(context -> {
							return ActionsCommand.executeShow(context, EntityArgumentType.getPlayer(context, "player"));
						}))
					.executes(context -> {
						return ActionsCommand.executeShow(context, context.getSource().getPlayer());
					})))
			.then(CommandManager.literal("queue")
				.then(CommandManager.argument("action", IntegerArgumentType.integer(0))
					.then(CommandManager.argument("player", EntityArgumentType.player())
						.requires(context -> {
							return context.hasPermissionLevel(2);
						})
						.executes(context -> {
							return ActionsCommand.executeQueue(context, EntityArgumentType.getPlayer(context, "player"));
						}))
					.executes(context -> {
						return ActionsCommand.executeQueue(context, context.getSource().getPlayer());
					})))
			.then(CommandManager.literal("execute")
				.then(CommandManager.argument("action", IntegerArgumentType.integer(0))
					.then(CommandManager.argument("player", EntityArgumentType.player())
						.requires(context -> {
							return context.hasPermissionLevel(2);
						})
						.executes(context -> {
							return ActionsCommand.executeExecute(context, EntityArgumentType.getPlayer(context, "player"));
						}))
					.executes(context -> {
						return ActionsCommand.executeExecute(context, context.getSource().getPlayer());
					})))
			.then(CommandManager.literal("list")
				.then(CommandManager.argument("player", EntityArgumentType.player())
					.requires(context -> {
						return context.hasPermissionLevel(2);
					})
					.executes(context -> {
						return ActionsCommand.executeList(context, EntityArgumentType.getPlayer(context, "player"));
					}))
				.executes(context -> {
					return ActionsCommand.executeList(context, context.getSource().getPlayer());
				})));
	}

	/**
	 * Gets a message containing debug information about an action.
	 */
	private static Text getActionMessage(Action action, AbstractPlayerEntry user, int index, boolean self) {
		return new LiteralText("- ").append(action.getName().shallowCopy().styled(style -> {
			HoverEvent.ItemStackContent content = new HoverEvent.ItemStackContent(action.getDisplayStack(user));
			style = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, content));

			if (user instanceof PlayerEntry) {
				PlayerEntry playerEntry = (PlayerEntry) user;
				String command = "/actions queue " + index + (self ? "" : " " + playerEntry.getPlayer().getGameProfile().getName());
				style = style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
			}

			return style;
		}));
	}

	private static AbstractPlayerEntry obtainPlayerEntry(CommandContext<ServerCommandSource> context, ServerPlayerEntity player) {
		GameWorld gameWorld = GameWorld.forWorld(player.getEntityWorld());
		if (gameWorld == null) {
			context.getSource().sendError(new TranslatableText("command.werewolf.actions.not_in_game", player.getDisplayName()));
			return null;
		}

		AbstractPlayerEntry entry = gameWorld.invoker(PlayerEntryObtainer.EVENT).obtainPlayerEntry(player);
		if (entry == null) {
			context.getSource().sendError(new TranslatableText("command.werewolf.actions.not_alive", player.getDisplayName()));
		}
		return entry;
	}

	private static int executeShow(CommandContext<ServerCommandSource> context, ServerPlayerEntity player) throws CommandSyntaxException {
		AbstractPlayerEntry entry = ActionsCommand.obtainPlayerEntry(context, player);
		if (entry == null) return 1;

		int index = IntegerArgumentType.getInteger(context, "action");
		Action action = entry.getAction(index);
		if (action == null) {
			context.getSource().sendError(new TranslatableText("command.werewolf.actions.action_does_not_exist", index));
			return 1;
		}

		context.getSource().sendFeedback(ActionsCommand.getActionMessage(action, entry, index, player.equals(context.getSource().getPlayer())), true);
		return 0;
	}

	private static int executeQueue(CommandContext<ServerCommandSource> context, ServerPlayerEntity player) {
		AbstractPlayerEntry entry = ActionsCommand.obtainPlayerEntry(context, player);
		if (entry == null) return 1;

		Action action = entry.getAction(IntegerArgumentType.getInteger(context, "action"));
		if (action == null) {
			context.getSource().sendError(new TranslatableText("command.werewolf.actions.action_does_not_exist"));
			return 1;
		}

		entry.getPhase().queueAction(action, entry);
		context.getSource().sendFeedback(new TranslatableText("command.werewolf.actions.queue.success"), true);
		return 0;
	}

	private static int executeExecute(CommandContext<ServerCommandSource> context, ServerPlayerEntity player) {
		AbstractPlayerEntry entry = ActionsCommand.obtainPlayerEntry(context, player);
		if (entry == null) return 1;

		Action action = entry.getAction(IntegerArgumentType.getInteger(context, "action"));
		if (action == null) {
			context.getSource().sendError(new TranslatableText("command.werewolf.actions.action_does_not_exist"));
			return 1;
		}

		action.execute(entry);
		context.getSource().sendFeedback(new TranslatableText("command.werewolf.actions.execute.success"), true);
		return 0;
	}

	private static int executeList(CommandContext<ServerCommandSource> context, ServerPlayerEntity player) throws CommandSyntaxException {
		AbstractPlayerEntry entry = ActionsCommand.obtainPlayerEntry(context, player);
		if (entry == null) return 1;

		context.getSource().sendFeedback(new TranslatableText("command.werewolf.actions.list.header", player.getDisplayName(), entry.getActions().size()), true);

		int index = 0;
		boolean self = player.equals(context.getSource().getPlayer());
		for (Action action : entry.getActions()) {
			context.getSource().sendFeedback(ActionsCommand.getActionMessage(action, entry, index, self), true);
			index += 1;
		}

		return 0;
	}
}
