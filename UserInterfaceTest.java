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
	
	private static final String sNEW_LINE = System.getProperty("line.separator");

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
	 * Depends on the iWindowWidth parameter in UserInterface.
	 */
	@Test
	public void testListToUIString() {
		// Create the input test lists.
		List<String> emptyList = new ArrayList<String>();
		List<String> list1 = Arrays.asList("Blue");
		List<String> list2 = Arrays.asList("Blue", "red", "PINK FLAMINGO");
		
		// Create the expected output test strings.	
		String emptyString = "";
		String string1 = "| 1. Blue                                                                         |" + sNEW_LINE;
		String string2 = "| 1. Blue                                                                         |" + sNEW_LINE +
						 "| 2. red                                                                          |" + sNEW_LINE +
						 "| 3. PINK FLAMINGO                                                                |" + sNEW_LINE;
		
		assertEquals(emptyString, UserInterface.listToUIString(emptyList));
		assertEquals(string1, UserInterface.listToUIString(list1));
		assertEquals(string2, UserInterface.listToUIString(list2));
	}

}
