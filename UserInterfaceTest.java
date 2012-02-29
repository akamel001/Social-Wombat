import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

/**
 * 
 */

/**
 * @author Julia
 *
 */
public class UserInterfaceTest {

	/**
	 * Test method for {@link UserInterface#getValidSelectionFromUser(int)}.
	 */
	@Test
	public void testGetValidSelectionFromUser() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link UserInterface#convertIntToStringPermissions(int)}.
	 */
	@Test
	public void testConvertIntToStringPermissions() {
		assertEquals("Student", UserInterface.convertIntToStringPermissions(1));
		assertEquals("Teaching Assistant", UserInterface.convertIntToStringPermissions(2));
		assertEquals("Instructor", UserInterface.convertIntToStringPermissions(3));
	}

	/**
	 * Test method for {@link UserInterface#mapStringKeysToList(java.util.Map)}.
	 */
	@Test
	public void testMapStringKeysToList() {
		// Create the input test maps.
		Map<String, Integer> nullMap = null;
		Map<String, Integer> emptyMap = new HashMap<String, Integer>();
		Map<String, Integer> map1 = new HashMap<String, Integer>();
		map1.put(null, null);
		Map<String, Integer> map2 = new HashMap<String, Integer>();
		map2.put("Blue", new Integer(4));
		
		// Create the expected output test lists.
		List<String> emptyList = new ArrayList<String>();
		List<String> list1 = new ArrayList<String>();
		list1.add(null);
		List<String> list2 = Arrays.asList("Blue");
		
		assertEquals(emptyList, UserInterface.mapStringKeysToList(nullMap));
		assertEquals(emptyList, UserInterface.mapStringKeysToList(emptyMap));
		assertEquals(list1, UserInterface.mapStringKeysToList(map1));
		assertEquals(list2, UserInterface.mapStringKeysToList(map2));
	}

	/**
	 * Test method for {@link UserInterface#listToUIString(java.util.List)}.
	 */
	@Test
	public void testListToUIString() {
//		// Create the input test lists.
//		List<String> emptyList = new ArrayList<String>();
//		List<String> list1 = new ArrayList<String>();
//		list1.add(null);
//		List<String> list2 = Arrays.asList("Blue");
//		
//		// Create the expected output test strings.	
//		emptyString = ''
//		
//		assertEquals(emptyList, UserInterface.listToUIString(emptylist));
//		assertEquals(list1, UserInterface.listToUIString(list1));
//		assertEquals(list2, UserInterface.listToUIString(list2));
	}

	/**
	 * Test method for {@link UserInterface#addFormattingAlignLeft(java.lang.String)}.
	 */
	@Test
	public void testAddFormattingAlignLeft() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link UserInterface#addFormattingAlignCenter(java.lang.String)}.
	 */
	@Test
	public void testAddFormattingAlignCenter() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link UserInterface#displayPage(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testDisplayPage() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link UserInterface#clearScreen()}.
	 */
	@Test
	public void testClearScreen() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link UserInterface#generateBigDivider()}.
	 */
	@Test
	public void testGenerateBigDivider() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link UserInterface#generateSmallDivider()}.
	 */
	@Test
	public void testGenerateSmallDivider() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link UserInterface#main(java.lang.String[])}.
	 */
	@Test
	public void testMain() {
		fail("Not yet implemented"); // TODO
	}

}
