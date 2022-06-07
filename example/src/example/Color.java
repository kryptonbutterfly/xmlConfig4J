package example;

/**
 * This is just a mock class so i don't have to require the java.awt module just
 * for the example
 * 
 * @author tinycodecrank
 */
public class Color
{
	public final int red, green, blue, alpha;
	
	public Color(int rgba)
	{
		this((rgba >> 24) & 0xFF, (rgba >> 16) & 0xFF, (rgba >> 8) & 0xFF, rgba & 0xFF);
	}
	
	public Color(int red, int green, int blue)
	{
		this(red, green, blue, 0xFF);
	}
	
	public Color(int red, int green, int blue, int alpha)
	{
		this.red	= red;
		this.green	= green;
		this.blue	= blue;
		this.alpha	= alpha;
	}
	
	public int getRed()
	{
		return red;
	}
	
	public int getGreen()
	{
		return green;
	}
	
	public int getBlue()
	{
		return blue;
	}
	
	public int getAlpha()
	{
		return alpha;
	}
	
	public final static Color ORANGE = new Color(0xFF7700FF);
}