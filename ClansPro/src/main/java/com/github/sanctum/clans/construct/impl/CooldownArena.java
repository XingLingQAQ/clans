package com.github.sanctum.clans.construct.impl;

import com.github.sanctum.clans.construct.api.Clan;
import com.github.sanctum.clans.construct.api.ClanCooldown;
import com.github.sanctum.clans.construct.api.ClansAPI;
import com.github.sanctum.labyrinth.data.FileManager;
import com.github.sanctum.labyrinth.library.HUID;

public class CooldownArena extends ClanCooldown {

	private int time;

	@Override
	public String getId() {
		return HUID.randomID().toString();
	}

	@Override
	public String getAction() {
		return "Clans:arena-timer";
	}

	public void setTime(int time) {
		this.time = time;
	}

	@Override
	public void setCooldown() {
		FileManager config = ClansAPI.getData().arenaFile();
		config.getConfig().set("Time-allotted", System.currentTimeMillis() + (time * 1000));
		config.saveConfig();
	}

	@Override
	public long getCooldown() {
		return ClansAPI.getData().arenaFile().getConfig().getLong("Time-allotted");
	}

	@Override
	public String fullTimeLeft() {
		return Clan.ACTION.format(Clan.ACTION.format(Clan.ACTION.format(Clan.ACTION.format(ClansAPI.getData().getMessage("cooldown-active"), "%d", String.valueOf(getDaysLeft())), "%h", String.valueOf(getHoursLeft())), "%m", String.valueOf(getMinutesLeft())), "%s", String.valueOf(getSecondsLeft()));
	}

	@Override
	public ClanCooldown getInstance() {
		return this;
	}
}
