package net.kivitechnologies.SupportLibrary.app;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import net.kivitechnologies.SupportLibrary.graphics.Typeface;
import net.kivitechnologies.SupportLibrary.utils.ColorUtils;
import net.kivitechnologies.SupportLibrary.utils.FilesUtils;
import net.kivitechnologies.SupportLibrary.utils.ScreenUtils;
import net.kivitechnologies.SupportLibrary.view.MarqueeTextView;
import net.kivitechnologies.SupportLibrary.view.PromptDialog;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
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

public class FileChooser extends Activity
{
	private static String INDEX_OF_STYLE = "indexOfStyle",
						  IS_DIR_CHOOSING = "isDirChoosing",
						  INITIAL_DIRECTORY_PATH = "pathOfInitialDirectory";
	
	private static ArrayList<Style> styles = new ArrayList<Style>();
	private static String DEFAULT_INITIAL_PATH = Environment.getExternalStorageDirectory().getParent();
	
	private ActionBarDrawer actionBarDrawer;
	private RelativeLayout rootContainer;
	private DirectoryContentView directoryContentView;
	private ManagementToolsView managementToolsView;
	private Style style;
	private boolean chooseDirectory, createDirectory;
	private File currentDirectory;
	private PromptDialog prompt; 
	
	public void onCreate(Bundle savedInstanceState)
	{
		try
		{
			super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		style = styles.get(intent.getIntExtra(INDEX_OF_STYLE, 0));
		chooseDirectory = intent.getBooleanExtra(IS_DIR_CHOOSING, false);
		currentDirectory = new File(intent.getStringExtra(INITIAL_DIRECTORY_PATH));
		
		runOnUiThread(new MainUIApplier());
		runOnUiThread(new ActionBarApplier());
		}catch(Throwable t)
		{
			Toast.makeText(this, t.toString(), Toast.LENGTH_LONG).show();
		}
	}
	
	public static Intent startChoosingFile(android.app.Activity parent, String initialPath, Style style)
	{
		if(initialPath == null)
			initialPath = DEFAULT_INITIAL_PATH;
		
		File initialDirectory = new File(initialPath);
		if(!initialDirectory.exists())
			initialDirectory.mkdirs();
		
		if(!styles.contains(style))
			styles.add(style);
		
		Intent intent = new Intent(parent, FileChooser.class);
		intent.putExtra(INDEX_OF_STYLE, styles.indexOf(style));
		intent.putExtra(IS_DIR_CHOOSING, false);
		intent.putExtra(INITIAL_DIRECTORY_PATH, initialPath);
		
		return intent;
	}
	
	public static Intent startChoosingDirectory(android.app.Activity parent, String initialPath, Style style)
	{
		if(initialPath == null)
			initialPath = DEFAULT_INITIAL_PATH;
		
		File initialDirectory = new File(initialPath);
		if(!initialDirectory.exists())
			initialDirectory.mkdirs();
		
		if(!styles.contains(style))
			styles.add(style);
		
		Intent intent = new Intent(parent, FileChooser.class);
		intent.putExtra(INDEX_OF_STYLE, styles.indexOf(style));
		intent.putExtra(IS_DIR_CHOOSING, true);
		intent.putExtra(INITIAL_DIRECTORY_PATH, initialPath);
		
		return intent;
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
		public abstract Drawable getUpFolderIcon();
		public abstract Drawable getNewFileIcon();
		public abstract Drawable getNewFolderIcon();
		
		public Typeface getTypeface()
		{
			return new Typeface(android.graphics.Typeface.DEFAULT);
		}
	}
	
	private class MainUIApplier implements Runnable
	{
		public void run()
		{
			try{
			rootContainer = new RelativeLayout(FileChooser.this);
			directoryContentView = new DirectoryContentView(FileChooser.this, FileChooser.this.style);
			directoryContentView.load(currentDirectory, chooseDirectory);
			RelativeLayout.LayoutParams directoryContentViewParams = new RelativeLayout.LayoutParams(-1, -1);
			directoryContentViewParams.setMargins(0, 0, 0, ScreenUtils.dpToPx(FileChooser.this, 55));
			
			managementToolsView = new ManagementToolsView(FileChooser.this, directoryContentView, FileChooser.this.style);
			RelativeLayout.LayoutParams managementToolsViewParams = new RelativeLayout.LayoutParams(-1, -2);
			managementToolsViewParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			
			rootContainer.setPadding(0, ScreenUtils.dpToPx(FileChooser.this, 54), 0, 0);
			rootContainer.addView(directoryContentView, directoryContentViewParams);
			rootContainer.addView(managementToolsView, managementToolsViewParams);
			setContentView(rootContainer);
			
			prompt = new PromptDialog(FileChooser.this, new PromptDialog.OnSubmitListener()
			{
				public void onTextSubmit(CharSequence text)
				{
					if(createDirectory)
					{
						try
						{
							currentDirectory = new File(currentDirectory, text.toString());
							currentDirectory.mkdirs();
							
							directoryContentView.load(currentDirectory, chooseDirectory);
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
							new File(currentDirectory, text.toString()).createNewFile();
							directoryContentView.invalidate();
						}
						catch(IOException ioe)
						{
							Toast.makeText(FileChooser.this, ioe.toString(), Toast.LENGTH_LONG).show();
						}
					}
				}
			});
			}
			catch(Throwable t)
			{
				Toast.makeText(FileChooser.this, t.toString(), Toast.LENGTH_LONG).show();
			}
		}
	}
	
