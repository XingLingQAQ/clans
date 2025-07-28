package com.github.sanctum.clans.commands;

import com.github.sanctum.clans.DataManager;
import com.github.sanctum.clans.model.Claim;
import com.github.sanctum.clans.model.Clan;
import com.github.sanctum.clans.model.ClanSubCommand;
import com.github.sanctum.clans.model.ClansAPI;
import com.github.sanctum.clans.model.Clearance;
import com.github.sanctum.clans.util.StringLibrary;
import com.github.sanctum.labyrinth.formatting.completion.SimpleTabCompletion;
import com.github.sanctum.labyrinth.formatting.completion.TabCompletionIndex;
import java.util.List;
import org.bukkit.entity.Player;

public class CommandUnclaim extends ClanSubCommand {
	public CommandUnclaim() {
		super("unclaim");
		setUsage(ClansAPI.getDataInstance().getMessageString("Commands.unclaim.text"));
	}

	@Override
	public boolean player(Player p, String label, String[] args) {
		StringLibrary lib = Clan.ACTION;
		Clan.Associate associate = ClansAPI.getInstance().getAssociate(p).orElse(null);

		if (args.length == 0) {
			if (!Clan.ACTION.test(p, this.getPermission() + "." + DataManager.Security.getPermission("unclaim")).deploy()) {
				lib.sendMessage(p, lib.noPermission(this.getPermission() + "." + DataManager.Security.getPermission("unclaim")));
				return true;
			}
			if (associate != null) {
				if (Claim.ACTION.isAllowed().deploy()) {
					if (Clearance.MANAGE_LAND.test(associate)) {
						Claim.ACTION.unclaim(p).run();
					} else {
						lib.sendMessage(p, lib.notEnoughClearance());
					}
				} else {
					lib.sendMessage(p, "&c&oYour server doesn't allow the use of clan land-claiming.");
					return true;
				}
			} else {
				lib.sendMessage(p, lib.notInClan());
			}
			return true;
		}

		if (args.length == 1) {
			if (Claim.ACTION.isAllowed().deploy()) {
				if (args[0].equalsIgnoreCase("all")) {
					if (!Clan.ACTION.test(p, this.getPermission() + "." + DataManager.Security.getPermission("unclaimall")).deploy()) {
						lib.sendMessage(p, lib.noPermission(this.getPermission() + "." + DataManager.Security.getPermission("unclaimall")));
						return true;
					}
					if (associate != null) {
						if (Clearance.MANAGE_ALL_LAND.test(associate)) {
							Claim.ACTION.unclaimAll(p).run();
						} else {
							lib.sendMessage(p, lib.notEnoughClearance());
							return true;
						}
					} else {
						lib.sendMessage(p, lib.notInClan());
						return true;
					}
					return true;
				}
			} else {
				lib.sendMessage(p, "&c&oYour server doesn't allow the use of clan land-claiming.");
				return true;
			}
			return true;
		}


		return true;
	}

	@Override
	public List<String> tab(Player p, String label, String[] args) {
		return SimpleTabCompletion.of(args).then(TabCompletionIndex.ONE, getBaseCompletion(args))
				.then(TabCompletionIndex.TWO, getLabel(), TabCompletionIndex.ONE, "all")
				.get();
	}
}
