package net.kivitechnologies.SupportLibrary.utils;

/**
 * Class for simplify work with Math
 * 
 * @author K. V. Bogatikov, KiVITechnologies Ltd, 2017
 * @version 1.0
 */
public final class MathUtils
{
	/**
	 * This method rounds some number to the nearest number of base in some degree
	 * @example round(18, 2) returns 16 because nearest 2^n is 16
	 * 
	 * @param number number for rounding
	 * @param base   number for use as base of degree to rounding
	 * @return rounded number
	 */
	public static int round(int number, int base)
	{
		int target = base;
		
		while(target < number)
			target *= base;
			
		if(target - number > number - (target / base))
			target /= base;
		
		return target;
	}
	
	/**
	 * This method rounds some double number with right simbols after point
	 * @example round(0.98765, 3) returns 0.988
	 * 
	 * @param number number for rounding
	 * @param right  count of symbols after point
	 * @return rounded number
	 */
	public static double round(double number, int right)
	{
		double e = Math.pow(10, right);
		return Math.round(number * e) / e;
	}
	
	/**
	 * This method returns logarithm by A on base B
	 * 
	 * @param a number to count log
	 * @param b base of log
	 * @return log by A on base B
	 */
	public static double log(int a, int b)
	{
		return Math.log10(a) / Math.log(b);
	}
}
