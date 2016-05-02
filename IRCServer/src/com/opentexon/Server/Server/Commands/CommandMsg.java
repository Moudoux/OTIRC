/**
 * This software is licensed under the MIT license.
 * If you wish to modify this software please give credit and link to the git: https://github.com/Moudoux/OTIRC.
 */
package com.opentexon.Server.Server.Commands;

import com.opentexon.Server.Main.Main;
import com.opentexon.Server.Server.User;

public class CommandMsg extends Command {

	private void runCommand(User user, String line, boolean isConsole) {
		boolean found = false;

		for (User u : Main.getInstance().getServer().users) {
			if (u.Username.toLowerCase().equals(line.split(" ")[1].toLowerCase())) {
				u.WriteToClient(isConsole
						? "[" + "Console" + " -> Me] -> " + line.replace("/msg " + line.split(" ")[1] + " ", "")
						: "[" + user.Username + " -> Me] -> " + line.replace("/msg " + line.split(" ")[1] + " ", ""));
				found = true;
				break;
			}
		}

		if (!found) {
			this.userNotFound();
		}
	}

	public CommandMsg(User user, String line, boolean isConsole) {
		super(isConsole ? null : user);

		boolean hasPerm = true;

		if (hasPerm) {
			if (Main.getInstance().getServer().e.CountArgs(line) >= 2) {
				runCommand(isConsole ? null : user, line, isConsole);
			} else {
				this.correctUssage("/msg [Username] [Message]");
			}
		} else {
			this.permissionDenied();
		}
	}

}
