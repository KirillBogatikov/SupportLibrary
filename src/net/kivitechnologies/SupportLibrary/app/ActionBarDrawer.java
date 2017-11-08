package net.kivitechnologies.SupportLibrary.app;

import java.util.HashMap;

import net.kivitechnologies.SupportLibrary.utils.ColorUtils;
import net.kivitechnologies.SupportLibrary.utils.ResourcesUtils;
import net.kivitechnologies.SupportLibrary.utils.ScreenUtils;
import net.kivitechnologies.SupportLibrary.view.MarqueeTextView;
import net.kivitechnologies.SupportLibrary.view.Menu;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.StateSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Class for implementing action bar with material design on different platform versions
 * 
 * This class creates a custom view with non-full functionallity of real action bar
 * Implemented: up button (home), icon, title and subtitle, adding context menu and showing "more" button
 * Added: scrollable container for custom tools, defined by icon
 * 
 * @author K. V. Bogatikov, KiVITechnologies Ltd, 2016-2017
 * @version 1.3
 */

/**
 * Change log
 * 
 * 1.0 private build, testing
 * 1.1 added up button and subtitle
 * 1.2 full support for subtitle, added menu and more button
 * 1.3 now action bar is documented and can be styled
 * 
 * @author K. V. Bogatikov, KiVITechnologies Ltd, 2016-2017
 */

public class ActionBarDrawer extends RelativeLayout implements View.OnClickListener
{
	/**
	 * Attributes for customizing action bar at constructor
	 * 
	 * ATTR_BACKGROUND_COLOR   color of action bar background
	 * ATTR_DEFAULT_ITEM_COLOR color of more button points as it default
	 * ATTR_PRESSED_ITEM_COLOR color of more button points as it pressed
	 * ATTR_TITLE_COLOR        color of action bar title
	 * ATTR_SUBTITLE_COLOR     color of action bar subtitle
	 */
	public static final int ATTR_BACKGROUND_COLOR = 0x000,
							ATTR_DEFAULT_ITEM_COLOR = 0x001,
							ATTR_PRESSED_ITEM_COLOR = 0x002,
							ATTR_TITLE_COLOR = 0x003,
							ATTR_SUBTITLE_COLOR = 0x004;							
	
	/**
	 * activity to this activity action bar will be attached
	 * delegate delegate for providing resources @see net.kivitechnologies.SupportLibrary.app.ResourcesDelegate
	 */
	private android.app.Activity activity;
	private ResourcesDelegate delegate;
	
	/**
	 * upButton   image view used for display "up" action     hidden as default
	 * iconView   image view used for display activity's icon showed as default
	 * moreButton image view used for diaply "more" icon      hidden as default
	 * 
	 * isLogoUsed          flag for valid displaying icon or logo of activity false as default
	 * isSubtitleDisplayed flag for manipulating with title layout params     false as default
	 * 
	 * lastIcon no comments
	 * lastLogo - * -
	 * 
	 * titleView    MarqueeTextView for display activity's title    showed as default
	 * subtitleView MarqueeTextView for display activity's subtitle hidden as default
	 * 
	 * progressBar Local implementation of Progress Bar for displaying task running hidden as default
	 * 
	 * fadingAnimation Local implementation of animation for animate show/hide of action bar
	 * 
	 * menu associated context menu, created by calling @see createContextMenu()
	 * 
	 * rightGroupLayout container for scrollable container of tools and more button
	 * toolsLayout      container for adding some tools 
	 * 
	 * toolsScroll scrollable container for wrap toolsLayout
	 * 
	 * toolsArray map of Tools and associated ImageViews
	 */
	private ImageView upButton, iconView, moreButton;
	private boolean isLogoUsed, isSubtitleDisplayed;
	private Drawable lastIcon, lastLogo;
	private TextView titleView, subtitleView;
	private MyProgressBar progressBar;
	private Animation fadingAnimation;
	private Menu menu;
	private LinearLayout rightGroupLayout, toolsLayout;
	private HorizontalScrollView toolsScroll;
	private HashMap<Tool, ImageView> toolsArray;
	
