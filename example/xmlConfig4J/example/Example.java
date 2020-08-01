package xmlConfig4J.example;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.tinycodecrank.xmlConfig4J.FileConfig;
import de.tinycodecrank.xmlConfig4J.annotations.Value;

public class Example extends FileConfig
{
	public Example()
	{
		super(new File("./example/config.xml"));
		addParser(new ColorParser());
	}
	
	@Value
	public String projectName = "xmlConfig4J2";
	
	@Value("Description for this field")
	public String author = "tinycodecrank";
	
	@Value
	public String comment = "null";
	
	@Value
	public int someNumber = 1337;
	
	@Value
	public CustomClass custom = new CustomClass();
	
	@Value
	public CustomClass empty = null;
	
	@Value
	public Color color = Color.ORANGE;

	@Value
	public boolean changeConfig = false;
	
	@Value
	public String[][] testArray = {{"Hallo, ", "Welt!"}, {"Hello, World!\n"}, null, {"TestNull", null}};
	
	@Value
	public String[] nullArray = null;
	
	@Value
	public String nullString = null;
	
	@Value
	public int runCounter = 0;
	
	@Value
	public HashMap<Integer, String> runResult = new HashMap<>();
	
	@Value
	public List<String> list = new ArrayList<>();
	
	@Value
	public Set<String> set = new HashSet<>();
	
	@Value
	public TestEnum testEnum = null;

	public void printConfigContent()
	{
		System.out.printf("Projectname:\t%s%n", projectName);
		System.out.printf("Author:\t%s%n", author);
		System.out.printf("Comment:\t%s%n", comment);
		System.out.printf("Some Number:\t%d%n", someNumber);
		System.out.printf("Custom:\t%s%n", custom);
		System.out.printf("Empty:\t%s%n", empty);
		System.out.printf("Color:\t%s%n", color);
		System.out.printf("%nTest-Array:\t%s%n", Arrays.deepToString(testArray));
		System.out.printf("%nNull-Array:\t%s%n", Arrays.toString(nullArray));
		System.out.printf("%nNull-String:\t%s%n", nullString);
		System.out.printf("Change config:\t%s%n", changeConfig);
		System.out.printf("RunCounter:\t%s%n", runCounter);
		
		System.out.printf("%nMap\t:%n");
		printMap();
		System.out.printf("%nList\t:%n");
		printList();
		System.out.printf("%nSet\t:%n");
		printSet();
		
		System.out.printf("Test-Enum:\t%s%n", testEnum);
	}
	
	public void changeValues()
	{
		comment = "The config has changed!";
		someNumber = 42;
		
		runCounter++;
		runResult.put(runCounter, "The program ran " + runCounter + " times before");
	}
	
	public void printMap()
	{
		runResult.forEach((i, s) -> System.out.printf("\t %s - %s%n", i, s));
	}
	
	public void printList()
	{
		list.forEach(s -> System.out.printf("\t %s%n", s));
	}
	
	public void printSet()
	{
		set.forEach(s -> System.out.printf("\t %s%n", s));
	}
}