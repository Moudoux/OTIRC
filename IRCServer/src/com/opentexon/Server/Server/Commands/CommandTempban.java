package com.opentexon.Server.Server.Commands;

import com.opentexon.Server.Main.Main;
import com.opentexon.Server.Server.User;
import com.opentexon.Server.Server.Packets.P02PacketString;
import com.opentexon.Utils.StringUtils;

public class CommandTempban extends Command {

	private void banIP(String ip, int minutes, User executor, String reason) {
		User bannedUser = this.getUserFromIP(ip);
		try {
			String banTime = this.getServer().formatLongDate(this.getServer().tempBanUser(ip, minutes));
			if (bannedUser != null) {
				String banner = (executor == null) ? "Console" : executor.getUsername();
				bannedUser.WriteToClient(new P02PacketString(null, "You were temporarily banned by " + banner));
				bannedUser.WriteToClient(new P02PacketString(null, "You will be unbanned at: " + banTime));
				if (!reason.equals("")) {
					bannedUser.WriteToClient(new P02PacketString(null, "Reason: " + reason));
				}
				bannedUser.Destroy();
			}
			if (executor != null) {
				this.notifyOpsAndConsole(executor.getUsername() + " temporarily banned ip " + ip + " until " + banTime);
			} else {
				this.notifyOpsAndConsole("Console temporarily banned ip " + ip + " until " + banTime);
			}
		} catch (Exception ex) {
			this.sendMessage("Could not temporarily ban the specified user");
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
			boolean found = false;

			String ip = line.getString().split(" ")[1];
			String minutes = line.getString().split(" ")[2];

			if (StringUtils.isIPAddress(ip)) {
				found = true;
			} else {
				for (User u : this.getServer().users) {
					if (u.getUsername().toLowerCase().equals(ip.toLowerCase())) {
						found = true;
						ip = u.getIp();
						break;
					}
				}
			}

			if (!found) {
				this.userNotFound();
			} else {

				String reason = Main.getInstance().getServer().e.CountArgs(line.getString()) >= 3
						? line.getString().replace(line.getString().split(" ")[0] + " " + line.getString().split(" ")[1]
								+ " " + line.getString().split(" ")[2] + " ", "")
						: "";

				banIP(ip, Integer.valueOf(minutes), user, reason);
			}
		}
	}

	public CommandTempban(User user, P02PacketString line, boolean isConsole) {
		super(isConsole ? null : user);
		this.execute(true, "/tempban [Username | IP] [Minutes] [Reason]", line, 2);
	}

}
