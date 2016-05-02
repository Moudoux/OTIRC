/**
 * This software is licensed under the MIT license.
 * If you wish to modify this software please give credit and link to the git: https://github.com/Moudoux/OTIRC.
 */
package com.opentexon.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Simple logger that outputs to console
 * 
 * @author Alexander
 *
 */
public class Logger {

	private String getCode(String line) {
		String code = line.substring(2).split("##")[0];
		return code;
	}

	private String getFormat() {
		return "[" + new SimpleDateFormat("dd/MM HH:mm:ss").format(Calendar.getInstance().getTime()) + "] ";
	}

	public void printInfoMessage(String message) {
		message = message.replace(getCode(message) + " ", "");
		System.out.println(getFormat() + "[Information]: " + message);
	}

	public void printErrorMessage(String message) {
		message = message.replace(getCode(message) + " ", "");
		System.err.println(getFormat() + "[Error]: " + message);
	}

	public void printWarningMessage(String message) {
		message = message.replace(getCode(message) + " ", "");
		System.out.println(getFormat() + "[Warning]: " + message);
	}

}
