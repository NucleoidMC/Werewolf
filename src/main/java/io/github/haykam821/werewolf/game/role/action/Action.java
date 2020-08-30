package io.github.haykam821.werewolf.game.role.action;

import io.github.haykam821.werewolf.game.PlayerEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public abstract class Action {
	public abstract void execute(PlayerEntry user);

	public void use(PlayerEntry user) {
		user.getPhase().queueAction(this, user);
		user.decrementRemainingActions();
	}

	public int getPriority() {
		return 0;
	}

	public abstract String getTranslationKey();

	public Text getName() {
		return new TranslatableText(this.getTranslationKey());
	}

	public abstract ItemStack getDisplayStack(PlayerEntry user);
}