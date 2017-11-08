package net.kivitechnologies.SupportLibrary.view;

import java.util.ArrayList;

import net.kivitechnologies.SupportLibrary.graphics.ShadowDrawable;
import net.kivitechnologies.SupportLibrary.utils.ColorUtils;
import net.kivitechnologies.SupportLibrary.utils.ResourcesUtils;
import net.kivitechnologies.SupportLibrary.utils.ScreenUtils;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class Menu
{
	private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener()
	{
		public void onItemClick(AdapterView<?> itemList, View itemView, int position, long id)
		{
			if(Menu.this.listener != null)
			{
				Menu.this.listener.onItemClick(position);
			}
		}
	};
	
	private PopupWindow window;
	private RelativeLayout contentRoot;
	private ListView itemsView;
	private ItemsAdapter itemsAdapter;
	private Activity activity;
	private MenuListener listener;
	private View staticAnchor, lastAnchor;
	
	public Menu(Activity context)
	{
		this.activity = context;
		
		itemsView = new ListView(context);
		itemsView.setBackgroundColor(ColorUtils.setValue(ColorUtils.parse("#FFFFFF"), 0.9f));
		itemsView.setOnItemClickListener(itemClickListener);
		itemsAdapter = new ItemsAdapter(context);
		itemsView.setAdapter(itemsAdapter);
		
		int dp10 = ScreenUtils.dpToPx(context, 10);
		contentRoot = new RelativeLayout(context);
		contentRoot.addView(itemsView);
		contentRoot.setPadding(dp10, dp10, dp10, dp10);
		
		window = new PopupWindow(contentRoot);
		window.setWidth(ScreenUtils.getWidth(context) * 3 / 4);
		window.setHeight(-2);
		window.setFocusable(true);
		window.setOutsideTouchable(true);
		window.setBackgroundDrawable(new ShadowDrawable(dp10, 0.55f));
	}
	
	public Item addItem(String name, Drawable icon, boolean checkable)
	{
		RelativeLayout itemRoot = new RelativeLayout(activity);
		Item item = new Item(activity, itemRoot);
		item.setTitle(name);
		if(icon != null)
			item.setIcon(icon);
		item.setCheckable(checkable);
		itemsAdapter.addItem(itemRoot);
		itemRoot.findFocus();
		
		return item;
	}
	
	public Item addItem(String name, Drawable icon)
	{
		return addItem(name, icon, false);
	}
	
	public Item addItem(String name, int resIcon)
	{
		return addItem(name, ResourcesUtils.getDrawable(activity, resIcon));
	}
	
	public Item addItem(String name)
	{
		return addItem(name, null, false);
	}
	
	public Item addItem(int titleRes)
	{
		return addItem(activity.getString(titleRes));
	}
	
	public Item addItem(int titleRes, int iconRes)
	{
		return addItem(activity.getString(titleRes), ResourcesUtils.getDrawable(activity, iconRes));
	}
	
	public Item addItem(int titleRes, Drawable icon)
	{
		return addItem(activity.getString(titleRes), icon);
	}
	
	public void dismiss()
	{
		window.dismiss();
		if(listener != null)
			listener.onMenuDismiss();
	}
	
	public void setMenuListener(MenuListener mListener)
	{
		this.listener = mListener;
	}
	
	public void setStaticAnchor(View anchor)
	{
		this.staticAnchor = anchor;
	}
	
	public void show()
	{
		show(staticAnchor != null ? staticAnchor : lastAnchor);
	}
	
	public void show(View anchor)
	{
		window.showAsDropDown(anchor);
		lastAnchor = anchor;
		
		if(listener != null)
			listener.onMenuShow();
	}
	
	public void showAsLast()
	{
		show(lastAnchor);
	}
	
	public interface MenuListener
	{
		public abstract void onMenuShow();
		public abstract void onMenuDismiss();
		public abstract void onItemClick(int itemId);
	}
	
	
	public final class Item
	{
		private RelativeLayout itemRoot;
		private ImageView iconView;
		private TextView titleView, subtitleView;
		private CheckBox checkView; 
		private boolean isSubtitleDisplayed;
		
		private Item(Context context, RelativeLayout associatedRoot)
		{
			int dp32 = ScreenUtils.dpToPx(context, 32),
				dp5 = ScreenUtils.dpToPx(context, 5);
			
			itemRoot = associatedRoot;
			itemRoot.setPadding(dp5, dp5, dp5, dp5);
			
			iconView = new ImageView(context);
			RelativeLayout.LayoutParams iconViewParams = new RelativeLayout.LayoutParams(dp32, dp32);
			iconViewParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			itemRoot.addView(iconView, iconViewParams);
			
			titleView = new MarqueeTextView(context, false);
			titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
			RelativeLayout.LayoutParams titleViewParams = new RelativeLayout.LayoutParams(-1, -2);
			titleViewParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			titleViewParams.addRule(RelativeLayout.CENTER_VERTICAL);
			titleViewParams.setMargins(dp32 + dp5, 0, dp5 + dp32, 0);
			itemRoot.addView(titleView, titleViewParams);
			
			subtitleView = new MarqueeTextView(context, false);
			subtitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
			RelativeLayout.LayoutParams subtitleViewParams = new RelativeLayout.LayoutParams(-1, -2);
			subtitleViewParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			subtitleViewParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			subtitleViewParams.setMargins(dp32 + dp5, 0, dp5 + dp32, 0);
			itemRoot.addView(subtitleView, subtitleViewParams);
			subtitleView.setVisibility(View.GONE);
			
			checkView = new CheckBox(context);
			RelativeLayout.LayoutParams checkViewParams = new RelativeLayout.LayoutParams(dp32, dp32);
			checkViewParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			checkViewParams.setMargins(0, dp5, dp5, 0);
			itemRoot.addView(checkView, checkViewParams);
		}
		
		public CharSequence getSubtitle()
		{
			return this.subtitleView.getText();
		}
		
		public int getSubtitleColor()
		{
			return this.subtitleView.getTextColors().getDefaultColor();
		}

		public CharSequence getTitle()
		{
			return this.titleView.getText();
		}
		
		public int getTitleColor()
		{
			return this.titleView.getTextColors().getDefaultColor();
		}
		
		public boolean isCheckable()
		{
			return this.checkView.getVisibility() != View.INVISIBLE;
		}
		
		public boolean isChecked()
		{
			return isCheckable() && this.checkView.isChecked();
		}
		
		public void setCheckable(boolean checkable)
		{
			this.checkView.setVisibility(checkable ? View.VISIBLE : View.INVISIBLE);
		}
		
		public void setChecked(boolean checked)
		{
			this.checkView.setChecked(checked);
		}
		
		public void setDisplaySubtitle(boolean showSubtitle)
		{
			isSubtitleDisplayed = showSubtitle;
			invalidateTitles();
		}
		
		public void setIcon(Drawable newIcon)
		{
			this.iconView.setImageDrawable(newIcon);
		}
		
		public void setSubitle(CharSequence title)
		{
			this.subtitleView.setText(title);
		}
		
		public void setSubtitle(int titleRes)
		{
			this.subtitleView.setText(titleRes);
		}
		
		public void setSubtitleColor(int subtitleColor)
		{
			this.subtitleView.setTextColor(subtitleColor);
		}
		
		public void setSubtitleTypeface(Typeface typeface)
		{
			this.subtitleView.setTypeface(typeface);
		}
		
		public void setTitle(CharSequence title)
		{
			this.titleView.setText(title);
		}
		
		public void setTitle(int titleRes)
		{
			this.titleView.setText(titleRes);
		}	
		
		public void setTitleColor(int titleColor)
		{
			this.titleView.setTextColor(titleColor);
		}
		
		public void setTitleTypeface(Typeface typeface)
		{
			this.titleView.setTypeface(typeface);
		}
		
		private void invalidateTitles()
		{
			if(isSubtitleDisplayed)
			{
				RelativeLayout.LayoutParams titleViewParams = new RelativeLayout.LayoutParams(-2, -2);
				titleViewParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				titleViewParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				titleViewParams.setMargins(((LayoutParams)titleView.getLayoutParams()).leftMargin, 0, 0, 0);
				titleView.setLayoutParams(titleViewParams);
				
				subtitleView.setVisibility(View.VISIBLE);
			}
			else
			{
				RelativeLayout.LayoutParams titleViewParams = new RelativeLayout.LayoutParams(-2, -2);
				titleViewParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				titleViewParams.addRule(RelativeLayout.CENTER_VERTICAL);
				titleViewParams.setMargins(((LayoutParams)titleView.getLayoutParams()).leftMargin, 0, 0, 0);
				titleView.setLayoutParams(titleViewParams);
				
				subtitleView.setVisibility(View.GONE);
			}
		}
	}

	private class ItemsAdapter extends ArrayAdapter<RelativeLayout>
	{
		private ArrayList<RelativeLayout> items;

		public ItemsAdapter(Context context)
		{
			super(context, 0x0);
			items = new ArrayList<RelativeLayout>();
		}
		
		public void addItem(RelativeLayout itemRoot)
		{
			items.add(itemRoot);
		}
		
		public int getCount()
		{
			return items.size();
		}
		
		public View getView(int position, View conevrtView, ViewGroup parent)
		{
			return items.get(position);
		}
	}
}