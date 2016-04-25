package com.opentexon.Utils;

public class StringUtils {

	public static boolean isNumeric(String line) {
		boolean result = true;

		if (line.contains(" ")) {
			return false;
		}

		for (String s : line.split("")) {
			if (s.equals("1") || s.equals("2") || s.equals("3") || s.equals("4") || s.equals("5") || s.equals("6")
					|| s.equals("7") || s.equals("8") || s.equals("9") || s.equals("0")) {

			} else {
				result = false;
			}
		}

		return result;
	}

}
