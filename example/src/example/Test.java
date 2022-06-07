package example;

import java.io.IOException;

public class Test
{
	public static void main(String[] args) throws ClassNotFoundException
	{
		Example config = new Example();
		config.list.add("element 1");
		config.list.add("element 2");
		config.list.add(null);
		
		if (config.exists())
		{
			try
			{
				config.load();
				config.printConfigContent();
				if (config.changeConfig)
				{
					config.changeValues();
					config.save();
				}
				else
				{
					System.out.println("Not Writing Stuff");
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			config.save();
		}
	}
}