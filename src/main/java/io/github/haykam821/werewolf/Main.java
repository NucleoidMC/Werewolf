package io.github.haykam821.werewolf;

import io.github.haykam821.werewolf.game.WerewolfConfig;
import io.github.haykam821.werewolf.game.phase.WerewolfWaitingPhase;
import io.github.haykam821.werewolf.game.role.Roles;
import io.github.haykam821.werewolf.game.role.RolesCommand;
import io.github.haykam821.werewolf.game.role.action.ActionsCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.block.Block;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import xyz.nucleoid.plasmid.game.GameType;

public class Main implements ModInitializer {
	public static final String MOD_ID = "werewolf";

	private static final Identifier EXTINGUISHABLE_CAMPFIRES_ID = new Identifier(MOD_ID, "extinguishable_campfires");
	public static final TagKey<Block> EXTINGUISHABLE_CAMPFIRES = TagKey.of(Registry.BLOCK_KEY, EXTINGUISHABLE_CAMPFIRES_ID);

	private static final Identifier WEREWOLF_ID = new Identifier(MOD_ID, "werewolf");
	public static final GameType<WerewolfConfig> WEREWOLF_GAME_TYPE = GameType.register(WEREWOLF_ID, WerewolfConfig.CODEC, WerewolfWaitingPhase::open);

	@Override
	public void onInitialize() {
		Roles.initialize();
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			ActionsCommand.register(dispatcher);
			RolesCommand.register(dispatcher);
		});
	}
}