import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests the StringLegalityChecker class.
 * @author Julia
 */
public class StringLegalityCheckerTest {

	/**
	 * Test method for {@link StringLegalityChecker#checkIfUsernameStringIsLegal(java.lang.String)}.
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
	 * Test method for {@link StringLegalityChecker#checkIfPasswordStringIsLegal(java.lang.String)}.
	 */
	@Test
	public void testCheckIfPasswordStringIsLegal() {	
		assertEquals(true, StringLegalityChecker.checkIfPasswordStringIsLegal("catLuvver1#")); // legal string
		assertEquals(true, StringLegalityChecker.checkIfPasswordStringIsLegal("ILoveComputer$cience3")); // legal string
		assertEquals(true, StringLegalityChecker.checkIfPasswordStringIsLegal("yoshi54A!@#$%^&*_+-=")); // legal string
		assertEquals(true, StringLegalityChecker.checkIfPasswordStringIsLegal("!@#$%^&*yo_+-=5S")); // legal string
		assertEquals(false, StringLegalityChecker.checkIfPasswordStringIsLegal("A1#aaaa")); // too short
		assertEquals(true, StringLegalityChecker.checkIfPasswordStringIsLegal("A1#aaaaa")); // legal string
		assertEquals(true,  StringLegalityChecker.checkIfPasswordStringIsLegal("A1#aaaaaaaaaaaaaaaaaaaaaa")); // legal string
//		assertEquals(false, StringLegalityChecker.checkIfPasswordStringIsLegal("A1#aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")); // too long
		assertEquals(false, StringLegalityChecker.checkIfPasswordStringIsLegal("catLuvver#")); // not enough variety (missing numeral)
		assertEquals(false, StringLegalityChecker.checkIfPasswordStringIsLegal("catluvver1#")); // not enough variety (missing uppercase letter)
		assertEquals(false, StringLegalityChecker.checkIfPasswordStringIsLegal("CATLUVVER1#")); // not enough variety (missing lowercase letter)
//		assertEquals(false, StringLegalityChecker.checkIfPasswordStringIsLegal("catLuvver1")); // not enough variety (missing special character)
		assertEquals(false, StringLegalityChecker.checkIfPasswordStringIsLegal("catLuvver1#?")); // invalid character
	}

}