	/**
	 * Default constructor
	 * 
	 * @param act activity for attaching action bar
	 * @param provider delegate for providing app resources
	 */
	public ActionBarDrawer(android.app.Activity act, ResourcesDelegate provider)
	{
		super(act);
		this.activity = act;
		this.delegate = provider;
		
		this.fadingAnimation = new FadingAnimation(1000);
		
		int dp54 = ScreenUtils.dpToPx(this.activity, 54),
			dp32 = ScreenUtils.dpToPx(this.activity, 32),
			dp11 = ScreenUtils.dpToPx(this.activity, 11),
			dp5 = ScreenUtils.dpToPx(this.activity, 5),
			dp2 = ScreenUtils.dpToPx(this.activity, 2);
		
		upButton = new ImageView(this.activity);
		upButton.setImageDrawable(generateUpImage());
		LayoutParams upButtonParams = new LayoutParams(dp32 / 3, dp32);
		upButtonParams.addRule(ALIGN_PARENT_LEFT);
		upButtonParams.setMargins(dp5, dp11, 0, 0);
		addView(upButton, upButtonParams);
		upButton.setVisibility(View.INVISIBLE);
		upButton.setOnClickListener(this);
		
		lastIcon = upButton.getDrawable();
		lastLogo = ResourcesUtils.getActivityLogo(this.activity);
		
		iconView = new ImageView(this.activity);
		iconView.setImageDrawable(ResourcesUtils.getActivityIcon(this.activity));
		LayoutParams iconViewParams = new LayoutParams(dp32, dp32);
		iconViewParams.addRule(ALIGN_PARENT_LEFT);
		iconViewParams.setMargins(upButtonParams.leftMargin + dp32 / 3, dp11, 0, 0);
		addView(iconView, iconViewParams);
		iconView.setOnClickListener(this);
		
		titleView = new MarqueeTextView(this.activity);
		titleView.setPadding(dp2, 0, dp2, 0);
		titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
		titleView.setTextColor(this.delegate.getColor(ATTR_TITLE_COLOR));
		LayoutParams titleViewParams = new LayoutParams(-2, -2);
		titleViewParams.addRule(ALIGN_PARENT_LEFT);
		titleViewParams.addRule(CENTER_VERTICAL);
		titleViewParams.setMargins(dp11 + dp32 / 3 + dp32 + dp5, 0, (dp32 + dp5) * 2, 0);
		addView(titleView, titleViewParams);
		
		subtitleView = new MarqueeTextView(this.activity);
		subtitleView.setPadding(dp2, 0, dp2, 0);
		subtitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		subtitleView.setTextColor(this.delegate.getColor(ATTR_SUBTITLE_COLOR));
		LayoutParams subtitleViewParams = new LayoutParams(-2, -2);
		subtitleViewParams.addRule(ALIGN_PARENT_LEFT);
		subtitleViewParams.addRule(ALIGN_PARENT_BOTTOM);
		subtitleViewParams.setMargins(dp11 + dp32 / 3 + dp32 + dp5, 0, (dp32 + dp5) * 2, 0);
		addView(subtitleView, subtitleViewParams);
		subtitleView.setVisibility(GONE);
		
		toolsArray = new HashMap<Tool, ImageView>();
		rightGroupLayout = new LinearLayout(this.activity);
		LayoutParams rightGroupParams = new LayoutParams(dp32 * 2 + dp5, dp32);
		rightGroupParams.addRule(ALIGN_PARENT_RIGHT);
		rightGroupParams.setMargins(0, dp11, dp11, 0);
		addView(rightGroupLayout, rightGroupParams);
		
		moreButton = new ImageView(this.activity);
		moreButton.setImageDrawable(generateMoreImage());
		LinearLayout.LayoutParams moreButtonParams = new LinearLayout.LayoutParams(-1, dp32, 1.0f);
		moreButtonParams.setMargins(dp5, 0, 0, 0);
		rightGroupLayout.addView(moreButton, moreButtonParams);
		moreButton.setVisibility(View.GONE);
		moreButton.setOnClickListener(this);
		
		toolsScroll = new HorizontalScrollView(this.activity);
		LinearLayout.LayoutParams toolsScrollParams = new LinearLayout.LayoutParams(-1, dp32, 1.0f);
		rightGroupLayout.addView(toolsScroll, 0, toolsScrollParams);
		toolsLayout = new LinearLayout(this.activity);
		toolsScroll.addView(toolsLayout);
		
		progressBar = new MyProgressBar(this.activity, this.delegate.getColor(ATTR_DEFAULT_ITEM_COLOR), this.delegate.getColor(ATTR_PRESSED_ITEM_COLOR));
		LayoutParams progressBarParams = new LayoutParams(-1, ScreenUtils.dpToPx(this.activity, 2));
		progressBarParams.addRule(ALIGN_PARENT_BOTTOM);
		addView(progressBar, progressBarParams);
		progressBar.setVisibility(View.INVISIBLE);

		setLayoutParams(new LayoutParams(-1, dp54));
		int bgColor = this.delegate.getColor(ATTR_BACKGROUND_COLOR);
		this.setBackgroundColor(bgColor);
		float v = ColorUtils.getValue(bgColor);
		ScreenUtils.setStatusBarColor(this.activity, ColorUtils.setValue(bgColor, v / 2));
	}
	
