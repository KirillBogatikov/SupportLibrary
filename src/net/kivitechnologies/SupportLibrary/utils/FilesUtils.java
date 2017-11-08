package net.kivitechnologies.SupportLibrary.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.Environment;

/**
 * Class for work with files
 * Main feutures: returns path otto SD card, path to local storage, moving files, deleting directories
 * 
 * @author K. V. Bogatikov, KiVITechnologies Ltd, 2017
 * @version 1.0
 */
public class FilesUtils
{
	/**
	 * Returns SD card directory
	 * 
	 * @warning This method can return file, similar with local storage, if device has no sd card
	 * 
	 * @return file, assocciated by system with sd card path
	 */
	public static File getSDCardFile()
	{
		return Environment.getExternalStorageDirectory();
	}
	
	/**
	 * Returns path SD card directory
	 * 
	 * @warning This method can return path to file, similar with local storage, if device has no sd card
	 * 
	 * @return path to file, assocciated by system with sd card path
	 */
	public static String getSDCardPath()
	{
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}
	
	/**
	 * Returns Storage directory
	 * In Android "Storage" directory contains SD Card file and Local storage directory
	 * 
	 * @return file, assocciated by system with sd card's parent
	 */
	public static File getStorageDirectory()
	{
		return Environment.getExternalStorageDirectory().getParentFile();
	}
	
	/**
	 * Returns path to Storage directory
	 * In Android "Storage" directory contains SD Card file and Local storage directory
	 * Such as My computer in Windows contains all disks
	 * 
	 * @return path to file, assocciated by system with sd card's parent
	 */
	public static String getStoragePath()
	{
		return Environment.getExternalStorageDirectory().getParent();
	}
	
	/**
	 * Moves file into new file
	 * 
	 * @warning target is not a directory for new file!
	 * 
	 * @param initial file for moving
	 * @param target  file for saving moved file
	 * 
	 * @return target's parent file
	 */
	public static File moveTo(File initial, File target) throws IOException
	{
		target.createNewFile();
		
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(target));
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(target));
		
		int byte_;
		while((byte_ = bis.read()) != -1)
			bos.write(byte_);
		
		bos.flush();
		bos.close();
		bis.close();
		
		initial.delete();
		
		return target.getParentFile();
	}
	
	/**
	 * Returns Storage directory
	 * In Android "Storage" directory contains SD Card file and Local storage directory
	 * 
	 * @return file, assocciated by system with sd card's parent
	 */
	public static void delete(File file)
	{
		if(file.isDirectory())
		{
			File[] files = file.listFiles();
			if(files != null)
			{
				for(File ifile : files)
					delete(ifile);
			}
		}
		
		file.delete();
	}

	/**
	 * Returns formatted file size
	 * All files has method "length", which returns size of file in bytes
	 * But usually we need to show file size in kilobytes or megabytes (or gigabytes)
	 * as <SIZE> KB or <SIZE> MB
	 * 
	 * @return String with formatted file size
	 */
	public static String getFileSize(File file)
	{
		float bytes = Long.valueOf(file.length()).floatValue();
		String unit = "";
		String[] units = { "B", "KB", "MB", "GB" };
		
		int i = 0;
		for(i = 0; i < units.length; i++)
		{
			if(bytes < Math.pow(1024, i + 1))
			{
				unit = units[i];
				bytes /= Math.pow(1024, i);
				
				if(bytes < 512)
					break;
			}	
		}
		
		String sizePattern;
		if(Math.round(bytes) == bytes)
			sizePattern = "%.0f";
		else
			sizePattern = "%.2f";
		
		return String.format(sizePattern, bytes) + unit;
	}
}
