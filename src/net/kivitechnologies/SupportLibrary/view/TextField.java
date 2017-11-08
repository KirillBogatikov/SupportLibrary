package net.kivitechnologies.SupportLibrary.view;

import android.content.Context;
import android.widget.EditText;

public class TextField extends EditText
{
	public static interface OnNewLineAddedListener
	{
		public void onNewLineAdded(int line);
	}
	
	private OnNewLineAddedListener listener = null;
	
	public TextField(Context context)
	{
		super(context);
	}

	protected void onMeasure(int width, int height)
	{
		if(getWidth() != 0 && getHeight() != 0)
		{
			float charWidth = getPaint().measureText("X");
			int columnsCount = getColumnsCount();
		
			width = Math.max(width, (int)(charWidth * columnsCount));
		}
		
		super.onMeasure(width, height);
	}
	
	public void setWidth(int pixels)
	{
		super.measure(pixels, getHeight());
	}
	
	public void setHeight(int pixels)
	{
		super.measure(getWidth(), pixels);
	}
	
	protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter)
	{
		String[] newLines = text.toString().split("\n");
		
		for(int i = 0; i < newLines.length; i++)
		{
			if(!newLines[i].equals(text.toString()) && listener != null)
				listener.onNewLineAdded(i + 1);
		}
	}
	
	public int getColumnsCount()
	{
		String[] lines = getText().toString().split("\n");
		int columnsCount = -1;
		
		for(String line : lines)
			columnsCount = Math.max(columnsCount, line.length());
		
		return columnsCount;
	}
	
	public int getColumnsCount(int lineNumber)
	{
		String[] lines = getText().toString().split("\n");
		if(lines.length <= lineNumber)
			return -1;
		
		return lines[lineNumber].length();
	}

	public int getLinesCount()
	{
		String txt = getText().toString();
		return txt.split("\n").length;
	}

	public int getLinesCount(int columnNumber)
	{
		String[] lines = getText().toString().split("\n");
		int linesCount = 0;
		
		for(int i = 0; i < lines.length; i++)
		{
			if(lines[i].length() <= columnNumber)
				continue;
			
			linesCount++;
		}
		
		return linesCount;
	}
}