	/**
	 * Overriden from View.OnClickListener
	 * 
	 * used for listening clicks on up button, icon and more button 
	 */
	@Override
	public void onClick(View view)
	{
		if(view == upButton || view == iconView)
			this.activity.finish();
		else if(view == moreButton && this.menu != null)
		{
			menu.show();
		}
		else if(toolsArray.containsValue(view))
		{
			Tool tool = (Tool)toolsArray.keySet().toArray()[view.getId()];
			tool.onClick();
		}
	}
	
	/**
	 * Method appends Tool into scrollable container
	 * 
	 * @param tool Special Tool defined by it's icon
	 */
	public void addTool(final Tool tool)
	{
		int dp32 = ScreenUtils.dpToPx(this.activity, 32),
			dp5 = ScreenUtils.dpToPx(this.activity, 5);
		
		ImageView view = new ImageView(this.activity);
		view.setPadding(dp5, 0, 0, 0);
		view.setOnClickListener(this);
		view.setId(toolsArray.size());
		toolsLayout.addView(view, dp32, dp32);
		
		this.toolsArray.put(tool, view);
		invalidateTools();
	}
	
	/**
	 * Method creates context menu and associate it with this action bar
	 * 
	 * @return context menu
	 */
	public Menu createContextMenu()
	{
		if(this.menu == null)
		{
			this.menu = new Menu(this.activity);
			this.menu.setStaticAnchor(this.moreButton);
		}
		
		this.moreButton.setVisibility(VISIBLE);
		return this.menu;
	}
	
	/**
	 * Method return max of action bar's progress bar
	 * Default is 10000;
	 * 
	 * @return maximum available progress
	 */
	public int getMaxProgress()
	{
		return progressBar.getMax();
	}
	
	/**
	 * Method returns current progress
	 * Default is 0
	 * 
	 * @return current progress defined by setProgress(...)
	 */
	public int getProgress()
	{
		return progressBar.getProgress();
	}
	
	/**
	 * Method returns subtitle, which was set into this action bar
	 * 
	 * @return subtitle displayed (or no) by action bar
	 */
	public CharSequence getSubtitle()
	{
		return this.subtitleView.getText();
	}

	/**
	 * Method returns title, which was set into this action bar
	 * 
	 * @return title displayed by action bar
	 */
	public CharSequence getTitle()
	{
		return this.titleView.getText();
	}
	
