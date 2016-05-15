/**
 * This software is licensed under the MIT license.
 * If you wish to modify this software please give credit and link to the git: https://github.com/Moudoux/OTIRC.
 */
package com.opentexon.Server.Server.Commands;

import com.opentexon.Server.Server.User;
import com.opentexon.Server.Server.Packets.P02PacketString;
import com.opentexon.Utils.StringUtils;

public class CommandWhois extends Command {

	@Override
	public void runCommand(User user, P02PacketString line, boolean isConsole) {
		User whoisUser = null;

		if (StringUtils.containsIPAddress(line.getString())) {
			whoisUser = this.getUserFromIP(line.getString().split(" ")[1]);
		} else {
			whoisUser = this.getUserFromUsername(line.getString().split(" ")[1]);
		}

		if (whoisUser != null) {
			this.sendMessage("== Who is " + whoisUser.getUsername() + " ==");

			this.sendMessage("IP Addresss: " + whoisUser.getIp());

			this.sendMessage("Current channel: " + whoisUser.getChannel().getName());

			this.sendMessage("OP: " + (whoisUser.isOP() ? "true" : "false"));

			this.sendMessage("Muted: " + (whoisUser.isMuted() ? "true" : "false"));
		} else {
			this.userNotFound();
		}
	}

	public CommandWhois(User user, P02PacketString line, boolean isConsole) {
		super(isConsole ? null : user);
		this.execute(true, "/whois [Username | IP]", line, 1);
	}

}
