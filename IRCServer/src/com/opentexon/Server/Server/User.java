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

import com.opentexon.Crypto.CryptoManager;
import com.opentexon.Server.Main.Main;

/**
 * User object with listener
 * 
 * @author Alexander
 *
 */
public class User {

	public String Username;
	public String Channel;
	public String Ip;
	public Socket socket;
	public DataOutputStream clientWriter;

	public boolean isOP = false;
	public boolean isMuted = false;
	public boolean isMc = false;

	private Thread ReciverThread;

	private int swearWarnings = 0;
	private int maxSwear = 3;

	public boolean isEncrypted = false;

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
					this.WriteToClient("pong");
				} else {
					if (!isMuted) {
						Filter f = new Filter();
						if (f.isSwearWord(line)) {
							swearWarnings += 1;
							if (swearWarnings > maxSwear) {
								this.WriteToClient("You got banned for swearing");
								Main.getInstance().getServer().bannedUsers.add(this.Ip);
								Main.getInstance().getServer().e
										.NotifyOpsAndConsole("Banned " + this.Username + " for swearing");
								this.Destroy();
							} else {
								Main.getInstance().getServer().e
										.NotifyOpsAndConsole("User " + this.Username + " was swearing");
								if (maxSwear == swearWarnings) {
									this.WriteToClient("This is your final warning, stop swearing");
								} else {
									this.WriteToClient("Don't swear, you will be banned if you continue, warning "
											+ String.valueOf(swearWarnings) + "/" + String.valueOf(maxSwear));
								}
							}
						} else {
							line = f.proccessIPAddresses(line);
							line = f.proccessLinks(line);
							Main.getInstance().getServer().onReciveFromClient(line, this);
						}
					} else {
						this.WriteToClient(Main.getInstance().getServer().messages.muted());
					}
				}
			}

			try {
				inFromClient.close();
				clientWriter.close();
				socket.close();
			} catch (IOException e) {

			}

			Main.getInstance().getServer().e.onDisconnect(this);
		} catch (Exception ex) {

			try {
				inFromClient.close();
				clientWriter.close();
				socket.close();
			} catch (IOException e) {

			}
			Main.getInstance().getServer().e.onDisconnect(this);
		}
	}

	public String removeCodes(String message) {
		String result = message;

		result = result.replace("§1", "");
		result = result.replace("§9", "");
		result = result.replace("§3", "");
		result = result.replace("§b", "");
		result = result.replace("§4", "");
		result = result.replace("§c", "");
		result = result.replace("§e", "");
		result = result.replace("§6", "");
		result = result.replace("§2", "");
		result = result.replace("§a", "");
		result = result.replace("§5", "");
		result = result.replace("§d", "");
		result = result.replace("§f", "");
		result = result.replace("§7", "");
		result = result.replace("§8", "");
		result = result.replace("§0", "");

		return result;
	}

	public void WriteToClient(String message) {
		try {
			if (!isMc) {
				message = removeCodes(message);
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

	public User(String username, String channel, Socket socket) {
		this.Username = username;
		this.Channel = channel;
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

}
