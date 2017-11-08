package net.kivitechnologies.SupportLibrary.graphics;

import net.kivitechnologies.SupportLibrary.utils.ColorUtils;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

/**
 * This class creates a special drawable, used to show shadow of some view
 * Used with Material design
 * 
 * @author K. V. Bogatikov, KiVITechnologies Ltd, 2017
 * @version 1.0
 */

public class ShadowDrawable extends Drawable
{
	/**
	 * This variables saves "borders" gradient, used for show shadow on top, left, right and bottom of view
	 */
	private GradientDrawable topBorder, leftBorder, rightBorder,bottomBorder;
	/**
	 * This gradient used to show rounded corners of shadow
	 */
	private RadialGradient corners;
	/**
	 * width of gradient, defined by Z-property of View
	 */
	private int width;
	
	/**
	 * Default constructor
	 * Creates default shadow with color "#777777"
	 * 
	 * @param widthPixels width of gradient
	 */
	public ShadowDrawable(int widthPixels)
	{
		this(widthPixels, ColorUtils.parse("#777777"));
	}
	
	/**
	 * Constructor
	 * Creates default shadow with color defined as white with brightness shadowValue
	 * 
	 * @param widthPixels width of gradient
	 * @param shadowValue brightness of shadow
	 */
	public ShadowDrawable(int widthPixels, float shadowValue)
	{
		this(widthPixels, ColorUtils.setValue(ColorUtils.parse("#FFFFFF"), shadowValue));
	}
	
	/**
	 * Constructor
	 * Creates default shadow with color shadowColor
	 * 
	 * @param widthPixels width of gradient
	 * @params shadowColor color of shadow
	 */
	public ShadowDrawable(int widthPixels, int shadowColor)
	{
		this.width = widthPixels;
		
		int[] shadowColors = new int[]{ shadowColor, ColorUtils.setAlpha(shadowColor, 0) };
		
		topBorder = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, shadowColors);
		topBorder.setGradientType(GradientDrawable.LINEAR_GRADIENT);
		
		leftBorder = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, shadowColors);
		leftBorder.setGradientType(GradientDrawable.LINEAR_GRADIENT);
		
		bottomBorder = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, shadowColors);
		bottomBorder.setGradientType(GradientDrawable.LINEAR_GRADIENT);
		
		rightBorder = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, shadowColors);
		rightBorder.setGradientType(GradientDrawable.LINEAR_GRADIENT);
		
		corners = new RadialGradient(shadowColors, widthPixels);
	}
	
	@Override
	public void draw(Canvas canvas)
	{
		corners.setCurrentPart(RadialGradient.Part.TOP_LEFT);
		corners.setBounds(0, 0, width, width);
		corners.draw(canvas);
		
		topBorder.setBounds(width, 0, canvas.getWidth() - width, width);
		topBorder.draw(canvas);
		
		corners.setCurrentPart(RadialGradient.Part.TOP_RIGHT);
		corners.setBounds(canvas.getWidth() - width, 0, canvas.getWidth(), width);
		corners.draw(canvas);
		
		rightBorder.setBounds(canvas.getWidth() - width, width, canvas.getWidth(), canvas.getHeight() - width);
		rightBorder.draw(canvas);
		
		corners.setCurrentPart(RadialGradient.Part.BOTTOM_RIGHT);
		corners.setBounds(canvas.getWidth() - width, canvas.getHeight() - width, canvas.getWidth(), canvas.getHeight());
		corners.draw(canvas);
		
		bottomBorder.setBounds(width, canvas.getHeight() - width, canvas.getWidth() - width, canvas.getHeight());
		bottomBorder.draw(canvas);
		
		corners.setCurrentPart(RadialGradient.Part.BOTTOM_LEFT);
		corners.setBounds(0, canvas.getHeight() - width, width, canvas.getHeight());
		corners.draw(canvas);
		
		leftBorder.setBounds(0, width, width, canvas.getHeight() - width);
		leftBorder.draw(canvas);
	}

	@Override
	public int getOpacity()
	{
		return corners.getOpacity();
	}

	/**
	 * Deprecated because this drawable is not alpha-stability
	 */
	@Deprecated
	public void setAlpha(int alpha)
	{
	
	}

	/**
	 * Deprecated because this drawable is not color-stability
	 */
	@Deprecated
	public void setColorFilter(ColorFilter filter)
	{
		
	}
}
