package com.github.sanctum.clans.model.addon.borders.event;

import com.github.sanctum.clans.model.addon.borders.BorderListener;
import com.github.sanctum.clans.model.addon.borders.BorderRegion;
import com.github.sanctum.clans.model.Claim;
import com.github.sanctum.clans.model.Clan;
import com.github.sanctum.clans.model.ClansAPI;
import com.github.sanctum.labyrinth.data.container.Cuboid;
import com.github.sanctum.labyrinth.data.container.Region;
import com.github.sanctum.panther.util.HUID;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class BorderTaskEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;
	private final Player p;

	public BorderTaskEvent(Player p) {
		this.p = p;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public boolean isInClaim() {
		return ClansAPI.getInstance().getClaimManager().isInClaim(p.getLocation());
	}

	public Claim getClaim() {
		return ClansAPI.getInstance().getClaimManager().getClaim(p.getLocation());
	}

	public Player getUser() {
		return p;
	}

	public void perform() {
		// receive borders
		Clan.Associate associate = ClansAPI.getInstance().getAssociate(p).orElse(null);
		if (ClansAPI.getInstance().getClaimManager().isInClaim(p.getLocation())) {
			Claim claim = getClaim();
			int cy1 = p.getLocation().getBlockY() + 5;
			int cy2 = p.getLocation().getBlockY() - 5;
			int cx1 = claim.getChunk().getX() * 16;
			int cz1 = claim.getChunk().getZ() * 16;
			int cx2 = claim.getChunk().getX() * 16 + 16;
			int cz2 = claim.getChunk().getZ() * 16 + 16;
			Region r = new BorderRegion(cx1, cx2, cy2, cy1, cz1, cz2, claim.getChunk().getWorld(), HUID.randomID());
			Cuboid.VisualBoundary boundary = r.getBoundary(p);
			if (associate != null) {

				if (claim.getOwner().getTag().getId().equals(associate.getClan().getId().toString())) {
					int i = 0;
					i++;
					p.getWorld().spawnParticle(Particle.HEART, p.getLocation().getX(), p.getEyeLocation().getY() + 0.5, p.getLocation().getZ(), 1);
					boundary.deploy(Cuboid.VisualBoundary.Color.GREEN);
					boundary.deploy(action -> action.getPlayer().spawnParticle(Particle.HEART, action.getX(), action.getY(), action.getZ(), 1));
					double add = p.getHealth() + i;
					int addF = p.getFoodLevel() + i;
					if (addF <= 20) {
						p.setFoodLevel(addF);
						i = 0;
					}
					if (add <= 20) {
						p.setHealth(add);
						i = 0;
					}
					return;
				}
				if (((Clan)claim.getHolder()).getRelation().isNeutral(associate.getClan())) {
					boundary.deploy(Cuboid.VisualBoundary.Color.WHITE);
				} else {
					if (((Clan)claim.getHolder()).getRelation().getAlliance().has(associate.getClan())) {
						boundary.deploy(Cuboid.VisualBoundary.Color.GREEN);
					}
					if (((Clan)claim.getHolder()).getRelation().getRivalry().has(associate.getClan())) {
						boundary.deploy(Cuboid.VisualBoundary.Color.RED);
					}
				}
			} else {
				boundary.deploy(Cuboid.VisualBoundary.Color.WHITE);
			}
		} else {
			int cy1 = p.getLocation().getBlockY() + 5;
			int cy2 = p.getLocation().getBlockY() - 5;
			int cx1 = p.getLocation().getChunk().getX() * 16;
			int cz1 = p.getLocation().getChunk().getZ() * 16;
			int cx2 = p.getLocation().getChunk().getX() * 16 + 16;
			int cz2 = p.getLocation().getChunk().getZ() * 16 + 16;
			Region r = new BorderRegion(cx1, cx2, cy2, cy1, cz1, cz2, p.getWorld(), HUID.randomID());
			Cuboid.VisualBoundary boundary = r.getBoundary(p);
			boundary.setViewer(p);
			boundary.deploy(Cuboid.VisualBoundary.Color.YELLOW);
			if (associate != null) {
				Clan clan = associate.getClan();
				Location base = clan.getBase();
				if (base != null) {
					if (BorderListener.baseLocate.contains(p.getUniqueId())) {
						boundary.laser(action -> action.getPlayer().spawnParticle(Particle.FLAME, action.getX(), action.getY(), action.getZ(), 2), base);
					}
				}
			}
			if (BorderListener.spawnLocate.contains(p.getUniqueId())) {
				boundary.laser(action -> action.getPlayer().spawnParticle(Particle.FLAME, action.getX(), action.getY(), action.getZ(), 2), p.getWorld().getSpawnLocation());
			}
			if (BorderListener.playerLocate.contains(p.getUniqueId())) {
				for (Entity e : p.getNearbyEntities(1000, 10, 1000)) {
					if (e instanceof Player) {
						Player target = (Player) e;
						boundary.laser(action -> action.getPlayer().spawnParticle(Particle.FLAME, action.getX(), action.getY(), action.getZ(), 2), target.getLocation());
					}
				}

			}
		}
	}


	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}
}
