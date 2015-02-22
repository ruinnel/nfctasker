package net.ruinnel.nfc.tasker.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidUtil {

	public static boolean find(String checkStr, String regex) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(checkStr);

		return matcher.find();
	}

	public static boolean isPhoneNum(String input) {
		if (input != null && input.length() > 0) {
			if(find(input, "^(\\+[0-9]{2})*[0-9\\-]+$")) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	public static boolean isMacAddress(String input) {
		if (input != null && input.length() > 0) {
			if(find(input, "^([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}$")) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	public static boolean isOnOffToogle(String input) {
		return ("on".equalsIgnoreCase(input) || "off".equalsIgnoreCase(input) || "toggle".equalsIgnoreCase(input));
	}

	public static boolean isOnOff(String input) {
		return ("on".equalsIgnoreCase(input) || "off".equalsIgnoreCase(input));
	}

	public static boolean isNumber(String input) {
		if (input != null && input.length() > 0) {
			if(find(input, "[^0-9]")) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}


	public static boolean isEmail(String input) {
		if (input != null && input.length() > 0) {
			if(!find(input, "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")) {
				return false;
			} else {
				return true;
			}
		}

		return false;
	}

	public static boolean isHex(String input) {
		if (input != null && input.length() > 0) {
			if(find(input, "[^0-9a-fA-F]")) {
				return false;
			} else {
				return true;
			}
		}

		return false;
	}

	public static boolean isIPAddr(String input) {
		return (input != null && input.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}"));
	}

	public static boolean isAlphaNumeric(String input) {
		if (input != null && input.length() > 0) {
			if(find(input, "[^0-9a-zA-Z_]+")) {
				return false;
			} else {
				return true;
			}
		}

		return false;
	}
}
