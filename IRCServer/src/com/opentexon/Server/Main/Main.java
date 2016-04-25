package com.opentexon.Server.Main;

import java.util.Scanner;

import com.opentexon.Server.Server.Server;
import com.opentexon.Utils.Logger;

public class Main {

	private static Main instance;
	private Logger logger;
	private Server server;

	private Thread inputListener;
	private Thread ircThread;

	public boolean setName = false;
	public boolean setPort = false;

	public String Name;

	public static Main getInstance() {
		return instance;
	}

	public Server getServer() {
		return server;
	}

	public Logger getLogger() {
		return logger;
	}

	public Thread getInputListenerThread() {
		return inputListener;
	}

	public Thread getIRCThread() {
		return ircThread;
	}

	private void initInputListener() {
		logger.printInfoMessage("Starting console input reader...");
		inputListener = new Thread() {
			@Override
			public void run() {
				try {
					Main.getInstance().getLogger().printInfoMessage("Started console input reader");

					Scanner scanIn = new Scanner(System.in);

					String line = null;

					while ((line = scanIn.nextLine()) != null) {
						if (!Main.getInstance().setName) {
							Main.getInstance().setName = true;
							Main.getInstance().Name = line;
							logger.printInfoMessage("Please type a port (Default/Recommended is 6687):");
						} else {
							if (!Main.getInstance().setPort) { // &&
																// StringUtils.isNumeric(line))
																// {
								Main.getInstance().setPort = true;
								Main.getInstance().server = new Server(Main.getInstance().Name, Integer.valueOf(line));
								Main.getInstance().ircThread.start();
								// } else if (!Main.getInstance().setPort &&
								// !StringUtils.isNumeric(line)) {
								// logger.printWarningMessage("Invalid port");
								// logger.printWarningMessage("Please type a
								// port (Default/Recommended is 6687):");
							} else {
								Main.getInstance().getServer().onInput(line);
							}
						}
					}

					scanIn.close();
				} catch (StringIndexOutOfBoundsException ex) {
					Main.getInstance().getLogger().printErrorMessage(
							"Input reader crashed because of a invalid input. Please restart the server.");
				}
			}
		};
		inputListener.start();
	}

	public void init(String[] args) {
		logger = new Logger();
		logger.printInfoMessage("Starting server...");
		initInputListener();

		ircThread = new Thread() {
			@Override
			public void run() {
				Main.getInstance().getServer().startServer();
			}
		};

		logger.printInfoMessage("Please type a name for this server:");

	}

	public static void main(String[] args) {
		instance = new Main();
		instance.init(args);
	}

}
