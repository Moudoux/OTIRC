package com.opentexon.Server.Server.Commands;

import com.opentexon.Server.Main.Main;
import com.opentexon.Server.Server.User;
import com.opentexon.Server.Server.Packets.P02PacketString;
import com.opentexon.Utils.StringUtils;

public class CommandReply extends Command {

	@Override
	public void runCommand(User user, P02PacketString line, boolean isConsole) {
		boolean allowed = true;

		if (user != null) {
			if (user.isMuted()) {
				allowed = false;
			}
		}

		if (!allowed) {
			this.permissionDenied();
		} else {

			if (user.lastMsgRecived != null) {
				String prefix = StringUtils.getPrefix(user);

				user.lastMsgRecived.WriteToClient(new P02PacketString(null,
						"[" + prefix + " " + user.getUsername() + " -> Me] -> " + line.getString().replace("/r ", "")));

				String sendTo = user.lastMsgRecived.getUsername();
				prefix = StringUtils.getPrefix(user.lastMsgRecived);

				this.sendMessage("[Me -> " + prefix + " " + sendTo + "] -> " + line.getString().replace("/r ", ""));
			} else {
				this.sendMessage("You don't have anyone to reply to");
			}

		}
	}

	public CommandReply(User user, P02PacketString line, boolean isConsole) {
		super(isConsole ? null : user);

		if (!isConsole) {
			if (Main.getInstance().getServer().e.CountArgs(line.getString()) >= 1) {
				runCommand(isConsole ? null : user, line, isConsole);
			} else {
				this.correctUssage("/r [Message]");
			}
		} else {
			this.permissionDenied();
		}

	}

}
