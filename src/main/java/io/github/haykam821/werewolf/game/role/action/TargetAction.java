package io.github.haykam821.werewolf.game.role.action;

import io.github.haykam821.werewolf.game.PlayerEntry;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public abstract class TargetAction extends Action {
	private final PlayerEntry target;
	private final boolean broadcastChoice;

	public TargetAction(PlayerEntry target, boolean broadcastChoice) {
		this.target = target;
		this.broadcastChoice = broadcastChoice;
	}

	public PlayerEntry getTarget() {
		return this.target;
	}

	@Override
	public void use(PlayerEntry user) {
		super.use(user);

		String translationKey = this.getTranslationKey() + ".select";
		Text targetName = this.getTarget().getPlayer().getDisplayName();

		if (this.broadcastChoice) {
			Text message = new TranslatableText(translationKey, user.getPlayer().getDisplayName(), targetName);
			user.getPhase().sendMessage(message);
		} else {
			Text message = new TranslatableText(translationKey, targetName);
			user.sendMessage(message);
		}
	}

	@Override
	public Text getName() {
		return new TranslatableText(this.getTranslationKey(), this.getTarget().getPlayer().getDisplayName());
	}

	@Override
	public String toString() {
		return "TargetAction{target=" + this.getTarget() + "}";
	}
}