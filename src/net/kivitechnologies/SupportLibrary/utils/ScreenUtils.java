package net.kivitechnologies.SupportLibrary.utils;

import static android.os.Build.VERSION.SDK_INT;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.view.Window;
import android.view.WindowManager;

/**
 * Class for easy work with current display
 * Forked from Sudoku Project
 * 
 * @author K. V. Bogatikov, KiVITechnologies Ltd, 2017
 * @version 1.3
 */
public final class ScreenUtils
{
	/*
	 * Contains orientation values
	 */
	public static enum Orientation
	{
		PORTRAIT, LANDSCAPE
	}
	
	/*
	 * Returns current width of screen 
	 */
	public static int getWidth(Context ctx)
	{
		return ctx.getResources().getDisplayMetrics().widthPixels;
	}
	
	/*
	 * Returns current height of screen
	 */
	public static int getHeight(Context ctx, boolean countStatusBar)
	{
		return ctx.getResources().getDisplayMetrics().heightPixels;
	}
	
	/*
	 * Returns current height of screen without status bar height
	 */
	public static int getHeight(Context ctx)
	{
		return getHeight(ctx, true);
	}
	
	/*
	 * Returns absolute width of screen
	 * Warning! This method returns invalid results on tablets. Use this only for phones
	 */
	public static int getAbsoluteWidth(Context ctx)
	{
		return Math.min(getWidth(ctx), getHeight(ctx));
	}
	
	/*
	 * Returns absolute height of screen
	 * Warning! This method returns invalid results on tablets. Use this only for phones 
	 */
	public static int getAbsoluteHeight(Context ctx)
	{
		return Math.max(getWidth(ctx), getHeight(ctx));
	}
	
	/*
	 * Return screen orientation defined in ScreenUtils.Orientation enum
	 */
	public static Orientation getOrientation(Context ctx)
	{
		if(ctx.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
			return Orientation.LANDSCAPE;
		else
			return Orientation.PORTRAIT;
	}
	
	/*
	 * Converts Dimension from Density-Independent-Pixel to Pixel
	 */
	public static int dpToPx(Context ctx, int dp)
	{
		return Math.round(dp * ctx.getResources().getDisplayMetrics().density);
	}
	
	/*
	 * Converts Dimension from Pixel to Density-Independent-Pixel
	 */
	public static int pxToDp(Context ctx, int px)
	{
		return Math.round(px / ctx.getResources().getDisplayMetrics().density);
	}
	
	/*
	 * Converts Dimension from Density-Independent-Pixel to Pixel
	 * Converts first int if orientation is portrait
	 * Converts second int if orientation is landscape
	 */
	public static int dpToPx(Context ctx, int pdp, int ldp)
	{
		if(getOrientation(ctx) == Orientation.PORTRAIT)
			return dpToPx(ctx, pdp);
		else
			return dpToPx(ctx, ldp);
	}
	
	/*
	 * Converts Dimension from Pixel to Density-Independent-Pixel
	 * Converts first int if orientation is portrait
	 * Converts second int if orientation is landscape
	 */
	public static int pxToDp(Context ctx, int ppx, int lpx)
	{
		if(getOrientation(ctx) == Orientation.PORTRAIT)
			return pxToDp(ctx, ppx);
		else
			return pxToDp(ctx, lpx);
	}
	
	/**
	 * Set color of status bar
	 * This method is API-level safe Your app dont crash if user's Android is lower than Lollipop
	 * 
	 * @param ctx Context for getting display
	 * @param color color of status bar
	 * @since 1.3
	 */
	public static void setStatusBarColor(Activity ctx, int color)
	{
		if(SDK_INT >= 21)
		{
			Window win = ctx.getWindow();
			win.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			win.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			win.setStatusBarColor(color);
		}
	}
	
	/**
	 * Set color of status bar
	 * This method is API-level safe Your app dont crash if user's Android is lower than Lollipop
	 * 
	 * @param ctx Context for getting display
	 * @param attr resource id of color to set to sattus bar
	 * @since 1.3
	 */
	public static void setStatusBarColorRes(Activity ctx, int attr)
	{
		int color = ctx.getTheme().obtainStyledAttributes(new int[]{ attr }).getColor(0, -1);
		if(color != -1)
			setStatusBarColor(ctx, color);
	}
}
