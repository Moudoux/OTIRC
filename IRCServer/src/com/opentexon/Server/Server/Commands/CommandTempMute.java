package com.opentexon.Server.Server.Commands;

import com.opentexon.Server.Main.Main;
import com.opentexon.Server.Server.User;
import com.opentexon.Server.Server.Packets.P02PacketString;
import com.opentexon.Utils.StringUtils;

public class CommandTempMute extends Command {

	private void muteIP(String ip, int minutes, User executor, String reason) {
		User mutedUser = this.getUserFromIP(ip);
		try {
			String unmuteTime = this.getServer().formatLongDate(this.getServer().tempMuteUser(ip, minutes));
			if (mutedUser != null) {
				String banner = (executor == null) ? "Console" : executor.getUsername();
				mutedUser.WriteToClient(new P02PacketString(null, "You were temporarily muted by " + banner));
				mutedUser.WriteToClient(new P02PacketString(null, "You will be unmuted at: " + unmuteTime));
				if (!reason.equals("")) {
					mutedUser.WriteToClient(new P02PacketString(null, "Mute reason: " + reason));
				}
			}
			if (executor != null) {
				this.notifyOpsAndConsole(
						executor.getUsername() + " temporarily muted ip " + ip + " until " + unmuteTime);
			} else {
				this.notifyOpsAndConsole("Console temporarily muted ip " + ip + " until " + unmuteTime);
			}
		} catch (Exception ex) {
			this.sendMessage("Could not temporarily mute the specified user");
			ex.printStackTrace();
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

				muteIP(ip, Integer.valueOf(minutes), user, reason);
			}
		}
	}

	public CommandTempMute(User user, P02PacketString line, boolean isConsole) {
		super(isConsole ? null : user);
		this.execute(true, "/tempmute [Username | IP] [Minutes] [Reason]", line, 2);
	}

}
