package com.github.sanctum.clans.events.core;

import com.github.sanctum.clans.construct.ClanAssociate;
import com.github.sanctum.clans.events.ClanEventBuilder;

public class ClanLeaveEvent extends ClanEventBuilder {

	private final ClanAssociate associate;

	public ClanLeaveEvent(ClanAssociate associate) {
		this.associate = associate;
	}

	public ClanAssociate getAssociate() {
		return associate;
	}

	@Override
	public String getName() {
		return getClass().getSimpleName();
	}

}
