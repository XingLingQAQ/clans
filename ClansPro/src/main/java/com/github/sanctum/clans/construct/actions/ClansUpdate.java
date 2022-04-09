package com.github.sanctum.clans.construct.actions;

import com.github.sanctum.labyrinth.library.UpdateChecker;
import org.bukkit.plugin.Plugin;

public class ClansUpdate extends UpdateChecker {

	private static final long serialVersionUID = -7156849826532257063L;

	public ClansUpdate(Plugin plugin) {
		super(plugin, 87515);
	}

	@Override
	public String getAuthor() {
		return "Hempfest";
	}

	@Override
	public int getId() {
		return 87515;
	}
}
