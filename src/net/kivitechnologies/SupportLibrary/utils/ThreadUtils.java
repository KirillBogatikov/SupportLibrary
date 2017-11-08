package net.kivitechnologies.SupportLibrary.utils;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Class for easy work with many threads and handlers
 * Forked from JavaCompiler Project
 * 
 * @author K. V. Bogatikov, KiVITechnologies Ltd, 2017
 * @version 1.0
 */
public class ThreadUtils
{	
	public static int KILL_THREAD_FLAG = 0x000111;
	
	private static ArrayList<Thread> threads;
	private static ArrayList<Handler> handlers;
	private static Handler mHandler;
	
	static
	{
		threads = new ArrayList<Thread>();
		handlers = new ArrayList<Handler>();
	}
	
	/**
	 * This method creates new thread and handler
	 * 
	 * @param code Runnable with code, which ill be executed at every thread run
	 * @return id of Thread and Handler in this system
	 */
	public static synchronized int createNewThread(final Runnable code)
	{
		Thread newThread = new HThread(code);
		newThread.start();
		
		threads.add(newThread);
		while(mHandler == null){}
		
		handlers.add(mHandler);
		mHandler = null;
		
		return threads.size() - 1;
	}
	
	/**
	 * This method kills thread
	 * 
	 * @param id id of thread in this system
	 */
	public static void killThread(int id)
	{
		Message msg = new Message();
		msg.what = KILL_THREAD_FLAG;
		getHandlerById(id).handleMessage(msg);
	}
	
	/**
	 * returns Thread
	 * 
	 * @param id id of thread in this system
	 * @return Thread with specified id
	 */
	public static Thread getThreadById(int id)
	{
		return threads.get(id);
	}
	
	/**
	 * Returns Handler
	 * 
	 * @param id id of thread in this system
	 * @return Handler with specified id
	 */
	public static Handler getHandlerById(int id)
	{
		return handlers.get(id);
	}
	
	/**
	 * Class for creating new Thread with specified Handler for receiving messages
	 * 
	 * @author K. V. Bogatikov, KiVITechnologies Ltd, 2017
	 * @version 1.0
	 */
	private static class HThread extends Thread implements Runnable
	{
		private boolean firstLaunch = true;
		private Runnable codeRun;
		
		public HThread(Runnable code)
		{
			codeRun = code;
		}
		
		@SuppressLint("HandlerLeak")
		public void run()
		{
			Looper.prepare();
			
			mHandler = new Handler()
			{
				public void handleMessage(Message msg)
				{
					if(msg.what == KILL_THREAD_FLAG)
						Looper.myLooper().quit();
					else
						super.handleMessage(msg);
				}
			};
			
			if(!firstLaunch && codeRun != null)
				codeRun.run();
			
			firstLaunch = false;
			
			Looper.loop();
		}
	}
}	
