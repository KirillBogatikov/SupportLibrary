package net.kivitechnologies.SupportLibrary.utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import net.kivitechnologies.SupportLibrary.app.ActionBarDrawer;
import net.kivitechnologies.SupportLibrary.app.ActionBarDrawer.Tool;
import net.kivitechnologies.SupportLibrary.app.ResourcesDelegate;
import net.kivitechnologies.SupportLibrary.graphics.Typeface;
import net.kivitechnologies.SupportLibrary.view.MarqueeTextView;
import net.kivitechnologies.SupportLibrary.view.PromptDialog;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public final class FileChooser extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener
{
	public static String CHECKED_FILE_PATH = "KiVITechnologies.SupportLibrary.UsefulUtilities.FileChooser-CHECKED_FILE_PATH";
	private static ArrayList<Style> styles = new ArrayList<Style>();
	
	private ActionBarDrawer actionBarDrawer;
	private Style style;
	private PromptDialog inputNameDialog;
	private boolean createDirectory, isDirChoosing;
	private RelativeLayout root;
	private LinearLayout tools;
	private Button cancel, ok;
	private ListView listView;
	private ListAdapter listAdapter;
	private File currentFile;
	private List<File> currentFiles;
	
	public static Intent chooseFile(Context ctx, String path, Style style)
	{
		Intent intent = new Intent(ctx, FileChooser.class);
		intent.putExtra("isDirectoryChoosing", false);
		intent.putExtra("currentPath", path);
		if(!styles.contains(style))
		{
			styles.add(style);
		}
		intent.putExtra("styleId", styles.indexOf(style));
		return intent;
	}
	
	public static Intent chooseDirectory(Context ctx, String path, Style style)
	{
		Intent intent = new Intent(ctx, FileChooser.class);
		intent.putExtra("isDirectoryChoosing", true);
		intent.putExtra("currentPath", path);
		if(!styles.contains(style))
		{
			styles.add(style);
		}
		intent.putExtra("styleId", styles.indexOf(style));
		return intent;
	}
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		try
		{
			Intent intent = getIntent();
			isDirChoosing = intent.getBooleanExtra("isDirectoryChoosing", false);
			String path = intent.getStringExtra("currentPath");
			if(path == null)
				currentFile = Environment.getExternalStorageDirectory();
			else
				currentFile = new File(path);
			
			if(!currentFile.exists() || !currentFile.isDirectory())
				finish();
			
			currentFiles = prepare(currentFile.listFiles());
			
			style = styles.get(intent.getIntExtra("styleId", 0));
			runOnUiThread(new Runnable()
			{
				public void run()
				{
					root = new RelativeLayout(FileChooser.this);
					listView = new ListView(FileChooser.this);
					RelativeLayout.LayoutParams listViewParams = new RelativeLayout.LayoutParams(-1,-1);
					listViewParams.setMargins(0, 0, 0, ScreenUtils.dpToPx(FileChooser.this, 55));
					root.addView(listView, listViewParams);
					
					tools = new LinearLayout(FileChooser.this);
					tools.setOrientation(LinearLayout.HORIZONTAL);
					RelativeLayout.LayoutParams toolsParams = new RelativeLayout.LayoutParams(-1, -2);
					toolsParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
					root.addView(tools, toolsParams);
					
					LinearLayout.LayoutParams toolParams = new LinearLayout.LayoutParams(-1, ScreenUtils.dpToPx(FileChooser.this, 55), 1.0f);
					
					cancel = new Button(FileChooser.this);
					cancel.setOnClickListener(FileChooser.this);
					cancel.setText(style.getCancelLabel());
					cancel.setTypeface(style.getTypeface().getBold());
					tools.addView(cancel, toolParams);
					
					if(isDirChoosing)
					{
						ok = new Button(FileChooser.this);
						ok.setOnClickListener(FileChooser.this);
						ok.setText(style.getSubmitLabel());
						ok.setTypeface(style.getTypeface().getBold());
						tools.addView(ok, toolParams);
					}
					
					root.setPadding(0, ScreenUtils.dpToPx(FileChooser.this, 54), 0, 0);
					listView.setOnItemClickListener(FileChooser.this);
					listAdapter = new ListAdapter(FileChooser.this);
					listView.setAdapter(listAdapter);
					listAdapter.setItems(currentFiles);
					setContentView(root);
				}
			});
			runOnUiThread(new ActionBarApplier());
			
		}
		catch(Throwable t)
		{
			Toast.makeText(this,  t.toString(), Toast.LENGTH_LONG).show();
		}
	}
	
	public void onItemClick(AdapterView<?> parent, View item, int position, long id)
	{
		File file = currentFiles.get(position);
		
		if(file.isDirectory())
		{
			currentFile = file;
			currentFiles = prepare(file.listFiles());
			
			listAdapter.setItems(currentFiles);
			listView.setAdapter(listAdapter);
			actionBarDrawer.setSubtitle(currentFile.getAbsolutePath());
		}
		else
		{
			finish(file.getAbsolutePath());
		}
	}
	
	public void onClick(View view)
	{
		if(view == ok)
		{
			finish(currentFile.getAbsolutePath());
		}
		else if(view == cancel)
		{
			finish(null);
		}
	}
	
	public void finish(String path)
	{
		Intent result = new Intent();
		result.putExtra(CHECKED_FILE_PATH, path);
		setResult(RESULT_OK, result);
		finish();
	}
	
	public void onBackPressed()
	{
		if(currentFile.getAbsolutePath().equals(Environment.getExternalStorageDirectory().getParent()))
			super.onBackPressed();
		else
			onItemClick(listView, null, 0, 0);
	}
	
	private List<File> prepare(File[] files)
	{
		List<File> result = new ArrayList<File>();
		if(!currentFile.getAbsolutePath().equals(FilesUtils.getStoragePath()))
			result.add(currentFile.getParentFile());
		
		List<File> filesList = null;
		
		if(isDirChoosing)
		{
			filesList = new ArrayList<File>();
			for(File file : files)
			{
				if(file.isDirectory())
					filesList.add(file);
			}
			
			Collections.sort(filesList, new Comparator<File>()
			{
				public int compare(File A, File B)
				{
					return A.getName().compareTo(B.getName());
				}
			});
		}
		else
		{
			filesList = Arrays.asList(files);
			Collections.sort(filesList, new Comparator<File>()
			{
				public int compare(File A, File B)
				{
					if(A.isDirectory())
					{
						if(B.isDirectory())
							return A.getName().compareTo(B.getName());
						else
							return -1;
					}
					else
					{
						if(B.isDirectory())
							return 1;
						else
							return A.getName().compareTo(B.getName());
					}
				}
			});
		}
		
		result.addAll(filesList);
		
		return result;
	}
	
	private boolean isUpDirectory(String filePath)
	{
		return currentFile.getParent().equals(filePath);
	}
	
	public static abstract class Style
	{
		public abstract String getSubmitLabel();
		public abstract String getCancelLabel();
		public abstract String getNewFileLabel();
		public abstract String getNewFolderLabel();
		
		public abstract int getActionBarBackgroundColor();
		public abstract int getActionBarTextColor();
		public abstract int getLabelsTextColor();
		public int getActionBarItemColor()
		{
			return ColorUtils.parse("#FFFFFF");
		}
		
		public abstract Drawable getFileIcon();
		public abstract Drawable getFolderIcon();
		public abstract Drawable getUpFolder();
		public abstract Drawable getNewFileIcon();
		public abstract Drawable getNewFolderIcon();
		
		public Typeface getTypeface()
		{
			return new Typeface(android.graphics.Typeface.DEFAULT);
		}
	}
	
	private static class ActionBarResources extends ResourcesDelegate
	{
		private Style style;
		
		public ActionBarResources(Style style_)
		{
			style = style_;
		}
		
		public int getColor(int resId)
		{
			if(resId == ActionBarDrawer.ATTR_BACKGROUND_COLOR)
				return style.getActionBarBackgroundColor();	
			else if(resId == ActionBarDrawer.ATTR_DEFAULT_ITEM_COLOR)
				return style.getActionBarItemColor();
			else if(resId == ActionBarDrawer.ATTR_PRESSED_ITEM_COLOR)
				return style.getActionBarItemColor();
			else if(resId == ActionBarDrawer.ATTR_SUBTITLE_COLOR)
				return style.getActionBarTextColor();
			else 
				return style.getActionBarTextColor();
		}
		
		public int getDimensionPixelSize(int resId)
		{
			return 0;
		}
		
		public Drawable getDrawable(int resId)
		{
			return null;
		}
		
		public String getString(int resId)
		{
			return null;
		}
	}
	
	private class ActionBarApplier implements Runnable
	{
		public void run()
		{
			actionBarDrawer = new ActionBarDrawer(FileChooser.this, new ActionBarResources(FileChooser.this.style));
			actionBarDrawer.setTitle(getTitle());
			actionBarDrawer.setTitleTypeface(style.getTypeface().getNormal());
			actionBarDrawer.setDisplayUpButton(true);
			actionBarDrawer.setDisplaySubtitle(true);
			actionBarDrawer.setSubtitle(currentFile.getAbsolutePath());
			actionBarDrawer.setSubtitleTypeface(style.getTypeface().getItalic());
			
			LinearLayout actionBarRoot = new LinearLayout(FileChooser.this);
			actionBarRoot.addView(actionBarDrawer, actionBarDrawer.getLayoutParams());
			
			int shadowColor = ColorUtils.parse("#777777");
			ImageView shadowView = new ImageView(FileChooser.this);
			Drawable shadowDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{ shadowColor, ColorUtils.setAlpha(shadowColor, 0x0) });
			shadowView.setImageDrawable(shadowDrawable);
			actionBarRoot.addView(shadowView, -1, ScreenUtils.dpToPx(FileChooser.this, 10));
			
			actionBarRoot.setOrientation(LinearLayout.VERTICAL);
			addContentView(actionBarRoot, new LinearLayout.LayoutParams(-1, -2));
			
			inputNameDialog = new PromptDialog(FileChooser.this, new PromptDialog.OnSubmitListener()
			{
				public void onTextSubmit(CharSequence text)
				{	
					if(createDirectory)
					{
						try
						{
							currentFile = new File(currentFile, text.toString());
							currentFile.mkdirs();
							currentFiles = prepare(currentFile.listFiles());
							
							listAdapter.setItems(currentFiles);
							listView.setAdapter(listAdapter);
							//listView.invalidate();
						}
						catch(Exception ioe)
						{
							Toast.makeText(FileChooser.this, ioe.toString(), Toast.LENGTH_LONG).show();
						}
					}
					else
					{
						try
						{
							new File(currentFile, text.toString()).createNewFile();
						}
						catch(IOException ioe)
						{
							Toast.makeText(FileChooser.this, ioe.toString(), Toast.LENGTH_LONG).show();
						}
					}
				}
			});
			
			Tool newFile = new Tool()
			{
				public Drawable getIcon()
				{
					return style.getNewFileIcon();
				}

				public void onClick()
				{
					try
					{
						inputNameDialog.setTitle(style.getNewFileLabel());
						createDirectory = false;
						inputNameDialog.show();
					}
					catch(Throwable t)
					{
						Toast.makeText(FileChooser.this, t.toString(), Toast.LENGTH_LONG).show();
					}
				}
			};
			
			Tool newFolder = new Tool()
			{
				public Drawable getIcon()
				{
					return style.getNewFolderIcon();
				}

				public void onClick()
				{
					inputNameDialog.setTitle(style.getNewFolderLabel());
					createDirectory = true;
					inputNameDialog.show();
				}
			};

			actionBarDrawer.addTool(newFile);
			actionBarDrawer.addTool(newFolder);
		}
	}
	
	private class ListAdapter extends ArrayAdapter<ListItem>
	{
		private List<ListItem> items;
		
		public ListAdapter(Context context)
		{
			super(context, 0);
			items = new ArrayList<ListItem>();
		}
		
		public void setItems(List<File> files)
		{
			Collections.sort(files, new Comparator<File>()
			{
				@Override
				public int compare(File A, File B)
				{
					if(A.getAbsolutePath().equals(currentFile.getParent()))
						return -1;
					else if(B.getAbsolutePath().equals(currentFile.getParent()))
						return 1;
			
					if(A.isDirectory())
					{
						if(!B.isDirectory())
						{
							return -1;
						}
					}
					else if(B.isDirectory())
					{
						return 1;
					}
					
					return A.getName().compareTo(B.getName());
				}
				
			});
			items = new ArrayList<ListItem>();
			for(File file : files)
			{
				items.add(new ListItem(FileChooser.this, file));
			}
		}
		
		public int getCount()
		{
			return items.size();
		}
		
		public View getView(int position, View convertView, ViewGroup parent)
		{
			return items.get(position);
		}
	}
	
	
	private class ListItem extends RelativeLayout
	{
		private ImageView icon;
		private TextView name, size, lastModified;
		
		public ListItem(Context context, File file)
		{
			super(context);
			int dp5 = ScreenUtils.dpToPx(context, 5);
			setPadding(dp5, dp5, dp5, dp5);
			
			icon = new ImageView(context);
			if(file.isDirectory())
			{
				String parent = currentFile.getParent();
				if(file.getAbsolutePath().equals(parent))
					icon.setImageDrawable(style.getUpFolder());
				else
					icon.setImageDrawable(style.getFolderIcon());
			}
			else
				icon.setImageDrawable(style.getFileIcon());
			int dp48 = ScreenUtils.dpToPx(context, 48),
				dp2 = ScreenUtils.dpToPx(context, 2); 
			addView(icon, dp48, dp48);
			
			name = new MarqueeTextView(context);
			name.setText(file.getName());
			name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
			name.setTextColor(style.getLabelsTextColor());
			name.setTypeface(style.getTypeface().getNormal());
			
			RelativeLayout.LayoutParams nameParams = new RelativeLayout.LayoutParams(-2, -2);
			nameParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			nameParams.setMargins(dp48 + dp5, 0, 0, 0);
			addView(name, nameParams);
			
			if(!file.isDirectory())
			{
				size = new TextView(context);
				size.setPadding(dp2, 0, dp2, 0);
				size.setText(FilesUtils.getFileSize(file));
				size.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				size.setTextColor(style.getLabelsTextColor());
				size.setTypeface(style.getTypeface().getItalic());
				
				RelativeLayout.LayoutParams size_params = new RelativeLayout.LayoutParams(-2, -2);
				size_params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				size_params.setMargins(dp48 + dp5, 0, 0, 0);
				addView(size, size_params);
			}
			
			if(!FileChooser.this.isUpDirectory(file.getAbsolutePath()))
			{
				lastModified = new TextView(context);
				lastModified.setPadding(dp2, 0, dp2, 0);
				lastModified.setText(getFileLastModified(file));
				lastModified.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
				lastModified.setTextColor(style.getLabelsTextColor());
				lastModified.setTypeface(style.getTypeface().getNormal());
				
				RelativeLayout.LayoutParams lastModified_params = new RelativeLayout.LayoutParams(-2, -2);
				lastModified_params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				lastModified_params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				addView(lastModified, lastModified_params);
			}
		}
		
		@SuppressLint("SimpleDateFormat")
		private String getFileLastModified(File file)
		{
			long lm = file.lastModified();
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
			return sdf.format(new Date(lm));
		}
	}
}