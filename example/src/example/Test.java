package example;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import kryptonbutterfly.xmlConfig4J.BindingBuilder;
import kryptonbutterfly.xmlConfig4J.XmlDataBinding;

public class Test
{
	public static final XmlDataBinding binding = new BindingBuilder()
		.indent(true)
		.indent(2)
		.addTypeAdapter(new ColorAdapter())
		.mapTypes(true)
		.build();
	
	public static final File exampleFile = new File("./config.xml");
	
	public static void main(String[] args) throws IOException
	{
		var example = load(exampleFile);
		if (example == null)
		{
			example = new Example();
			example.list.add("element 1");
			example.list.add("element 2");
			example.list.add(null);
			save(exampleFile, example);
		}
		
		example.printConfigContent();
		
		if (example.changeConfig)
		{
			example.changeValues();
			save(exampleFile, example);
		}
		else
		{
			System.out.println("Nothing changed, so nothing to write!");
		}
	}
	
	private static Example load(File file) throws IOException
	{
		if (!file.exists())
			return null;
		try (var iStream = new FileInputStream(file))
		{
			return binding.fromXml(iStream);
		}
	}
	
	private static void save(File file, Example example)
	{
		try (var oStream = new FileOutputStream(file))
		{
			binding.toXml(example, oStream);
		}
		catch (IllegalAccessException | ParserConfigurationException | TransformerException | IOException e)
		{
			e.printStackTrace();
		}
	}
}