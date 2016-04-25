package com.opentexon.Server.Server.Commands;

import com.opentexon.Server.Main.Main;
import com.opentexon.Server.Server.User;

public class CommandHelp {

	private void runCommand(User user, String line, boolean isConsole) {
		Main.getInstance().getServer().e.printMessageToUserOrConsole(user, isConsole,
				Main.getInstance().getServer().messages.helpMessage());

		Main.getInstance().getServer().e.printMessageToUserOrConsole(user, isConsole,
				"/kick [Username] [Message] (OP/Console)");
		Main.getInstance().getServer().e.printMessageToUserOrConsole(user, isConsole,
				"/ban [Username] [Message] (OP/Console)");
		Main.getInstance().getServer().e.printMessageToUserOrConsole(user, isConsole,
				"/mute [Username] [Message] (OP/Console)");

		Main.getInstance().getServer().e.printMessageToUserOrConsole(user, isConsole, "/op [Username] (OP/Console)");
		Main.getInstance().getServer().e.printMessageToUserOrConsole(user, isConsole, "/deop [Username] (OP/Console)");
		Main.getInstance().getServer().e.printMessageToUserOrConsole(user, isConsole, "/whois [Username] (OP/Console)");
		Main.getInstance().getServer().e.printMessageToUserOrConsole(user, isConsole, "/unban [Username] (OP/Console)");
		Main.getInstance().getServer().e.printMessageToUserOrConsole(user, isConsole, "/banlist (OP/Console)");
		Main.getInstance().getServer().e.printMessageToUserOrConsole(user, isConsole, "/msg [Username] [Message]");
		Main.getInstance().getServer().e.printMessageToUserOrConsole(user, isConsole, "/users");
		Main.getInstance().getServer().e.printMessageToUserOrConsole(user, isConsole, "/ver");

		if (isConsole) {
			Main.getInstance().getServer().e.printMessageToUserOrConsole(user, isConsole, "/glob [Message]");
			Main.getInstance().getServer().e.printMessageToUserOrConsole(user, isConsole,
					"/channel [Channel] [Message]");
		} else {
			Main.getInstance().getServer().e.printMessageToUserOrConsole(user, isConsole, "/quit");
			Main.getInstance().getServer().e.printMessageToUserOrConsole(user, isConsole, "/ping");
		}

	}

	public CommandHelp(User user, String line, boolean isConsole) {
		boolean hasPerm = true;

		if (hasPerm) {
			if (Main.getInstance().getServer().e.CountArgs(line) == 0) {
				runCommand(isConsole ? null : user, line, isConsole);
			} else {
				String correctUssage = Main.getInstance().getServer().messages.correctUssage(user, line, isConsole)
						+ " /help";
				if (isConsole) {
					Main.getInstance().getLogger().printWarningMessage(correctUssage);
				} else {
					user.WriteToClient(correctUssage);
				}
			}
		} else {
			user.WriteToClient(Main.getInstance().getServer().messages.permissionDenied(user, line, isConsole));
		}
	}

}
