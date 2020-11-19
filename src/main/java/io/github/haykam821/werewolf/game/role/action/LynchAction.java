package io.github.haykam821.werewolf.game.role.action;

import io.github.haykam821.werewolf.game.player.AbstractPlayerEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class LynchAction extends TargetAction {
	public LynchAction(AbstractPlayerEntry target) {
		super(target, true);
	}

	@Override
	public void execute(AbstractPlayerEntry user) {
		user.getPhase().getVoteManager().addVote(this.getTarget());
	}

	@Override
	public ItemStack getDisplayStack(AbstractPlayerEntry user) {
		return new ItemStack(Items.LEAD);
	}

	@Override
	public String getTranslationKey() {
		return "action.lynch";
	}

	@Override
	public boolean isNormal() {
		return true;
	}
}