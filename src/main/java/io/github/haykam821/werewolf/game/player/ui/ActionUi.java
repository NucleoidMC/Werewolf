package io.github.haykam821.werewolf.game.player.ui;

import java.util.List;
import java.util.stream.Collectors;

import eu.pb4.sgui.api.GuiHelpers;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.gui.HotbarGui;
import io.github.haykam821.werewolf.game.player.PlayerEntry;
import io.github.haykam821.werewolf.game.role.action.Action;

public class ActionUi extends HotbarGui {
	private static final int WIDTH = 9;
	private static final int PAGE_SIZE = WIDTH - 2;

	private final PlayerEntry entry;
	private int page = 0;

	private final GuiElement previousPage = PageElement.ofPrevious(this);
	private final GuiElement nextPage = PageElement.ofNext(this);

	public ActionUi(PlayerEntry entry) {
		super(entry.getPlayer());

		this.entry = entry;
	}

	public void update() {
		int size = this.getSize();

		for (int slot = 0; slot < size; slot++) {
			this.clearSlot(slot);
		}

		if (this.page > 0) {
			this.setSlot(0, this.previousPage);
		}

		if (this.page < this.getMaxPage()) {
			this.setSlot(WIDTH - 1, this.nextPage);
		}

		List<Action> actions = this.entry.getActions()
			.stream()
			.skip(this.page * PAGE_SIZE)
			.limit(PAGE_SIZE)
			.collect(Collectors.toUnmodifiableList());

		int slot = 1;

		for (Action action : actions) {
			this.setSlot(slot, ActionElement.of(action, this.entry));

			slot += 1;
			if (slot >= size) {
				break;
			}
		}

		GuiHelpers.sendPlayerInventory(player);
	}

	@Override
	public void close(boolean screenHandlerIsClosed) {
		super.close(screenHandlerIsClosed);

		if (!this.isOpen()) {
			this.open();
			this.update();
		}
	}

	// Pagination
	public int getPage() {
		return this.page;
	}

	private int getMaxPage() {
		return this.entry.getActions().size() / PAGE_SIZE - 1;
	}

	private void setPage(int page) {
		this.page = page;
		this.update();
	}

	public void movePage(int offset) {
		this.setPage(this.getPage() + offset);
	}
}
