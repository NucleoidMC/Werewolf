package io.github.haykam821.werewolf.game.role;

import java.util.ArrayList;
import java.util.List;

import io.github.haykam821.werewolf.game.PlayerEntry;
import io.github.haykam821.werewolf.game.role.action.AbstainAction;
import io.github.haykam821.werewolf.game.role.action.Action;
import io.github.haykam821.werewolf.game.role.action.LynchAction;
import io.github.haykam821.werewolf.game.timecycle.TimeCycle;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import xyz.nucleoid.plasmid.registry.TinyRegistry;
import xyz.nucleoid.plasmid.util.ItemStackBuilder;

public abstract class Role {
	public static final TinyRegistry<Role> REGISTRY = TinyRegistry.newStable();

	private String translationKey;

	private String getTranslationKey() {
		if (this.translationKey == null) {
			this.translationKey = Util.createTranslationKey("role", REGISTRY.getIdentifier(this));
		}
		return this.translationKey;
	}

	public Text getName() {
		return new TranslatableText(this.getTranslationKey());
	}

	public void unapply(PlayerEntry entry) {
		entry.getPlayer().inventory.clear();
		entry.getActionStacks().clear();
	}

	private List<Action> getActions(PlayerEntry entry) {
		TimeCycle timeCycle = entry.getPhase().getTimeCycle();
		return timeCycle == TimeCycle.NIGHT ? this.getNightActions(entry) : this.getDayActions(entry);
	}

	public void apply(PlayerEntry entry) {
		if (entry.getRemainingActions() <= 0) return;

		ServerPlayerEntity player = entry.getPlayer();

		List<Action> actions = this.getActions(entry);
		int slot = 0;
		for (Action action : actions) {
			ItemStack stack = ItemStackBuilder.of(action.getDisplayStack(entry))
				.setName(action.getName())
				.build();

			entry.putActionStack(stack, action);
			player.inventory.setStack(slot, stack);

			slot += 1;
		}

		// Update inventory
		player.currentScreenHandler.sendContentUpdates();
		player.playerScreenHandler.onContentChanged(player.inventory);
		player.updateCursorStack();
	}

	public void reapply(PlayerEntry entry) {
		this.unapply(entry);
		this.apply(entry);
	}

	public int getMaxDayActions(PlayerEntry user) {
		return 1;
	}

	public List<Action> getDayActions(PlayerEntry user) {
		List<Action> actions = new ArrayList<>();
		actions.add(new AbstainAction());

		for (PlayerEntry entry : user.getPhase().getPlayers()) {
			if (!user.equals(entry)) {
				actions.add(new LynchAction(entry));
			}
		}

		return actions;
	}

	public int getMaxNightActions(PlayerEntry user) {
		return 1;
	}

	public List<Action> getNightActions(PlayerEntry user) {
		return new ArrayList<>();
	}

	public abstract Alignment getAlignment();

	public Alignment getSeenAlignment() {
		return this.getAlignment();
	}
}