package net.kivitechnologies.SupportLibrary.utils;

/**
 * Class for work with colors
 * Main feutures: parsing, changing properties and chennels
 * 
 * @author K. V. Bogatikov, KiVITechnologies Ltd, 2017
 * @version 1.0
 */
public class ColorUtils
{
	/**
	 * Method parses three channels of color model:
	 * h (hsv[0]) - Hue
	 * s (hsv[1]) - Sautration
	 * v (hsv[2]) - Value / Brightness
	 * 
	 * @param hsv an array with channels of HSV model
	 * @return color parsed from hsv
	 */
	public static int parse(float[] hsv)
	{
		return android.graphics.Color.HSVToColor(hsv);
	}

	/**
	 * Method parses three channels of color model HSV:
	 * @param h - Hue - 0...1
	 * @param s - Sautration - 0...1
	 * @param v - Value / Brightness 0...1
	 * 
	 * @return color parsed from h, s and v
	 */
	public static int parse(float h, float s, float v)
	{
		return android.graphics.Color.HSVToColor(new float[]{ h, s, v });
	}

	/**
	 * Method parses three channels of color model RGB:
	 * @param r - Red component - 0...255
	 * @param g - Green component - 0...255
	 * @param b - Blue component - 0...255
	 * 
	 * @return color parsed from r, g, b
	 */
	
	public static int parse(int r, int g, int b)
	{
		return android.graphics.Color.rgb(r, g, b);
	}

	/**
	 * Method parses hexademical color
	 * 
	 * @param hex Hexademical color such as "#RRGGBB" or "#AARRGGBB" or "RRGGBB" or "AARRGGBB" or "RGB"; 
	 * @return color parsed from hsv
	 */
	public static int parse(String hex)
	{
		if(!hex.startsWith("#"))
			hex += "#";
		
		if(hex.length() == 4) //#RGB
			hex = new String(new char[]{ hex.charAt(0), hex.charAt(1), hex.charAt(1), hex.charAt(2), hex.charAt(2), hex.charAt(3), hex.charAt(3) });
		
		return android.graphics.Color.parseColor(hex);
	}
	
	/**
	 * Method parses color into hexademical vision
	 * 
	 * @param color color represented as int 
	 * @return color parsed from color such as "#AARRGGBB"
	 */
	public static String parse(int color)
	{
		int[] rgb = getARGB(color);
		String hex = "#";
		hex += rgb[0] < 10 ? "0" : "" + rgb[0];
		hex += rgb[1] < 10 ? "0" : "" + rgb[1];
		hex += rgb[2] < 10 ? "0" : "" + rgb[2];
		hex += rgb[3] < 10 ? "0" : "" + rgb[3];
		return hex;
	}

	/**
	 * Method parses color into rgb channels
	 * 
	 * @param color color represented as int 
	 * @return channels array (r, g, b) parsed from color
	 */
	public static int[] getRGB(int color)
	{
		return new int[]{
			getRed(color),
			getGreen(color),
			getBlue(color)
		};
	}

	/**
	 * Method parses color into argb channels
	 * 
	 * @param color color represented as int 
	 * @return channels array (a, r, g, b) parsed from color
	 */
	public static int[] getARGB(int color)
	{
		return new int[]{
			getAlpha(color),
			getRed(color),
			getGreen(color),
			getBlue(color)
		};
	}

	/**
	 * Method returns alpha component of color
	 * 
	 * @param color color represented as int 
	 * @return alpha channel of color (0...255)
	 */
	public static int getAlpha(int color)
	{
		return android.graphics.Color.alpha(color);
	}

	/**
	 * Method returns red component of color
	 * 
	 * @param color color represented as int
	 * @return red component of color (0...255)
	 */
	public static int getRed(int color)
	{
		return android.graphics.Color.red(color);
	}

	/**
	 * Method returns green component of color
	 * 
	 * @param color color represented as int
	 * @return green component of color (0...255)
	 */
	public static int getGreen(int color)
	{
		return android.graphics.Color.green(color);
	}

