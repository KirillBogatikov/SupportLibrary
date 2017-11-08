package net.kivitechnologies.SupportLibrary.utils;

import static android.os.Build.VERSION.SDK_INT;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.webkit.WebView;
import android.widget.Toast;

/**
 * Class for easy work with resources
 * 
 * @author K. V. Bogatikov, KiVITechnologies Ltd, 2016 - 2017
 * @version 2.1
 */
public final class ResourcesUtils
{
	/**
	 * Returns activity icon, defined in Manifext.xml
	 * 
	 * @param ctx Context of activity
	 * @return Drawable - icon of activity 
	 */
	public static Drawable getActivityIcon(Context ctx)
	{
		try
		{
			return ctx.getPackageManager().getActivityIcon(((Activity)ctx).getIntent());
		}
		catch(PackageManager.NameNotFoundException | ClassCastException e)
		{
			return ctx.getPackageManager().getApplicationIcon(ctx.getApplicationInfo());
		}
	}
	
	/**
	 * Returns activity logo, defined in Manifext.xml
	 * 
	 * @param ctx Context of activity
	 * @return Drawable - logo of activity 
	 */
	public static Drawable getActivityLogo(Context ctx)
	{
		try
		{
			return ctx.getPackageManager().getActivityLogo(((Activity)ctx).getIntent());
		}
		catch(PackageManager.NameNotFoundException | ClassCastException e)
		{
			return ctx.getPackageManager().getApplicationLogo(ctx.getApplicationInfo());
		}
	}
	
	/**
	 * Returns drawable from resources with valid density (useful on Jelly Bean)
	 * 
	 * @param ctx Context for getting data
	 * @param resId id for drawable
	 * @return Drawable - needed drawable 
	 */
	public static Drawable getDrawable(Context ctx, int resId)
	{
		return getDrawable(ctx.getResources(), resId);
	}
	
	/**
	 * Returns drawable from resources with valid density (useful on Jelly Bean)
	 * 
	 * @param ctx Context for getting data
	 * @param resId id for drawable
	 * @return Drawable - needed drawable 
	 */
	@SuppressWarnings("deprecation")
	public static Drawable getDrawable(Resources res, int resId)
	{
		/* 
		 * In Android 16 (JellyBean) this method return invalid density drawable 
		 * In Android 22 this method was deprecated 
		 */
		if(SDK_INT != 16 && SDK_INT < 22) 
			return res.getDrawable(resId);
			
		TypedArray resultArray = res.obtainTypedArray(resId);
		Drawable result = resultArray.getDrawable(0);
		resultArray.recycle();
		return result;
	}
	
	/**
	 * Applies some raw html file into web view
	 * 
	 * @param webView web view to importing some html file
	 * @param rawId id for html file in R.raw
	 */
	public static void loadRawToWebView(WebView webView, int rawId)
	{
		InputStream input = new BufferedInputStream(webView.getContext().getResources().openRawResource(rawId));
		try
		{
			if(input != null && input.available() > 0)
			{
				byte[] bytes = new byte[input.available()];
				input.read(bytes);
					
				webView.loadDataWithBaseURL(null, new String(bytes, "UTF-8"), "text/html", "UTF-8", null);
			}
		}
		catch(IOException ioe)
		{
			Toast.makeText(webView.getContext(), ioe.toString(), Toast.LENGTH_SHORT).show();
		}
	}
}
