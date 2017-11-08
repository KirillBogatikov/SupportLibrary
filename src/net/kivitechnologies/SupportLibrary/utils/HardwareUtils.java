package net.kivitechnologies.SupportLibrary.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

import android.os.Build;
import static android.os.Build.VERSION;

/**
 * This class help programmers get device's hardware information
 * This class was extracted from Mojang AB's game Minecraft
 * But it was not adapted to fast work on Android and was converted from Assembly into Java and rewrited (optimized) 
 * 
 * @author Mojang AB, 2009 - 2017
 * Rewrited and adpted to Android OS 
 * @author K. V. Bogatikov, KiVITechnologies Ltd, 2017
 * @version 1.0
 */
public class HardwareUtils
{
	/**
	 * CoresCounterFilter file filter used for specify some system directory content
	 * used for counting cores
	 * 
	 * coresCount this variable saves count of cores, counted once
	 * 
	 *  CPUFeatures - String, contains CPU feautures defined in system files
	 *  
	 *  CPUName - String. contains CPU Name and Model
	 */
	private static final FileFilter CoresCounterFilter;
	private static int coresCount;
	private static String CPUFeatures;
	private static String CPUName;
	    
	static
	{
		CoresCounterFilter = new FileFilter()
		{
	        public boolean accept(File pathname)
	        {
	            return Pattern.matches("cpu[0-9]+", pathname.getName());
	        }
	    };
	    
	    coresCount = -1;
	    CPUFeatures = "unknown";
	    CPUName = "unknown";
	}
	
	/**
	 * This method wraps AndroidAPI's method
	 * 
	 * @return Device Manufacturer name
	 */
	public static String getDeviceManufacturer()
	{
		return Build.MANUFACTURER;
	}
	
	/**
	 * This method wraps AndroidAPI's method
	 * 
	 * @return Device model
	 */
	public static String getDeviceModel()
	{
		return Build.MODEL;
	}
	
	/**
	 * @return Device model name such as <MANUFACTURER> <MODEL>
	 */
	public static String getDeviceModelName()
	{
		return getDeviceManufacturer() + " " + getDeviceModel();
	}
	
	/**
	 * @return Android version such as Jelly Bean, Kit Kat, Lollypop ... Oreo
	 */
	public static String getAndroidVersion()
	{
		return "Android " + VERSION.RELEASE;
	}

	/**
	 * @warning use this method in try/catch block. THis method uses deprecated field in Androdi API and in some version they can be removed
	 * 
	 * @return returns CPU type such as <CPU ABI 1>/<CPU ABI 2>
	 */
	@SuppressWarnings("deprecation")
	public static String getCPUType()
	{
		return Build.CPU_ABI + "/" + Build.CPU_ABI2;
	}

	/**
	 * @return name of CPU, defined in system files and readed by method readCPUInfo
	 */
	public static String getCPUName()
	{
		if(CPUName == "unknown")
			readCPUInfo();
			
		return CPUName;
	}
	
	/**
	 * @return CPU feutures list as string, defined in system files and readed by method readCPUFeatures
	 */
	public static String getCPUFeatures()
	{
		if(CPUFeatures == "unknown")
			readCPUInfo();
			
		return CPUFeatures;
	}
	
	/**
	 * @return cores count
	 */
	public static int getCoresCount()
	{
		if(coresCount == -1)
			countCPUCores();
		
		return coresCount;
	}
	
	/**
	 * @warning use this method in try/catch block because it is not tested on Intel processors
	 * Tested only on ARM v6 / v7
	 * 
	 * @return CPU info, readed in some system file
	 */
	public static String readCPUInfo()
	{
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = null;
		
		try
		{
			reader = new BufferedReader(new FileReader("/proc/cpuinfo"));
			
			String line;
			while((line = reader.readLine()) != null)
			{
				builder.append(line).append("\n");
				
				line = line.trim();
				if(line.startsWith("Features") || line.startsWith("flags"))
					CPUFeatures = line.split(":")[1];
				else if(line.startsWith("Hardware") || line.startsWith("model name"))
					CPUName = line.split(":")[1];
			}
		}
		catch(IOException ioe)
		{
			
		}
		
		try
		{
			reader.close();
		}
		catch(Exception e)
		{
			/*
			 * STREAM IS NOT CLOSED! IT MAY BE DANGEROUS, BUT MAY BE NULLPOINTEREXCEPTION CAUGHT
			 */
		}
		
		return builder.toString();
	}
	
	/**
	 * Counts CPU cores by two methods:
	 * if available some specal directory in system files, which contains per-cpu directories
	 * else use non-useful java method availableProcessors 
	 */
	public static void countCPUCores()
	{
		try
		{
            coresCount = new File("/sys/devices/system/cpu/").listFiles(CoresCounterFilter).length;
        }
		catch (Exception e)
		{
            coresCount = Math.max(1, Runtime.getRuntime().availableProcessors());
        }
	}
}
