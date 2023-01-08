package io.github.haykam821.werewolf.game.channel;

import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;

public abstract class ChatChannel extends Channel {
	public abstract RegistryKey<MessageType> getMessageType();

	public void sendChatMessage(ServerPlayerEntity sender, SignedMessage message) {
		SentMessage sent = SentMessage.of(message);
		MessageType.Parameters params = MessageType.params(this.getMessageType(), sender);

		for (ServerPlayerEntity target : this.getTargets()) {
			target.sendChatMessage(sent, sender.shouldFilterMessagesSentTo(target), params);
		}
	}
}
