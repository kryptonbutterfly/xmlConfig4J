package xmlConfig4J2.example;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Test
{
	private static final Logger log = LogManager.getLogger(Test.class);
	
	public static void main(String[] args) throws ClassNotFoundException
	{
		Example config = new Example();
		config.list.add("element 1");
		config.list.add("element 2");
		config.list.add(null);
		
		if(config.exists())
		{
			try
			{
				config.load();
				config.printConfigContent();
				if(config.changeConfig)
				{
					config.changeValues();
					config.save();
				}
				else
				{
					System.out.println("Not Writing Stuff");
				}
			}
			catch(IOException e)
			{
				log.error(e::getMessage, e);
			}
		}
		else
		{
			config.save();
		}
	}
}