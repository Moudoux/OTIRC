package com.opentexon.Server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.opentexon.Crypto.CryptoManager;
import com.opentexon.Server.Main.Main;
import com.opentexon.Server.Server.Packets.P00PacketLogin;
import com.opentexon.Utils.WebHelper;

public class Server {

	public ArrayList<User> users = new ArrayList<User>();
	public ArrayList<String> bannedUsers = new ArrayList<String>();
	public ArrayList<String> opUsers = new ArrayList<String>();

	public int ServerPort;
	public String ServerName;

	public Events e = new Events();
	public MessageManager messages = new MessageManager();

	public int totalLogins = 0;

	/**
	 * The server version
	 */
	public String version = "1.0.5";

	/**
	 * Max connections from the same ip
	 */
	public int maxConnections = 3;

	/**
	 * Server object
	 * 
	 * @param ServerName
	 * @param Port
	 */
	public Server(String ServerName, int Port) {
		this.ServerName = ServerName;
		this.ServerPort = Port;
	}

	/**
	 * On recive client login event
	 * 
	 * @param socket
	 */
	@SuppressWarnings("unused")
	public void onRecive(Socket socket) {
		if (socket.equals(null) || !socket.isConnected()) {
			Main.getInstance().getLogger().printInfoMessage("Client socket is null, aborting");
			return;
		}
		try {
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			String line = null;

			Main.getInstance().getLogger().printInfoMessage("Listening for authentication...");

			boolean isEncrypted = true;

			while ((line = inFromClient.readLine()) != null) {
				if (line.contains("JOIN")) {
					isEncrypted = false;
				}
				if (isEncrypted) {
					line = CryptoManager.decode(line);
					Main.getInstance().getLogger().printInfoMessage("Client is using encryption");
				} else {
					Main.getInstance().getLogger().printWarningMessage("Client is not using encryption");
				}
				if (!line.startsWith("JOIN")) {
					Main.getInstance().getLogger().printWarningMessage("Client failed authentication, aborting");
					inFromClient.close();
					socket.close();
					return;
				} else {
					Main.getInstance().getLogger().printInfoMessage("Recived client authentication...");
					P00PacketLogin j = new P00PacketLogin(line, socket, isEncrypted);
					return;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Starts the server
	 */
	public void startServer() {
		ServerSocket welcomeSocket = null;
		try {

			welcomeSocket = new ServerSocket();

			InetSocketAddress insa = new InetSocketAddress("0.0.0.0", ServerPort);

			welcomeSocket.bind(insa);

			Main.getInstance().getLogger().printInfoMessage("Server started");
			Main.getInstance().getLogger().printInfoMessage("Listening for clients on "
					+ WebHelper.getPage("https://api.ipify.org/") + ":" + String.valueOf(ServerPort));

			while (true) {
				final Socket connectionSocket = welcomeSocket.accept();

				Thread listen;

				listen = new Thread() {
					@Override
					public void run() {
						Main.getInstance().getLogger().printInfoMessage("Client@"
								+ connectionSocket.getInetAddress().toString().substring(1) + " is connecting...");
						onRecive(connectionSocket);
					}
				};

				listen.start();

			}
		} catch (Exception ex) {
			ex.printStackTrace();
			try {
				welcomeSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Main.getInstance().getLogger().printErrorMessage("Failed to start server");
		}
	}

	/**
	 * On console input event
	 * 
	 * @param line
	 */
	public void onInput(String line) {
		e.onInput(line);
	}

	/**
	 * On recive raw from client
	 * 
	 * @param line
	 * @param u
	 */
	public void onReciveFromClient(String line, User u) {
		e.onRecive(line, u);
	}

}
