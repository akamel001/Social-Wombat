
public class UserInterfaceHelper {

	static int iWINDOWWIDTH = 78;
	static final String sNEW_LINE = System.getProperty("line.separator");
	static final String sBIG_DIVIDER = generateBigDivider();
	static final String sSMALL_DIVIDER = generateSmallDivider();
	
	/**
	 * Adds borders and appropriate amount of white space depending on the length of the input string.
	 * Uses center text alignment.
	 * @param string
	 * @return formattedString
	 */
	public static String addFormattingAlignCenter(String string) {
		int leftWidth = (iWINDOWWIDTH - string.length())/2;
		int rightWidth = iWINDOWWIDTH - string.length() - leftWidth;
		
		String formattedString = "|";
		for (int i = 0; i < leftWidth; i++){
			formattedString = formattedString.concat(" ");
		}
		formattedString = formattedString.concat(string);
		for (int j = 0; j < rightWidth; j++){
			formattedString = formattedString.concat(" ");
		}
		formattedString = formattedString.concat("|" + sNEW_LINE);
		return formattedString;		
	}
	
	/**
	 * Clears the the top of the screen depending on the size of your window.
	 */
	public static void clearScreen() {
		System.out.println(((char) 27)+"[2J");
	}
	
	////////////////////////////////////////////////
	//         GENERATORS FOR UI BORDERS          //
	////////////////////////////////////////////////
	
	/**
	 * Generates a big divider string.
	 * @return big divider
	 */
	public static String generateBigDivider() {
		String bigDivider = "+";
		for (int i = 0; i < iWINDOWWIDTH; i++){
			bigDivider = bigDivider.concat("=");
		}
		return bigDivider.concat("+" + sNEW_LINE);		
	}
	
	/**
	 * Generates a small divider string.
	 * @return small divider
	 */
	public static String generateSmallDivider() {
		String smallDivider = "+";
		for (int i = 0; i < iWINDOWWIDTH; i++){
			smallDivider = smallDivider.concat("-");
		}
		return smallDivider.concat("+" + sNEW_LINE);		
	}

	/**
	 * Adds borders and appropriate amount of white space depending on the length of the input string.
	 * Uses left text alignment.
	 * @param string
	 * @return formattedString
	 */
	public static String addFormattingAlignLeft(String string) {
		String formattedString = "| ";
		formattedString = formattedString.concat(string);
		if (string.contains("%%")) { // this relies on the fact that the percent symbol is used only once within a line
			for (int i = 0; i < iWINDOWWIDTH - string.length(); i++){
				formattedString = formattedString.concat(" ");
			}
		} else {
			for (int i = 0; i < iWINDOWWIDTH - string.length() - 1; i++){
				formattedString = formattedString.concat(" ");
			}
		}
		formattedString = formattedString.concat("|" + sNEW_LINE);
		return formattedString;
	}	

}