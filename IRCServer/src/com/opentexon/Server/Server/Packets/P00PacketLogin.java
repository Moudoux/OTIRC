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

		if (Main.getInstance().getServer().isTempBanned(user.Ip)) {
			user.WriteToClient("You will be unbanned at: " + Main.getInstance().getServer().getUnbanTime(user.Ip));
		}

		user.Destroy();
	}

	private void addUser(User user) {
		Main.getInstance().getServer().users.add(user);

		Main.getInstance().getServer().totalLogins += 1;

		Main.getInstance().getLogger().printInfoMessage(
				"User " + user.Username + "@" + user.socket.getInetAddress().toString().substring(1) + " has joined");

		user.WriteToClient("Welcome to " + Main.getInstance().getServer().ServerName);

		Main.getInstance().getServer().e.onJoin(user);
	}

	public P00PacketLogin(String line, Socket socket, boolean isEncrypted) {
		boolean invalidArgs = false;

		String ori = line;

		line = line.replace("JOIN ", "");
		line = line.replace("join ", "");
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

		boolean isTempBanned = Main.getInstance().getServer().isTempBanned(user.Ip);

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
			kickUser("You have been permanently banned from this server", user);
		} else {
			if (isTempBanned) {
				kickUser("You have been temporarily banned from this server", user);
			} else {
				if (invalidArgs) {
					kickUser("Invalid login arguments", user);
				} else {
					if (userExists) {
						kickUser("A user with your username has alredy logged in", user);
					} else {
						if (maxConnections) {
							kickUser("You have reached the maximum connections from your ip", user);
						} else {
							user.WriteToClient("Successfully authenticated");
							addUser(user);
						}
					}
				}
			}
		}

	}

}