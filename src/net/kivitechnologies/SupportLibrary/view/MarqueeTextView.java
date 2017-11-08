package net.kivitechnologies.SupportLibrary.view;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.widget.TextView;

public class MarqueeTextView extends TextView
{
	public MarqueeTextView(Context context)
	{
		this(context, true);
	}
	
	public MarqueeTextView(Context context, boolean autoScroll)
	{
		super(context);
		if(autoScroll)
		{
			super.setEllipsize(TruncateAt.MARQUEE);
			super.setSelected(true);
			super.setSingleLine(true);
			super.setFocusable(true);
			super.setFocusableInTouchMode(true);
		}
		else
		{
			super.setHorizontallyScrolling(true);
		}
	}

	@Deprecated
	public void setEllipsize(TruncateAt ellipsize)
	{
		
	}
	
	@Deprecated
	public void setSelected(boolean selected)
	{
		
	}

	@Deprecated
	public void setFocusable(boolean focusable)
	{
		
	}

	@Deprecated
	public void setFocusableinTouchMode(boolean focusableInTouchMode)
	{
		
	}

	@Deprecated
	public void setSingleLine(boolean singleLine)
	{
		
	}
}
