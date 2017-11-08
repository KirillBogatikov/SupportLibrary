package net.kivitechnologies.SupportLibrary.utils;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;

/**
 * Class for saving some data by using SharedPreferences
 * Forked from Sudoku Project
 * 
 * @author K. V. Bogatikov, KiVITechnologies Ltd, 2016 - 2017
 * @version 1.7
 */
public class SavingUtils
{
	/**
	 * Used for cache some already created objects
	 */
	private static ArrayList<SavingUtils> objects;
	
	static
	{
		objects = new ArrayList<SavingUtils>();
	}
	
	private SharedPreferences prefs;
	private Context ctx;
	
	/**
	 * Constructor
	 * 
	 * @param ctx Context for getting Shared Preferences
	 * @param name Which name of Preference will be used
	 */
	public SavingUtils(Context ctx, String name)
	{
		objects.add(this);
		
		if(name == null || name.isEmpty())
			name = ctx.getPackageName();
			
		prefs = ctx.getSharedPreferences(name, 0);
		this.ctx = ctx;
	}
	
	/**
	 * Constructor
	 * Creates SharedPreference with default name - name of Context's package
	 * 
	 * @param ctx Context for getting Shared Preferences
	 */
	public SavingUtils(Context ctx)
	{
		this(ctx, null);
	}
	
	/**
	 * Return true if this object created on ctx, false otherwise
	 * 
	 * @param ctx Context for comparing
	 * @return true if this object created on ctx, false otherwise
	 */
	public boolean isBasedOn(Context ctx)
	{
		return this.ctx.equals(ctx);
	}
	
	/**
	 * Returns int value from preferences
	 * 
	 * @param name name of value
	 * @param def  default value
	 * @return int value from preferences by given name or default value if this name not found
	 */
	public int getInt(String name, int def)
	{
		return prefs.getInt(name,  def);
	}
	
	/**
	 * Returns int value from preferences with default value - 0
	 * 
	 * @param name name of value
	 * @return int value from preferences by given name or 0 if this name not found
	 */
	public int getInt(String name)
	{
		return prefs.getInt(name, 0);
	}

	/**
	 * Returns float value from preferences
	 * 
	 * @param name name of value
	 * @param def  default value
	 * @return float value from preferences by given name or default value if this name not found
	 */
	public float getFloat(String name, float def)
	{
		return prefs.getFloat(name,  def);
	}	
	
	/**
	 * Returns float value from preferences with default value - 0.0f
	 * 
	 * @param name name of value
	 * @return float value from preferences by given name or 0.0f if this name not found
	 */
	public float getFloat(String name)
	{
		return prefs.getFloat(name,  0.0f);
	}

	/**
	 * Returns double value from preferences
	 * 
	 * @param name name of value
	 * @param def  default value
	 * @return double value from preferences by given name or default value if this name not found
	 */
	public double getDouble(String name, double def)
	{
		return (double)prefs.getFloat(name,  (float)def);
	}
	
	/**
	 * Returns double value from preferences
	 * 
	 * @param name name of value
	 * @param def  default value
	 * @return double value from preferences by given name or 0.0 if this name not found
	 */
	public double getDouble(String name)
	{
		return (double)prefs.getFloat(name, 0.0f);
	}

	/**
	 * Returns long value from preferences
	 * 
	 * @param name name of value
	 * @param def  default value
	 * @return long value from preferences by given name or default value if this name not found
	 */
	public long getLong(String name, long def)
	{
		return prefs.getLong(name, def);
	}
	
	/**
	 * Returns long value from preferences
	 * 
	 * @param name name of value
	 * @param def  default value
	 * @return long value from preferences by given name or 0L if this name not found
	 */
	public long getLong(String name)
	{
		return prefs.getLong(name, 0);
	}

	/**
	 * Returns booolean value from preferences
	 * 
	 * @param name name of value
	 * @param def  default value
	 * @return boolean value from preferences by given name or default value if this name not found
	 */
	public boolean getBoolean(String name, boolean def)
	{
		return prefs.getBoolean(name, def);
	}
	
	/**
	 * Returns boolean value from preferences
	 * 
	 * @param name name of value
	 * @param def  default value
	 * @return boolean value from preferences by given name or false if this name not found
	 */
	public boolean getBoolean(String name)
	{
		return prefs.getBoolean(name, false);
	}

