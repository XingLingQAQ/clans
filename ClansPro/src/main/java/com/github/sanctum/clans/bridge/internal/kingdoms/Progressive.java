package com.github.sanctum.clans.bridge.internal.kingdoms;

import com.github.sanctum.clans.bridge.ClanAddon;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Progressive {

	protected static final List<Progressive> PROGRESSIVES = new LinkedList<>();

	abstract public @NotNull String getName();

	abstract public int getLevel();

	abstract public @NotNull List<Quest> getQuests();

	abstract public @Nullable Quest getQuest(String title);

	abstract public void loadQuest(Quest... quests);

	abstract public void save(ClanAddon cycle);

	public static List<Progressive> getProgressives() {
		return Collections.unmodifiableList(PROGRESSIVES);
	}

	public static void capture(Progressive progressive) {
		PROGRESSIVES.add(progressive);
	}

}