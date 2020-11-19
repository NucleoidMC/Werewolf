package io.github.haykam821.werewolf.game.role.action;

import io.github.haykam821.werewolf.game.player.AbstractPlayerEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

public class KillAction extends TargetAction {
	public KillAction(AbstractPlayerEntry target) {
		super(target, false);
	}

	@Override
	public void execute(AbstractPlayerEntry user) {
		Text targetName = this.getTarget().getName();

		user.sendDirectMessage(this.getTranslationKey() + ".result", targetName);
		user.getPhase().sendGameMessage(this.getTranslationKey() + ".announce", targetName);

		user.getPhase().eliminate(this.getTarget());
	}

	@Override
	public ItemStack getDisplayStack(AbstractPlayerEntry user) {
		return new ItemStack(Items.IRON_SWORD);
	}

	@Override
	public String getTranslationKey() {
		return "action.kill";
	}
}