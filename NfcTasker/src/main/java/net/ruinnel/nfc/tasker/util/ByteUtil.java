package net.ruinnel.nfc.tasker.util;

public class ByteUtil {
	private static final String TAG = ByteUtil.class.getSimpleName();

	/**
	 * Returns an hexadecimal string representation of the given byte array,
	 * where each byte is represented by two hexadecimal characters and padded
	 * with a zero if its value is comprised between 0 and 15 (inclusive). As an
	 * example, this method will return "6d75636f0a" when called with the byte
	 * array {109, 117, 99, 111, 10}.
	 *
	 * @param bytes
	 *            the array of bytes for which to get an hexadecimal string
	 *            representation
	 * @return an hexadecimal string representation of the given byte array
	 */

	/**
	 * byte를 String로 변환합니다.
	 *
	 * @param b
	 * @return
	 */
	public static String toHexString(byte b) {
		String hexByte;
		hexByte = Integer.toHexString(b & 0xFF).toUpperCase();
		if (hexByte.length() == 1) {
			hexByte = "0" + hexByte;
		}
		return hexByte;
	}

	/**
	 * byte[]를 string로 변경합니다. 변환시 1바이트 단위로 space가 삽입됩니다.
	 *
	 * @param bytes 변환 할 데이터
	 * @return
	 */
	public static String toHexString(byte[] bytes) {
		if (bytes == null || bytes.length == 0) {
			return "00";
		}
		StringBuffer sb = new StringBuffer();

		int bytesLen = bytes.length;
		String hexByte;
		for (int i = 0; i < bytesLen; i++) {
			hexByte = Integer.toHexString(bytes[i] & 0xFF).toUpperCase();
			if (hexByte.length() == 1) {
				sb.append('0');
			}
			sb.append(hexByte);
			sb.append(" ");
		}

		return sb.toString();
	}

	/**
	 * byte[]를 string로 변경합니다. 변환시 1바이트 단위로 delimiter가 삽입됩니다.
	 *
	 * @param bytes 변환 할 데이터
	 * @return
	 */
	public static String toHexString(byte[] bytes, String delimiter) {
		if (bytes == null || bytes.length == 0) {
			return "00";
		}
		StringBuffer sb = new StringBuffer();

		int bytesLen = bytes.length;
		String hexByte;
		for (int i = 0; i < bytesLen; i++) {
			hexByte = Integer.toHexString(bytes[i] & 0xFF).toUpperCase();
			if (hexByte.length() == 1) {
				sb.append('0');
			}
			sb.append(hexByte);
			sb.append(delimiter);
		}

		return sb.toString();
	}

	/**
	 * byte[]를 string로 변경합니다. 변환시 1바이트 단위로 space가 삽입됩니다.
	 *
	 * @param bytes  변환할 데이터
	 * @param length 변환 할 길이
	 * @return
	 */
	public static String toHexString(byte[] bytes, int length) {
		if (bytes == null || bytes.length < length) {
			return "00";
		}
		StringBuffer sb = new StringBuffer();

		int bytesLen = length;
		String hexByte;
		for (int i = 0; i < bytesLen; i++) {
			hexByte = Integer.toHexString(bytes[i] & 0xFF).toUpperCase();
			if (hexByte.length() == 1) {
				sb.append('0');
			}
			sb.append(hexByte);
			sb.append(" ");
		}

		return sb.toString();
	}

	/**
	 * byte array를 서로 비교합니다.
	 *
	 * @param ba1
	 * @param ba2
	 * @return 같을 경우 true, 다른 경우 false
	 */
	public static boolean compare(byte[] ba1, byte[] ba2) {

		if (ba1.length != ba2.length) {
			return false;
		} else {
			for (int i = 0; i < ba1.length; i++) {
				if (ba1[i] != ba2[i]) {
					return false;
				}
			}
			return true;
		}
	}

	/**
	 * byte[]를 int로 변경합니다.(4개 바이트를 넘을 경우 제대로 동작하지 않습니다.)
	 *
	 * @param ba
	 * @return
	 */
	public static int toInt(byte[] ba) {
		int result = 0;
		for (int i = 0; i < ba.length; i++) {
			result = (result << 8) | (ba[i] & 0x000000FF);
		}
		return result;
	}

	/**
	 * byte array를 length 만큼 확장 합니다.(0x00) 으로 채움
	 *
	 * @param val
	 * @param length
	 * @return
	 */
	public static byte[] expend(byte[] val, int length) {
		byte[] result = new byte[length];
		System.arraycopy(val, 0, result, 0, val.length);
		if (val.length < length) {
			int diff = length - val.length;
			byte[] dummy = new byte[diff];
			for (int i = 0; i < diff; i++) {
				dummy[i] = 0x00;
			}
			System.arraycopy(dummy, 0, result, val.length, diff);
		}
		return result;
	}

