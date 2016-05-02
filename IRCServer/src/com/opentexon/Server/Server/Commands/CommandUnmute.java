/**
 * This software is licensed under the MIT license.
 * If you wish to modify this software please give credit and link to the git: https://github.com/Moudoux/OTIRC.
 */
package com.opentexon.Server.Server.Commands;

import com.opentexon.Server.Main.Main;
import com.opentexon.Server.Server.User;

public class CommandUnmute extends Command {

	private void runCommand(User user, String line, boolean isConsole) {
		boolean found = false;

		for (User u : Main.getInstance().getServer().users) {
			if (u.Username.toLowerCase().equals(line.split(" ")[1].toLowerCase())) {
				u.isMuted = false;

				String executor = isConsole ? "Console" : user.Username;

				u.WriteToClient(executor + " unmuted you");

				this.sendMessage("Unmuted " + u.Username);
				this.notifyOpsAndConsole(executor + " unmuted " + u.Username);

				found = true;
				break;
			}
		}

		if (!found) {
			this.userNotFound();
		}
	}

	public CommandUnmute(User user, String line, boolean isConsole) {
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
			if (Main.getInstance().getServer().e.CountArgs(line) == 1) {
				runCommand(isConsole ? null : user, line, isConsole);
			} else {
				this.correctUssage("/unmute [Username]");
			}
		} else {
			this.permissionDenied();
		}
	}

}
