package com.github.sanctum.clans.event;

import com.github.sanctum.clans.construct.api.Clan;
import com.github.sanctum.clans.event.player.PlayerEvent;
import com.github.sanctum.labyrinth.event.custom.Vent;
import java.util.UUID;

/**
 * A both asynchronous & synchronous concurrent timer abstraction.
 *
 * <pre>
 * (Manual checks required via {@link Vent#isAsynchronous()}!)
 *
 * The ability to listen to this event should be possible at all times!
 * The sole purpose for this abstraction is to run in the background on another thread.
 */
public abstract class TimerEvent extends PlayerEvent {

	public TimerEvent(UUID id, boolean isAsync) {
		super(id, isAsync);
	}

	@Override
	public Clan getClan() {
		return null;
	}
}
