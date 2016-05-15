/**
 * This software is licensed under the MIT license.
 * If you wish to modify this software please give credit and link to the git: https://github.com/Moudoux/OTIRC.
 */
package com.opentexon.Server.Server.Commands;

import com.opentexon.Server.Main.Main;
import com.opentexon.Server.Server.User;
import com.opentexon.Server.Server.Packets.P02PacketString;
import com.opentexon.Utils.StringUtils;

public class CommandKick extends Command {

	private void kickAll(String executor, String reason) {
		for (User u : Main.getInstance().getServer().users) {
			if (!u.isOP()) {
				u.WriteToClient(new P02PacketString(null, "You were kicked by " + executor));
				if (!reason.equals("")) {
					u.WriteToClient(new P02PacketString(null, "Reason: " + reason));
				}
			}
		}
		this.notifyOpsAndConsole(executor + " kicked all non op users");
	}

	private void kickUser(User user, String reason, String executor) {
		user.WriteToClient(new P02PacketString(null, "You were kicked by " + executor));
		if (!reason.equals("")) {
			user.WriteToClient(new P02PacketString(null, "Reason: " + reason));
		}
		String uname = user.getUsername();
		user.Destroy();
		this.notifyOpsAndConsole(executor + " kicked " + uname);
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
			String reason = Main.getInstance().getServer().e.CountArgs(line.getString()) >= 2 ? line.getString()
					.replace(line.getString().split(" ")[0] + " " + line.getString().split(" ")[1] + " ", "") : "";

			String kick = line.getString().split(" ")[1];

			if (kick.equals("*")) {
				this.kickAll(isConsole ? "Console" : user.getUsername(), reason);
			} else if (StringUtils.containsIPAddress(kick)) {
				User kicked = this.getUserFromIP(kick);
				if (kicked != null) {
					this.kickUser(kicked, reason, isConsole ? "Console" : user.getUsername());
				} else {
					this.userNotFound();
				}
			} else {
				User kicked = this.getUserFromUsername(kick);
				if (kicked != null) {
					this.kickUser(kicked, reason, isConsole ? "Console" : user.getUsername());
				} else {
					this.userNotFound();
				}
			}

		}
	}

	public CommandKick(User user, P02PacketString line, boolean isConsole) {
		super(isConsole ? null : user);
		this.execute(true, "/kick [Username | IP | *] [Reason]", line, 1);
	}

}
