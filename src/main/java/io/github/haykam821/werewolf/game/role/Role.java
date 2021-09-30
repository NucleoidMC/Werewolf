package io.github.haykam821.werewolf.game.role;

import java.util.ArrayList;
import java.util.List;

import io.github.haykam821.werewolf.game.player.AbstractPlayerEntry;
import io.github.haykam821.werewolf.game.player.PlayerEntry;
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
	public static final TinyRegistry<Role> REGISTRY = TinyRegistry.create();

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

	public void unapply(AbstractPlayerEntry entry) {
		if (entry instanceof PlayerEntry) {
			((PlayerEntry) entry).getPlayer().getInventory().clear();
		}
		entry.clearActions();
	}

	private List<Action> getActions(AbstractPlayerEntry entry) {
		TimeCycle timeCycle = entry.getPhase().getTimeCycle();
		return timeCycle == TimeCycle.NIGHT ? this.getNightActions(entry) : this.getDayActions(entry);
	}

	public void apply(AbstractPlayerEntry entry) {
		if (entry.getRemainingActions() <= 0) return;

		List<Action> actions = this.getActions(entry);
		if (actions.size() < entry.getRemainingActions()) {
			entry.setRemainingActions(actions.size());
		}

		int index = 0;
		List<ItemStack> stacks = new ArrayList<>();
		for (Action action : actions) {
			ItemStackBuilder builder = ItemStackBuilder.of(action.getDisplayStack(entry)).setName(action.getName());
			for (Text lore : action.getLore()) {
				builder.addLore(lore);
			}

			ItemStack stack = builder.build();
			stack.getNbt().putInt("ActionIndex", index);

			entry.putAction(action);
			stacks.add(stack);

			index += 1;
		}

		if (entry instanceof PlayerEntry) {
			ServerPlayerEntity player = ((PlayerEntry) entry).getPlayer();

			int slot = 0;
			for (ItemStack stack : stacks) {
				player.getInventory().setStack(slot, stack);
				slot += 1;
			}

			// Update inventory
			player.currentScreenHandler.sendContentUpdates();
			player.playerScreenHandler.onContentChanged(player.getInventory());
		}
	}

	public void reapply(AbstractPlayerEntry entry) {
		this.unapply(entry);
		this.apply(entry);
	}

	public int getMaxDayActions(AbstractPlayerEntry user) {
		return 1;
	}

	public List<Action> getDayActions(AbstractPlayerEntry user) {
		List<Action> actions = new ArrayList<>();
		actions.add(new AbstainAction());

		for (AbstractPlayerEntry entry : user.getPhase().getPlayers()) {
			if (!user.equals(entry)) {
				actions.add(new LynchAction(entry));
			}
		}

		return actions;
	}

	public int getMaxNightActions(AbstractPlayerEntry user) {
		return 1;
	}

	public List<Action> getNightActions(AbstractPlayerEntry user) {
		return new ArrayList<>();
	}

	public abstract Alignment getAlignment();

	public Role getSeenRole(AbstractPlayerEntry entry) {
		return entry.isCursed() ? Roles.WOLF.getRole() : this;
	}

	public boolean canBeCursed() {
		return this.getAlignment() != Alignment.WOLF;
	}

	public boolean canUseWolfChannel() {
		return false;
	}
}