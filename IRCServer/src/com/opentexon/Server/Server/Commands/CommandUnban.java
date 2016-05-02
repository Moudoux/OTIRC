/**
 * This software is licensed under the MIT license.
 * If you wish to modify this software please give credit and link to the git: https://github.com/Moudoux/OTIRC.
 */
package com.opentexon.Server.Server.Commands;

import com.opentexon.Server.Main.Main;
import com.opentexon.Server.Server.User;

public class CommandUnban extends Command {

	private void runCommand(User user, String line, boolean isConsole) {
		boolean found = false;
		String ip = line.split(" ")[1];

		if (this.getServer().bannedUsers.contains(ip)) {
			found = true;

			this.getServer().bannedUsers.remove(ip);
			this.getServer().removeTempBan(ip);
			this.sendMessage("Unbanned ip " + ip);
			String executor = isConsole ? "Console" : user.Username;
			this.notifyOpsAndConsole(executor + " unbanned ip " + ip);
		}

		if (!found) {
			this.userNotFound();
		}
	}

	public CommandUnban(User user, String line, boolean isConsole) {
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
				this.correctUssage("/unban [IP Address]");
			}
		} else {
			this.permissionDenied();
		}
	}

}
