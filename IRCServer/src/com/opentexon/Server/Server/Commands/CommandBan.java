/**
 * This software is licensed under the MIT license.
 * If you wish to modify this software please give credit and link to the git: https://github.com/Moudoux/OTIRC.
 */
package com.opentexon.Server.Server.Commands;

import com.opentexon.Server.Main.Main;
import com.opentexon.Server.Server.User;
import com.opentexon.Utils.StringUtils;

public class CommandBan extends Command {

	private void banIP(String ip, User executor, String reason) {
		if (this.getServer().opUsers.contains(ip)) {
			this.getServer().opUsers.remove(ip);
		}

		if (!this.getServer().bannedUsers.contains(ip)) {
			this.getServer().bannedUsers.add(ip);
		}

		User bannedUser = this.getUserFromIP(ip);

		if (bannedUser != null) {
			String banner = (executor == null) ? "Console" : executor.Username;
			bannedUser.WriteToClient("You were permanently banned by " + banner);

			if (!reason.equals("")) {
				bannedUser.WriteToClient("Ban reason: " + reason);
			}

			bannedUser.Destroy();
		}

		if (executor != null) {
			this.notifyOpsAndConsole(executor.Username + " permanently banned ip " + ip);
		} else {
			this.notifyOpsAndConsole("Console permanently banned ip " + ip);
		}

	}

	private void runCommand(User user, String line, boolean isConsole) {
		boolean found = false;
		String ip = line.split(" ")[1];

		if (StringUtils.isIPAddress(ip)) {
			found = true;
		} else {
			for (User u : this.getServer().users) {
				if (u.Username.toLowerCase().equals(ip.toLowerCase())) {
					found = true;
					ip = u.Ip;
					break;
				}
			}
		}

		if (!found) {
			this.userNotFound();
		} else {

			String reason = Main.getInstance().getServer().e.CountArgs(line) >= 2
					? line.replace(line.split(" ")[0] + " " + line.split(" ")[1] + " ", "") : "";

			banIP(ip, user, reason);
		}
	}

	public CommandBan(User user, String line, boolean isConsole) {
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
				this.correctUssage("/ban [Username/IP] [Message]");
			}
		} else {
			this.permissionDenied();
		}
	}

}
