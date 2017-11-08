package net.kivitechnologies.SupportLibrary.app;

import static android.os.Build.VERSION.SDK_INT;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This class used as wrapper for standart Android activity
 * This class adds some feutures and callbacks for easy listening some important events 
 * 
 * @author K. V. Bogatikov, KiVITechnologies Ltd, 2016-2017
 * @version 2.1
 */

/**
 * Change log
 * 
 * 1.0 trying listening changes of orientaion
 * 1.1 rewrited listening changes of orientation, trying listening of HOME pressed
 * 1.2 bug fix
 * 1.3 bug fix, listening starts after pause from HOME
 * 1.4 bug fix
 * 1.5 rewrited for use with MediaPlayer
 * 1.6 bug fix
 * 1.7 forked from Sudoku Project into Support Library
 * 1.8 rewrited for use without MediaPlayer
 * 1.9 bug fix
 * 2.0 constants are removed, added Enums
 * 2.1 RELEASE
 * 
 * @author K. V. Bogatikov, KiVITechnologies Ltd, 2016-2017
 *
 */

public class Activity extends android.app.Activity 
{
	/**
	 * Constants for defining kind of result - back or exit
	 * Used with forceBack and forceExit methods
	 */
	public static int RESULT_BACK = 0x00FA, RESULT_EXIT = 0x00FB;
	
	/**
	 * Enumeration contains kinds of callbacks
	 * 
	 * @author K. V. Bogatikov, KiVITechnologies Ltd, 2016-2017
	 * @version 1.1
	 */
	public static enum CallbackReason
	{
		BACK_PRESSED, HOME_PRESSED
	}
	
	/**
	 * Enumeration contains kinds of new scren orientation
	 * 
	 * @author K. V. Bogatikov, KiVITechnologies Ltd, 2016-2017
	 * @version 1.1
	 */
	public static enum ScreenOrientation
	{
		LANDSCAPE, PORTRAIT
	}
	
	/**
	 * flags for searching events
	 * too hard for describe it's eork in this comment...
	 */
	private static boolean RESTART, INITED, OCHANGED, CREATED, STARTED, ASTARTED, BPRESSED, ARESULTED, PAUSED, STOPPED, DESTROYED;
	/**
	 * stopHandler handler for check back or hoe buttons pressed
	 * 
	 * customTheme resource defining current theme of activity
	 * used for non-restart appling theme
	 */
	private Handler stopHandler;
	private int customTheme;
	