	/**
	 * Hides action bar by starting fading out animation
	 */
	public void hide()
	{
		fadingAnimation.start();
	}
	
	/**
	 * Method returns true if action abr is showed, false otherwise
	 * 
	 * @return is action bar showed on display
	 */
	public boolean isShowing()
	{
		return getVisibility() != View.GONE;
	}
	
	/**
	 * Method removes Tool from map @see toolsArray and from scrollable container
	 * 
	 * @param tool Tool for remove from action bar
	 */
	public void removeTool(Tool tool)
	{
		if(toolsArray.containsKey(tool))
		{
			toolsLayout.removeView(toolsArray.get(tool));
			toolsArray.remove(tool);
		}
	}
	
	/**
	 * Method shows activity's logo if useLogo is true, actiovity's icon otherwise
	 * 
	 * @param useLogo is need for hide icon and show logo 
	 */
	public void setDisplayLogo(boolean useLogo)
	{
		isLogoUsed = useLogo;
		invalidateIconView();
	}
	
	/**
	 * Method shows up button if showUp is true, hides it otherwise
	 * 
	 * @param showUp is nedded for show up button
	 */
	public void setDisplayUpButton(boolean showUp)
	{
		this.upButton.setVisibility(showUp ? View.VISIBLE : View.INVISIBLE);
	}

	/**
	 * Method shows subtitle and moves title to upper part of action bar, if showSubtitle is true
	 *        hides subtitle and moves title to center of action bar otherwise
	 * 
	 * @param showSubtitle is need for show subtitle
	 */
	public void setDisplaySubtitle(boolean showSubtitle)
	{
		isSubtitleDisplayed = showSubtitle;
		invalidateTitles();
	}
	
	/**
	 * Method applies new icon
	 * if at now used logo, icon will be only saved
	 * else icon will be saved and aplied to iconView
	 * 
	 * @param newIcon new icon
	 */
	public void setIcon(Drawable newIcon)
	{
		lastIcon = newIcon;
		invalidateIconView();
	}
	
	/**
	 * Method applies new logo
	 * if at now not used logo, logo will be only saved
	 * else logo will be saved and aplied to iconView
	 * 
	 * @param newLogo new logo
	 */
	public void setLogo(Drawable newLogo)
	{
		lastLogo = newLogo;
		invalidateIconView();
	}
	
	/**
	 * Method set max available progress
	 * 
	 * @param max maximum progress
	 */
	public void setMaxProgress(int max)
	{
		progressBar.setMax(max);
	}

	/**
	 * Mathod set current progress of executing some task
	 * 
	 * @param progress current progress
	 */
	public void setProgress(int progress)
	{
		progressBar.setProgress(progress);
	}
	
	/**
	 * Method update subtitleView's text with new string
	 * 
	 * @param title new subtitle for using in action bar
	 */
	public void setSubtitle(CharSequence title)
	{
		this.subtitleView.setText(title);
	}
	
	/**
	 * Method update subtitleView's text with string, wich will be get from resources by attribute id
	 * 
	 * @param titleRes attribute in R class of your app, which defines string for using in action bar
	 */
	public void setSubtitle(int titleRes)
	{
		this.subtitleView.setText(titleRes);
	}
	
	/**
	 * Method applies new typeface of subtitle
	 * 
	 * @param typeface new Typeface @see android.graphics.Typeface
	 */
	public void setSubtitleTypeface(Typeface typeface)
	{
		this.subtitleView.setTypeface(typeface);
	}
	
	/**
	 * Method update titleView's text with new string
	 * 
	 * @param title new title for using in action bar
	 */
	public void setTitle(CharSequence title)
	{
		this.titleView.setText(title);
	}
	
	/**
	 * Method update titleView's text with string, wich will be get from resources by attribute id
	 * 
	 * @param titleRes attribute in R class of your app, which defines string for using in action bar
	 */
	public void setTitle(int titleRes)
	{
		this.titleView.setText(titleRes);
	}		
	
