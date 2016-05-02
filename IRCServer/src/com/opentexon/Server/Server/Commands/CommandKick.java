/**
 * This software is licensed under the MIT license.
 * If you wish to modify this software please give credit and link to the git: https://github.com/Moudoux/OTIRC.
 */
package com.opentexon.Server.Server.Commands;

import com.opentexon.Server.Main.Main;
import com.opentexon.Server.Server.User;

public class CommandKick extends Command {

	private void runCommand(User user, String line, boolean isConsole) {
		boolean found = false;

		String reason = Main.getInstance().getServer().e.CountArgs(line) >= 2
				? line.replace(line.split(" ")[0] + " " + line.split(" ")[1] + " ", "") : "";

		for (User u : Main.getInstance().getServer().users) {
			if (u.Username.toLowerCase().equals(line.split(" ")[1].toLowerCase())) {

				String executor = isConsole ? "Console" : user.Username;

				u.WriteToClient(executor + " kicked you");

				if (!reason.equals("")) {
					u.WriteToClient("Kick reason: " + reason);
				}

				if (this.getServer().opUsers.contains(u.Ip)) {
					this.getServer().opUsers.remove(u.Ip);
				}

				String uname = u.Username;
				u.Destroy();

				this.sendMessage("Kicked " + uname);
				this.notifyOpsAndConsole(executor + " kicked " + uname);

				found = true;
				break;
			}
		}

		if (!found) {
			this.userNotFound();
		}
	}

	public CommandKick(User user, String line, boolean isConsole) {
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
			if (Main.getInstance().getServer().e.CountArgs(line) >= 1) {
				runCommand(isConsole ? null : user, line, isConsole);
			} else {
				this.correctUssage("/kick [Username] [Reason]");
			}
		} else {
			this.permissionDenied();
		}
	}

}
