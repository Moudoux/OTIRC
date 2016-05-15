/**
 * This software is licensed under the MIT license.
 * If you wish to modify this software please give credit and link to the git: https://github.com/Moudoux/OTIRC.
 */
package com.opentexon.Server.Server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

import com.opentexon.Crypto.CryptoManager;
import com.opentexon.Server.Main.Main;
import com.opentexon.Server.Server.Packets.P02PacketString;

/**
 * User object with listener
 * 
 * @author Alexander
 *
 */
public class User {

	private String Username;
	private String Ip;
	private Socket socket;
	private DataOutputStream clientWriter;

	private boolean isOP = false;
	private boolean isMuted = false;
	private boolean isMc = false;
	private boolean serverUser = false;

	private Channel channel;

	private Thread ReciverThread;

	private int swearWarnings = 0;
	private int maxSwear = 3;
	private int maxRepeat = 3;

	private String lastMsg = "";
	private int repeatWarnings = 0;

	public boolean isEncrypted = false;
	public String extraInfo = "";

	public User lastMsgRecived = null;

	public boolean authenticated = false;

	public ArrayList<User> ignoredUsers = new ArrayList<User>();

	/**
	 * 0 = Normal user or normal op 1 = OP+, can ban, mute, kick, etc other
	 * operators
	 */
	public int PermissionLevel = 0;

	private void Reciver() {
		BufferedReader inFromClient = null;
		try {
			inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String line = null;

			while ((line = inFromClient.readLine()) != null && !socket.isClosed()) {

				if (isEncrypted) {
					line = CryptoManager.decode(line);
				}

				if (line.equals("/pong")) {

				} else if (line.equals("/ping")) {
					this.WriteToClient(new P02PacketString(null, "pong"));
				} else if (authenticated) {
					boolean flag = false;

					if (!flag) {
						flag = isMuted;
					}

					if (!flag) {
						flag = Main.getInstance().getServer().isTempMuted(this.Ip);
					}

					if (!flag) {
						Filter f = new Filter();
						if (f.isSwearWord(line)) {
							swearWarnings += 1;
							if (swearWarnings > maxSwear) {
								this.WriteToClient(new P02PacketString(null, "You got tempbanned for swearing"));
								Main.getInstance().getServer().tempBanUser(this.Ip, 5040);
								this.WriteToClient(new P02PacketString(null, "You will be unbanned at: "
										+ Main.getInstance().getServer().getUnbanTime(this.Ip)));
								Main.getInstance().getServer().e.NotifyOpsAndConsole(
										new P02PacketString(null, "Tempbanned " + this.Username + " for swearing"),
										null);
								this.Destroy();
							} else {
								if (maxSwear == swearWarnings) {
									this.WriteToClient(
											new P02PacketString(null, "This is your final warning, stop swearing"));
								} else {
									this.WriteToClient(
											new P02PacketString(null,
													"Don't swear, you will be tempbanned if you continue, warning "
															+ String.valueOf(swearWarnings) + "/"
															+ String.valueOf(maxSwear)));
								}
							}
						} else {

							boolean allowRecive = true;

							if (lastMsg.equals("")) {
								lastMsg = line;
							} else {
								if (lastMsg.toLowerCase().equals(line.toLowerCase()) && !(this.PermissionLevel == 1)) {
									allowRecive = false;
									repeatWarnings += 1;
									if (repeatWarnings == maxRepeat) {
										this.WriteToClient(
												new P02PacketString(null, "This is your final warning, stop spamming"));
									} else {
										if (repeatWarnings > maxRepeat) {
											this.WriteToClient(
													new P02PacketString(null, "You got tempbanned for spamming"));
											Main.getInstance().getServer().tempBanUser(this.Ip, 1440);
											this.WriteToClient(new P02PacketString(null, "You will be unbanned at: "
													+ Main.getInstance().getServer().getUnbanTime(this.Ip)));
											Main.getInstance().getServer().e
													.NotifyOpsAndConsole(
															new P02PacketString(null,
																	"Tempbanned " + this.Username + " for spamming"),
															null);
											this.Destroy();
										} else {
											this.WriteToClient(new P02PacketString(null,
													"Don't spam, you will be tempbanned if you continue, warning "
															+ String.valueOf(repeatWarnings) + "/"
															+ String.valueOf(maxRepeat)));
										}
									}
								} else {
									lastMsg = line;
									repeatWarnings = 0;
								}
							}

							if (allowRecive) {
								P02PacketString pChat = new P02PacketString(this, line);
								Main.getInstance().getServer().onReciveFromClient(pChat, this);
							}
						}
					} else {
						if (Main.getInstance().getServer().isTempMuted(this.Ip)) {
							this.WriteToClient(new P02PacketString(null, "You are temporarily muted until: "
									+ Main.getInstance().getServer().getUnmuteTime(this.Ip)));
						} else {
							this.WriteToClient(new P02PacketString(null, "You are permanently muted"));
						}
					}
				}
			}

			try {
				inFromClient.close();
				clientWriter.close();
				socket.close();
			} catch (IOException e) {

			}

			if (authenticated) {
				Main.getInstance().getServer().e.onDisconnect(this);
			}

		} catch (Exception ex) {

			try {
				inFromClient.close();
				clientWriter.close();
				socket.close();
			} catch (IOException e) {

			}

			if (authenticated) {
				Main.getInstance().getServer().e.onDisconnect(this);
			}

		}
	}

