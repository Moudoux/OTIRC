/**
 * This software is licensed under the MIT license.
 * If you wish to modify this software please give credit and link to the git: https://github.com/Moudoux/OTIRC.
 */
package com.opentexon.Server.Server.Commands;

import com.opentexon.Server.Main.Main;
import com.opentexon.Server.Server.User;

public class CommandUsers extends Command {

	@SuppressWarnings("unused")
	private void runCommand(User user, String line, boolean isConsole) {
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

		int tempbanedUsers = 0;

		for (String s : Main.getInstance().getServer().tempBannedUsers) {
			tempbanedUsers += 1;
		}

		this.sendMessage("== All users ==");
		this.sendMessage("Users online: " + String.valueOf(counter));
		this.sendMessage("Operators online: " + String.valueOf(opCounter));
		this.sendMessage("Permbanned users: " + String.valueOf(banned));
		this.sendMessage("Tempbanned users: " + String.valueOf(tempbanedUsers));
		this.sendMessage("Total logins: " + String.valueOf(Main.getInstance().getServer().totalLogins));

	}

	public CommandUsers(User user, String line, boolean isConsole) {
		super(isConsole ? null : user);

		boolean hasPerm = true;

		if (hasPerm) {
			if (Main.getInstance().getServer().e.CountArgs(line) == 0) {
				runCommand(isConsole ? null : user, line, isConsole);
			} else {
				this.correctUssage("/users");
			}
		} else {
			this.permissionDenied();
		}
	}

}
