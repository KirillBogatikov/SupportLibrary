package net.kivitechnologies.SupportLibrary.app;

import static android.os.Build.VERSION.SDK_INT;

import java.util.HashMap;

import net.kivitechnologies.SupportLibrary.utils.ColorUtils;
import net.kivitechnologies.SupportLibrary.utils.ResourcesUtils;
import net.kivitechnologies.SupportLibrary.utils.ScreenUtils;
import net.kivitechnologies.SupportLibrary.view.MarqueeTextView;
import android.app.Activity;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.StateSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class ActionBar
{
	/*
	 * Used to declare colors used for display action bar 
	 */
	public static abstract class Style
	{
		public static final Style obtain(Activity ctx, int[] attrs)
		{
			final TypedArray styles = ctx.getTheme().obtainStyledAttributes(attrs);
			
			return new Style()
			{
				public int getBackgroundColor()
				{
					return styles.getColor(0, Color.TRANSPARENT);
				}
				
				public int getDividerColor()
				{
					return styles.getColor(1, Color.TRANSPARENT);
				}
				
				public int getDefaultItemColor()
				{
					return styles.getColor(2, Color.TRANSPARENT);
				}
				
				public int getPressedItemColor()
				{
					return styles.getColor(3, Color.TRANSPARENT);
				}
			};
		}
		
		public abstract int getBackgroundColor();  //Solid background color of ActionBar
		public abstract int getDividerColor();     //Solid color for divider line and shadow
		public abstract int getDefaultItemColor(); //Solid color for draw item foreground
		public abstract int getPressedItemColor(); //Solid color for draw item foreground if item is pressed
	}
	
	/*
	 * Used for display simple tool such as More Button for showing context menu
	 */
	public static abstract class Tool
	{
		public abstract Drawable getIcon();   /* Called for represent your tool at ActionBar */
		public void onShow(){};               /* Called if place on ActionBar anew is available and your tool moved from ContextMenu */
		public void onHide(){};				  /* Called if place on ActionBar smaller than size of tool. Tool will be moved to ContextMenu */
		public abstract void onClick();       /* Called if user click on your Tool */
	}
	
	/*
	 * Used for display "Up" Button, Icon, TItle, Subtitle 
	 */
	private class UpButton extends LinearLayout implements View.OnClickListener
	{
		private class TouchController extends StateListDrawable
		{
			public boolean onStateChange(int[] stateSet)
			{
				boolean isPressed = false;
				
				for(int state : stateSet)
				{
					if(state == android.R.attr.state_pressed)
					{
						isPressed = true;
						break;
					}
				}
				
				if(isPressed)
					UpButton.this.setBackgroundColor(ColorUtils.setAlpha(ActionBar.this.style.getPressedItemColor(), 0x40));
				else
					UpButton.this.setBackgroundColor(Color.TRANSPARENT);
				
				return super.onStateChange(stateSet);
			}
		}
		
		private ImageView upButton, icon;
		private MarqueeTextView title, subtitle;
		
		@SuppressWarnings("deprecation")
		public UpButton()
		{
			super(ActionBar.this.ctx);
			
			upButton = new ImageView(ActionBar.this.ctx);
			upButton.setImageDrawable(generateUpImage(ActionBar.this.ctx.getResources()));
			addView(upButton, ScreenUtils.dpToPx(ActionBar.this.ctx, 10), ScreenUtils.dpToPx(ActionBar.this.ctx, 32));
			upButton.setVisibility(View.INVISIBLE);
			
			icon = new ImageView(ActionBar.this.ctx);
			icon.setImageDrawable(ResourcesUtils.getActivityIcon(ActionBar.this.ctx));
			addView(icon, ScreenUtils.dpToPx(ActionBar.this.ctx, 32), ScreenUtils.dpToPx(ActionBar.this.ctx, 32));
			
			RelativeLayout titlesContainer = new RelativeLayout(ActionBar.this.ctx);
			addView(titlesContainer, -2, ScreenUtils.dpToPx(ActionBar.this.ctx, 35));
			
			title = new MarqueeTextView(ActionBar.this.ctx);
			title.setTextColor(style.getDefaultItemColor());
			title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
			title.setMaxWidth(ScreenUtils.getWidth(ActionBar.this.ctx) * 3/4);
			RelativeLayout.LayoutParams tparams = new RelativeLayout.LayoutParams(-2, -2);
			tparams.setMargins(ScreenUtils.dpToPx(ActionBar.this.ctx, 8), -ScreenUtils.dpToPx(ActionBar.this.ctx, 6, 4), 0, 0);
			tparams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			titlesContainer.addView(title, tparams);
			
			subtitle = new MarqueeTextView(ActionBar.this.ctx);
			subtitle.setTextColor(style.getDefaultItemColor());
			subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
			subtitle.setMaxWidth(ScreenUtils.getWidth(ActionBar.this.ctx) * 5/9);
			RelativeLayout.LayoutParams stparams = new RelativeLayout.LayoutParams(-2, -2);
			stparams.setMargins(ScreenUtils.dpToPx(ActionBar.this.ctx, 8), 0, 0, 0);
			stparams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			titlesContainer.addView(subtitle, stparams);
			
			upButton.setOnClickListener(this);
			icon.setOnClickListener(this);
			titlesContainer.setOnClickListener(this);
			title.setOnClickListener(this);
			subtitle.setOnClickListener(this);
			setOnClickListener(this);
			
			Drawable bg = new TouchController();
			
			if(SDK_INT >= 16)
			{
				icon.setBackground(bg);
				titlesContainer.setBackground(bg);
				title.setBackground(bg);
				subtitle.setBackground(bg);
			}
			else
			{
				icon.setBackgroundDrawable(bg);
				titlesContainer.setBackgroundDrawable(bg);
				title.setBackgroundDrawable(bg);
				subtitle.setBackgroundDrawable(bg);
			}
		}
		
		public void onClick(View view)
		{
			((Activity)getContext()).finish();
		}
		
		public void setShowUpEnabled(boolean enabled)
		{
			upButton.setVisibility(enabled ? View.VISIBLE : View.GONE);
		}
		
		public void setIcon(Drawable appIcon)
		{
			icon.setImageDrawable(appIcon);
		}
		
		public CharSequence getTitle()
		{
			return title.getText();
		}
		
		public void setTitle(CharSequence title)
		{
			this.title.setText(title);
		}
		
		public CharSequence getSubtitle()
		{
			return subtitle.getText();
		}
		
		public void setSubtitle(CharSequence subtitle)
		{
			this.subtitle.setText(subtitle);
		}
		
		public void setShowSubtitleEnabled(boolean enabled)
		{
			if(enabled)
			{
				title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)title.getLayoutParams();
				params.setMargins(ScreenUtils.dpToPx(getContext(), 8), -ScreenUtils.dpToPx(getContext(), 6), 0, 0);
				params.removeRule(RelativeLayout.CENTER_VERTICAL);
				params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				title.setLayoutParams(params);
				
				subtitle.setVisibility(View.VISIBLE);
			}
			else
			{
				title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)title.getLayoutParams();
				params.setMargins(ScreenUtils.dpToPx(getContext(), 8), 0, 0, 0);
				params.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
				params.addRule(RelativeLayout.CENTER_VERTICAL);
				title.setLayoutParams(params);
				
				subtitle.setVisibility(View.GONE);
			}
		}
		
		public void setUpIndicator(Drawable upIndicator)
		{
			if(upIndicator != null)
				upButton.setImageDrawable(upIndicator);
		}
		
		private Drawable generateUpImage(Resources res)
		{
			StateListDrawable sld = new TouchController();
			
			Bitmap bitmap = Bitmap.createBitmap(300, 900, Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			Paint lineDrawer = new Paint();
			lineDrawer.setColor(style.getPressedItemColor());
			lineDrawer.setStrokeWidth(90);
			lineDrawer.setStrokeCap(Paint.Cap.ROUND);
			
			canvas.drawLine(100, 450, 200, 200, lineDrawer);
			canvas.drawLine(100, 450, 200, 700, lineDrawer);
			sld.addState(new int[]{ android.R.attr.state_pressed },
					     new BitmapDrawable(res, bitmap));
			
			lineDrawer.setColor(style.getDefaultItemColor());
			canvas.drawLine(100, 450, 200, 200, lineDrawer);
			canvas.drawLine(100, 450, 200, 700, lineDrawer);
			sld.addState(StateSet.WILD_CARD,
					     new BitmapDrawable(res, bitmap));
			
			return sld;
		}
	}
	
	/*
	 * Used for display Tools in scrollable parent 
	 */
	private class ToolsContainer extends LinearLayout
	{
		private HorizontalScrollView scroll;
		private LinearLayout layout;
		
		public ToolsContainer()
		{
			super(ActionBar.this.ctx);
			scroll = new HorizontalScrollView(ActionBar.this.ctx);
			addView(scroll);
			layout = new LinearLayout(ActionBar.this.ctx);
			scroll.addView(layout);
		}
		
		public void addToolView(ToolView view)
		{
			layout.addView(view);
		}
		
		public void removeToolView(ToolView view)
		{
			layout.removeView(view);
		}
		
		@SuppressWarnings("unused")
		public void addMoreTool(View more)
		{
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ScreenUtils.dpToPx(ActionBar.this.ctx, 32), ScreenUtils.dpToPx(ActionBar.this.ctx, 32));
			params.setMargins(ScreenUtils.dpToPx(ActionBar.this.ctx, 8), 0, 0, 0);
			addView(more, params);
		}

		@SuppressWarnings("unused")
		public void removeMoreTool()
		{
			removeViewAt(1);
		}
	}
	
	/*
	 * Used for display progress at ActionBar
	 */
	private class ProgressView extends ImageView
	{
		private int max, current;
		private Drawable progress;
		
		public ProgressView()
		{
			super(ActionBar.this.ctx);
			
			setBackgroundColor(ColorUtils.setAlpha(ActionBar.this.style.getPressedItemColor(), 0x40));
			progress = new ColorDrawable(ActionBar.this.style.getDefaultItemColor());
			max = 1;
			update();
		}
		
		public void setMax(int maxx)
		{
			this.max = maxx;
			update();
		}
		
		public int getMax()
		{
			return this.max;
		}
		
		public void setProgress(int progress)
		{
			this.current = progress;
			update();
		}
		
		public int getProgress()
		{
			return this.current;
		}
		
		private void update()
		{
			int width = getWidth() == 0 ? ScreenUtils.getWidth(ctx) : getWidth();
			int height = getHeight() == 0 ? ScreenUtils.dpToPx(ctx, 2) : getHeight();
			
			Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			Canvas can = new Canvas(bmp);
			progress.setBounds(0, 0, width * (current / max), height);
			progress.draw(can);
			
			setImageBitmap(bmp);
		}
	}
	
	/*
	 * Used for wrap tools described by Tool class
	 */
	private class ToolView extends ImageView implements View.OnClickListener
	{
		private Tool tool;
		public ToolView(Tool toool)
		{
			super(ActionBar.this.ctx);
			this.tool = toool;
			
			update();
			setOnClickListener(this);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ScreenUtils.dpToPx(ActionBar.this.ctx, 32), ScreenUtils.dpToPx(ActionBar.this.ctx, 32));
			params.setMargins(ScreenUtils.dpToPx(ActionBar.this.ctx, 6), 0, 0, 0);
			setLayoutParams(params);
		}
		
		public void onClick(View view)
		{
			if(view.equals(this))
				tool.onClick();
		}
		
		public void update()
		{
			Drawable icon = tool.getIcon();
			if(icon == null)
			{
				Bitmap bmp = Bitmap.createBitmap(ScreenUtils.dpToPx(ctx, 32), ScreenUtils.dpToPx(ctx, 32), Bitmap.Config.ARGB_8888);
				Canvas can = new Canvas(bmp);
				Paint paint = new Paint();
				paint.setColor(ActionBar.this.style.getDefaultItemColor());
				int p = ScreenUtils.dpToPx(ctx, 6);
				can.drawOval(new RectF(p, p, p, p), paint);
				setImageBitmap(bmp);
			}
			else
				setImageDrawable(icon);
		}
	}
	
	private class FadingListener implements Animation.AnimationListener
	{
		public void onAnimationStart(Animation anim)
		{
			if(anim == showAnimation)
				ActionBar.this.root.setVisibility(View.VISIBLE);
		}

		public void onAnimationRepeat(Animation anim)
		{
			anim.cancel();
		}

		public void onAnimationEnd(Animation anim)
		{
			if(anim == hideAnimation)
				ActionBar.this.root.setVisibility(View.GONE);
		}
	}
	
	private Activity ctx;
	private Style style;
	private RelativeLayout root;
	private UpButton upButton;
	private ToolsContainer toolsContainer;
	private HashMap<Tool, ToolView> tools;
	private ProgressView progress;
	private ImageView moreButton;
	protected AnimationSet showAnimation, hideAnimation;
	/*private Menu contextMenu;*/
	
	public ActionBar(Activity ctxx, int[] attrs)
	{
		this(ctxx, Style.obtain(ctxx, attrs));
	}
	
	@SuppressWarnings("deprecation")
	public ActionBar(Activity ctxx, Style styles)
	{
		this.ctx = ctxx;
		this.style = styles;
		
		root = new RelativeLayout(ctx);
		
		if(SDK_INT >= 16)
			root.setBackgroundDrawable(generateBackgroundDrawable());
		else
			root.setBackground(generateBackgroundDrawable());
		
		root.setPadding(0, ScreenUtils.dpToPx(ctx, 8, 6), 0, 0);
		
		upButton = new UpButton();
		RelativeLayout.LayoutParams upButtonParams = new RelativeLayout.LayoutParams(-2, ScreenUtils.dpToPx(ctx, 32));
		upButtonParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		root.addView(upButton, upButtonParams);
		
		tools = new HashMap<Tool, ToolView>();
		
		toolsContainer = new ToolsContainer();
		RelativeLayout.LayoutParams toolsContainerParams = new RelativeLayout.LayoutParams(-2, ScreenUtils.dpToPx(ctx, 32));
		toolsContainerParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		root.addView(toolsContainer, toolsContainerParams);
		
		moreButton = new ImageView(ctx);
		moreButton.setImageDrawable(generateMoreDrawable());
		
		progress = new ProgressView();
		RelativeLayout.LayoutParams progressParams = new RelativeLayout.LayoutParams(ScreenUtils.getWidth(ctx), ScreenUtils.dpToPx(ctx, 2));
		progressParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		progressParams.setMargins(0, 0, 0, ScreenUtils.dpToPx(ctx, 8));
		root.addView(progress, progressParams);
		root.setLayoutParams(new RelativeLayout.LayoutParams(-1, ScreenUtils.dpToPx(ctx, 54, 48)));
		
		FadingListener fadingListener = new FadingListener();
		
		Animation alpha_fade_in = new AlphaAnimation(0.0f, 1.0f);
		alpha_fade_in.setDuration(1000);
		
		Animation trans_fade_in = new TranslateAnimation(0.0f, 0.0f, -(float)root.getLayoutParams().height, 0.0f);
		trans_fade_in.setDuration(1000);
		
		showAnimation = new AnimationSet(false);
		showAnimation.addAnimation(alpha_fade_in);
		showAnimation.addAnimation(trans_fade_in);
		showAnimation.setAnimationListener(fadingListener);
		
		Animation alpha_fade_out = new AlphaAnimation(1.0f, 0.0f);
		alpha_fade_out.setDuration(1000);

		Animation trans_fade_out = new TranslateAnimation(0.0f, 0.0f, 0.0f, -(float)root.getLayoutParams().height);
		trans_fade_out.setDuration(1000);

		hideAnimation = new AnimationSet(false);
		hideAnimation.addAnimation(alpha_fade_out);
		hideAnimation.addAnimation(trans_fade_out);
		hideAnimation.setAnimationListener(fadingListener);
		
		ctx.addContentView(root, root.getLayoutParams());
	}
	
	public boolean addTool(Tool tool)
	{
		if(tools.containsKey(tool))
				return false;
		
		ToolView view = new ToolView(tool);
		tools.put(tool, view);
		toolsContainer.addToolView(view);
		return true;
	}
	
	public int getCurrentProgress()
	{
		return progress.getProgress();
	}
	
	public int getHeight()
	{
		return ScreenUtils.dpToPx(ctx, 46, 40);
	}
		
	public int getMaxProgress()
	{
		return progress.getMax();
	}
		
	public CharSequence getSubtitle()
	{
		return upButton.getSubtitle();
	}
	
	public CharSequence getTitle()
	{
		return upButton.getTitle();
	}
	
	public int getToolsCount()
	{
		return tools.size();
	}
	
	public void hide()
	{
		root.startAnimation(hideAnimation);
	}
	
	public boolean isShowing()
	{
		return this.root.getVisibility() == View.VISIBLE;
	}
	
	public boolean removeTool(Tool tool)
	{
		if(!tools.containsKey(tool))
			return false;
		
		toolsContainer.removeToolView(tools.get(tool));
		tools.remove(tool);
		return true;
	}
	
	@SuppressWarnings("deprecation")
	public void setBackgroundDrawable(Drawable background)
	{
		customBackgroundDrawable = background;
		
		if(SDK_INT >= 16)
			root.setBackgroundDrawable(generateBackgroundDrawable());
		else
			root.setBackground(generateBackgroundDrawable());
		
		customBackgroundDrawable = null;
	}

	public void setBackgroundColor(int color)
	{
		setBackgroundDrawable(new ColorDrawable(color));
	}

	public void setDisplayUpButton(boolean upButtonShow)
	{
		upButton.setShowUpEnabled(upButtonShow);
	}
	
	public void setDisplayTaskProgress(boolean progressShow)
	{
		progress.setVisibility(progressShow ? View.VISIBLE : View.GONE);
	}
	
	public void setDisplaySubtitle(boolean subtitleShow)
	{
		upButton.setShowSubtitleEnabled(subtitleShow);
	}
	
	public void setMaxProgress(int max)
	{
		progress.setMax(max);
	}
	
	public void setCurrentProgress(int p)
	{
		progress.setProgress(p);
	}
	
	private Drawable appIcon, appLogo;
	private boolean useLogo;
	
	public void setDisplayUseLogo(boolean logo)
	{
		useLogo = logo;
		if(logo)
			upButton.setIcon(appLogo);
		else
			upButton.setIcon(appIcon);
	}

	public void setIcon(Drawable icon)
	{
		appIcon = icon;
		if(!useLogo)
			upButton.setIcon(appIcon);
	}
	
	public void setLogo(Drawable logo)
	{
		appLogo = logo;
		if(useLogo)
			upButton.setIcon(appLogo);
	}
	
	public void setMoreIndicator(Drawable moreIndicator)
	{
		moreButton.setImageDrawable(moreIndicator);
	}

	public void setTitle(CharSequence title)
	{
		upButton.setTitle(title);
	}
	
	public void setTitle(int title)
	{
		upButton.setTitle(ctx.getString(title));
	}
	
	public void setSubtitle(CharSequence subtitle)
	{
		upButton.setSubtitle(subtitle);
	}
	
	public void setSubtitle(int subtitle)
	{
		upButton.setSubtitle(ctx.getString(subtitle));
	}
	
	public void setUpIndicator(Drawable upIndicator)
	{
		upButton.setUpIndicator(upIndicator);
	}
	
	/*
	 * TODO: Add support for Menus after creating Menu's architecture
	 * 
	 * public void setContextMenu(Menu ctxMenu)
	 * {
	 * 	this.menu = ctxMenu;
	 *  moreButton.setVisibility(View.VISIBLE);
	 *	toolsContainer.addMoreTool(moreButton);
	 * }
	 * 
	 * public void removeContextMenu()
	 * {
	 * 	this.menu = null;
	 *  moreButton.setVisibility(View.GONE);
	 *	toolsContainer.removeMoreTool(moreButton);
	 * }
	 * 
	 * public boolean addOnMenuStateChangedListener(OnMenuStateChangedListener listener)
	 * {
	 * 	if(this.menuListeners.contains(lsistener)
	 * 		return false;
	 * 
	 *  this.menuListeners.add(listener);
	 *  return true;
	 * }
	 * 
	 * public boolean removeOnMenuStateChangedListener(OnMenuStateChangedListener listener)
	 * {
	 * 	return this.menuListeners.remove(listener);
	 * }
	 */
	
	private Drawable moreDrawable;
	private Drawable generateMoreDrawable()
	{
		if(moreDrawable != null)
			return moreDrawable;
		
		StateListDrawable sld = new StateListDrawable();
		
		Bitmap moreBitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
		
		for(int i = 0; i < 9; i++)
		{
			moreBitmap.setPixel(4, i + 1, style.getPressedItemColor());
			moreBitmap.setPixel(4, i + 2, style.getPressedItemColor());
			moreBitmap.setPixel(5, i + 1, style.getPressedItemColor());
			moreBitmap.setPixel(5, i + 2, style.getPressedItemColor());
		}
		
		Bitmap moreBitmapScaled = Bitmap.createScaledBitmap(moreBitmap, ScreenUtils.dpToPx(ctx, 32), ScreenUtils.dpToPx(ctx, 32), false);
		sld.addState(new int[]{ android.R.attr.state_pressed }, new BitmapDrawable(ctx.getResources(), moreBitmapScaled));
		
		for(int i = 0; i < 9; i++)
		{
			moreBitmap.setPixel(4, i + 1, style.getDefaultItemColor());
			moreBitmap.setPixel(4, i + 2, style.getDefaultItemColor());
			moreBitmap.setPixel(5, i + 1, style.getDefaultItemColor());
			moreBitmap.setPixel(5, i + 2, style.getDefaultItemColor());
		}
		
		moreBitmapScaled = Bitmap.createScaledBitmap(moreBitmap, ScreenUtils.dpToPx(ctx, 32), ScreenUtils.dpToPx(ctx, 32), false);
		sld.addState(StateSet.WILD_CARD, new BitmapDrawable(ctx.getResources(), moreBitmapScaled));
		moreDrawable = sld;
		
		return sld;
	}

	private GradientDrawable dividerDrawable;
	private Drawable generateDividerDrawable()
	{
		if(dividerDrawable != null)
			return dividerDrawable;
		
		dividerDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{ style.getDividerColor(), Color.TRANSPARENT });
		dividerDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
		dividerDrawable.setBounds(0, 0, ScreenUtils.getWidth(ctx), ScreenUtils.dpToPx(ctx, 8));
		
		return dividerDrawable;
	}

	private Drawable customBackgroundDrawable;
	private Drawable backgroundDrawable;
	private Drawable generateBackgroundDrawable()
	{
		if(backgroundDrawable != null && customBackgroundDrawable == null)
			return backgroundDrawable;
		
		Bitmap bmp = Bitmap.createBitmap(ScreenUtils.getWidth(ctx), ScreenUtils.dpToPx(ctx,  54), Bitmap.Config.ARGB_8888);
		Canvas can = new Canvas(bmp);
		
		if(customBackgroundDrawable == null)
		{
			Paint paint = new Paint();
			paint.setColor(style.getBackgroundColor());
			can.drawRect(0, 0, bmp.getWidth(), ScreenUtils.dpToPx(ctx, 46), paint);
		}
		else
		{
			customBackgroundDrawable.setBounds(0, 0, bmp.getWidth(), ScreenUtils.dpToPx(ctx, 46));
			customBackgroundDrawable.draw(can);
		}
		
		Drawable shadow = generateDividerDrawable();
		shadow.setBounds(0, ScreenUtils.dpToPx(ctx, 46), bmp.getWidth(), ScreenUtils.dpToPx(ctx, 54));
		shadow.draw(can);
		
		backgroundDrawable = new BitmapDrawable(ctx.getResources(), bmp);
		return backgroundDrawable;
	}
}
