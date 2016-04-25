package com.opentexon.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Logger {

	private String removeCodes(String message) {
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

	private String getCode(String line) {
		String code = line.substring(2).split("##")[0];
		return code;
	}

	private String getFormat() {
		return "[" + new SimpleDateFormat("dd/MM HH:mm:ss").format(Calendar.getInstance().getTime()) + "] ";
	}

	public void printInfoMessage(String message) {
		message = message.replace(getCode(message) + " ", "");
		message = removeCodes(message);
		System.out.println(getFormat() + "[Information]: " + message);
	}

	public void printErrorMessage(String message) {
		message = message.replace(getCode(message) + " ", "");
		message = removeCodes(message);
		System.err.println(getFormat() + "[Error]: " + message);
	}

	public void printWarningMessage(String message) {
		message = message.replace(getCode(message) + " ", "");
		message = removeCodes(message);
		System.out.println(getFormat() + "[Warning]: " + message);
	}

}
