/**
 * This software is licensed under the MIT license.
 * If you wish to modify this software please give credit and link to the git: https://github.com/Moudoux/OTIRC.
 */
package com.opentexon.Server.Server.Packets;

import java.net.Socket;

import com.opentexon.Server.Main.Main;
import com.opentexon.Server.Server.User;

public class P00PacketLogin {

	private void kickUser(String message, User user) {
		user.WriteToClient(message);
		user.Destroy();
	}

	private void addUser(User user) {
		Main.getInstance().getServer().users.add(user);

		Main.getInstance().getServer().totalLogins += 1;

		Main.getInstance().getLogger().printInfoMessage(Main.getInstance().getServer().messages.joinMessage4(user));

		user.WriteToClient(Main.getInstance().getServer().messages.joinMessage7());

		Main.getInstance().getServer().e.onJoin(user);
	}

	public P00PacketLogin(String line, Socket socket, boolean isEncrypted) {
		if (socket.isClosed()) {
			Main.getInstance().getLogger().printErrorMessage(Main.getInstance().getServer().messages.joinMessage());
			return;
		}

		boolean invalidArgs = false;

		String ori = line;

		line = line.replace("JOIN ", "");
		String username = line.split(" ")[0];
		String channel = line.split(" ")[1];

		if (Main.getInstance().getServer().e.CountArgs(ori) < 2) {
			invalidArgs = true;
			username = "InvalidUser";
			channel = "";
		}

		boolean userExists = false;

		for (User u : Main.getInstance().getServer().users) {
			if (u.Username.toLowerCase().equals(username.toLowerCase())) {
				userExists = true;
				break;
			}
		}

		boolean isMC = false;

		if (line.contains("##MC##")) {
			isMC = true;
			line = line.replace(" ##MC##", "");
		}

		User user = new User(username, channel, socket);

		user.isEncrypted = isEncrypted;

		user.isMc = isMC;

		if (Main.getInstance().getServer().opUsers.contains(user.Ip)) {
			Main.getInstance().getLogger().printWarningMessage("Client is opped");
			user.isOP = true;
		}

		user.WriteToClient("Authenticating...");

		boolean isBanned = false;

		if (Main.getInstance().getServer().bannedUsers.contains(socket.getInetAddress().toString().substring(1))) {
			isBanned = true;
		}

		boolean maxConnections = false;
		int connections = 0;

		for (User u : Main.getInstance().getServer().users) {
			if (u.Ip.equals(user.Ip)) {
				connections += 1;
			}
		}

		if (connections > Main.getInstance().getServer().maxConnections) {
			maxConnections = true;
		}

		if (isBanned) {
			kickUser(Main.getInstance().getServer().messages.joinMessage3(), user);
		} else {
			if (invalidArgs) {
				kickUser(Main.getInstance().getServer().messages.joinMessage5(), user);
			} else {
				if (userExists) {
					kickUser(Main.getInstance().getServer().messages.joinMessage6(), user);
				} else {
					if (maxConnections) {
						kickUser(Main.getInstance().getServer().messages.joinMessage8(), user);
					} else {
						user.WriteToClient("Successfully authenticated");
						addUser(user);
					}
				}
			}
		}

	}

}