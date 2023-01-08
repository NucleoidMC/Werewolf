package io.github.haykam821.werewolf.game.role.action;

import java.util.List;

import io.github.haykam821.werewolf.game.player.AbstractPlayerEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

public class GiveTotemAction extends TargetAction {
	private final Totem totem;

	public GiveTotemAction(AbstractPlayerEntry target, Totem totem) {
		super(target, false);
		this.totem = totem;
	}

	@Override
	public void sendUseMessage(AbstractPlayerEntry user) {
		user.sendDirectMessage(this.getTranslationKey() + ".select", this.totem.getName(), this.getTarget().getName());
	}

	@Override
	public void execute(AbstractPlayerEntry user) {
		this.getTarget().putTotem(this.totem);

		Text targetName = this.getTarget().getName();
		Text totemName = this.totem.getName();

		user.sendDirectMessage(this.getTranslationKey() + ".result", totemName, targetName);
		user.getPhase().sendGameMessage(this.getTranslationKey() + ".announce", targetName);
	}

	@Override
	public int getPriority() {
		return Priorities.GIVE_TOTEM;
	}

	@Override
	public ItemStack getDisplayStack(AbstractPlayerEntry user) {
		return new ItemStack(Items.TOTEM_OF_UNDYING);
	}

	@Override
	public String getTranslationKey() {
		return "action.giveTotem";
	}

	@Override
	public Text getName() {
		return Text.translatable(this.getTranslationKey(), this.totem.getName(), this.getTarget().getName());
	}

	@Override
	public List<Text> getLore() {
		List<Text> lore = super.getLore();

		if (this.totem != null) {
			lore.add(Text.literal("Totem: ").append(this.totem.getName()));
		}

		return lore;
	}

	@Override
	public String toString() {
		return "GiveTotemAction{target=" + this.getTarget() + ",totem=" + this.totem + "}";
	}
}