	public void WriteToClient(P02PacketString pChat) {
		try {
			Filter f = new Filter();

			String message = pChat.getString();

			if (!pChat.getSender().equals(this)) {
				message = f.proccessIPAddresses(message);
				message = f.proccessLinks(message);
			}

			if (isEncrypted) {
				message = CryptoManager.encode(message);
			}

			clientWriter.writeBytes(message + "\n");
			clientWriter.flush();
		} catch (Exception e) {
			Destroy();
		}
	}

	public void Destroy() {
		try {
			socket.close();
			clientWriter.close();
		} catch (IOException e) {

		}
		Main.getInstance().getServer().users.remove(this);
	}

	public boolean isConsole() {
		return serverUser;
	}

	/**
	 * Used to create a server user
	 * 
	 * @param username
	 */
	public User(String username) {
		this.Username = username;
		this.channel = new Channel("#Server");
		this.Ip = "127.0.0.1";
		this.isOP = true;
		this.PermissionLevel = 1;
		this.serverUser = true;
	}

	/**
	 * Creates a normal user
	 * 
	 * @param username
	 * @param socket
	 * @param channel
	 */
	public User(String username, Socket socket, Channel channel) {
		this.Username = username;
		this.channel = channel;
		this.socket = socket;
		this.Ip = socket.getInetAddress().toString().substring(1);

		try {
			this.clientWriter = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {

		}

		ReciverThread = new Thread() {
			@Override
			public void run() {
				try {
					Reciver();
				} catch (Exception e) {

				}
			}
		};

		ReciverThread.start();
	}

	public boolean isOP() {
		return isOP;
	}

	public void setOP(boolean isOP) {
		this.isOP = isOP;
	}

	public boolean isMuted() {
		return isMuted;
	}

	public void setMuted(boolean isMuted) {
		this.isMuted = isMuted;
	}

	public String getLastMsg() {
		return lastMsg;
	}

	public void setLastMsg(String lastMsg) {
		this.lastMsg = lastMsg;
	}

	public String getExtraInfo() {
		return extraInfo;
	}

	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}

	public User getLastMsgRecived() {
		return lastMsgRecived;
	}

	public void setLastMsgRecived(User lastMsgRecived) {
		this.lastMsgRecived = lastMsgRecived;
	}

	public ArrayList<User> getIgnoredUsers() {
		return ignoredUsers;
	}

	public void setIgnoredUsers(ArrayList<User> ignoredUsers) {
		this.ignoredUsers = ignoredUsers;
	}

	public int getPermissionLevel() {
		return PermissionLevel;
	}

	public void setPermissionLevel(int permissionLevel) {
		PermissionLevel = permissionLevel;
	}

	public String getUsername() {
		return Username;
	}

	public String getIp() {
		return Ip;
	}

	public Socket getSocket() {
		return socket;
	}

	public boolean isMc() {
		return isMc;
	}

	public boolean isServerUser() {
		return serverUser;
	}

	public Channel getChannel() {
		return channel;
	}

	public boolean isEncrypted() {
		return isEncrypted;
	}

	public void setMc(boolean isMc) {
		this.isMc = isMc;
	}

}
