package com.opentexon.Client.Main;

import java.util.Scanner;

import com.opentexon.Client.Client.Client;
import com.opentexon.Utils.Logger;
import com.opentexon.Utils.StringUtils;

public class Main {

	private static Main instance;
	private Client client;
	private Logger logger;

	private String version = "1.0.6";

	public static Main getInstance() {
		return instance;
	}

	public Logger getLogger() {
		return logger;
	}

	public Client getClient() {
		return client;
	}

	private void init(String[] args) {
		logger = new Logger();
		startInputListener();
	}

	public static void main(String[] args) {
		instance = new Main();
		instance.init(args);
	}

	private void startInputListener() {
		Thread inputListener = new Thread() {
			@Override
			public void run() {
				try {
					System.out.println("Welcome to the IRC client version: " + version + "\nCopyright"
							+ " © Alexander 2016 All Rights Reserved");

					System.out.println("Please authenticate by typing /join [IP] [Port] [Username] [Channel]");
					System.out.println("Example: /join 81.8.246.246 6687 John #Talk");

					Scanner scanIn = new Scanner(System.in);

					String line = null;

					while ((line = scanIn.nextLine()) != null) {
						if (line.equals("/clear")) {
							try {
								final String os = System.getProperty("os.name");

								if (os.contains("Windows")) {
									new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
									Main.getInstance().logger.printInfoMessage("Cleared the screen");
								} else {
									Main.getInstance().logger.printInfoMessage("Could not clear the screen");
								}

							} catch (Exception e) {
								Main.getInstance().logger.printInfoMessage("Could not clear the screen");
							}
						} else {
							if (line.startsWith("/join")) {
								String channel = line.split(" ")[4];
								String username = line.split(" ")[3];
								String ip = line.split(" ")[1];
								String port = line.split(" ")[2];

								if (username.equals("")) {
									System.out.println("Username cannot be empty");
									continue;
								}

								if (channel.equals("")) {
									System.out.println("Channel cannot be empty");
									continue;
								} else if (!channel.startsWith("#")) {
									System.out.println("Channel must start with a hashtag");
									continue;
								}

								if (ip.equals("")) {
									System.out.println("Server ip address cannot be empty");
									continue;
								}

								if (port.equals("")) {
									System.out.println("Server port cannot be empty");
									continue;
								} else {
									if (!StringUtils.isNumeric(port)) {
										System.out.println("Server port can only contain numbers");
										continue;
									}
								}

								client = new Client(ip, Integer.valueOf(port), username, channel);
								client.Start();
							} else {
								client.onInput(line);
							}
						}

					}

					scanIn.close();
				} catch (StringIndexOutOfBoundsException ex) {
					Main.getInstance().logger.printErrorMessage(
							"Input reader crashed because of a invalid input. Please restart the client.");
				}
			}
		};
		inputListener.start();
	}

}
