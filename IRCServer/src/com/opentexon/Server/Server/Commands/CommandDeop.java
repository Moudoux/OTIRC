/**
 * This software is licensed under the MIT license.
 * If you wish to modify this software please give credit and link to the git: https://github.com/Moudoux/OTIRC.
 */
package com.opentexon.Server.Server.Commands;

import com.opentexon.Server.Main.Main;
import com.opentexon.Server.Server.User;
import com.opentexon.Utils.StringUtils;

public class CommandDeop extends Command {

	private void runCommand(User user, String line, boolean isConsole) {
		String ip = line.split(" ")[1];
		if (StringUtils.containsIPAddress(line)) {
			User oppedUser = this.getUserFromIP(ip);
			if (this.getServer().opUsers.contains(ip)) {
				this.getServer().opUsers.remove(ip);
			}
			String executor = isConsole ? "Console" : user.Username;
			if (oppedUser != null) {
				oppedUser.isOP = false;
				this.notifyOpsAndConsole(executor + " deopped " + oppedUser.Username);
				oppedUser.WriteToClient(executor + " deopped you");
			} else {
				this.notifyOpsAndConsole(executor + " deopped " + ip);
			}
		} else {
			User oppedUser = this.getUserFromUsername(ip);
			String executor = isConsole ? "Console" : user.Username;
			if (oppedUser != null) {
				ip = oppedUser.Ip;
				if (this.getServer().opUsers.contains(ip)) {
					this.getServer().opUsers.remove(ip);
				}
				oppedUser.isOP = false;
				this.notifyOpsAndConsole(executor + " deopped " + oppedUser.Username);
				oppedUser.WriteToClient(executor + " deopped you");
			} else {
				this.userNotFound();
			}
		}
	}

	public CommandDeop(User user, String line, boolean isConsole) {
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
				this.correctUssage("/deop [Username/IP]");
			}
		} else {
			this.permissionDenied();
		}
	}

}
