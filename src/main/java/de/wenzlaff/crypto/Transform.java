package de.wenzlaff.crypto;

/**
 * Util Funktionen.
 * 
 * @author Thomas Wenzlaff
 *
 */
public final class Transform {

	/**
	 * Liefert die Bytes in HEX z.B. 52783243c1697bdbe16d37f97f68f08325dc1528
	 * 
	 * @param hash der Hash in Byte
	 * @return den HEX String in groß/klein Buchstaben z.B.
	 *         52783243c1697bdbe16d37f97f68f08325dc1528
	 */
	public static String bytesToHex(byte[] hash) {
		StringBuilder hexString = new StringBuilder(2 * hash.length);
		for (int i = 0; i < hash.length; i++) {
			String hex = Integer.toHexString(0xff & hash[i]);
			if (hex.length() == 1) {
				hexString.append('0');
			}
			hexString.append(hex);
		}
		return hexString.toString();
	}
}