	/**
	 * Method applies new typeface of subtitle
	 * 
	 * @param typeface new Typeface @see android.graphics.Typeface
	 */
	public void setTitleTypeface(Typeface typeface)
	{
		this.titleView.setTypeface(typeface);
	}
	
	/**
	 * Method shows action bar on display by starting fading in animation
	 */
	public void show()
	{
		fadingAnimation.start();
	}
	
	/**
	 * Method invalidates (redraw) tools container
	 * used for update UI if Tool added / removed
	 */
	private void invalidateTools()
	{
		for(Tool tool : toolsArray.keySet())
		{
			toolsArray.get(tool).setImageDrawable(tool.getIcon());
		}
	}
		
	/**
	 * Method invalidates (redraw) icon view
	 * used for update UI if useLogo changed or changed current icon / logo
	 */
	private void invalidateIconView()
	{
		if(isLogoUsed)
			iconView.setImageDrawable(lastLogo);
		else
			iconView.setImageDrawable(lastIcon);
	}
	
	/**
	 * Method invalidates title and subtitle
	 * used for update UI if setDisplaySubtitle(...) called
	 */
	private void invalidateTitles()
	{
		if(isSubtitleDisplayed)
		{
			LayoutParams titleViewParams = new LayoutParams(-2, -2);
			titleViewParams.addRule(ALIGN_PARENT_LEFT);
			titleViewParams.addRule(ALIGN_PARENT_TOP);
			titleViewParams.setMargins(((LayoutParams)titleView.getLayoutParams()).leftMargin, 0, 0, 0);
			titleView.setLayoutParams(titleViewParams);
			
			subtitleView.setVisibility(VISIBLE);
		}
		else
		{
			LayoutParams titleViewParams = new LayoutParams(-2, -2);
			titleViewParams.addRule(ALIGN_PARENT_LEFT);
			titleViewParams.addRule(CENTER_VERTICAL);
			titleViewParams.setMargins(((LayoutParams)titleView.getLayoutParams()).leftMargin, 0, 0, 0);
			titleView.setLayoutParams(titleViewParams);
			
			subtitleView.setVisibility(GONE);
		}
	}
	
	/**
	 * This class used for display some useful tool on action bar
	 * Any tool MUST be defined by it's unique (on current action bar) icon
	 * 
	 * @author K. V. Bogatikov, KiVITechnologies Ltd, 2017
	 * @version 1.0
	 */
	public static abstract class Tool
	{
		public abstract Drawable getIcon();	
		public abstract void onClick();
	}
	
	/**
	 * This class used for animate action bar's fading in/out
	 * Can be used by other classes with similar animation
	 * 
	 * @author K. V. Bogatikov, KiVITechnologies Ltd, 2017
	 * @version 1.1
	 */
	public class FadingAnimation extends AnimationSet implements AnimationListener
	{
		/**
		 * animations:
		 * fadeInAlpha      alpha animation for fade in
		 * fadeOutAlpha     alpha animation for fade out
		 * fadeInTranslate  translate animation for fade in
		 * fadeOuttranslate translate animation for fade out
		 */
		Animation fadeInAlpha, fadeOutAlpha, fadeInTranslate, fadeOutTranslate;
		
		/**
		 * Default constructor
		 * 
		 * @param length length of animation in milliseconds
		 */
		public FadingAnimation(long length)
		{
			super(false);
			
			int dp54 = ScreenUtils.dpToPx(ActionBarDrawer.this.getContext(), -54);
			
			fadeInAlpha = new AlphaAnimation(0.0f, 1.0f);
			fadeInAlpha.setDuration(length);
			fadeOutAlpha = new AlphaAnimation(1.0f, 0.0f);
			fadeOutAlpha.setDuration(length);
			setAnimationListener(this);

			fadeInTranslate = new TranslateAnimation(0.0f, 0.0f, dp54, 0.0f);
			fadeInTranslate.setDuration(length);
			fadeOutTranslate = new TranslateAnimation(0.0f, 0.0f, 0.0f, dp54);
			fadeOutTranslate.setDuration(length);
			setAnimationListener(this);
		}

