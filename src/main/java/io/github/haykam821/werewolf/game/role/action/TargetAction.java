package io.github.haykam821.werewolf.game.role.action;

import java.util.List;

import io.github.haykam821.werewolf.game.PlayerEntry;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

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
		Text targetName = this.getTarget().getName();

		if (this.broadcastChoice) {
			Text message = new TranslatableText(translationKey, user.getName(), targetName).formatted(Formatting.DARK_GREEN);
			user.getPhase().sendMessage(message);
		} else {
			Text message = new TranslatableText(translationKey, targetName).formatted(Formatting.DARK_GREEN);
			user.sendMessage(message);
		}
	}

	@Override
	public Text getName() {
		return new TranslatableText(this.getTranslationKey(), this.getTarget().getName());
	}

	@Override
	public List<Text> getLore() {
		List<Text> lore = super.getLore();

		if (this.target != null) {
			lore.add(new LiteralText("Target: " + this.target));
		}

		return lore;
	}

	@Override
	public String toString() {
		return "TargetAction{target=" + this.getTarget() + "}";
	}
}