package xmlConfig4J.example;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.tinycodecrank.xmlConfig4J.FileConfig;
import de.tinycodecrank.xmlConfig4J.annotations.Value;

public class Example extends FileConfig
{
	private static final Logger log = LogManager.getLogger(Example.class);
	
	public Example()
	{
		super(new File("./example/config.xml"));
	}
	
	@Value
	public String projectName = "xmlConfig4J";
	
	@Value("Description for this field")
	public String author = "tinycodecrank";
	
	@Value
	public String comment = "null";
	
	@Value
	public int someNumber = 1337;
	
	@Value
	public CustomClass custom = new CustomClass();
	
	@Value
	public HashMap<String, Double> aMap = new HashMap<>();
	
	@Value
	public ArrayList<CustomClass> aList = new ArrayList<>();
	
	@Value
	public Day dayEnum = Day.MONDAY;
	
	@Value
	public boolean changeConfig = false;
	

	public static void main(String[] args)
	{
		Example config = new Example();
		
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
			catch (IOException e)
			{
				log.error(e.getMessage(), e);
			}
		}
		else
		{
			config.save();
		}
	}
	
	private void printConfigContent()
	{
		System.out.printf("Projectname:\t%s%n", projectName);
		System.out.printf("Author:\t%s%n", author);
		System.out.printf("Comment:\t%s%n", comment);
		System.out.printf("Some Number:\t%d%n", someNumber);
		System.out.printf("Custom:\t%s%n", custom);
		System.out.printf("Day.\t%s%n", dayEnum.name());
	}
	
	private void changeValues()
	{
		dayEnum = Day.FRIDAY;
		comment = "The config has changed!";
		someNumber = 42;
		aMap.put("42", 42.0);
		aMap.put("pi", Math.PI);
	}
}