		/**
		 * Method for listening animations end
		 * when fade in animation end, action bar view myst be sowed (i.e. it's visibility must be VISIBLE)
		 * Overrides from Animation.AnimationListener
		 * 
		 */
		@Override
		public void onAnimationEnd(Animation anim)
		{
			if(ActionBarDrawer.this.isShowing())
				ActionBarDrawer.this.setVisibility(GONE);
			else
				ActionBarDrawer.this.setVisibility(VISIBLE);
		}

		/**
		 * Method for listening animations repeat
		 * when animation is repeated (WTF?!) it MUST be cancelled
		 * Overrides from Animation.AnimationListener
		 * 
		 */
		@Override
		public void onAnimationRepeat(Animation anim)
		{
			anim.cancel();
		}

		/**
		 * Method for listening animations start
		 * do nothing in this implementation
		 * Overrides from Animation.AnimationListener
		 * 
		 */
		@Override
		public void onAnimationStart(Animation anim)
		{
			
		}
	}
	
	/**
	 * This class used for shows task execting progress
	 * Simple progress bar is FOO
	 * 
	 * @author K. V. Bogatikov, KiVITechnologies Ltd, 2017
	 * @version 1.0
	 */
	private static class MyProgressBar extends View
	{
		/**
		 * max             saves maximum available value of progress
		 * currentProgress saves current value of progress
		 * 
		 * progress   Drawable for displaying current progress
		 * background Drawable for displaying progress bar background
		 */
		private int max, currentProgress;
		private Drawable progress, background;
		
		/**
		 * Default constructor
		 * 
		 * @param ctx     Context for creating a view
		 * @param colorFG color of foreground i.e. progress
		 * @param colorBG color of background i.e. background of bar
		 */
		public MyProgressBar(Context ctx, int colorFG, int colorBG)
		{
			super(ctx);
			
			max = 100;
			currentProgress = 0;
			
			progress = new ColorDrawable(colorFG);
			background = new ColorDrawable(colorBG);
		}
		
		/**
		 * Unused method
		 * Returns array of two Drawables: used for display progress and background drawable
		 * 
		 * @return array: 0 - progress's drawable, 1 - background drawable
		 */
		@SuppressWarnings("unused")
		public Drawable[] getProgressDrawable()
		{
			return new Drawable[]{ progress, background };
		}
		
		/**
		 * Unused method
		 * Applies new drawables for drawing progress bar
		 * 
		 * @param progressDrawable   drawable for display current progress
		 * @param backgroundDrawable drawable for display background of bar
		 */
		@SuppressWarnings("unused")
		public void setProgressDrawable(Drawable progressDrawable, Drawable backgroundDrawable)
		{
			progress = progressDrawable;
			background = backgroundDrawable;
		}
		
		/**
		 * Method returns max available value of progress
		 * 
		 * @return maximum progress
		 */
		public int getMax()
		{
			return max;
		}
		
		/**
		 * Method returns current value of progress
		 * 
		 * @return current progress
		 */
		public int getProgress()
		{
			return currentProgress;
		}
		
		/**
		 * Method applies max available value of progress
		 * 
		 * @params newMax new maximum value of progress
		 */
		public void setMax(int newMax)
		{
			max = newMax;
		}
		
		/**
		 * Method applies new current value of progress
		 * 
		 * @params newProgress current progress
		 */
		public void setProgress(int newProgress)
		{
			currentProgress = newProgress;
		}
		
