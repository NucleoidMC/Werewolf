package io.github.haykam821.werewolf;

import io.github.haykam821.werewolf.game.WerewolfConfig;
import io.github.haykam821.werewolf.game.channel.WolfMsgCommand;
import io.github.haykam821.werewolf.game.phase.WerewolfWaitingPhase;
import io.github.haykam821.werewolf.game.role.Roles;
import io.github.haykam821.werewolf.game.role.RolesCommand;
import io.github.haykam821.werewolf.game.role.action.ActionsCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.block.Block;
import net.minecraft.network.message.MessageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import xyz.nucleoid.plasmid.game.GameType;

public class Main implements ModInitializer {
	public static final String MOD_ID = "werewolf";

	private static final Identifier EXTINGUISHABLE_CAMPFIRES_ID = new Identifier(MOD_ID, "extinguishable_campfires");
	public static final TagKey<Block> EXTINGUISHABLE_CAMPFIRES = TagKey.of(RegistryKeys.BLOCK, EXTINGUISHABLE_CAMPFIRES_ID);

	private static final Identifier WEREWOLF_ID = new Identifier(MOD_ID, "werewolf");
	public static final GameType<WerewolfConfig> WEREWOLF_GAME_TYPE = GameType.register(WEREWOLF_ID, WerewolfConfig.CODEC, WerewolfWaitingPhase::open);

	private static final Identifier WOLF_CHANNEL_ID = new Identifier(MOD_ID, "wolf_channel");
	public static final RegistryKey<MessageType> WOLF_CHANNEL_KEY = RegistryKey.of(RegistryKeys.MESSAGE_TYPE, WOLF_CHANNEL_ID);

	@Override
	public void onInitialize() {
		Roles.initialize();
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			ActionsCommand.register(dispatcher);
			RolesCommand.register(dispatcher);
			WolfMsgCommand.register(dispatcher);
		});
	}
}