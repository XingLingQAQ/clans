package com.github.sanctum.clans.commands;

import com.github.sanctum.clans.model.ClanVentBus;
import com.github.sanctum.clans.DataManager;
import com.github.sanctum.clans.model.Clan;
import com.github.sanctum.clans.model.ClanSubCommand;
import com.github.sanctum.clans.model.ClansAPI;
import com.github.sanctum.clans.model.Clearance;
import com.github.sanctum.clans.util.AboveHeadDisplayName;
import com.github.sanctum.clans.util.StringLibrary;
import com.github.sanctum.clans.event.associate.AssociateRenameClanEvent;
import com.github.sanctum.labyrinth.LabyrinthProvider;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class CommandTag extends ClanSubCommand {
	public CommandTag() {
		super("tag");
		setUsage(ClansAPI.getDataInstance().getMessageString("Commands.tag.text"));
	}

	@Override
	public boolean player(Player p, String label, String[] args) {
		StringLibrary lib = Clan.ACTION;
		Clan.Associate associate = ClansAPI.getInstance().getAssociate(p).orElse(null);

		if (args.length == 0) {
			if (!Clan.ACTION.test(p, this.getPermission() + "." + DataManager.Security.getPermission("tag")).deploy()) {
				lib.sendMessage(p, lib.noPermission(this.getPermission() + "." + DataManager.Security.getPermission("tag")));
				return true;
			}
			lib.sendMessage(p, lib.commandTag());
			return true;
		}

		if (args.length == 1) {
			if (!Clan.ACTION.test(p, this.getPermission() + "." + DataManager.Security.getPermission("tag")).deploy()) {
				lib.sendMessage(p, lib.noPermission(this.getPermission() + "." + DataManager.Security.getPermission("tag")));
				return true;
			}
			if (associate != null) {
				Clan clan = associate.getClan();
				if (Clearance.MANAGE_NAME.test(associate)) {
					if (!isAlphaNumeric(args[0])) {
						lib.sendMessage(p, lib.nameInvalid(args[0]));
						return true;
					}
					if (args[0].length() > ClansAPI.getDataInstance().getConfig().read(f -> f.getInt("Formatting.tag-size"))) {
						Clan.ACTION.sendMessage(p, lib.nameTooLong(args[0]));
						return true;
					}
					if (Clan.ACTION.getAllClanNames().contains(args[0])) {
						lib.sendMessage(p, lib.alreadyMade(args[0]));
						return true;
					}
					for (String s : ClansAPI.getDataInstance().getConfig().getRoot().getNode("Clans.name-blacklist").getKeys(false)) {
						if (Pattern.compile(Pattern.quote(args[0]), Pattern.CASE_INSENSITIVE).matcher(s).find()) {
							lib.sendMessage(p, "&c&oThis name is not allowed!");
							String response = ClansAPI.getDataInstance().getConfig().getRoot().getNode("Clans.name-blacklist").getNode(s).getNode("action").toPrimitive().getString();
							if (response != null && !response.isEmpty()) {
								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), response.replace("{PLAYER}", p.getName()));
							}
							return true;
						}
					}
					AssociateRenameClanEvent ev = ClanVentBus.call(new AssociateRenameClanEvent(p, clan.getName(), args[0]));
					if (!ev.isCancelled()) {
						clan.setName(ev.getTo());
					}
					if (!LabyrinthProvider.getInstance().isLegacy()) {
						clan.getMembers().forEach(a -> {
							OfflinePlayer op = a.getTag().getPlayer();
							if (op.isOnline()) {
								if (ClansAPI.getDataInstance().isDisplayTagsAllowed()) {
									if (clan.getPalette().isGradient()) {
										Clan c = a.getClan();
										AboveHeadDisplayName.set(op.getPlayer(), ClansAPI.getDataInstance().formatDisplayTag("", c.getPalette().toGradient().context(c.getName()).translate()));
									} else {
										AboveHeadDisplayName.set(op.getPlayer(), ClansAPI.getDataInstance().formatDisplayTag(ClansAPI.getInstance().getClanManager().getClan(op.getUniqueId()).getPalette().toString(), ClansAPI.getInstance().getClanManager().getClan(op.getUniqueId()).getName()));

									}
								}
							}
						});
					}
				} else {
					lib.sendMessage(p, lib.notEnoughClearance());
				}
			} else {
				lib.sendMessage(p, lib.notInClan());
				return true;
			}
			return true;
		}


		return true;
	}
}
