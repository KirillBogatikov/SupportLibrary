package net.kivitechnologies.SupportLibrary.app;

import net.kivitechnologies.SupportLibrary.utils.ResourcesUtils;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

/**
 * This class created for provide Support Library to app's resoures
 * Similar as TypedArray or AttributeSet
 * 
 * @author K. V. Bogatikov, KiVITechnologies Ltd, 2017
 * @version 1.0
 */
public abstract class ResourcesDelegate
{
	/**
	 * Factory-method for create "default" provider - redirect
	 * Do NOT USE this nethod if you want to provide some of library classes!
	 * In ActionBarDrawer, SettingsView and more other used ABOLUTE ATTRIBUTES
	 * It is not resources ids, int int constants!
	 *  
	 * @param initial initial resources provider, given by System 
	 * @return new delegate for providing styleble classes
	 */
	public ResourcesDelegate createByRedirect(final Resources initial)
	{
		return new ResourcesDelegate()
		{
			public int getColor(int resId)
			{
				return initial.getColor(resId);						
			}
			
			public int getDimensionPixelSize(int resId)
			{
				return initial.getDimensionPixelOffset(resId);
			}
			
			public Drawable getDrawable(int resId)
			{
				return ResourcesUtils.getDrawable(initial, resId);
			}
			
			public String getString(int resId)
			{
				return initial.getString(resId);
			}
		};
	}
	
	/**
	 * Method must return color, generated or was get by some resId
	 * 
	 * @param resId resources or attribute or constant, defined in some class
	 * @return color such as Color.parseColor(...) or ColorUtils.parse(...)
	 */
	public abstract int getColor(int resId);
	/**
	 * Method must return dimen in px, generated or was get by some resId
	 * @param resId resources or attribute or constant, defined in some class
	 * @return dimen's px value
	 */
	public abstract int getDimensionPixelSize(int resId);
	/**
	 * Method must return Drawable, generated or was get by some resId
	 * @param resId resources or attribute or constant, defined in some class
	 * @return Drawable for this resId
	 */
	public abstract Drawable getDrawable(int resId);
	/**
	 * Method must return Stirng, generated or was get by some resId
	 * @param resId resources or attribute or constant, defined in some class
	 * @return String for this resId
	 */
	public abstract String getString(int resId);
}
