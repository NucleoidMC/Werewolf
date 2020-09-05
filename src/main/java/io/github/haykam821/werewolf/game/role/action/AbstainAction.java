package io.github.haykam821.werewolf.game.role.action;

import io.github.haykam821.werewolf.game.PlayerEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class AbstainAction extends Action {
	@Override
	public void use(PlayerEntry user) {
		super.use(user);

		Text message = new TranslatableText(this.getTranslationKey() + ".select", user.getName()).formatted(Formatting.DARK_GREEN);
		user.getPhase().sendMessage(message);
	}

	@Override
	public void execute(PlayerEntry user) {
		user.getPhase().addAbstainVote();
	}

	@Override
	public ItemStack getDisplayStack(PlayerEntry user) {
		return new ItemStack(Items.BELL);
	}

	@Override
	public String getTranslationKey() {
		return "action.abstain";
	}
}