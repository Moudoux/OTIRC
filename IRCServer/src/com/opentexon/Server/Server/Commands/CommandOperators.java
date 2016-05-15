package com.opentexon.Server.Server.Commands;

import java.util.ArrayList;

import com.opentexon.Server.Main.Main;
import com.opentexon.Server.Server.User;
import com.opentexon.Server.Server.Packets.P02PacketString;

public class CommandOperators extends Command {

	@Override
	public void runCommand(User user, P02PacketString line, boolean isConsole) {

		ArrayList<User> ops = new ArrayList<User>();

		for (User u : Main.getInstance().getServer().users) {
			if (u.isOP()) {
				ops.add(u);
			}
		}

		this.sendMessage("== All operators online ==");

		String opsString = "";

		for (User u : ops) {
			if (opsString.equals("")) {
				opsString = u.getIp() + "@" + u.getUsername() + ":" + u.getChannel().getName();
			} else {
				opsString = opsString + ", " + u.getIp() + "@" + u.getUsername() + ":" + u.getChannel().getName();
			}
		}

		this.sendMessage(opsString);

	}

	public CommandOperators(User user, P02PacketString line, boolean isConsole) {
		super(isConsole ? null : user);
		this.execute(true, "/ops", line, 0);
	}

}
