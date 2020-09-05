package io.github.haykam821.werewolf.game.role.action;

import java.util.ArrayList;
import java.util.List;

import io.github.haykam821.werewolf.game.PlayerEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public abstract class Action {
	public void execute(PlayerEntry user) {
		return;
	}

	public void use(PlayerEntry user) {
		user.getPhase().queueAction(this, user);
		user.decrementRemainingActions();
		user.getRole().reapply(user);
	}

	public int getPriority() {
		return Priorities.DEFAULT;
	}

	public abstract String getTranslationKey();

	public Text getName() {
		return new TranslatableText(this.getTranslationKey());
	}

	public List<Text> getLore() {
		return new ArrayList<>();
	}

	public abstract ItemStack getDisplayStack(PlayerEntry user);
}