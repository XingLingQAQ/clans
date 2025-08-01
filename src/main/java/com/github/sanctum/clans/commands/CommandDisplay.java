package com.github.sanctum.clans.commands;

import com.github.sanctum.clans.DataManager;
import com.github.sanctum.clans.model.Clan;
import com.github.sanctum.clans.model.ClanSubCommand;
import com.github.sanctum.clans.model.ClansAPI;
import com.github.sanctum.clans.model.Clearance;
import com.github.sanctum.clans.util.StringLibrary;
import org.bukkit.entity.Player;

public class CommandDisplay extends ClanSubCommand {
	public CommandDisplay() {
		super("display");
		setUsage(ClansAPI.getDataInstance().getMessageString("Commands.display.text"));
		setInvisible(ClansAPI.getInstance().isTrial());
	}

	@Override
	public boolean player(Player p, String label, String[] args) {
		StringLibrary lib = Clan.ACTION;
		Clan.Associate associate = ClansAPI.getInstance().getAssociate(p).orElse(null);

		if (associate == null) {
			sendMessage(p, lib.notInClan());
			return true;
		}

		if (args.length == 0) {
			if (!Clan.ACTION.test(p, this.getPermission() + "." + DataManager.Security.getPermission("display")).deploy()) {
				lib.sendMessage(p, lib.noPermission(this.getPermission() + "." + DataManager.Security.getPermission("display")));
				return true;
			}
			lib.sendMessage(p, ClansAPI.getDataInstance().getMessageResponse("display"));
			return true;
		}

		if (args.length == 1) {
			if (!Clan.ACTION.test(p, this.getPermission() + "." + DataManager.Security.getPermission("display")).deploy()) {
				lib.sendMessage(p, lib.noPermission(this.getPermission() + "." + DataManager.Security.getPermission("display")));
				return true;
			}
			if (Clearance.MANAGE_NICK_NAME.test(associate)) {
				if (ClansAPI.getInstance().isNameBlackListed(args[0])) {
					lib.sendMessage(p, "&4This name is not allowed!");
					return true;
				}
				if (args[0].equalsIgnoreCase("reset") || args[0].equalsIgnoreCase("empty")) {
					associate.getClan().setNickname(null);
					associate.getClan().broadcast("Our nickname has been reset.");
					return true;
				}
				associate.getClan().setNickname(args[0]);
				associate.getClan().broadcast("Our new nickname has been updated to '" + associate.getClan().getNickname() + "'");
			} else {
				lib.sendMessage(p, lib.notEnoughClearance());
			}
			return true;
		}

		if (args.length == 2) {
			if (!Clan.ACTION.test(p, this.getPermission() + "." + DataManager.Security.getPermission("display")).deploy()) {
				lib.sendMessage(p, lib.noPermission(this.getPermission() + "." + DataManager.Security.getPermission("display")));
				return true;
			}
			if (Clearance.MANAGE_NICK_NAME.test(associate)) {
				if (ClansAPI.getInstance().isNameBlackListed(args[0]) || ClansAPI.getInstance().isNameBlackListed(args[1])) {
					lib.sendMessage(p, "&4This name is not allowed!");
					return false;
				}
				associate.getClan().setNickname(args[0] + " " + args[1]);
				associate.getClan().broadcast("Our new nickname has been updated to '" + associate.getClan().getNickname() + "'");
			} else {
				lib.sendMessage(p, lib.notEnoughClearance());
			}
			return true;
		}

		return true;
	}
}