	/**
	 * byte의 부호비트를 무시하고 int로 변환합니다.
	 *
	 * @param b
	 * @return
	 */
	public static int unsignedByteToInt(byte b) {
		return (int) b & 0xFF;
	}

	/** crc 처리용 상수 */
	public static final byte r1[] = {(byte) 0x00, (byte) 0x5e, (byte) 0xbc, (byte) 0xe2, (byte) 0x61, (byte) 0x3f, (byte) 0xdd, (byte) 0x83, (byte) 0xc2, (byte) 0x9c, (byte) 0x7e, (byte) 0x20, (byte) 0xa3, (byte) 0xfd, (byte) 0x1f, (byte) 0x41};

	/** crc 처리용 상수 */
	public static final byte r2[] = {(byte) 0x00, (byte) 0x9d, (byte) 0x23, (byte) 0xbe, (byte) 0x46, (byte) 0xdb, (byte) 0x65, (byte) 0xf8, (byte) 0x8c, (byte) 0x11, (byte) 0xaf, (byte) 0x32, (byte) 0xca, (byte) 0x57, (byte) 0xe9, (byte) 0x74};

	/**
	 * crc8을 생성합니다.
	 *
	 * @param cCrc
	 * @param cData
	 * @return
	 */
	public static byte CRC8Byte(byte cCrc, byte cData) {
		byte i = (byte) ((cData ^ cCrc) & 0xff);
		cCrc = (byte) (r1[i & 0xf] ^ r2[(i >> 4) & 0x0f]);
		return cCrc;
	}

	/**
	 * crc8를 생성합니다.
	 *
	 * @param crc_in
	 * @param buff
	 * @param count
	 * @return
	 */
	public static byte getCRC8(byte crc_in, byte[] buff, int count) {
		byte rtn;
		byte inx;

		rtn = crc_in;
		for (int i = 0; i < count; i++) {
			inx = (byte) ((rtn ^ buff[i]) & 0xff);
			rtn = (byte) (r1[inx & 0x0f] ^ r2[(inx >> 4) & 0x0f]);
		}
		return rtn;
	}

	/**
	 * 16진수 문자열을 byte array로 변환 합니다.(단, 문자열 길이는 짝수만)
	 *
	 * @param val
	 * @return
	 */
	public static byte[] hexStringToByteArray(String val) {
		if (val.length() % 2 != 0) {
			return null;
		}

		byte[] result = new byte[val.length() / 2];
		for (int i = 0; i < val.length(); i = i + 2) {
			String s = val.substring(i, i + 2);
			result[i == 0 ? 0 : i / 2] = (byte) (Integer.parseInt(s, 16) & 0x000000FF);
		}
		return result;
	}

	/**
	 * int를 byte array로 변경합니다.
	 *
	 * @param val
	 * @return 변환된 byte[] (4 byte)
	 */
	public static byte[] toByteArray(int val) {
		return new byte[]{(byte) (val >>> 24), (byte) (val >>> 16), (byte) (val >>> 8), (byte) val};
	}

	/**
	 * int를 byte array로 변환합니다. 변환된 바이트 중 지정된 길이 만큼을 반환합니다. 변환 후의 바이트 수를 고려해서 사용해
	 * 주세요.
	 *
	 * @param val
	 * @param len
	 * @return 변환된 byte array
	 */
	public static byte[] toByteArray(int val, int len) {
		if (len > 4 || len < 0) {
			return null;
		}

		if (val > Math.pow(256, len)) {
			return null;
		}
		byte[] ba = new byte[]{(byte) (val >>> 24), (byte) (val >>> 16), (byte) (val >>> 8), (byte) val};

		byte[] result = new byte[len];

		System.arraycopy(ba, ba.length - len, result, 0, len);

		return result;
	}

	/**
	 * XOR Checksum을 생성합니다. JRM-700용이며, 0으로 시작하는 인덱스 값을 기준으로 1번째부터 n - 2까지의 패킷에
	 * 대한 check sum 을 반환합니다.
	 *
	 * @param packet
	 * @return XOR checksum
	 */
	public static byte getCheckSum(byte[] packet) {
		byte bcc = 0x00;
		for (int i = 1; i < packet.length - 1; i++) {
			bcc = (byte) (bcc ^ packet[i]);
		}
		return bcc;
	}

	/**
	 * null(0x00) 으로 끝나는 문자열(byte array)를 String 타입으롤 변환합니다.
	 *
	 * @param strArray
	 * @return
	 */
	public static String byteArrayToString(byte[] strArray) {
		int idx = 0;
		for (int i = 0; i < strArray.length; i++) {
			if (strArray[i] == 0x00) { // null
				break;
			} else {
				idx++;
			}
		}

		return new String(strArray, 0, idx);
	}
}