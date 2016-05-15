package com.opentexon.Server.Server;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.opentexon.Server.Main.Main;
import com.opentexon.Utils.StringUtils;

public class ConfigManager {

	private String configFile = Main.getInstance().executionPath + "Config.dat";
	private final String configVersion = "0.0.1";

	private static ConfigManager instance = null;

	public static ConfigManager getInstance() {
		if (instance == null) {
			instance = new ConfigManager();
		}
		return instance;
	}

	public String getConfig() {
		try {
			return StringUtils.readFile(configFile, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	public void removeListValue(String node, String remove) throws IOException {
		File f = new File(configFile);
		ArrayList<String> config = new ArrayList<String>();

		if (f.exists()) {
			List<String> lines = Files.readAllLines(Paths.get(configFile), StandardCharsets.UTF_8);
			for (String line : lines) {
				if (line.split(":")[0].toLowerCase().equals(node.toLowerCase())) {
					if (line.split(":")[1].contains(", ")) {
						if (line.contains(", " + remove)) {
							line = line.replace(", " + remove, "");
						} else {
							line = line.replace(remove + ", ", "");
						}
						config.add(line);
					}
				} else {
					config.add(line);
				}
			}
		}

		try {
			this.flushConfig(config);
		} catch (Exception e) {
			Main.getInstance().getLogger().printErrorMessage("Failed to save config file");
		}
	}

	public void addListValue(String node, String add) throws IOException {
		File f = new File(configFile);
		ArrayList<String> config = new ArrayList<String>();

		boolean found = false;

		if (f.exists()) {
			List<String> lines = Files.readAllLines(Paths.get(configFile), StandardCharsets.UTF_8);
			for (String line : lines) {
				if (line.split(":")[0].toLowerCase().equals(node.toLowerCase()) && !line.contains(add)) {

					found = true;

					if (line.split(":")[1].contains(", ")) {
						line = line + ", " + add;
					} else if (!line.equals(node + ":")) {
						line = line + ", " + add;
					} else {
						line = node + ":" + add;
					}

					config.add(line);

				} else if (line.split(":")[0].toLowerCase().equals(node.toLowerCase())) {
					found = true;
					config.add(line);
				} else {
					config.add(line);
				}
			}
		}

		if (!found) {
			config.add(node + ":" + add);
		}

		try {
			this.flushConfig(config);
		} catch (Exception e) {
			Main.getInstance().getLogger().printErrorMessage("Failed to save config file");
		}
	}

	public ArrayList<String> getListValue(String node, String defaultValue) throws IOException {
		File f = new File(configFile);
		ArrayList<String> output = new ArrayList<String>();
		if (!f.exists()) {
			output.add(defaultValue);
			return output;
		}
		List<String> lines = Files.readAllLines(Paths.get(configFile), StandardCharsets.UTF_8);
		for (String line : lines) {
			if (line.split(":")[0].toLowerCase().equals(node.toLowerCase())) {
				if (line.split(":")[1].contains(", ")) {
					for (String spliter : line.split(":")[1].split(", ")) {
						output.add(spliter);
					}
				} else {
					output.add(line.split(":")[1]);
				}
				break;
			}
		}
		return output;
	}

	public String getValue(String node, String defaultValue) throws IOException {
		File f = new File(configFile);
		if (!f.exists()) {
			return defaultValue;
		}
		List<String> lines = Files.readAllLines(Paths.get(configFile), StandardCharsets.UTF_8);
		for (String line : lines) {
			if (line.split(":")[0].toLowerCase().equals(node.toLowerCase())) {
				return line.split(":")[1];
			}
		}
		return defaultValue;
	}

	public void flushConfig(ArrayList<String> config) throws Exception {
		PrintWriter writer = new PrintWriter(configFile, "UTF-8");
		writer.println("[ver] " + this.configVersion);
		for (String line : config) {
			if (line.startsWith("[ver]")) {
				continue;
			}
			writer.println(line);
		}
		writer.close();

	}

	public void setValue(String node, String value) throws IOException {
		boolean flag = false;
		File f = new File(configFile);
		ArrayList<String> config = new ArrayList<String>();

		if (f.exists()) {
			List<String> lines = Files.readAllLines(Paths.get(configFile), StandardCharsets.UTF_8);
			for (String line : lines) {
				if (line.split(":")[0].toLowerCase().equals(node.toLowerCase())) {
					flag = true;
					line = node + ":" + value;
					config.add(line);
				} else {
					config.add(line);
				}
			}
		}

		if (!flag) {
			config.add(node + ":" + value);
		}

		try {
			this.flushConfig(config);
		} catch (Exception e) {
			Main.getInstance().getLogger().printErrorMessage("Failed to save config file");
		}
	}

}
