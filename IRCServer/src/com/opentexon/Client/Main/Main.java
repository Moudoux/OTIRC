/**
 * This software is licensed under the MIT license.
 * If you wish to modify this software please give credit and link to the git: https://github.com/Moudoux/OTIRC.
 */
package com.opentexon.Client.Main;

import java.util.Calendar;
import java.util.Scanner;

import com.opentexon.Client.Client.Client;
import com.opentexon.Utils.Logger;
import com.opentexon.Utils.StringUtils;
import com.opentexon.Utils.WebHelper;

public class Main {

	private static Main instance;
	private Client client;
	private Logger logger;

	public final String version = "1.0.6a";

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

	private void listen() {
		try {
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

						String pw = "";

						if (line.contains("-p")) {
							pw = " -p " + line.split("-p")[1].substring(1);
						}

						client = new Client(ip, Integer.valueOf(port), username,
								channel + " Offical IRC Client v." + version + pw);
						client.Start();
					} else {
						client.onInput(line);
					}
				}

			}

			scanIn.close();
		} catch (Exception ex) {
			listen();
		}
	}

	private void startInputListener() {
		Thread inputListener = new Thread() {
			@Override
			public void run() {

				Calendar now = Calendar.getInstance();
				System.out.println("Welcome to the IRC client version: " + version + "\nCopyright" + " � Alexander "
						+ String.valueOf(now.get(Calendar.YEAR)) + " All Rights Reserved");

				// Do NOT remove the following line, it has to be here
				// because of the MIT license
				System.out.println("Source and executables for client and server avaiable at: \n"
						+ "https://github.com/Moudoux/OTIRC");

				System.out.println("Please authenticate by typing /join [IP] [Port] [Username] [Channel]");
				System.out.println("Example: /join " + WebHelper.getPage("https://dl.opentexon.com/.irc/ip.php")
						+ " 6687 John #Talk");
				listen();

			}
		};
		inputListener.start();
	}

}
