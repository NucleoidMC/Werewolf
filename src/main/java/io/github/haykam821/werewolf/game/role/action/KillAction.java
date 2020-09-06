package io.github.haykam821.werewolf.game.role.action;

import io.github.haykam821.werewolf.game.PlayerEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

public class KillAction extends TargetAction {
	public KillAction(PlayerEntry target) {
		super(target, false);
	}

	@Override
	public void execute(PlayerEntry user) {
		Text targetName = this.getTarget().getName();

		user.sendDirectMessage(this.getTranslationKey() + ".result", targetName);
		user.getPhase().sendGameMessage(this.getTranslationKey() + ".announce", targetName);

		user.getPhase().eliminate(this.getTarget());
	}

	@Override
	public ItemStack getDisplayStack(PlayerEntry user) {
		return new ItemStack(Items.IRON_SWORD);
	}

	@Override
	public String getTranslationKey() {
		return "action.kill";
	}
}