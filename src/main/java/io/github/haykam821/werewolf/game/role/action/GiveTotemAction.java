package io.github.haykam821.werewolf.game.role.action;

import java.util.List;

import io.github.haykam821.werewolf.game.PlayerEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class GiveTotemAction extends TargetAction {
	private final Totem totem;

	public GiveTotemAction(PlayerEntry target, Totem totem) {
		super(target, false);
		this.totem = totem;
	}

	@Override
	public void sendUseMessage(PlayerEntry user) {
		String translationKey = this.getTranslationKey() + ".select";
		Text targetName = this.getTarget().getName();
		Text totemName = this.getTarget().getName();

		Text message = new TranslatableText(translationKey, totemName, targetName).formatted(Formatting.DARK_GREEN);
		user.sendMessage(message);
	}

	@Override
	public void execute(PlayerEntry user) {
		this.getTarget().putTotem(this.totem);

		Text targetName = this.getTarget().getName();
		Text totemName = this.getTarget().getName();

		user.sendMessage(new TranslatableText(this.getTranslationKey() + ".result", totemName, targetName).formatted(Formatting.DARK_GREEN));
		user.getPhase().sendMessage(new TranslatableText(this.getTranslationKey() + ".announce", targetName).formatted(Formatting.DARK_GREEN));
	}

	@Override
	public int getPriority() {
		return Priorities.GIVE_TOTEM;
	}

	@Override
	public ItemStack getDisplayStack(PlayerEntry user) {
		return new ItemStack(Items.TOTEM_OF_UNDYING);
	}

	@Override
	public String getTranslationKey() {
		return "action.giveTotem";
	}

	@Override
	public Text getName() {
		return new TranslatableText(this.getTranslationKey(), this.totem.getName(), this.getTarget().getName());
	}

	@Override
	public List<Text> getLore() {
		List<Text> lore = super.getLore();

		if (this.totem != null) {
			lore.add(new LiteralText("Totem: ").append(this.totem.getName()));
		}

		return lore;
	}

	@Override
	public String toString() {
		return "GiveTotemAction{target=" + this.getTarget() + ",totem=" + this.totem + "}";
	}
}