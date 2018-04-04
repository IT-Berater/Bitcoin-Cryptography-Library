/* 
 * Bitcoin cryptography library
 * Copyright (c) Project Nayuki
 * 
 * https://www.nayuki.io/page/bitcoin-cryptography-library
 * https://github.com/nayuki/Bitcoin-Cryptography-Library
 */

package io.nayuki.bitcoin.crypto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;


/**
 * Converts data to and from Bech32 strings. Not instantiable.
 */
public final class Bech32 {
	
	/*---- Static functions ----*/
	
	/**
	 * Encodes the specified human-readable part prefix plus
	 * the specified array of 5-bit data into a Bech32 string.
	 * @param humanPart the prefix given to the resulting string, which should be a mnemonic for
	 * the cryptocurrency name; must be not {@code null}, must have length in the range [1, 83],
	 * must have all characters in the ASCII range [33, 126] but excluding uppercase characters
	 * @param data a non-{@code null} sequence of zero or more values, where each value is a uint5
	 * @return the Bech32 string representing the specified two pieces of data;
	 * the result is entirely ASCII, lacks uppercase, and at most 90 characters long
	 * @throws IllegalArgumentException if any argument violates the stated
	 * preconditions, or {@code humanPart.length() + data.length > 83}
	 */
	public static String bitGroupsToBech32(String humanPart, byte[] data) {
		// Check arguments
		char[] human = humanPart.toCharArray();
		checkHumanReadablePart(human);
		Objects.requireNonNull(data);
		for (byte b : data) {
			if ((b >>> 5) != 0)
				throw new IllegalArgumentException("Expected 5-bit groups");
		}
		if (human.length + 1 + data.length + 6 > 90)
			throw new IllegalArgumentException("Output too long");
		
		// Compute checksum
		int checksum;
		try {
			ByteArrayOutputStream temp = expandHumanReadablePart(human);  // Every element is uint5
			temp.write(data);
			temp.write(new byte[CHECKSUM_LEN]);
			checksum = polymod(temp.toByteArray()) ^ 1;
		} catch (IOException e) {
			throw new AssertionError(e);  // Impossible
		}
		
		// Encode to base-32
		StringBuilder sb = new StringBuilder(humanPart).append('1');
		for (byte b : data)
			sb.append(ALPHABET.charAt(b));
		for (int i = 0; i < CHECKSUM_LEN; i++) {
			int b = (checksum >>> ((CHECKSUM_LEN - 1 - i) * 5)) & 0x1F;  // uint5
			sb.append(ALPHABET.charAt(b));
		}
		return sb.toString();
	}
	
	
	// Throws an exception if any of the following:
	// * The string is null.
	// * Its length is outside the range [1, 83].
	// * It contains non-ASCII characters outside the range [33, 126].
	// * It contains uppercase characters.
	// Otherwise returns silently.
	static void checkHumanReadablePart(char[] s) {
		Objects.requireNonNull(s);
		int n = s.length;
		if (n < 1 || n > 83)
			throw new IllegalArgumentException("Invalid length of human-readable part string");
		
		for (char c : s) {
			if (c < 33 || c > 126)
				throw new IllegalArgumentException("Invalid character in human-readable part string");
			if ('A' <= c && c <= 'Z')
				throw new IllegalArgumentException("Human-readable part string must be lowercase");
		}
	}
	
	
	// Returns a new byte buffer containing uint5 values, representing the given string
	// expanded into the prefix data for the purpose of computing/verifying a checksum.
	private static ByteArrayOutputStream expandHumanReadablePart(char[] s) {
		ByteArrayOutputStream result = new ByteArrayOutputStream();  // Every element is uint5
		for (char c : s)
			result.write(c >>> 5);  // uint3 from high bits
		result.write(0);
		for (char c : s)
			result.write(c & 0x1F);  // uint5 from low bits
		return result;
	}
	
	
	// Computes the polynomial remainder of the given sequence of 5-bit groups. The result is a uint30.
	private static int polymod(byte[] data) {
		int result = 1;
		for (byte b : data) {
			assert 0 <= b && b < 32;  // uint5
			int x = result >>> 25;
			result = ((result & ((1 << 25) - 1)) << 5) | b;
			for (int i = 0; i < GENERATOR.length; i++)
				result ^= ((x >>> i) & 1) * GENERATOR[i];
			assert (result >>> 30) == 0;  // uint30
		}
		return result;
	}
	
	
	
	/*---- Class constants ----*/
	
	// The base-32 alphabet. Designed so that visually similar characters having small bit differences.
	private static final String ALPHABET = "qpzry9x8gf2tvdw0s3jn54khce6mua7l";
	
	// For computing/verifying checksums. Each element is a uint30.
	private static final int[] GENERATOR = {0x3B6A57B2, 0x26508E6D, 0x1EA119FA, 0x3D4233DD, 0x2A1462B3};
	
	// Number of uint5 groups. Do not modify.
	private static final int CHECKSUM_LEN = 6;
	
	
	
	/*---- Miscellaneous ----*/
	
	private Bech32() {}  // Not instantiable
	
}