	private class ActionBarApplier implements Runnable
	{
		public void run()
		{
			try{
			ResourcesDelegate resourcesDelegate = new ResourcesDelegate()
			{
				public int getColor(int resId)
				{
					if(resId == ActionBarDrawer.ATTR_BACKGROUND_COLOR)
						return FileChooser.this.style.getActionBarBackgroundColor();
					else if(resId == ActionBarDrawer.ATTR_DEFAULT_ITEM_COLOR || resId == ActionBarDrawer.ATTR_PRESSED_ITEM_COLOR)
						return FileChooser.this.style.getActionBarItemColor();
					else if(resId == ActionBarDrawer.ATTR_SUBTITLE_COLOR || resId == ActionBarDrawer.ATTR_TITLE_COLOR)
						return FileChooser.this.style.getActionBarTextColor();
					
					return ColorUtils.parse("#FFFFFF");
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
					return "";
				}
			};
			
			actionBarDrawer = new ActionBarDrawer(FileChooser.this, resourcesDelegate);
			actionBarDrawer.setDisplayUpButton(true);
			actionBarDrawer.setDisplaySubtitle(true);
			actionBarDrawer.setTitle(FileChooser.this.getTitle());
			actionBarDrawer.setTitleTypeface(FileChooser.this.style.getTypeface().getNormal());
			actionBarDrawer.setSubtitle(currentDirectory.getAbsolutePath());
			actionBarDrawer.setSubtitleTypeface(FileChooser.this.style.getTypeface().getItalic());
			
			actionBarDrawer.addTool(new NewFileTool());
			actionBarDrawer.addTool(new NewDirectoryTool());
			
			LinearLayout actionBarRoot = new LinearLayout(FileChooser.this);
			actionBarRoot.addView(actionBarDrawer, actionBarDrawer.getLayoutParams());
			
			int shadowColor = ColorUtils.parse("#777777");
			ImageView shadowView = new ImageView(FileChooser.this);
			Drawable shadowDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{ shadowColor, ColorUtils.setAlpha(shadowColor, 0x0) });
			shadowView.setImageDrawable(shadowDrawable);
			actionBarRoot.addView(shadowView, -1, ScreenUtils.dpToPx(FileChooser.this, 10));
			
			actionBarRoot.setOrientation(LinearLayout.VERTICAL);
			addContentView(actionBarRoot, new LinearLayout.LayoutParams(-1, -2));
			
			}
			catch(Throwable t)
			{
				Toast.makeText(FileChooser.this, t.toString(), Toast.LENGTH_LONG).show();
			}
		}
	}
	
	private class NewFileTool extends ActionBarDrawer.Tool
	{
		public Drawable getIcon() 
		{
			return FileChooser.this.style.getNewFileIcon();
		}

		public void onClick()
		{
			createDirectory = false;
			FileChooser.this.prompt.setTitle(FileChooser.this.style.getNewFileLabel());
			FileChooser.this.prompt.show();
		}
	};
	
	private class NewDirectoryTool extends ActionBarDrawer.Tool
	{
		public Drawable getIcon() 
		{
			return FileChooser.this.style.getNewFolderIcon();
		}

		public void onClick()
		{
			createDirectory = true;
			FileChooser.this.prompt.setTitle(FileChooser.this.style.getNewFolderLabel());
			FileChooser.this.prompt.show();
		}
	};
	
	private static class DirectoryContentView extends ListView implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener
	{
		private List<File> currentDirectoryContent;
		private File currentDirectory;
		private Style style;
		private ListAdapter listAdapter;
		private boolean isDirChoosing;
		
		public DirectoryContentView(Context ctx, Style style_)
		{
			super(ctx);
			this.style = style_;
			
			listAdapter = new ListAdapter(ctx);
			setAdapter(listAdapter);
			setOnItemClickListener(this);
			setOnItemLongClickListener(this);
		}
		
		public void load(File file, boolean chooseDir)
		{
			this.isDirChoosing = chooseDir;
			
			currentDirectory = file;
			currentDirectoryContent = listAdapter.load(currentDirectory, isDirChoosing);
			setAdapter(listAdapter);
			invalidate();
		}
		
		public void onItemClick(AdapterView<?> adapterView, View itemView, int position, long id)
		{
			if(((ListItem)itemView).usedUpMode())
				load(currentDirectory.getParentFile(), isDirChoosing);
			else
			{
				Toast.makeText(getContext(), position+":", Toast.LENGTH_SHORT).show();
				File file = currentDirectoryContent.get(position == 0 ? 0 : position - 1);
				if(file.isDirectory())
					load(file, isDirChoosing);
				else
					finish(file);
			}
		}
		
		public boolean onItemLongClick(AdapterView<?> adapterView, View itemView, int position, long id)
		{
			if(!((ListItem)itemView).usedUpMode())
			{
				showFileOptions(currentDirectoryContent.get(position == 0 ? 0 : position - 1));
			}
			
			return true;
		}
		
		public File getCurrentDirectory()
		{
			return currentDirectory;
		}
		
		public void finish(File file)
		{
			Intent intent = new Intent();
			intent.setData(Uri.fromFile(file));
			((Activity)getContext()).setResult(RESULT_OK, intent);
			((Activity)getContext()).finish();
		}
		
		private boolean rename;
		
		private void showFileOptions(final File file)
		{
			final PromptDialog prompt = new PromptDialog(getContext(), new PromptDialog.OnSubmitListener()
			{
				public void onTextSubmit(CharSequence text)
				{
					if(rename)
					{
						file.renameTo(new File(file.getParent(), text.toString()));
						load(currentDirectory, isDirChoosing);
					}
					else
					{
						try
						{
							File newDirectory = FilesUtils.moveTo(file, new File(file.getParent(), text.toString()));
							load(newDirectory, isDirChoosing);
						}
						catch(IOException ioe)
						{
							Toast.makeText(getContext(), ioe.toString(), Toast.LENGTH_LONG).show();
						}
					}
				}
			});
			
			AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
			builder.setItems(new String[]{ "Rename", "Move", "Delete" }, new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface arg0, int which)
				{
					if(which == 0)
					{
						rename = true;
						prompt.show();
					}
					else if(which == 1)
					{
						rename = false;
						prompt.show();
					}
					else
						FilesUtils.delete(file);
					
					load(currentDirectory, isDirChoosing);
				}
			}); 
			
			builder.create().show();
		}
		
		private class ListItem extends RelativeLayout
		{
			public static final int MODE_FILE = 0, MODE_DIRECTORY = 1, MODE_UP = 2;
			
			private ImageView icon;
			private TextView name, size, lastModified;
			private int itemMode;
			
			@SuppressLint("SimpleDateFormat")
			public ListItem(Context ctx, File file, int mode)
			{
				super(ctx);
				this.itemMode = mode;
				
				int dp44 = ScreenUtils.dpToPx(ctx, 44),
					dp5 = ScreenUtils.dpToPx(ctx, 5);
				
				setPadding(dp5, dp5, dp5, dp5);
				
				this.icon = new ImageView(ctx);
				if(mode == MODE_DIRECTORY)
					this.icon.setImageDrawable(DirectoryContentView.this.style.getFolderIcon());
				else if(mode == MODE_UP)
					this.icon.setImageDrawable(DirectoryContentView.this.style.getUpFolderIcon());
				else
					this.icon.setImageDrawable(DirectoryContentView.this.style.getFileIcon());
				
				LayoutParams iconParams = new LayoutParams(dp44, dp44);
				iconParams.addRule(ALIGN_PARENT_LEFT);
				addView(this.icon, iconParams);
				
				this.name = new MarqueeTextView(ctx);
				this.name.setText(file.getName());
				this.name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
				this.name.setTextColor(DirectoryContentView.this.style.getLabelsTextColor());
				this.name.setTypeface(DirectoryContentView.this.style.getTypeface().getNormal());
				
				RelativeLayout.LayoutParams nameParams = new RelativeLayout.LayoutParams(-2, -2);
				nameParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				nameParams.setMargins(iconParams.width + dp5, 0, 0, 0);
				addView(name, nameParams);
				
				if(!file.isDirectory())
				{
					this.size = new TextView(ctx);
					this.size.setText(FilesUtils.getFileSize(file));
					this.size.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
					this.size.setTextColor(DirectoryContentView.this.style.getLabelsTextColor());
					this.size.setTypeface(DirectoryContentView.this.style.getTypeface().getItalic());
					
					RelativeLayout.LayoutParams sizeParams = new RelativeLayout.LayoutParams(-2, -2);
					sizeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
					sizeParams.setMargins(iconParams.width + dp5, 0, 0, 0);
					addView(this.size, sizeParams);
				}
				
				if(mode != MODE_UP)
				{
					long lm = file.lastModified();
					SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
					
					this.lastModified = new TextView(ctx);
					this.lastModified.setText(sdf.format(new Date(lm)));
					this.lastModified.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
					this.lastModified.setTextColor(style.getLabelsTextColor());
					this.lastModified.setTypeface(style.getTypeface().getNormal());
					
					RelativeLayout.LayoutParams lastModifiedParams = new RelativeLayout.LayoutParams(-2, -2);
					lastModifiedParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
					lastModifiedParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
					addView(this.lastModified, lastModifiedParams);
				}
			}
			
			public boolean usedUpMode()
			{
				return itemMode == MODE_UP;
			}
		}

		private class ListAdapter extends ArrayAdapter<ListView>
		{
			private File currentInitialDirectory;
			private Comparator<File> filesSorter;
			private List<ListItem> items;
			
			public ListAdapter(Context context)
			{
				super(context, 0);
				items = new ArrayList<ListItem>();
				
				filesSorter = new Comparator<File>()
				{
					public int compare(File A, File B)
					{
						if(A.getAbsolutePath().equals(currentInitialDirectory.getParent()))
							return -1;
						else if(B.getAbsolutePath().equals(currentInitialDirectory.getParent()))
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
				};
			}
			
			public int getCount()
			{
				return items.size();
			}
			
			public List<File> load(File initialDirectory, boolean removeFiles)
			{
				List<File> files = Arrays.asList(initialDirectory.listFiles());
				
				if(removeFiles)
				{
					for(File file : files)
					{
						if(!file.isDirectory())
							files.remove(file);
					}
				}
				
				this.currentInitialDirectory = initialDirectory;
				Collections.sort(files, filesSorter);
				
				items.clear();
				
				if(!initialDirectory.getAbsolutePath().equals(DEFAULT_INITIAL_PATH))
					items.add(new ListItem(getContext(), initialDirectory.getParentFile(), ListItem.MODE_UP));
				
				for(int i = 0; i < files.size(); i++)
				{
					File file = files.get(i);
					items.add(new ListItem(getContext(), file, file.isDirectory() ? ListItem.MODE_DIRECTORY : ListItem.MODE_FILE));
				}
				
				return files;
			}
			
			public View getView(int position, View convertView, ViewGroup parent)
			{
				return items.get(position);
			}
		}


	}
	
	private static class ManagementToolsView extends LinearLayout implements View.OnClickListener
	{
		private Button submit, cancel;
		private DirectoryContentView content;
		
		public ManagementToolsView(Context ctx, DirectoryContentView contentView, Style style)
		{
			super(ctx);
			content = contentView;
			
			LayoutParams toolsParams = new LayoutParams(-1, ScreenUtils.dpToPx(ctx, 55), 1.0f);
			
			submit = new Button(ctx);
			submit.setText(style.getSubmitLabel());
			submit.setTypeface(style.getTypeface().getBold());
			submit.setTextColor(style.getLabelsTextColor());
			submit.setOnClickListener(this);
			addView(submit, toolsParams);
			if(!contentView.isDirChoosing)
				submit.setVisibility(View.GONE);
			
			cancel = new Button(ctx);
			cancel.setText(style.getCancelLabel());
			cancel.setTypeface(style.getTypeface().getBold());
			submit.setTextColor(style.getLabelsTextColor());
			cancel.setOnClickListener(this);
			addView(cancel, toolsParams);
		}
		
		public void onClick(View view)
		{
			if(view == submit)
			{
				finish(content.getCurrentDirectory());
			}
			else if(view == cancel)
			{
				finish(null);
			}
		}
		
		public void finish(File file)
		{
			Intent intent = new Intent();
			if(file != null)
				intent.setData(Uri.fromFile(file));
			((Activity)getContext()).setResult(file == null ? RESULT_CANCELED : RESULT_OK, intent);
			((Activity)getContext()).finish();
		}
	}
}
