package com.github.sanctum.clans.event.associate;

import com.github.sanctum.clans.construct.api.Channel;
import com.github.sanctum.clans.construct.api.Clan;

/**
 * Called when a clan associate receives a message from a chat channel
 */
public class AssociateMessageReceiveEvent extends AssociateEvent {

	private final Clan.Associate sender;
	private final String message;
	private final Channel channel;

	public AssociateMessageReceiveEvent(Clan.Associate associate, Clan.Associate sender, Channel channel, String message) {
		super(associate, true);
		this.sender = sender;
		this.message = message;
		this.channel = channel;
	}

	public Clan.Associate getSender() {
		return sender;
	}

	public String getMessage() {
		return message;
	}

	public Channel getChannel() {
		return this.channel;
	}
}
