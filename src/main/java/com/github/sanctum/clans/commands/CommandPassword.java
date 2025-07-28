package com.github.sanctum.clans.commands;

import com.github.sanctum.clans.DataManager;
import com.github.sanctum.clans.model.Clan;
import com.github.sanctum.clans.model.ClanSubCommand;
import com.github.sanctum.clans.model.ClansAPI;
import com.github.sanctum.clans.model.Clearance;
import com.github.sanctum.clans.util.StringLibrary;
import org.bukkit.entity.Player;

public class CommandPassword extends ClanSubCommand {
	public CommandPassword() {
		super("password");
		setUsage(ClansAPI.getDataInstance().getMessageString("Commands.password.text"));
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
			if (!Clan.ACTION.test(p, this.getPermission() + "." + DataManager.Security.getPermission("password")).deploy()) {
				lib.sendMessage(p, lib.noPermission(this.getPermission() + "." + DataManager.Security.getPermission("password")));
				return true;
			}
			lib.sendMessage(p, lib.commandPassword());
		}

		if (args.length == 1) {
			if (!Clan.ACTION.test(p, this.getPermission() + "." + DataManager.Security.getPermission("password")).deploy()) {
				lib.sendMessage(p, lib.noPermission(this.getPermission() + "." + DataManager.Security.getPermission("password")));
				return true;
			}
			Clan clan = associate.getClan();
			if (Clearance.MANAGE_PASSWORD.test(associate)) {
				if (!isAlphaNumeric(args[0])) {
					lib.sendMessage(p, lib.invalidPasswordFormat());
					return true;
				}
				clan.setPassword(args[0]);
			} else {
				lib.sendMessage(p, lib.notEnoughClearance());
				return true;
			}
			return true;
		}

		return true;
	}
}
