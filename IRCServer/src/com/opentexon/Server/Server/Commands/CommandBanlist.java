/**
 * This software is licensed under the MIT license.
 * If you wish to modify this software please give credit and link to the git: https://github.com/Moudoux/OTIRC.
 */
package com.opentexon.Server.Server.Commands;

import com.opentexon.Server.Main.Main;
import com.opentexon.Server.Server.User;

public class CommandBanlist {

	private void runCommand(User user, String line, boolean isConsole) {
		Main.getInstance().getServer().e.printMessageToUserOrConsole(user, isConsole, "§7== §6Banned users §7==");
		int banned = 0;
		for (String ip : Main.getInstance().getServer().bannedUsers) {
			banned += 1;
			Main.getInstance().getServer().e.printMessageToUserOrConsole(user, isConsole, ip);
		}
		if (banned == 0) {
			Main.getInstance().getServer().e.printMessageToUserOrConsole(user, isConsole, "There is no banned users");
		}
	}

	public CommandBanlist(User user, String line, boolean isConsole) {
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
				String correctUssage = Main.getInstance().getServer().messages.correctUssage(user, line, isConsole)
						+ " /banlist";
				if (isConsole) {
					Main.getInstance().getLogger().printWarningMessage(correctUssage);
				} else {
					user.WriteToClient(correctUssage);
				}
			}
		} else {
			user.WriteToClient(Main.getInstance().getServer().messages.permissionDenied(user, line, isConsole));
		}
	}

}
