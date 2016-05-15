/**
 * This software is licensed under the MIT license.
 * If you wish to modify this software please give credit and link to the git: https://github.com/Moudoux/OTIRC.
 */
package com.opentexon.Server.Server.Packets;

import java.net.Socket;

import com.opentexon.Server.Main.Main;
import com.opentexon.Server.Server.Channel;
import com.opentexon.Server.Server.User;

public class P00PacketLogin {

	private void kickUser(String message, User user) {
		user.WriteToClient(new P02PacketString(null, message));

		if (Main.getInstance().getServer().isTempBanned(user.getIp())) {
			user.WriteToClient(new P02PacketString(null,
					"You will be unbanned at: " + Main.getInstance().getServer().getUnbanTime(user.getIp())));
		}

		user.Destroy();
	}

	private void addUser(User user) {
		Main.getInstance().getServer().users.add(user);

		user.authenticated = true;

		Main.getInstance().getServer().totalLogins += 1;

		Main.getInstance().getLogger().printInfoMessage("User " + user.getUsername() + "@"
				+ user.getSocket().getInetAddress().toString().substring(1) + " has joined");

		user.WriteToClient(new P02PacketString(null, "Welcome to " + Main.getInstance().getServer().ServerName));
		user.WriteToClient(new P02PacketString(null, "You are on channel " + user.getChannel().getName()));
		user.WriteToClient(new P02PacketString(null, "Enjoy your stay"));

		Main.getInstance().getServer().e.onJoin(user);
	}

	public P00PacketLogin(String line, Socket socket, boolean isEncrypted) {
		try {
			boolean invalidArgs = false;

			String ori = line;

			line = line.replace("JOIN ", "");
			line = line.replace("join ", "");
			String username = line.split(" ")[0];

			String pw = "";

			if (line.contains("-p")) {
				pw = line.split("-p")[1].substring(1);
			}

			Channel channel = null;

			boolean passwordAllowed = true;

			boolean found = false;
			boolean channelPW = false;

			for (Channel c : Main.getInstance().getServer().channels) {
				if (c.getName().toLowerCase().equals(line.split(" ")[1].toLowerCase())) {
					found = true;
					channelPW = c.isPasswordProtected();
					channel = c;
				}
			}

			if (found) {
				if (channelPW) {
					if (!channel.checkPassword(pw)) {
						passwordAllowed = false;
					}
				}
			} else {
				if (!pw.equals("")) {
					channel = new Channel(line.split(" ")[1], pw);
					Main.getInstance().getLogger()
							.printInfoMessage("Created channel " + channel.getName() + " with password protection");
				} else {
					channel = new Channel(line.split(" ")[1]);
					Main.getInstance().getLogger().printInfoMessage("Created channel " + channel.getName());
				}
				Main.getInstance().getServer().channels.add(channel);
			}

			String extraInfo = "";

			if (Main.getInstance().getServer().e.CountArgs(ori) < 2) {
				invalidArgs = true;
				username = "InvalidUser";
			} else if (Main.getInstance().getServer().e.CountArgs(ori) > 2 && !ori.contains("##MC##")) {
				extraInfo = ori.replace(ori.split(" ")[0] + " " + ori.split(" ")[1] + " " + ori.split(" ")[2] + " ",
						"");
			} else if (Main.getInstance().getServer().e.CountArgs(ori) > 3 && ori.contains("##MC##")) {
				extraInfo = ori
						.replace(ori.split(" ")[0] + " " + ori.split(" ")[1] + " " + ori.split(" ")[2] + " ##MC##", "");
			}

			if (extraInfo.equals("")) {
				extraInfo = "None";
			}

			boolean userExists = false;

			for (User u : Main.getInstance().getServer().users) {
				if (u.getUsername().toLowerCase().equals(username.toLowerCase())) {
					userExists = true;
					break;
				}
			}

			boolean isMC = false;

			if (line.contains("##MC##")) {
				isMC = true;
				line = line.replace(" ##MC##", "");
			}

			User user = new User(username, socket, channel);

			user.isEncrypted = isEncrypted;

			user.extraInfo = extraInfo;
			user.setMc(isMC);

			if (Main.getInstance().getServer().permMutedUsers.contains(user.getIp())) {
				user.setMuted(true);
			}

			if (Main.getInstance().getServer().opUsers.contains(user.getIp())) {
				Main.getInstance().getLogger().printWarningMessage("Client is opped");
				user.setOP(true);
				if (Main.getInstance().getServer().opUsersPlus.contains(user.getIp())) {
					user.PermissionLevel = 1;
				}
			}

			user.WriteToClient(new P02PacketString(null, "Authenticating..."));

			if (channelPW) {
				user.WriteToClient(new P02PacketString(null, "Channel is password protected..."));
			}

			boolean isBanned = false;

			if (Main.getInstance().getServer().permBannedUsers
					.contains(socket.getInetAddress().toString().substring(1))) {
				isBanned = true;
			}

			boolean isTempBanned = Main.getInstance().getServer().isTempBanned(user.getIp());

			boolean maxConnections = false;
			int connections = 0;

			for (User u : Main.getInstance().getServer().users) {
				if (u.getIp().equals(user.getIp())) {
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
								boolean flag = false;
								if (Main.getInstance().getServer().whitelist) {
									if (!Main.getInstance().getServer().whitelistedUsers.contains(user.getIp())) {
										flag = true;
									}
								}
								if (flag) {
									kickUser("The server is currently whitelisted", user);
								} else {
									if (!passwordAllowed && !user.isOP()) {
										kickUser("Channel is password protected, invalid password", user);
									} else {
										user.WriteToClient(new P02PacketString(null, "Successfully authenticated"));
										channel.addUser(user);
										addUser(user);
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			Main.getInstance().getLogger().printWarningMessage("Failed to authenticate user");
		}
	}

}