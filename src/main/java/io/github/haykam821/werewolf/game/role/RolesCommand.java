package io.github.haykam821.werewolf.game.role;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class RolesCommand {
	public static final Text TRUE_TEXT = new LiteralText("✔").formatted(Formatting.GREEN);
	public static final Text FALSE_TEXT = new LiteralText("❌").formatted(Formatting.RED);

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(CommandManager.literal("roles")
			.then(CommandManager.literal("show")
			.then(CommandManager.argument("role", IdentifierArgumentType.identifier())
				.suggests((context, builder) -> {
					for (Identifier id : Role.REGISTRY.keySet()) {
						Role role = Role.REGISTRY.get(id);
						builder.suggest(id.toString(), role.getName());
					}
					return builder.buildFuture();
				})
				.executes(RolesCommand::executeShow)))
			.then(CommandManager.literal("list")
				.executes(RolesCommand::executeList)));
	}

	private static Text getBooleanText(boolean value, Text description) {
		Text valueText = value ? TRUE_TEXT : FALSE_TEXT;
		return valueText.shallowCopy().styled(style -> {
			Text fullDescription = valueText.shallowCopy().append(" ").append(description);
			return style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, fullDescription));
		});
	}

	private static Text getBooleanText(boolean value, String description) {
		return RolesCommand.getBooleanText(value, new LiteralText(description));
	}

	/**
	 * Gets a message containing debug information about a role.
	 */
	private static Text getRoleMessage(Role role, Identifier id) {
		MutableText text = new LiteralText("");

		// Values
		text.append(RolesCommand.getBooleanText(role.canBeCursed(), "can be cursed?"));
		text.append(RolesCommand.getBooleanText(role.canUseWolfChannel(), "can use wolf channel?"));

		// Name and alignment
		Text entryText = new TranslatableText("command.werewolf.roles.role_entry", role.getName(), role.getAlignment().getName()).styled(style -> {
			MutableText hoverText = new LiteralText("");
			hoverText.append(new LiteralText(id.toString()).formatted(Formatting.DARK_GRAY));

			hoverText.append("\n\nInheritance:");
			Class<?> roleClass = role.getClass();
			while (roleClass != null) {
				hoverText.append("\n- " + roleClass.getSimpleName());
				roleClass = roleClass.getSuperclass();
			}

			return style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText));
		});
		text.append(new LiteralText(" - ").append(entryText).formatted(Formatting.GRAY));

		return text;
	}

	private static int executeShow(CommandContext<ServerCommandSource> context) {
		Identifier id = IdentifierArgumentType.getIdentifier(context, "role");

		Role role = Role.REGISTRY.get(id);
		if (role == null) {
			context.getSource().sendError(new TranslatableText("command.werewolf.roles.show.role_does_not_exist", id.toString()));
			return 1;
		}

		context.getSource().sendFeedback(RolesCommand.getRoleMessage(role, id), false);
		return 0;
	}

	private static int executeList(CommandContext<ServerCommandSource> context) {
		for (Identifier id : Role.REGISTRY.keySet()) {
			Role role = Role.REGISTRY.get(id);
			context.getSource().sendFeedback(RolesCommand.getRoleMessage(role, id), false);
		}
		return 0;
	}
}
