/**
 * This software is licensed under the MIT license.
 * If you wish to modify this software please give credit and link to the git: https://github.com/Moudoux/OTIRC.
 */
package com.opentexon.Server.Server.Commands;

import com.opentexon.Server.Main.Main;
import com.opentexon.Server.Server.User;

public class CommandWhois {

	private void runCommand(User user, String line, boolean isConsole) {
		boolean found = false;
		for (User u : Main.getInstance().getServer().users) {
			if (u.Username.toLowerCase().equals(line.split(" ")[1].toLowerCase())) {
				Main.getInstance().getServer().e.printMessageToUserOrConsole(user, isConsole,
						"== Whois " + u.Username + " ==");
				Main.getInstance().getServer().e.printMessageToUserOrConsole(user, isConsole,
						Main.getInstance().getServer().messages.whoisMessage1(user, line, isConsole, u));
				Main.getInstance().getServer().e.printMessageToUserOrConsole(user, isConsole,
						Main.getInstance().getServer().messages.whoisMessage2(user, line, isConsole, u));

				Main.getInstance().getServer().e.NotifyOpsAndConsole(
						Main.getInstance().getServer().messages.whoisMessage3(user, line, isConsole, u));
				found = true;
				break;
			}
		}

		if (!found) {
			Main.getInstance().getServer().e.printMessageToUserOrConsole(user, isConsole,
					Main.getInstance().getServer().messages.userNotFound(user, line, isConsole));
		}
	}

	public CommandWhois(User user, String line, boolean isConsole) {
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
						+ " /whois [Username]";
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
