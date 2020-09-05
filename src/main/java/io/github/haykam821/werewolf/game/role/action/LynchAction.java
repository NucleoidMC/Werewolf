package io.github.haykam821.werewolf.game.role.action;

import io.github.haykam821.werewolf.game.PlayerEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class LynchAction extends TargetAction {
	public LynchAction(PlayerEntry target) {
		super(target, true);
	}

	@Override
	public void execute(PlayerEntry user) {
		user.getPhase().addVote(this.getTarget());
	}

	@Override
	public ItemStack getDisplayStack(PlayerEntry user) {
		return new ItemStack(Items.LEAD);
	}

	@Override
	public String getTranslationKey() {
		return "action.lynch";
	}
}