package io.github.haykam821.werewolf.game.phase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.haykam821.werewolf.game.PlayerEntry;
import io.github.haykam821.werewolf.game.role.action.Totem;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;

public class VoteManager {
	private final WerewolfActivePhase phase;
	private final Object2IntLinkedOpenHashMap<PlayerEntry> votes = new Object2IntLinkedOpenHashMap<>();
	private int abstainVotes = 0;

	public VoteManager(WerewolfActivePhase phase) {
		this.phase = phase;
		this.votes.defaultReturnValue(0);
	}

	/**
	 * Votes for a given player.
	 * If the player has a pacifism totem, the vote will be for abstaining instead.
	 */
	public void addVote(PlayerEntry target) {
		if (target.hasTotem(Totem.PACIFISM)) {
			this.addAbstainVote();
			return;
		}
		this.votes.addTo(target, 1);
	}

	public void addAbstainVote() {
		this.abstainVotes += 1;
	}

	/**
	 * Resets the vote counts.
	 */
	public void reset() {
		this.abstainVotes = 0;
		this.votes.clear();
	}

	/**
	 * Gets a list of all players who have the highest votes.
	 * If there is a tie, there will be more than one player in this list.
	 * This does not take into account votes for abstaining.
	 */
	private List<PlayerEntry> getPossibleLynches(int maxVotes) {
		List<PlayerEntry> possibleLynches = new ArrayList<>();
		for (Object2IntMap.Entry<PlayerEntry> entry : this.votes.object2IntEntrySet()) {
			if (entry.getIntValue() == maxVotes) {
				possibleLynches.add(entry.getKey());
			}
		}
		return possibleLynches;
	}

	public void lynch() {
		if (this.votes.size() == 0) {
			this.phase.sendGameMessage("action.lynch.announce.none");
			return;
		}

		int maxVotes = Collections.max(this.votes.values());
		if (this.abstainVotes >= maxVotes) {
			this.phase.sendGameMessage("action.lynch.announce.abstain");
			return;
		}

		List<PlayerEntry> possibleLynches = this.getPossibleLynches(maxVotes);

		if (possibleLynches.size() == 0) {
			this.phase.sendGameMessage("action.lynch.announce.none");
		} else if (possibleLynches.size() == 1) {
			PlayerEntry toLynch = possibleLynches.get(0);
			toLynch.onLynched();
		} else {
			this.phase.sendGameMessage("action.lynch.announce.tie");
		}
	}
}
