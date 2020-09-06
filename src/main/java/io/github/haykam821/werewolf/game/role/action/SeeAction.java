package io.github.haykam821.werewolf.game.role.action;

import io.github.haykam821.werewolf.game.PlayerEntry;
import io.github.haykam821.werewolf.game.role.Role;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class SeeAction extends TargetAction {
	public SeeAction(PlayerEntry target) {
		super(target, false);
	}

	@Override
	public void execute(PlayerEntry user) {
		Role role = this.getTarget().getSeenRole();
		user.sendDirectMessage(this.getTranslationKey() + ".result", this.getTarget().getName(), role.getName());
	}

	@Override
	public ItemStack getDisplayStack(PlayerEntry user) {
		return new ItemStack(Items.ENDER_EYE);
	}

	@Override
	public String getTranslationKey() {
		return "action.see";
	}
}