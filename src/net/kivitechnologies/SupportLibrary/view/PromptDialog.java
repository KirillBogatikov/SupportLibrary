package net.kivitechnologies.SupportLibrary.view;

import net.kivitechnologies.SupportLibrary.utils.ScreenUtils;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PromptDialog extends AlertDialog
{
	private TextView title;
	private LinearLayout root;
	private EditText textField;
	private Button submitButton;
	private OnSubmitListener listener;
	
	public PromptDialog(Context ctx, final OnSubmitListener osl)
	{
		super(ctx);
		this.listener = osl;
		
		title = new TextView(ctx);
		title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
		
		setCustomTitle(title);
		
		int dp5 = ScreenUtils.dpToPx(ctx, 5);
		root = new LinearLayout(ctx);
		root.setOrientation(LinearLayout.VERTICAL);
		root.setPadding(dp5, dp5, dp5, dp5);
		
		textField = new EditText(ctx);
		textField.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
		
		submitButton = new Button(ctx);
		submitButton.setText("OK");
		submitButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View view)
			{
				if(listener != null)
					listener.onTextSubmit(textField.getText());
			}
		});
		
		root.addView(textField, -1, -2);
		root.addView(submitButton, -1, -2);
		setView(root);
	}
	
	public void setOnTextSubmitListener(OnSubmitListener osl)
	{
		this.listener = osl;
	}
	
	public void setTitle(int titleResId)
	{
		this.title.setText(titleResId);
	}
	
	public void setTitle(CharSequence titleText)
	{
		this.title.setText(titleText);
	}
	
	public void setTitleColor(int color)
	{
		this.title.setTextColor(color);
	}
	
	public void setTypeface(Typeface typeface)
	{
		this.title.setTypeface(typeface);
		this.textField.setTypeface(typeface);
		this.submitButton.setTypeface(typeface);
	}
	
	public void setFieldHint(int hintResId)
	{
		this.textField.setHint(hintResId);
	}
	
	public void setFieldHint(CharSequence hintText)
	{
		this.textField.setHint(hintText);
	}
	
	public void setFieldColor(int color)
	{
		this.textField.setTextColor(color);
	}
	
	public static interface OnSubmitListener
	{
		public void onTextSubmit(CharSequence text);
	}
}