		/**
		 * Method load to this progress bar default drawables for display progress and background
		 * used in constructor, if color values is not valid
		 */
		private void loadDefaultDrawables()
		{
			progress = null;
			background = null;
			
			LayerDrawable layer = (LayerDrawable)ResourcesUtils.getDrawable(getContext(), android.R.drawable.progress_horizontal);
			for(int i = 0; i < layer.getNumberOfLayers(); i++)
			{
				if(layer.getId(i) == android.R.id.progress)
					progress = layer.getDrawable(i);
				else if(layer.getId(i) == android.R.id.background)
					background = layer.getDrawable(i);
				
				if(progress != null && background != null)
					break;
			}
		}
		
		/**
		 * Draws this view on canvas
		 */
		@Override
		public void onDraw(Canvas canvas)
		{
			 if(progress == null || background == null)
				 loadDefaultDrawables();
			 
			 background.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
			 background.draw(canvas);
			 progress.setBounds(0, 0, currentProgress * canvas.getWidth() / max, canvas.getHeight());
			 progress.draw(canvas);
		}	
	}
	
	/**
	 * Method returns Drawable used for display up button
	 * 
	 * @return upImage
	 */
	private Drawable generateUpImage()
	{
		Resources res = this.activity.getResources();
		StateListDrawable sld = new StateListDrawable();
		
		Bitmap bitmap = Bitmap.createBitmap(300, 900, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		Paint lineDrawer = new Paint();
		lineDrawer.setColor(this.delegate.getColor(ATTR_PRESSED_ITEM_COLOR));
		lineDrawer.setStrokeWidth(90);
		lineDrawer.setStrokeCap(Paint.Cap.ROUND);
		
		canvas.drawLine(100, 450, 200, 200, lineDrawer);
		canvas.drawLine(100, 450, 200, 700, lineDrawer);
		sld.addState(new int[]{ android.R.attr.state_pressed },
				     new BitmapDrawable(res, bitmap));
		
		lineDrawer.setColor(this.delegate.getColor(ATTR_DEFAULT_ITEM_COLOR));
		canvas.drawLine(100, 450, 200, 200, lineDrawer);
		canvas.drawLine(100, 450, 200, 700, lineDrawer);
		sld.addState(StateSet.WILD_CARD,
				     new BitmapDrawable(res, bitmap));
		
		return sld;
	}
	
	/**
	 * Method returns Drawable used for display more button 
	 * 
	 * @return moreImage
	 */
	private Drawable generateMoreImage()
	{
		StateListDrawable sld = new StateListDrawable();
		
		Bitmap moreBitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
		int pressedColor = this.delegate.getColor(ATTR_PRESSED_ITEM_COLOR),
			defaultColor = this.delegate.getColor(ATTR_DEFAULT_ITEM_COLOR),
			dp32 = ScreenUtils.dpToPx(this.activity, 32);
		Resources resources = this.activity.getResources();
		
		for(int i = 0; i < 9; i+=3)
		{
			moreBitmap.setPixel(4, i + 1, pressedColor);
			moreBitmap.setPixel(4, i + 2, pressedColor);
			moreBitmap.setPixel(5, i + 1, pressedColor);
			moreBitmap.setPixel(5, i + 2, pressedColor);
		}
		
		Bitmap moreBitmapScaled = Bitmap.createScaledBitmap(moreBitmap, dp32, dp32, false);
		sld.addState(new int[]{ android.R.attr.state_pressed }, new BitmapDrawable(resources, moreBitmapScaled));
		
		for(int i = 0; i < 9; i+=3)
		{
			moreBitmap.setPixel(4, i + 1, defaultColor);
			moreBitmap.setPixel(4, i + 2, defaultColor);
			moreBitmap.setPixel(5, i + 1, defaultColor);
			moreBitmap.setPixel(5, i + 2, defaultColor);
		}
		
		moreBitmapScaled = Bitmap.createScaledBitmap(moreBitmap, dp32, dp32, false);
		sld.addState(StateSet.WILD_CARD, new BitmapDrawable(resources, moreBitmapScaled));
		
		return sld;
	}
}