/**
 * This software is licensed under the MIT license.
 * If you wish to modify this software please give credit and link to the git: https://github.com/Moudoux/OTIRC.
 */
package com.opentexon.Client.Client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import com.opentexon.Client.Main.Main;
import com.opentexon.Crypto.CryptoManager;

public class Client {

	public String ip;
	public int port;
	public String username;
	public String channel;

	private DataOutputStream outToServer;
	private Socket socket;

	public Client(String ip, int port, String username, String channel) {
		this.ip = ip;
		this.port = port;
		this.username = username;
		this.channel = channel;
	}

	public void Start() {
		Thread client = new Thread() {
			@Override
			public void run() {
				try {
					Main.getInstance().getClient().Run();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		client.start();
	}

	private void Run() {
		Main.getInstance().getLogger().printInfoMessage("Connecting to " + ip + ":" + String.valueOf(port) + "...");

		try {
			socket = new Socket(ip, port);

			outToServer = new DataOutputStream(socket.getOutputStream());
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			outToServer.writeBytes(CryptoManager.encode("JOIN " + username + " " + channel) + "\n");
			outToServer.flush();

			String line = null;

			while ((line = inFromServer.readLine()) != null && !socket.isClosed()) {
				onRecive(line);
			}

			try {
				outToServer.close();
				inFromServer.close();
				socket.close();
			} catch (Exception e) {

			}

			Main.getInstance().getLogger()
					.printInfoMessage("You were disconnected by the server or the server is not online.");

		} catch (Exception ex) {
			try {
				socket.close();
			} catch (Exception e) {

			}
			Main.getInstance().getLogger()
					.printInfoMessage("You were disconnected by the server or the server is not online.");
		}
	}

	private void onRecive(String line) {
		line = CryptoManager.decode(line);
		Main.getInstance().getLogger().printInfoMessage(line);
	}

	public void onInput(String line) {
		try {
			if (line.startsWith("/")) {
				outToServer.writeBytes(CryptoManager.encode(line) + "\n");
				outToServer.flush();
			} else {
				outToServer.writeBytes(CryptoManager.encode("/channelmsg " + line) + "\n");
				outToServer.flush();
			}
		} catch (Exception ex) {
			Main.getInstance().getLogger().printWarningMessage("You are not connected to a server.");
		}
	}

}