	/**
	 * Called when activity is created. Used for applu theme and UI
	 * Also listening and analyze flags
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		if(customTheme != 0)
			setTheme(customTheme);
			
		if(RESTART)
		{
			RESTART = false;
			return;
		}
		
		CREATED = true;

		stopHandler = new Handler();
		
		if(!INITED)
		{
			INITED = true;
			onFirstCreate();
		}
		
		super.onCreate(savedInstanceState);
	}
	
	/**
	 * API-level-safe method
	 * 
	 * @return null if api level is 10 or lower, otherwise - native action bar assocated with activity
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public android.app.ActionBar getActionBar()
	{
		if(SDK_INT >= 11)
			return super.getActionBar();
		else
			return null;
	}
	
	/**
	 * Prints some message as Toast
	 * 
	 * @param messageParts some messages, which will be concatenated
	 */
	public void print(Object... messageParts)
	{
		StringBuilder message = new StringBuilder();
		
		for(Object part : messageParts)
			message.append(part.toString());
		
		Toast.makeText(this, message.toString(), message.length() > 15 ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * Called when activity is started (after onCreate or pause)
	 * analyze falgs and calls special callbacks
	 */
	@Override
	protected void onStart()
	{
		super.onStart();
		STARTED = true;
		
		if(PAUSED && STOPPED && DESTROYED && CREATED)
		{
			if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
				onOrientationChanged(ScreenOrientation.PORTRAIT);
			else
				onOrientationChanged(ScreenOrientation.LANDSCAPE);

			OCHANGED = true;
			PAUSED = STOPPED = DESTROYED = CREATED = false;
		}
		
		if(DESTROYED && CREATED)
		{
			onStart(CallbackReason.BACK_PRESSED);
			DESTROYED = CREATED = false;
		}

		if(STOPPED && !ARESULTED)
		{
			onStart(CallbackReason.HOME_PRESSED);
		}
	}
	
	/**
	 * Called when user touch Back button
	 * change flags
	 */
	public void onBackPressed()
	{
		super.onBackPressed();
		BPRESSED = true;
	}

	/**
	 * Called when another activity, started from there, finished
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		ARESULTED = true;
		
		if(resultCode == RESULT_EXIT)
		{
			setResult(RESULT_EXIT);
			finish();
		}
	}
	
	/**
	 * Finish this activity and tell it's parent "do nothing"
	 */
	public void forceBack()
	{
		setResult(RESULT_BACK);
		finish();
	}
	
	/**
	 * Finsih this activity and tell it's parent "exit as me"
	 */
	public void forceExit()
	{
		setResult(RESULT_EXIT);
		finish();
	}

	/**
	 * Called when activity was stopped
	 * change and analyze flags
	 * 
	 * Also, in some variants, post to handler code for listening next user's actions 
	 */
	protected void onStop()
	{
		super.onStop();
		
		STOPPED = true;

		if(ASTARTED && PAUSED && CREATED && STARTED)
		{
			ASTARTED = PAUSED = CREATED = STARTED = false;
			return;
		}

		if(PAUSED)
		{
			stopHandler.postDelayed(new Runnable()
				{
					public void run()
					{
						if(!DESTROYED && !OCHANGED && !ARESULTED)
						{
							PAUSED = false;
							onStop(CallbackReason.HOME_PRESSED);
						}

						if(OCHANGED)
							OCHANGED = false;

						if(ARESULTED)
							ARESULTED  = false;
					}
				}, 20);
		}
	}

	/**
	 * Called when activity is paused
	 */
	protected void onPause()
	{
		super.onPause();
		PAUSED = true;
	}

	/**
	 * Called when activity is destroyed...
	 */
	protected void onDestroy()
	{
		super.onDestroy();
		if(RESTART)
			return;
		
		DESTROYED = true;
		
		if(BPRESSED && PAUSED && ARESULTED && STARTED && STOPPED)
		{
			DESTROYED = BPRESSED = PAUSED = STARTED = STOPPED = false;
			return;
		}
		
		if(BPRESSED && PAUSED && STOPPED)
		{
			onStop(CallbackReason.BACK_PRESSED);
			BPRESSED = PAUSED = STOPPED = false;
		}
	}
	
	/**
	 * Redraw this activity
	 */
	public final void restart()
	{
		RESTART = true;
		onDestroy();
		onCreate(null);
	}

	/**
	 * Force change activity theme!
	 * 
	 * @param resid new theme resource
	 */
	public final void changeTheme(int resid)
	{
		customTheme = resid;
		restart();
	}

	@Override
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void startActivities(Intent[] intents, Bundle options)
	{
		for(Intent intent : intents)
			onAnotherActivityStarted(intent);

		super.startActivities(intents, options);
	}

	@Override
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void startActivities(Intent[] intents)
	{
		for(Intent intent : intents)
			onAnotherActivityStarted(intent);

		super.startActivities(intents);
	}

	@Override
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void startActivity(Intent intent, Bundle options)
	{
		onAnotherActivityStarted(intent);
		super.startActivity(intent, options);
	}

	@Override
	public void startActivity(Intent intent)
	{
		onAnotherActivityStarted(intent);
		super.startActivity(intent);
	}

	@Override
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void startActivityForResult(Intent intent, int requestCode, Bundle options)
	{
		onAnotherActivityStarted(intent);
		super.startActivityForResult(intent, requestCode, options);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode)
	{
		onAnotherActivityStarted(intent);
		super.startActivityForResult(intent, requestCode);
	}

	/**
	 * Callback for listening changing of orientation
	 * used in sound player for non-stop playing
	 * 
	 * @param orientation new orientation @see Orientation
	 */
	protected void onOrientationChanged(ScreenOrientation orientation)
	{

	}
	
	/**
	 * Callback for listening first create or INIT of this activity
	 * 
	 */
	protected void onFirstCreate()
	{
		
	}

	/**
	 * Callback for listening starting of this activity
	 * 
	 * @param mode from back or from home? @see CallbackReason
	 */
	protected void onStart(CallbackReason mode)
	{

	}

	/**
	 * Callback for listening stops of this activity
	 * 
	 * @param mode from back or from home? @see CallbackReason
	 */
	protected void onStop(CallbackReason mode)
	{

	}

	/**
	 * Callback for listening starting other activity
	 * 
	 * @param data Intent with which activity was started
	 */
	protected void onAnotherActivityStarted(Intent data)
	{
		ASTARTED = true;
	}
	
	/**
	 * Return ViewGroup found by id
	 * 
	 * @param id id of ViewGroup, displaed in this activity
	 * @return ViewGroup, found in this activity
	 */
	public ViewGroup findViewGroupById(int id)
	{
		return (ViewGroup)findViewById(id);
	}
	
	/**
	 * Return CompoundButton found by id
	 * 
	 * @param id id of Checkable, displaed in this activity
	 * @return Checkable, found in this activity
	 */
	public CompoundButton findCheckableById(int id)
	{
		return (CompoundButton)findViewById(id);
	}
	
	/**
	 * Return TextView found by id
	 * 
	 * @param id id of TextView, displaed in this activity
	 * @return TextView, found in this activity
	 */
	public TextView findTextViewById(int id)
	{
		return (TextView)findViewById(id);
	}
	
	/**
	 * Return ImageView found by id
	 * 
	 * @param id id of ImageView, displaed in this activity
	 * @return imageView, found in this activity
	 */
	public ImageView findImageViewById(int id)
	{
		return (ImageView)findViewById(id);
	}
}