	/**
	 * Method returns blue component of color
	 * 
	 * @param color color represented as int
	 * @return blue component of color (0...255)
	 */
	public static int getBlue(int color)
	{
		return android.graphics.Color.blue(color);
	}

	/**
	 * Method returns color with new alpha channel
	 * 
	 * @param color color represented as int
	 * @param alpha new alpha channel 0...255
	 * @return color with new alpha component
	 */
	public static int setAlpha(int color, int alpha)
	{
		return android.graphics.Color.argb(alpha, getRed(color), getGreen(color), getBlue(color));
	}

	/**
	 * Method returns color with new red channel
	 * 
	 * @param color color represented as int
	 * @param red new red channel 0...255
	 * @return color with new red component
	 */
	public static int setRed(int color, int red)
	{
		return android.graphics.Color.argb(getAlpha(color), red, getGreen(color), getBlue(color));
	}

	/**
	 * Method returns color with new green channel
	 * 
	 * @param color color represented as int
	 * @param green new green channel 0...255
	 * @return color with new green component
	 */
	public static int setGreen(int color, int green)
	{
		return android.graphics.Color.argb(getAlpha(color), getRed(color), green, getBlue(color));
	}

	/**
	 * Method returns color with new blue channel
	 * 
	 * @param color color represented as int
	 * @param blue new blue channel 0...255
	 * @return color with new blue component
	 */
	public static int setBlue(int color, int blue)
	{
		return android.graphics.Color.argb(getAlpha(color), getRed(color), getGreen(color), blue);
	}

	/**
	 * Method returns array of channels in HSV model of color
	 * 
	 * @param color color represented as int
	 * @return array of Hue, Saturation and Value/Brightness of color
	 */
	public static float[] getHSV(int color)
	{
		float[] hsv = new float[3];
		android.graphics.Color.colorToHSV(color, hsv);
		return hsv;
	}

	/**
	 * Method returns array of channels in AHSV model of color
	 * 
	 * @param color color represented as int
	 * @return array of Alpha, Hue, Saturation and Value/Brightness of color
	 */
	public static float[] getAHSV(int color)
	{
		return new float[]{
			getAlpha(color),
			getHue(color),
			getSaturation(color),
			getValue(color)
		};
	}

	/**
	 * Method returns hue channel of color
	 * 
	 * @param color color represented as int
	 * @return hue channel of color (0...1)
	 */
	public static float getHue(int color)
	{
		return getHSV(color)[0];
	}

	/**
	 * Method returns saturation channel of color
	 * 
	 * @param color color represented as int
	 * @return saturation channel of color (0...1)
	 */
	public static float getSaturation(int color)
	{
		return getHSV(color)[1];
	}

	/**
	 * Method returns value channel of color
	 * 
	 * @param color color represented as int
	 * @return value channel of color (0...255)
	 */
	public static float getValue(int color)
	{
		return getHSV(color)[2];
	}

	/**
	 * Method returns color with new hue channel
	 * 
	 * @param color color represented as int
	 * @param hue new hue channel 0...1
	 * @return color with new hue component
	 */
	public static int setHue(int color, float hue)
	{
		float[] hsv = getHSV(color);
		hsv[0] = hue;
		return parse(hsv);
	}

	/**
	 * Method returns color with new saturation channel
	 * 
	 * @param color color represented as int
	 * @param saturation new saturation channel 0...1
	 * @return color with new saturation component
	 */
	public static int setSaturation(int color, float saturation)
	{
		float[] hsv = getHSV(color);
		hsv[1] = saturation;
		return parse(hsv);
	}

	/**
	 * Method returns color with new value channel
	 * 
	 * @param color color represented as int
	 * @param value new value channel 0...255
	 * @return color with new value component
	 */
	public static int setValue(int color, float value)
	{
		float[] hsv = getHSV(color);
		hsv[2] = value;
		return parse(hsv);
	}
}
