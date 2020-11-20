package io.github.haykam821.werewolf.game.role.action;

import java.util.ArrayList;
import java.util.List;

import io.github.haykam821.werewolf.game.player.AbstractPlayerEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public abstract class Action {
	public void execute(AbstractPlayerEntry user) {
		return;
	}

	public void use(AbstractPlayerEntry user) {
		user.getPhase().queueAction(this, user);
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

	/**
	 * Whether the action should be kept even when a player has a {@linkplain Totem.SILENCE silence totem}.
	 */
	public boolean isNormal() {
		return false;
	}

	public abstract ItemStack getDisplayStack(AbstractPlayerEntry user);
}