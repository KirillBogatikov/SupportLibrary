package net.kivitechnologies.SupportLibrary.graphics;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import net.kivitechnologies.SupportLibrary.utils.ResourcesUtils;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.DrawableContainer.DrawableContainerState;
import android.graphics.drawable.NinePatchDrawable;

/**
 * This class generates 9-Patch drawable
 * This code was get from Habrahabr
 * Dear programmers, tahnk your for your help!
 * 
 * @author unknwon author
 * @rewritted K. V. Bogatikov, KiVITechnologies Ltd, 2017
 * @version beta
 */

/**
 * Rewritting results:
 * added caches and some millions bugs 
 * @author K. V. Bogatikov, KiVITechnologies Ltd, 2017
 */

public class NinePatchBuilder
{
	private static HashMap<Object, NinePatchDrawable> cache;
	
	static
	{
		cache = new HashMap<Object, NinePatchDrawable>();
	}
	
	private ArrayList<Integer> dataX, dataY;
	private float[] padding;
	private Resources res;
	private String srcName;
	
	public NinePatchBuilder(Resources ress)
	{
		this.dataX = new ArrayList<Integer>();
		this.dataY = new ArrayList<Integer>();
	
		this.res = ress;
	}
	
	public void addSegmentX(int s, int e)
	{
		addSegment(dataX, s, e);
	}
	
	public void addSegmentY(int s, int e)
	{
		addSegment(dataY, s, e);
	}
	
	private void addSegment(ArrayList<Integer> segments, int s, int e)
	{
		while(s < e)
		{
			if(segments.size() > s)
				segments.set(s, 1);
			else
				segments.add(1);
			s++;
		}
	}
	
	public void removeSegmentX(int s, int e)
	{
		removeSegment(dataX, s, e);
	}
	
	public void removeSegmentY(int s, int e)
	{
		removeSegment(dataY, s, e);
	}
	
	private void removeSegment(ArrayList<Integer> segments, int s, int e)
	{
		while(s < e && segments.size() > s)
		{
			segments.set(s,  0);
			s++;
		}
	}

	private ArrayList<Integer> getSegments(ArrayList<Integer> data)
	{
		ArrayList<Integer> pos = new ArrayList<Integer>();
		
		for(int x = 1; x < dataX.size(); x++)
		{
			if(dataX.get(x) != dataX.get(x - 1))
				pos.add(x - pos.size() % 2);
		}
		
		return pos;
	}
	
	private byte[] prepareChunk(int width, int height)
	{
		ArrayList<Integer> segmentsX = getSegments(dataX),
				   		   segmentsY = getSegments(dataY);
		int dX = dataX.size(), dY = dataY.size(),
			r = Math.max(1, segmentsX.size()) * Math.max(1, segmentsY.size());
		
		ByteBuffer buffer = ByteBuffer.allocate(32 + (dX + dY) * 8 + r * 4);
		
		//wasDeserialized property
		buffer.put((byte)1);
		//count of XDivs
		buffer.put((byte)(dX * 2));
		//count of YDivs
		buffer.put((byte)(dY * 2));
		//count of regions
		buffer.put((byte)(r));
		
		//Magic 8 bytes
		buffer.putInt(0);
		buffer.putInt(0);
		
		//paddings
		buffer.putInt((int)(padding[0] * width));
		buffer.putInt((int)(padding[1] * height));
		buffer.putInt((int)(padding[2] * width));
		buffer.putInt((int)(padding[3] * height));
		
		//Magic 4 bytes
		buffer.putInt(0);
		
		//XDivs 
		for(int x : segmentsX)
		{
			buffer.putInt(x);
		}
		
		//YDivs 
		for(int y : segmentsY)
		{
			buffer.putInt(y);
		}
		
		//Regions
		for(int i = 0; i < r; i++)
		{
			buffer.putInt(1);
		}
		
		return buffer.array();
	}
	
	public Drawable build(Bitmap source)
	{
		if(cache.containsKey(source))
		{
			return cache.get(source);
		}
		
		byte[] chunk = prepareChunk(source.getWidth(), source.getHeight());
		Rect paddingRect = new Rect((int)padding[0] * source.getWidth(), (int)padding[1] * source.getHeight(), (int)padding[2] * source.getWidth(), (int)padding[3] * source.getWidth());
		
		if(srcName == null || srcName.isEmpty())
			srcName = "NinePatch@" + chunk.hashCode();
		
		NinePatchDrawable npd = new NinePatchDrawable(res, source, chunk, paddingRect, srcName);	
		cache.put(source, npd);
		return npd;
	}
	
	public Drawable build(int resId, int width, int height)
	{
		return build(ResourcesUtils.getDrawable(res, resId), width, height);
	}
	
	public Drawable build(Drawable source, int width, int height)
	{
		if(cache.containsKey(source))
		{
			return cache.get(source);
		}
		
		if(source instanceof NinePatchDrawable)
		{
			return source;
		}
		
		if(source instanceof BitmapDrawable)
		{
			return build(((BitmapDrawable)source).getBitmap());
		}
		
		if(source instanceof DrawableContainer)
		{
			try
			{
				DrawableContainerState src_dcs = (DrawableContainerState)((DrawableContainer)source).getConstantState();
				Drawable[] src = src_dcs.getChildren();
			
				DrawableContainer result = (DrawableContainer)source.getClass().newInstance();
				DrawableContainerState res_dcs = (DrawableContainerState)result.getConstantState();
				
				for(Drawable drawable : src)
				{
					res_dcs.addChild(build(drawable, width, height));
				}
				
				return result;
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas can = new Canvas(bmp);
		source.setBounds(0, 0, width, height);
		source.draw(can);
		return build(bmp);
	}
	
	public NinePatchBuilder reset()
	{
		dataX.clear();
		dataY.clear();
		padding[0] = padding[1] = padding[2] = padding[3] = 0;
		srcName = null;
		return this;
	}
	
	public NinePatchBuilder setName(String name)
	{
		srcName = name;
		return this;
	}
	
	public NinePatchBuilder setPadding(RectF padding)
	{
		return setPadding(padding.left, padding.top, padding.right, padding.bottom);
	}
	
	public NinePatchBuilder setPadding(float left, float top, float right, float bottom)
	{
		padding[0] = Math.min(left, 1.0f);
		padding[1] = Math.min(top, 1.0f);
		padding[2] = Math.min(right, 1.0f);
		padding[3] = Math.min(bottom, 1.0f);
		return this;
	}
}