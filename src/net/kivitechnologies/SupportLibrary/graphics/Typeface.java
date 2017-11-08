package net.kivitechnologies.SupportLibrary.graphics;

import java.io.File;

import android.content.res.AssetManager;

/**
 * This class wraps "native" Typeface class, defined by Android API
 * 
 * @author K. V. Bogatikov, KiVITechnologies Ltd, 2017
 * @version 1.0
 */

public class Typeface
{
	/**
	 * Variables for saving normal, bold, italic, bolditalic fonts
	 */
	private android.graphics.Typeface nativeNormal, nativeBold, nativeItalic, nativeBoldItalic;
	
	/**
	 * Constructor
	 * Creates native typeface by typeface family
	 * 
	 * @param typefaceFamily family of font
	 */
	public Typeface(String typefaceFamily)
	{
		this.nativeNormal = android.graphics.Typeface.create(typefaceFamily, android.graphics.Typeface.NORMAL);
	}
	
	/**
	 * Constructor
	 * Creates native typeface by assets and TrueType font there
	 * 
	 * @param assets asset manager for read font file
	 * @params fontPath path to font in your assets
	 */
	public Typeface(AssetManager assets, String fontPath)
	{
		this.nativeNormal = android.graphics.Typeface.createFromAsset(assets, fontPath);
	}
	
	/**
	 * Constructor
	 * Creates native typeface by TrueType font file
	 * 
	 * @param file file of font (ttf, otf)
	 */
	public Typeface(File file)
	{
		this.nativeNormal = android.graphics.Typeface.createFromFile(file);
	}
	
	/**
	 * Constructor
	 * Wraps native typeface
	 * 
	 * @param nativeTypeface android typeface for wrap
	 */
	public Typeface(android.graphics.Typeface nativeTypeface)
	{
		this.nativeNormal = nativeTypeface;
	}
	
	/**
	 * Return normal typeface
	 * 
	 * @return normal (non-bold, non-italic) font
	 */
	public android.graphics.Typeface getNormal()
	{
		return nativeNormal;
	}
	
	/**
	 * Return bold typeface
	 * 
	 * @return bold font
	 */
	public android.graphics.Typeface getBold()
	{
		if(this.nativeBold == null)
			this.nativeBold = android.graphics.Typeface.create(nativeNormal, android.graphics.Typeface.BOLD);
		
		return nativeBold;
	}
	
	/**
	 * Return italic typeface
	 * 
	 * @return italic font
	 */
	public android.graphics.Typeface getItalic()
	{
		if(this.nativeItalic == null)
			this.nativeItalic = android.graphics.Typeface.create(nativeNormal, android.graphics.Typeface.ITALIC);
		
		return nativeItalic;
	}
	
	/**
	 * Return bold-italicl typeface
	 * 
	 * @return bold-italic font
	 */
	public android.graphics.Typeface getBoldItalic()
	{
		if(this.nativeBoldItalic == null)
			this.nativeBoldItalic = android.graphics.Typeface.create(nativeNormal, android.graphics.Typeface.BOLD_ITALIC);
		
		return nativeBoldItalic;
	}
}
