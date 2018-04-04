/* 
 * Bitcoin cryptography library
 * Copyright (c) Project Nayuki
 * 
 * https://www.nayuki.io/page/bitcoin-cryptography-library
 * https://github.com/nayuki/Bitcoin-Cryptography-Library
 */

package io.nayuki.bitcoin.crypto;

import static org.junit.Assert.assertEquals;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the functions of the Bech32 class.
 * @see Bech32
 */
public final class Bech32Test {
	
	@Test public void testBitGroupsToBech32() {
		Object[][] cases = {
			{"a12uel5l", "a", new byte[0]},
			{"an83characterlonghumanreadablepartthatcontainsthenumber1andtheexcludedcharactersbio1tt5tgs", "an83characterlonghumanreadablepartthatcontainsthenumber1andtheexcludedcharactersbio", new byte[0]},
			{"abcdef1qpzry9x8gf2tvdw0s3jn54khce6mua7lmqqqxw", "abcdef", new byte[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31}},
			{"11qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqc8247j", "1", new byte[82]},
			{"split1checkupstagehandshakeupstreamerranterredcaperred2y9e3w", "split", new byte[]{24,23,25,24,22,28,1,16,11,29,8,25,23,29,19,13,16,23,29,22,25,28,1,16,11,3,25,29,27,25,3,3,29,19,11,25,3,3,25,13,24,29,1,25,3,3,25,13}},
			{"?1ezyfcl", "?", new byte[0]},
		};
		for (Object[] cs : cases)
			assertEquals(cs[0], Bech32.bitGroupsToBech32((String)cs[1], (byte[])cs[2]));
	}
	
	
	@Test public void testCheckHumanReadablePartValid() {
		String[] cases = {
			"a",
			"bc",
			"1",
			"111",
			"the-quick.brown*fox",
		};
		for (String cs : cases)
			Bech32.checkHumanReadablePart(cs.toCharArray());
	}
	
	
	@Test public void testCheckHumanReadablePartInvalid() {
		String[] cases = {
			"",
			"012345678901234567890123456789012345678901234567890123456789012345678901234567890123",
			"abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz",
			"A",
			"xXx",
			"\u0020",
			"\u007F",
			"\u0080",
			"\u2000",
			"\uD852\uDF62",
		};
		for (String cs : cases) {
			try {
				Bech32.checkHumanReadablePart(cs.toCharArray());
				Assert.fail();
			} catch (IllegalArgumentException e) {}  // Pass
		}
	}
	
}
