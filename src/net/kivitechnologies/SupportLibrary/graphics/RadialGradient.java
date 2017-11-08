package net.kivitechnologies.SupportLibrary.graphics;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;

/**
 * This class creates a special radial gradients
 * You can cut from this gradients parts! 
 * used for rounded shadow
 * 
 * @author K. V. Bogatikov, KiVITechnologies Ltd, 2017
 * @version 1.0
 */

public class RadialGradient extends GradientDrawable
{
	/**
	 * Enumeartion contains kinds of PART for cutting
	 * 
	 * @author K. V. Bogatikov, KiVITechnologies Ltd, 2017
	 */
	public static enum Part
	{
		TOP_RIGHT, TOP_LEFT, BOTTOM_RIGHT, BOTTOM_LEFT
	}
	
	/**
	 * startX and startY used for tell to draw method which part it must draw
	 */
	private float startX, startY;
	
	/**
	 * Constructor 
	 * 
	 * @param colors array of two colors used for gradient
	 * @param radius radius of radial
	 */
	public RadialGradient(int[] colors, float radius)
	{
		super(Orientation.TOP_BOTTOM, colors);
		
		setGradientType(RADIAL_GRADIENT);
		setGradientCenter(0.5f, 0.5f);
		setGradientRadius(radius);
	}
	
	/**
	 * Method translate your Part to coordinates and tell to draw method which bounds need for use
	 * 
	 * @param part Part for cutting
	 */
	public void setCurrentPart(Part part)
	{
		if(part == Part.TOP_LEFT)
		{
			startX = 0.0f;
			startY = 0.0f;
		}
		else if(part == Part.TOP_RIGHT)
		{
			startX = 0.5f;
			startY = 0.0f;
		}
		else if(part == Part.BOTTOM_LEFT)
		{
			startX = 0.0f;
			startY = 0.5f;
		}
		else if(part == Part.BOTTOM_RIGHT)
		{
			startX = 0.5f;
			startY = 0.5f;
		}
	}
	
	@Override
	public void draw(Canvas canvas)
	{
		int realLeft = getBounds().left, 
			realTop = getBounds().top, 
			realRight = getBounds().right,
			realBottom = getBounds().bottom;
		int realWidth = Math.abs(realLeft - realRight),
			realHeight = Math.abs(realTop - realBottom);
		
		Bitmap main_bitmap = Bitmap.createBitmap(realWidth * 2, realHeight * 2, Bitmap.Config.ARGB_8888);
		Canvas main_canvas = new Canvas(main_bitmap);
		setBounds(0, 0, main_bitmap.getWidth(), main_bitmap.getHeight());
		super.draw(main_canvas);
		
		Bitmap part_bitmap = Bitmap.createBitmap(main_bitmap, (int)(startX * main_bitmap.getWidth()), (int)(startY * main_bitmap.getHeight()), realWidth, realHeight);
		canvas.drawBitmap(part_bitmap, realLeft, realTop, null);
	}
}
