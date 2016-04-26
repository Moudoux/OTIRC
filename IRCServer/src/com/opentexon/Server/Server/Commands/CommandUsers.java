/**
 * This software is licensed under the MIT license.
 * If you wish to modify this software please give credit and link to the git: https://github.com/Moudoux/OTIRC.
 */
package com.opentexon.Server.Server.Commands;

import com.opentexon.Server.Main.Main;
import com.opentexon.Server.Server.User;

public class CommandUsers {

	@SuppressWarnings("unused")
	private void runCommand(User user, String line, boolean isConsole) {
		Main.getInstance().getServer().e.printMessageToUserOrConsole(user, isConsole,
				Main.getInstance().getServer().messages.usersMessage());

		int counter = 0;
		int opCounter = 0;
		int banned = 0;

		for (User u : Main.getInstance().getServer().users) {
			counter += 1;
			if (u.isOP) {
				opCounter += 1;
			}
		}

		for (String s : Main.getInstance().getServer().bannedUsers) {
			banned += 1;
		}

		Main.getInstance().getServer().e.printMessageToUserOrConsole(user, isConsole,
				Main.getInstance().getServer().messages.usersMessage1() + " " + String.valueOf(counter));
		Main.getInstance().getServer().e.printMessageToUserOrConsole(user, isConsole,
				Main.getInstance().getServer().messages.usersMessage2() + " " + String.valueOf(opCounter));
		Main.getInstance().getServer().e.printMessageToUserOrConsole(user, isConsole,
				Main.getInstance().getServer().messages.usersMessage3() + " " + String.valueOf(banned));
		Main.getInstance().getServer().e.printMessageToUserOrConsole(user, isConsole,
				Main.getInstance().getServer().messages.usersMessage4() + " "
						+ String.valueOf(Main.getInstance().getServer().totalLogins));

	}

	public CommandUsers(User user, String line, boolean isConsole) {
		boolean hasPerm = true;

		if (hasPerm) {
			if (Main.getInstance().getServer().e.CountArgs(line) == 0) {
				runCommand(isConsole ? null : user, line, isConsole);
			} else {
				String correctUssage = Main.getInstance().getServer().messages.correctUssage(user, line, isConsole)
						+ " /users";
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
