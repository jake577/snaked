package com.jesttek.snakeWar;

public class TextUtil {
	
	/**
	 * Left pad a number with zeros
	 * Used instead of String.Format, which is unavailable in GWT
	 * @param num The number to be displayed
	 * @param pad The number of digits to pad to
	 * @return The number as a a formatted String
	 */
	public static String intToZeroPaddedString(int num, int pad) {
		
		String result = String.valueOf(num);
		int length = result.length();
		if(length >= pad) {
			return result;
		}
		char[] z = new char[pad-length];
		for(int i=0; i < z.length; i++) {
			z[i]='0';
		}
		return "" + new String(z) + Integer.toString(num);
	} 
}
