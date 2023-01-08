package io.github.haykam821.werewolf.game.role.action;

import java.util.List;

import io.github.haykam821.werewolf.game.player.AbstractPlayerEntry;
import net.minecraft.text.Text;

public abstract class TargetAction extends Action {
	private final AbstractPlayerEntry target;
	private final boolean broadcastChoice;

	public TargetAction(AbstractPlayerEntry target, boolean broadcastChoice) {
		this.target = target;
		this.broadcastChoice = broadcastChoice;
	}

	public AbstractPlayerEntry getTarget() {
		return this.target;
	}

	@Override
	public void use(AbstractPlayerEntry user) {
		super.use(user);
		this.sendUseMessage(user);
	}

	public void sendUseMessage(AbstractPlayerEntry user) {
		String translationKey = this.getTranslationKey() + ".select";
		Text targetName = this.getTarget().getName();

		if (this.broadcastChoice) {
			user.getPhase().sendGameMessage(translationKey, user.getName(), targetName);
		} else {
			user.sendDirectMessage(translationKey, targetName);
		}
	}

	@Override
	public Text getName() {
		return Text.translatable(this.getTranslationKey(), this.getTarget().getName());
	}

	@Override
	public List<Text> getLore() {
		List<Text> lore = super.getLore();

		if (this.target != null) {
			lore.add(Text.literal("Target: ").append(this.target.getName()));
		}

		return lore;
	}

	@Override
	public String toString() {
		return "TargetAction{target=" + this.getTarget() + "}";
	}
}