	/**
	 * Returns String from preferences
	 * 
	 * @param name name of value
	 * @param def  default value
	 * @return String from preferences by given name or default value if this name not found
	 */
	public String getString(String name, String def)
	{
		return prefs.getString(name, def);
	}
	
	/**
	 * Returns String from preferences
	 * @warning You MUST be sure that this preference exists or catch null results, if you use this method
	 * 
	 * @param name name of value
	 * @return String from preferences by given name or null if this name not found
	 */
	public String getString(String name)
	{
		return prefs.getString(name, null);
	}

	/**
	 * Returns int array from preferences
	 * 
	 * @param name name of value
	 * @throws NoSuchArrayException if this preferences does ot contains array with this name
	 * @return int array from preferences by given name
	 */
	public int[] getIntArray(String name) throws NoSuchArrayException
	{
		String[] str = getString(name, "").split("|");
		if(str.length == 1 && str[0].isEmpty())
			throw new NoSuchArrayException("Array or matrix with name \"" + name + "\" not found!");
		
		int[] array = new int[str.length];
		
		for(int i = 0; i < str.length; i++)
		{
			Integer n = Integer.parseInt(str[i]);
			array[i] = n.intValue();
		}
		
		return array;
	}
	
	/**
	 * Returns float array from preferences
	 * 
	 * @param name name of value
	 * @throws NoSuchArrayException if this preferences does ot contains array with this name
	 * @return float array from preferences by given name
	 */
	public float[] getFloatArray(String name) throws NoSuchArrayException
	{
		String[] str = getString(name, "").split("|");
		if(str.length == 1 && str[0].isEmpty())
			throw new NoSuchArrayException("Array or matrix with name \"" + name + "\" not found!");
		
		float[] array = new float[str.length];
		
		for(int i = 0; i < str.length; i++)
		{
			Float n = Float.parseFloat(str[i]);
			array[i] = n.floatValue();
		}
		
		return array;
	}

	/**
	 * Returns double array from preferences
	 * 
	 * @param name name of value
	 * @throws NoSuchArrayException if this preferences does ot contains array with this name
	 * @return double array from preferences by given name
	 */
	public double[] getDoubleArray(String name) throws NoSuchArrayException
	{
		String[] str = getString(name, "").split("|");
		if(str.length == 1 && str[0].isEmpty())
			throw new NoSuchArrayException("Array or matrix with name \"" + name + "\" not found!");
		
		double[] array = new double[str.length];
		
		for(int i = 0; i < str.length; i++)
		{
			Double n = Double.parseDouble(str[i]);
			array[i] = n.doubleValue();
		}
		
		return array;
	}

	/**
	 * Returns boolean array from preferences
	 * 
	 * @param name name of value
	 * @throws NoSuchArrayException if this preferences does ot contains array with this name
	 * @return boolean array from preferences by given name
	 */
	public boolean[] getBooleanArray(String name) throws NoSuchArrayException
	{
		String[] str = getString(name, "").split("|");
		if(str.length == 1 && str[0].isEmpty())
			throw new NoSuchArrayException("Array or matrix with name \"" + name + "\" not found!");
		
		boolean[] array = new boolean[str.length];
		
		for(int i = 0; i < str.length; i++)
		{
			Boolean n = Boolean.parseBoolean(str[i]);
			array[i] = n.booleanValue();
		}
		
		return array;
	}

	/**
	 * Returns int matrix XxY from preferences
	 * 
	 * @param name name of value
	 * @param x    first dimension of matrix
	 * @param y    second dimension of matrix
	 * 
	 * @throws NoSuchArrayException if this preferences does ot contains matrix with this name
	 * @return int matrix from preferences by given name
	 */
	public int[][] getIntMatrix(String name, int x, int y) throws NoSuchArrayException
	{
		int[][] matrix = new int[x][y];
		
		String[] ints = getString(name + "_MATRIX", "").split("|");
		if(ints.length == 1 && ints[0].isEmpty())
			throw new NoSuchArrayException("Array or matrix with name \"" + name + "\" not found!");
		
		for(int i = 0; i < x; i++)
			for(int j = 0; j < y; j++)
				matrix[i][j] = Integer.parseInt(ints[i * x + j]);
		
		return matrix;
	}
	
