/**
 * This software is licensed under the MIT license.
 * If you wish to modify this software please give credit and link to the git: https://github.com/Moudoux/OTIRC.
 */
package com.opentexon.Server.Server.Commands;

import com.opentexon.Server.Main.Main;
import com.opentexon.Server.Server.User;
import com.opentexon.Utils.StringUtils;

public class CommandWhois extends Command {

	private void runCommand(User user, String line, boolean isConsole) {
		User whoisUser = null;

		if (StringUtils.containsIPAddress(line)) {
			whoisUser = this.getUserFromIP(line.split(" ")[1]);
		} else {
			whoisUser = this.getUserFromUsername(line.split(" ")[1]);
		}

		if (whoisUser != null) {
			this.sendMessage("== Who is " + whoisUser.Username + " ==");

			this.sendMessage("IP Addresss: " + whoisUser.Ip);

			this.sendMessage("Current channel: " + whoisUser.Channel);
		} else {
			this.userNotFound();
		}
	}

	public CommandWhois(User user, String line, boolean isConsole) {
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
				this.correctUssage("/whois [Username]");
			}
		} else {
			this.permissionDenied();
		}
	}

}
