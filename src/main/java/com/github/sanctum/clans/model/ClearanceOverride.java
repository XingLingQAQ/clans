package com.github.sanctum.clans.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.sanctum.panther.annotation.Ordinal;
import com.github.sanctum.panther.container.PantherMap;
import com.github.sanctum.panther.file.Configurable;
import com.github.sanctum.panther.util.OrdinalProcedure;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class ClearanceOverride {

	final ClearanceData data;

	public ClearanceOverride() {
		this.data = new ClearanceData();
	}

	public ClearanceOverride(@NotNull ClearanceOverride.ClearanceData data) {
		this.data = data;
		if (data.rankMap.isEmpty()) {
			RankRegistry registry = RankRegistry.getInstance();
			registry.getRanks().forEach(this::loadClearances); // called one time.
		}
	}

	public boolean add(@NotNull Clearance clearance, @NotNull Clan.Rank position) {
		if (!data.rankMap.containsKey(position.getName())) {
			loadClearances(position);
		}
		List<String> perms = data.rankMap.get(position.getName());
		if (!perms.contains(clearance.getName())) {
			perms.add(clearance.getName());
			return true;
		}
		return false;
	}

	public boolean remove(@NotNull Clearance clearance, @NotNull Clan.Rank position) {
		if (!data.rankMap.containsKey(position.getName())) {
			loadClearances(position);
		}
		List<String> perms = data.rankMap.get(position.getName());
		if (perms.contains(clearance.getName())) {
			perms.remove(clearance.getName());
			return true;
		}
		return false;
	}

	@Ordinal(2)
	boolean remove(@NotNull String rank) {
		if (data.rankMap.containsKey(rank)) {
			data.rankMap.remove(rank);
			return true;
		} else return false;
	}

	public boolean addInheritance(@NotNull Clan.Rank child, @NotNull Clan.Rank parent) {
		List<String> inheritance = getInheritanceNames(parent);
		if (!inheritance.contains(child.getName())) {
			inheritance.add(child.getName());
			return true;
		}
		return false;
	}

	public boolean removeInheritance(@NotNull Clan.Rank child, @NotNull Clan.Rank parent) {
		List<String> inheritance = getInheritanceNames(parent);
		if (inheritance.contains(child.getName())) {
			inheritance.remove(child.getName());
			return true;
		}
		return false;
	}

	void loadClearances(Clan.Rank p) {// this loads default clearances
		List<String> clearances = new ArrayList<>();
		for (Clearance c : p.getDefaultPermissions()) {
			clearances.add(c.getName());
		}
		for (Clan.Rank pos : p.getInheritance()) {
			addInheritance(pos, p);
		}
		data.rankMap.put(p.getName(), clearances);
	}

	List<String> getInheritanceNames(Clan.Rank position) {
		if (!data.inheritanceMap.containsKey(position.getName())) {
			data.inheritanceMap.put(position.getName(), new ArrayList<>());
		}
		return data.inheritanceMap.get(position.getName());
	}

	List<String> getPermissions(Clan.Rank position, boolean combine) {// clan permit Owner MANAGE_LAND
		if (!data.rankMap.containsKey(position.getName())) {  // clan inherit [groupToInherit] [group] ->
			loadClearances(position);
		}
		RankRegistry registry = RankRegistry.getInstance();
		// make new list to live grab inheritance nodes from positions
		List<String> list = new ArrayList<>(data.rankMap.get(position.getName()));
		// quick check to remove old inheritance that doesn't exist.
		if (combine) {
			List<String> toRemove = new ArrayList<>();
			List<String> inheritance = getInheritanceNames(position);
			inheritance.forEach(rank -> {
				Clan.Rank asRank = registry.getRank(rank);
				if (asRank != null) {
					List<String> individualPermList = getPermissions(asRank, false);
					list.addAll(individualPermList);
				} else {
					// this code should only be ran when a rank is found to be missing.
					// slightly heavier operation with IO Scan but only circumstantial.
					// instead of removing here immediately lets try putting in its replacement
					// Goal: go through all ranks and look for a match of 'old-name'
					Configurable ranks = ClansAPI.getInstance().getFileList().get("Ranks", "Configuration").getRoot();
					for (String rankSection : ranks.getKeys(false)) {
						String old_name = ranks.getNode(rankSection).getNode("old_name").toPrimitive().getString();
						if (old_name.equals(rank)) {
							// we found a match lets break out and replace the inheritance
							Clan.Rank asNewRank = registry.getRank(ranks.getNode(rankSection).getNode("name").toPrimitive().getString());
							// double check the new rank is in cache.
							if (asNewRank != null) {
								List<String> individualPermList = getPermissions(asNewRank, false);
								list.addAll(individualPermList);
								break;
							} else {
								toRemove.add(rank);
							}
						}
					}
				}
			});
			for (String t : toRemove) {
				inheritance.remove(t);
			}
		}
		return list;
	}

	/**
	 * @param position
	 * @return
	 */
	public @NotNull List<Clearance> get(@NotNull Clan.Rank position) {
		return getPermissions(position, true).stream().map(Clearance::valueOf).collect(Collectors.toList());
	}

	/**
	 * @param position
	 * @return
	 */
	public @NotNull List<Clearance> getRaw(@NotNull Clan.Rank position) {
		return getPermissions(position, false).stream().map(Clearance::valueOf).collect(Collectors.toList());
	}

	/**
	 * @param rank
	 * @return
	 */
	public @NotNull List<Clan.Rank> getInheritance(@NotNull Clan.Rank rank) {
		return getInheritanceNames(rank).stream().map(s -> {
			Bukkit.getLogger().severe("Got rank " + s + " loaded into '" + rank + "'s' inheritance");
			return RankRegistry.getInstance().getRank(s);
		}).collect(Collectors.toList());
	}

	public static final class ClearanceData implements Serializable {
		private static final long serialVersionUID = -7497706686470009039L;
		public Map<String, List<String>> rankMap = new HashMap<>();
		public Map<String, List<String>> inheritanceMap = new HashMap<>();
	}

}
