package test;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import util.StringLegalityChecker;

/**
 * Tests the StringLegalityChecker class.
 * @author Julia
 */
public class StringLegalityCheckerTest {

	/**
	 * test method for {@link StringLegalityChecker#checkIfUsernameStringIsLegal(java.lang.String)}.
	 */
	@Test
	public void testCheckIfUsernameStringIsLegal() {		
		assertEquals(true, StringLegalityChecker.checkIfUsernameStringIsLegal("cat")); // legal string
		assertEquals(true, StringLegalityChecker.checkIfUsernameStringIsLegal("ILoveComputerScience")); // legal string
		assertEquals(true, StringLegalityChecker.checkIfUsernameStringIsLegal("yoshi54")); // legal string
		assertEquals(true, StringLegalityChecker.checkIfUsernameStringIsLegal("yoshi_54")); // legal string
		assertEquals(false, StringLegalityChecker.checkIfUsernameStringIsLegal("aa")); // too short
		assertEquals(true,  StringLegalityChecker.checkIfUsernameStringIsLegal("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")); // legal string
		assertEquals(false, StringLegalityChecker.checkIfUsernameStringIsLegal("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")); // too long
		assertEquals(false, StringLegalityChecker.checkIfUsernameStringIsLegal("ke$ha")); // invalid character
	}

	/**
	 * test method for {@link StringLegalityChecker#checkIfPasswordStringIsLegal(java.lang.String)}.
	 */
	@Test
	public void testCheckIfPasswordStringIsLegal() {	
		assertEquals(true, StringLegalityChecker.checkIfPasswordStringIsLegal("catLuvver1#".toCharArray())); // legal string
		assertEquals(true, StringLegalityChecker.checkIfPasswordStringIsLegal("ILoveComputer$cience3".toCharArray())); // legal string
		assertEquals(true, StringLegalityChecker.checkIfPasswordStringIsLegal("yoshi54A@#$%^&+=".toCharArray())); // legal string
		assertEquals(true, StringLegalityChecker.checkIfPasswordStringIsLegal("@#$%^&+=yo5S".toCharArray())); // legal string
		assertEquals(false, StringLegalityChecker.checkIfPasswordStringIsLegal("A1#a5aa".toCharArray())); // too short (7 chars)
		assertEquals(true,  StringLegalityChecker.checkIfPasswordStringIsLegal("A1#a5aaa".toCharArray())); // legal string (8 chars)
		assertEquals(true,  StringLegalityChecker.checkIfPasswordStringIsLegal("A1#a5aaaa5aaaa5aaaa5aaaa5".toCharArray())); // legal string (25 chars)
		assertEquals(false, StringLegalityChecker.checkIfPasswordStringIsLegal("A1#a5aaaa5aaaa5aaaa5aaaa5a".toCharArray())); // too long (26 chars)
		assertEquals(false, StringLegalityChecker.checkIfPasswordStringIsLegal("catLuvver#".toCharArray())); // not enough variety (missing numeral)
		assertEquals(false, StringLegalityChecker.checkIfPasswordStringIsLegal("catluvver1#".toCharArray())); // not enough variety (missing uppercase letter)
		assertEquals(false, StringLegalityChecker.checkIfPasswordStringIsLegal("CATLUVVER1#".toCharArray())); // not enough variety (missing lowercase letter)
		assertEquals(false, StringLegalityChecker.checkIfPasswordStringIsLegal("catLuvver1".toCharArray())); // not enough variety (missing special character)
		assertEquals(false, StringLegalityChecker.checkIfPasswordStringIsLegal("catLuvver1#?".toCharArray())); // invalid character
		assertEquals(false, StringLegalityChecker.checkIfPasswordStringIsLegal("catLuvver1#?#".toCharArray())); // invalid character
		assertEquals(false, StringLegalityChecker.checkIfPasswordStringIsLegal("?catLuvver1#".toCharArray())); // invalid character
		assertEquals(false, StringLegalityChecker.checkIfPasswordStringIsLegal("catLuvver1#?#".toCharArray())); // invalid character
		assertEquals(false, StringLegalityChecker.checkIfPasswordStringIsLegal("catLuvver1# #".toCharArray())); // invalid character
		assertEquals(true, StringLegalityChecker.checkIfPasswordStringIsLegal("catLuvver1##".toCharArray())); // invalid character
	}

}
