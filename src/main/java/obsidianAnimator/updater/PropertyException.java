package obsidianAnimator.updater;

public class PropertyException extends Exception
{
	
	public static final int FORMAT_ERROR = 0;
	public static final int VALUE_ERROR = 1;
	public static final int PROP_NOT_FOUND_ERROR = 2;
	
	public PropertyException(String s, int type)
	{
		super(generateErrorString(s, type));
	}

	private static String generateErrorString(String s, int type) 
	{
		String error = "";
		switch(type)
		{
		case FORMAT_ERROR: error += "Format error, property line not in the form 'property:value': "; break;
		case VALUE_ERROR: error += "Invalid value, should be true or false: "; break;
		case PROP_NOT_FOUND_ERROR: error += "Property not found: "; break;
		}
		error += s;
		return error;
	}

}
