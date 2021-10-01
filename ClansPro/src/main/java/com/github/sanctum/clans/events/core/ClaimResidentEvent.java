package com.github.sanctum.clans.events.core;

import com.github.sanctum.clans.construct.Claim;
import com.github.sanctum.clans.construct.DataManager;
import com.github.sanctum.clans.construct.actions.ClaimAction;
import com.github.sanctum.clans.construct.actions.ClanAction;
import com.github.sanctum.clans.construct.api.Clan;
import com.github.sanctum.clans.construct.api.ClansAPI;
import com.github.sanctum.clans.construct.extra.StringLibrary;
import com.github.sanctum.clans.construct.impl.Resident;
import com.github.sanctum.clans.events.ClanEventBuilder;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class ClaimResidentEvent extends ClanEventBuilder {

	private final HashMap<String, String> titleContext = new HashMap<>();

	private final Player p;

	private final Resident r;

	private final Claim claim;

	private boolean titlesAllowed = DataManager.titlesAllowed();

	public ClaimResidentEvent(Player p) {
		this.p = p;
		this.claim = Claim.from(p.getLocation());
		if (ClansAPI.getData().RESIDENTS.stream().noneMatch(r -> r.getPlayer().getName().equals(p.getName()))) {
			Resident res = new Resident(p);
			res.setLastKnownClaim(this.claim);
			r = res;
			ClansAPI.getData().RESIDENTS.add(r);
		} else {
			r = ClansAPI.getData().RESIDENTS.stream().filter(r -> r.getPlayer().getName().equals(p.getName())).findFirst().orElse(null);
		}
	}

	{
		if (!titleContext.containsKey("TITLE") || !titleContext.containsKey("SUB-TITLE")) {
			titleContext.put("TITLE", "&3&oClaimed land");
			titleContext.put("SUB-TITLE", "&7Owned by: &b%s");
		}
	}

	public void setTitlesAllowed(boolean b) {
		this.titlesAllowed = b;
	}

	public void setClaimTitle(String title, String subtitle) {
		titleContext.put("TITLE", title);
		titleContext.put("SUB-TITLE", subtitle);
	}

	public String getClaimTitle() {
		return titleContext.get("TITLE");
	}

	public String getClaimSubTitle() {
		return titleContext.get("SUB-TITLE");
	}

	public Claim getClaim() {
		return claim;
	}

	public Collection<Chunk> getChunksAroundPlayer(int xoff, int yoff, int zoff) {
		int[] offset = {xoff, yoff, zoff};

		World world = p.getLocation().getWorld();
		int baseX = p.getLocation().getChunk().getX();
		int baseZ = p.getLocation().getChunk().getZ();

		Collection<Chunk> chunksAroundPlayer = new HashSet<>();
		for (int x : offset) {
			for (int z : offset) {
				Chunk chunk = world.getChunkAt(baseX + x, baseZ + z);
				chunksAroundPlayer.add(chunk);
			}
		}
		return chunksAroundPlayer;
	}

	public boolean isTitleAllowed() {
		return titlesAllowed;
	}

	public Resident getResident() {
		return r;
	}

	public ClanAction getUtil() {
		return Clan.ACTION;
	}

	@Override
	public StringLibrary stringLibrary() {
		return Clan.ACTION;
	}

	public ClaimAction getClaimUtil() {
		return Claim.ACTION;
	}

	public void sendNotification() {
		String clanName = ClansAPI.getInstance().getClanName(getClaim().getOwner());
		String color;
		if (ClansAPI.getInstance().getClanID(p.getUniqueId()) != null) {
			color = getUtil().getRelationColor(ClansAPI.getInstance().getClan(p.getUniqueId()), getClaim().getClan());
		} else {
			color = "&f&o";
		}
		if (titlesAllowed) {
			titleContext.put("TITLE", MessageFormat.format(ClansAPI.getData().getMain().getConfig().getString("Clans.land-claiming.in-land.title"), clanName, color));
			titleContext.put("SUB-TITLE", MessageFormat.format(ClansAPI.getData().getMain().getConfig().getString("Clans.land-claiming.in-land.sub-title"), clanName, color));
			p.sendTitle(getClaimUtil().color(titleContext.get("TITLE")), getClaimUtil().color(titleContext.get("SUB-TITLE")), 10, 25, 10);
		}
		if (ClansAPI.getData().isTrue("Clans.land-claiming.send-messages")) {
			getClaimUtil().sendMessage(p, MessageFormat.format(ClansAPI.getData().getMain().getConfig().getString("Clans.land-claiming.in-land.message"), clanName, color));
		}
	}

	@Override
	public String getName() {
		return getClass().getSimpleName();
	}

}
