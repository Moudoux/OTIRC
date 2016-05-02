/**
 * This software is licensed under the MIT license.
 * If you wish to modify this software please give credit and link to the git: https://github.com/Moudoux/OTIRC.
 */
package com.opentexon.Server.Server.Commands;

import com.opentexon.Server.Main.Main;
import com.opentexon.Server.Server.User;

public class CommandBanlist extends Command {

	private void runCommand(User user, String line, boolean isConsole) {
		this.sendMessage("== Banned users ==");

		int banned = 0;
		String bannedUsers = "";
		for (String ip : Main.getInstance().getServer().bannedUsers) {
			banned += 1;
			if (bannedUsers.equals("")) {
				bannedUsers = ip;
			} else {
				bannedUsers = bannedUsers + ", " + ip;
			}
		}

		if (banned == 0) {
			this.sendMessage("There are no banned users");
		} else {
			this.sendMessage(bannedUsers);
		}
	}

	public CommandBanlist(User user, String line, boolean isConsole) {
		super(isConsole ? null : user);

		boolean hasPerm = false;
		if (isConsole) {
			hasPerm = true;
		} else {
			if (user.isOP) {
				hasPerm = true;
			}
		}

		if (hasPerm) {
			if (Main.getInstance().getServer().e.CountArgs(line) == 0) {
				runCommand(isConsole ? null : user, line, isConsole);
			} else {
				this.correctUssage("/banlist");
			}
		} else {
			this.permissionDenied();
		}
	}

}