	/**
	 * Returns float matrix XxY from preferences
	 * 
	 * @param name name of value
	 * @param x    first dimension of matrix
	 * @param y    second dimension of matrix
	 * 
	 * @throws NoSuchArrayException if this preferences does ot contains matrix with this name
	 * @return float matrix from preferences by given name
	 */
	public float[][] getFloatMatrix(String name, int x, int y) throws NoSuchArrayException
	{
		float[][] matrix = new float[x][y];
		
		String[] floats = getString(name + "_MATRIX", "").split("|");
		if(floats.length == 1 && floats[0].isEmpty())
			throw new NoSuchArrayException("Array or matrix with name \"" + name + "\" not found!");
		
		for(int i = 0; i < x; i++)
			for(int j = 0; j < y; j++)
				matrix[i][j] = Float.parseFloat(floats[i * x + j]);
		
		return matrix;
	}

	/**
	 * Returns double matrix XxY from preferences
	 * 
	 * @param name name of value
	 * @param x    first dimension of matrix
	 * @param y    second dimension of matrix
	 * 
	 * @throws NoSuchArrayException if this preferences does ot contains matrix with this name
	 * @return double matrix from preferences by given name
	 */
	public double[][] getDoubleMatrix(String name, int x, int y) throws NoSuchArrayException
	{
		double[][] matrix = new double[x][y];
		
		String[] doubles = getString(name + "_MATRIX", "").split("|");
		if(doubles.length == 1 && doubles[0].isEmpty())
			throw new NoSuchArrayException("Array or matrix with name \"" + name + "\" not found!");
		
		for(int i = 0; i < x; i++)
			for(int j = 0; j < y; j++)
				matrix[i][j] = Double.parseDouble(doubles[i * x + j]);
		
		return matrix;
	}

	/**
	 * Returns boolean matrix XxY from preferences
	 * 
	 * @param name name of value
	 * @param x    first dimension of matrix
	 * @param y    second dimension of matrix
	 * 
	 * @throws NoSuchArrayException if this preferences does ot contains matrix with this name
	 * @return boolean matrix from preferences by given name
	 */
	public boolean[][] getBooleanMatrix(String name, int x, int y) throws NoSuchArrayException
	{
		boolean[][] matrix = new boolean[x][y];
		
		String[] booleans = getString(name + "_MATRIX", "").split("|");
		if(booleans.length == 1 && booleans[0].isEmpty())
			throw new NoSuchArrayException("Array or matrix with name \"" + name + "\" not found!");
		
		for(int i = 0; i < x; i++)
			for(int j = 0; j < y; j++)
				matrix[i][j] = Boolean.parseBoolean(booleans[i * x + j]);
		
		return matrix;
	}
	
	/**
	 * Saves value
	 * 
	 * @param name name of value
	 * @param val  value
	 */
	public void putInt(String name, int val)
	{
		prefs.edit().putInt(name, val).commit();
	}
	
	/**
	 * Saves value
	 * 
	 * @param name name of value
	 * @param val  value
	 */
	public void putFloat(String name, float val)
	{
		prefs.edit().putFloat(name, val).commit();
	}
	
	/**
	 * Saves value
	 * 
	 * @param name name of value
	 * @param val  value
	 */
	public void putDouble(String name, double val)
	{
		prefs.edit().putFloat(name, (float)val).commit();
	}
	
	/**
	 * Saves value
	 * 
	 * @param name name of value
	 * @param val  value
	 */
	public void putLong(String name, long val)
	{
		prefs.edit().putLong(name, val).commit();
	}

	/**
	 * Saves value
	 * 
	 * @param name name of value
	 * @param val  value
	 */
	public void putBoolean(String name, boolean val)
	{
		prefs.edit().putBoolean(name, val).commit();
	}

	/**
	 * Saves value
	 * 
	 * @param name name of value
	 * @param val  value
	 */
	public void putString(String name, String val)
	{
		prefs.edit().putString(name, val).commit();
	}
	
	/**
	 * Saves array of int
	 * 
	 * @param name  name of value
	 * @param array aray of integers
	 */
	public void putIntArray(String name, int[] array)
	{
		StringBuilder builder = new StringBuilder();
		
		for(int i = 0; i < array.length; i++)
			builder.append(array[i]).append("|");
		
		prefs.edit().putString(name, builder.toString().substring(0, builder.length() - 1)).commit();
	}
	
