/**
 * This software is licensed under the MIT license.
 * If you wish to modify this software please give credit and link to the git: https://github.com/Moudoux/OTIRC.
 */
package com.opentexon.Server.Server.Commands;

import com.opentexon.Server.Server.User;
import com.opentexon.Server.Server.Packets.P02PacketString;
import com.opentexon.Utils.StringUtils;

public class CommandMsg extends Command {

	private void sendMessage(User sender, User reciver, String message) {
		String Reciver = StringUtils.getPrefix(reciver) + " [" + reciver.getUsername() + "]";
		String Sender = (sender == null) ? "[Console]"
				: StringUtils.getPrefix(sender) + " [" + sender.getUsername() + "]";

		this.sendMessage("[Me -> " + Reciver + "] -> " + message);
		reciver.WriteToClient(new P02PacketString(null, "[" + Sender + " -> Me] -> " + message));

		if (sender != null) {
			reciver.lastMsgRecived = sender;
			sender.lastMsgRecived = reciver;
		}

	}

	@Override
	public void runCommand(User user, P02PacketString line, boolean isConsole) {
		String message = line.getString().replace("/msg " + line.getString().split(" ")[1] + " ", "");
		String reciver = line.getString().split(" ")[1];
		if ((user != null) && user.isMuted()) {
			this.permissionDenied();
		} else {
			User Reciver = null;
			if (StringUtils.containsIPAddress(reciver)) {
				Reciver = this.getUserFromIP(reciver);
			} else {
				Reciver = this.getUserFromUsername(reciver);
			}
			if (Reciver != null) {
				this.sendMessage(isConsole ? null : user, Reciver, message);
			} else {
				this.userNotFound();
			}
		}
	}

	public CommandMsg(User user, P02PacketString line, boolean isConsole) {
		super(isConsole ? null : user);
		this.execute(false, "/msg [Username | IP] [Message]", line, 2);
	}

}
