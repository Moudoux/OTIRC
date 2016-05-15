/**
 * This software is licensed under the MIT license.
 * If you wish to modify this software please give credit and link to the git: https://github.com/Moudoux/OTIRC.
 */
package com.opentexon.Server.Server.Commands;

import com.opentexon.Server.Server.ConfigManager;
import com.opentexon.Server.Server.User;
import com.opentexon.Server.Server.Packets.P02PacketString;
import com.opentexon.Utils.StringUtils;

public class CommandOp extends Command {

	private void opIP(User user, String ip, String executor, int permLevel) {
		if (!this.getServer().opUsers.contains(ip)) {
			this.getServer().opUsers.add(ip);
		}
		if (!this.getServer().opUsersPlus.contains(ip) && (permLevel == 1)) {
			this.getServer().opUsersPlus.add(ip);
		}
		if (user != null) {
			user.setOP(true);
			user.WriteToClient(new P02PacketString(null, "You were opped by " + executor));
		}
		this.notifyOpsAndConsole(executor + " opped " + ((user == null) ? ip : user.getUsername()));
		try {
			ConfigManager.getInstance().addListValue("ops", ip);
			if (permLevel == 1) {
				ConfigManager.getInstance().addListValue("opsplus", ip);
			}
		} catch (Exception ex) {

		}
	}

	@Override
	public void runCommand(User user, P02PacketString line, boolean isConsole) {
		boolean PermissionPlus = false;

		if (user != null && !isConsole) {
			if (user.PermissionLevel == 1) {
				PermissionPlus = true;
			}
		}

		if (!PermissionPlus && this.getServer().isUserOP(line.getString().split(" ")[1]) && !isConsole) {
			this.permissionDenied();
		} else {
			String ip = line.getString().split(" ")[1];

			if (!StringUtils.containsIPAddress(line.getString())) {
				User oppedUser = this.getUserFromUsername(ip);
				if (oppedUser != null) {
					ip = oppedUser.getIp();
				}
			}

			int PermissionLevel = 0;

			if (this.getServer().e.CountArgs(line.getString()) == 2) {
				PermissionLevel = Integer.valueOf(line.getString().split(" ")[2]);
			}

			if (StringUtils.containsIPAddress(ip)) {
				this.opIP(this.getUserFromIP(ip), ip, isConsole ? "Console" : user.getUsername(), PermissionLevel);
			} else {
				this.userNotFound();
			}
		}
	}

	public CommandOp(User user, P02PacketString line, boolean isConsole) {
		super(isConsole ? null : user);
		this.execute(true, "/op [Username | IP] [Permission Level]", line, 1);
	}
}
