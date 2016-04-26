/**
 * This software is licensed under the MIT license.
 * If you wish to modify this software please give credit and link to the git: https://github.com/Moudoux/OTIRC.
 */
package com.opentexon.Server.Server.Commands;

import com.opentexon.Server.Main.Main;
import com.opentexon.Server.Server.User;
import com.opentexon.Utils.StringUtils;

public class CommandDeop {

	private void runCommand(User user, String line, boolean isConsole) {
		if (StringUtils.isIPAddress(line.split(" ")[1].toLowerCase())) {
			if (Main.getInstance().getServer().opUsers.contains(line.split(" ")[1].toLowerCase())) {
				Main.getInstance().getServer().opUsers.remove(line.split(" ")[1].toLowerCase());
			}
			Main.getInstance().getServer().e.printMessageToUserOrConsole(user, isConsole,
					"�cDeopped " + line.split(" ")[1].toLowerCase());
		} else {
			boolean found = false;
			for (User u : Main.getInstance().getServer().users) {
				if (u.Username.toLowerCase().equals(line.split(" ")[1].toLowerCase())) {
					u.WriteToClient(Main.getInstance().getServer().messages.deopMessage(user, line, isConsole));

					Main.getInstance().getServer().e.printMessageToUserOrConsole(user, isConsole,
							Main.getInstance().getServer().messages.deopMessage1(user, line, isConsole, u));

					u.isOP = false;

					if (Main.getInstance().getServer().opUsers.contains(u.Ip)) {
						Main.getInstance().getServer().opUsers.remove(u.Ip);
					}

					Main.getInstance().getServer().e.NotifyOpsAndConsole(
							Main.getInstance().getServer().messages.deopMessage2(user, line, isConsole, u));

					found = true;
					break;
				}
			}

			if (!found) {
				Main.getInstance().getServer().e.printMessageToUserOrConsole(user, isConsole,
						Main.getInstance().getServer().messages.userNotFound(user, line, isConsole));
			}
		}
	}

	public CommandDeop(User user, String line, boolean isConsole) {
		boolean hasPerm = false;
		if (isConsole) {
			hasPerm = true;
		} else {
			if (user.isOP) {
				hasPerm = true;
			}
		}

		if (hasPerm) {
			if (Main.getInstance().getServer().e.CountArgs(line) == 1) {
				runCommand(isConsole ? null : user, line, isConsole);
			} else {
				String correctUssage = Main.getInstance().getServer().messages.correctUssage(user, line, isConsole)
						+ " /deop [Username]";
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
