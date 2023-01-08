package io.github.haykam821.werewolf.game.channel;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import io.github.haykam821.werewolf.game.player.AbstractPlayerEntry;
import io.github.haykam821.werewolf.game.role.action.ActionsCommand;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class WolfMsgCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(CommandManager.literal("wolfmsg")
			.then(CommandManager.argument("message", MessageArgumentType.message())
			.executes(WolfMsgCommand::execute)));
	}

	private static int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerPlayerEntity player = context.getSource().getPlayer();
		AbstractPlayerEntry entry = ActionsCommand.obtainPlayerEntry(context, player);

		if (!entry.getRole().canUseWolfChannel()) {
			entry.sendDirectMessage("channel.wolf.denied");
			return 0;
		}

		MessageArgumentType.getSignedMessage(context, "message", message -> {
			entry.getPhase().sendWolfMessage(player, message);
		});
		
		return Command.SINGLE_SUCCESS;
	}
}
