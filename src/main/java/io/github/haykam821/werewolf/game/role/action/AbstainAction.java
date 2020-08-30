package io.github.haykam821.werewolf.game.role.action;

import io.github.haykam821.werewolf.game.PlayerEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class AbstainAction extends Action {
	@Override
	public void execute(PlayerEntry user) {
		user.getPhase().addAbstainVote();
	}

	@Override
	public ItemStack getDisplayStack(PlayerEntry user) {
		return new ItemStack(Items.BELL);
	}

	@Override
	public String getTranslationKey() {
		return "action.abstain";
	}
}