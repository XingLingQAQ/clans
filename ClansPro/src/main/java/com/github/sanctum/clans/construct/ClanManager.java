package com.github.sanctum.clans.construct;

import com.github.sanctum.clans.bridge.ClanVentBus;
import com.github.sanctum.clans.construct.api.Clan;
import com.github.sanctum.clans.construct.api.ClansAPI;
import com.github.sanctum.clans.construct.extra.ClanRosterElement;
import com.github.sanctum.clans.construct.impl.DefaultClan;
import com.github.sanctum.clans.event.clan.ClansLoadingProcedureEvent;
import com.github.sanctum.labyrinth.data.FileManager;
import com.github.sanctum.labyrinth.data.LabyrinthUser;
import com.github.sanctum.labyrinth.formatting.UniformedComponents;
import com.github.sanctum.labyrinth.library.HUID;
import com.github.sanctum.labyrinth.task.Schedule;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.bukkit.OfflinePlayer;

public final class ClanManager {

	private final List<Clan> CLANS = new LinkedList<>();
	private final ClanRosterElement element;

	public ClanManager() {
		this.element = new ClanRosterElement(CLANS);
	}

	public UniformedComponents<Clan> getClans() {
		return this.element.update(CLANS);
	}

	/**
	 * Converts and clan id into a clan name
	 *
	 * @param clanID The clan id to convert
	 * @return A clan name or null
	 */
	public String getClanName(HUID clanID) {
		Clan c = getClan(clanID);
		return c != null ? c.getNode("name").toPrimitive().getString() : null;
	}

	/**
	 * Converts a clan name into a clan id
	 *
	 * @param clanName The clan tag to convert
	 * @return A clan id or null
	 */
	public HUID getClanID(String clanName) {
		for (Clan c : getClans().list()) {
			if (c.getName().equals(clanName)) {
				return c.getId();
			}
		}
		return null;
	}

	/**
	 * Get the bare id object for a player's given clan.
	 *
	 * @param uuid The player to search for.
	 * @return A clan id or null
	 */
	public HUID getClanID(UUID uuid) {
		Clan.Associate associate = ClansAPI.getInstance().getAssociate(uuid).orElse(null);
		if (associate != null && associate.isValid()) {
			return associate.getClan().getId();
		}
		return null;
	}

	/**
	 * Gets a clan object from a unique user id if found.
	 *
	 * @param target The target to look for
	 * @return A clan object or null
	 */
	public Clan getClan(UUID target) {
		for (Clan c : getClans().list()) {
			if (c.getMember(m -> m.getId().equals(target)) != null) {
				return c;
			}
		}
		return null;
	}

	/**
	 * Gets a clan object from a clan id
	 *
	 * @param clanID The clan id to convert
	 * @return A clan object or null
	 */
	public Clan getClan(HUID clanID) {
		Clan clan = null;
		for (Clan c : getClans().list()) {
			if (c.getId().equals(clanID)) {
				clan = c;
			}
		}
		return clan;
	}

	/**
	 * Gets a clan object from an offline-player if they're in one.
	 *
	 * @param player The player to search for.
	 * @return A clan object or null
	 */
	public Clan getClan(OfflinePlayer player) {
		return getClan(LabyrinthUser.get(player.getName()).getId());
	}

	/**
	 * Load a custom implementation of a clan into cache.
	 *
	 * @param c The clan object to load into cache.
	 * @return true if the clan successfully got added to cache.
	 */
	public boolean load(Clan c) {
		if (c == null) throw new IllegalArgumentException("The provided clan cannot be null!");

		if (CLANS.stream().noneMatch(cl -> cl.getName().equals(c.getName()))) {
			CLANS.add(c);
			return true;
		}

		return false;
	}

	public boolean unload(Clan c) {
		return CLANS.remove(c);
	}

	public <T extends Clan> T cast(Class<T> clanImpl, Clan clan) {
		if (clanImpl.isAssignableFrom(clan.getClass())) {
			return (T) clan;
		}
		return null;
	}

	/**
	 * Delete a clan from cache.
	 * <p>
	 * If the specified clan shares a persistent data space it will also get removed.
	 *
	 * @param c The clan to delete.
	 * @return true if the clan was able to be removed.
	 */
	public synchronized boolean delete(Clan c) {
		try {
			for (Clan.Associate associate : c.getMembers()) {
				if (!associate.getId().equals(c.getOwner().getId())) {
					associate.remove();
				}
			}
			final FileManager m = ClansAPI.getDataInstance().getClanFile(c);
			Schedule.sync(() -> {
				if (!m.getRoot().delete()) {
					ClansAPI.getInstance().getPlugin().getLogger().warning("- Something is wrong server side a clan failed to delete.");
				}
			}).run();
			return CLANS.remove(c);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Clears the clan and associate cache base and reloads from file.
	 */
	public int refresh() {
		for (Clan c : CLANS) {
			try {
				c.save();
			} catch (Exception ignored) {
			}
		}
		CLANS.clear();
		Set<Clan> clans = new HashSet<>();
		for (String clanID : Clan.ACTION.getAllClanIDs()) {
			DefaultClan instance = new DefaultClan(clanID);
			clans.add(instance);
		}
		ClansLoadingProcedureEvent loading = ClanVentBus.call(new ClansLoadingProcedureEvent(clans));
		loading.getClans().forEach(this::load);
		return loading.getClans().size();
	}

}