	/**
	 * Saves array of float
	 * 
	 * @param name  name of value
	 * @param array aray of floats
	 */
	public void putFloatArray(String name, float[] array)
	{
		StringBuilder builder = new StringBuilder();
		
		for(int i = 0; i < array.length; i++)
			builder.append(array[i]).append("|");
		
		prefs.edit().putString(name, builder.toString().substring(0, builder.length() - 1)).commit();
	}

	/**
	 * Saves array of double
	 * 
	 * @param name  name of value
	 * @param array aray on doubles
	 */
	public void putDoubleArray(String name, double[] array)
	{
		StringBuilder builder = new StringBuilder();
		
		for(int i = 0; i < array.length; i++)
			builder.append(array[i]).append("|");
		
		prefs.edit().putString(name, builder.toString().substring(0, builder.length() - 1)).commit();
	}

	/**
	 * Saves array of boolean
	 * 
	 * @param name  name of value
	 * @param array array of booleans
	 */
	public void putBooleanArray(String name, boolean[] array)
	{
		StringBuilder builder = new StringBuilder();
		
		for(int i = 0; i < array.length; i++)
			builder.append(array[i]).append("|");
		
		prefs.edit().putString(name, builder.toString().substring(0, builder.length() - 1)).commit();
	}

	/**
	 * Saves matrix of int
	 * 
	 * @param name   name for this matrix in preferences
	 * @param matrix matrix MxN of integers
	 */
	public void putIntMatrix(String name, int[][] matrix)
	{
		StringBuilder builder = new StringBuilder();
		
		for(int i = 0; i < matrix.length; i++)
			for(int j = 0; j < matrix[i].length; j++)
				builder.append(matrix[i][j]).append("|");
		
		prefs.edit().putString(name+ "_MATRIX", builder.toString().substring(0, builder.length() - 1)).commit();
	}
	
	/**
	 * Saves matrix of float
	 * 
	 * @param name   name for this matrix in preferences
	 * @param matrix matrix MxN of floats
	 */
	public void putFloatMatrix(String name, float[][] matrix)
	{
		StringBuilder builder = new StringBuilder();
		
		for(int i = 0; i < matrix.length; i++)
			for(int j = 0; j < matrix[i].length; j++)
				builder.append(matrix[i][j]).append("|");
		
		prefs.edit().putString(name+ "_MATRIX", builder.toString().substring(0, builder.length() - 1)).commit();
	}

	/**
	 * Saves matrix of double
	 * 
	 * @param name   name for this matrix in preferences
	 * @param matrix matrix MxN of double
	 */
	public void putDoubleMatrix(String name, double[][] matrix)
	{
		StringBuilder builder = new StringBuilder();
		
		for(int i = 0; i < matrix.length; i++)
			for(int j = 0; j < matrix[i].length; j++)
				builder.append(matrix[i][j]).append("|");
		
		prefs.edit().putString(name+ "_MATRIX", builder.toString().substring(0, builder.length() - 1)).commit();
	}

	/**
	 * Saves matrix of boolean
	 * 
	 * @param name   name for this matrix in preferences
	 * @param matrix matrix MxN of booleans
	 */
	public void putBooleanMatrix(String name, boolean[][] matrix)
	{
		StringBuilder builder = new StringBuilder();
		
		for(int i = 0; i < matrix.length; i++)
			for(int j = 0; j < matrix[i].length; j++)
				builder.append(matrix[i][j]).append("|");
		
		prefs.edit().putString(name+ "_MATRIX", builder.toString().substring(0, builder.length() - 1)).commit();
	}
	
	/**
	 * Exception, which throws if array or matrix not found
	 */
	public static class NoSuchArrayException extends Exception
	{
		private static final long serialVersionUID = 7421076827647245159L;

		public NoSuchArrayException(String s)
		{
			super(s);
		}
	}
	
	/**
	 * Static method for create new object or load cached
	 * 
	 * @param ctx Context, on which object will be or was created
	 * @return instance of SavingUtils
	 */
	public static SavingUtils forContext(Context ctx)
	{
		for(SavingUtils utils : objects)
			if(utils.isBasedOn(ctx))
				return utils;
				
		return new SavingUtils(ctx);
	}
}
