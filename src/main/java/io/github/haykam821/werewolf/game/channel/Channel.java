package io.github.haykam821.werewolf.game.channel;

import java.util.List;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public abstract class Channel {
	public abstract List<ServerPlayerEntity> getTargets();

	public abstract String getTranslationKey();

	public Formatting getPrefixColor() {
		return Formatting.GREEN;
	}

	public Formatting getTextColor() {
		return Formatting.GRAY;
	}

	private Text getColoredText(Text text, boolean textColor) {
		return textColor ? text.copy().formatted(this.getTextColor()) : text;
	}

	public Text getWrappedText(Text text, boolean textColor) {
		return Text.translatable(this.getTranslationKey(), this.getColoredText(text, textColor)).formatted(this.getPrefixColor());
	}

	public void sendMessage(Text message, boolean textColor) {
		Text wrappedMessage = this.getWrappedText(message, textColor);
		for (ServerPlayerEntity target : this.getTargets()) {
			target.sendMessage(wrappedMessage, false);
		}
	}

	public void sendMessage(Text message) {
		this.sendMessage(message, true);
	}

	public void sendMessage(String key, Object... args) {
		this.sendMessage(Text.translatable(key, args));
	}
}