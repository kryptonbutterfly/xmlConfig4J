package de.tinycodecrank.xmlConfig4J;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.channels.FileLock;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.tinycodecrank.xmlConfig4J.annotations.Value;
import de.tinycodecrank.xmlConfig4J.parser.Parser;
import de.tinycodecrank.xmlConfig4J.parser.StringParser;
import de.tinycodecrank.xmlConfig4J.parser.UUIDParser;
import de.tinycodecrank.xmlConfig4J.parser.assignable.ArrayParser;
import de.tinycodecrank.xmlConfig4J.parser.assignable.EnumParser;
import de.tinycodecrank.xmlConfig4J.parser.assignable.ListParser;
import de.tinycodecrank.xmlConfig4J.parser.assignable.MapParser;
import de.tinycodecrank.xmlConfig4J.parser.assignable.ParserAssignable;
import de.tinycodecrank.xmlConfig4J.parser.assignable.SetParser;
import de.tinycodecrank.xmlConfig4J.parser.primitiv.BooleanParser;
import de.tinycodecrank.xmlConfig4J.parser.primitiv.ByteParser;
import de.tinycodecrank.xmlConfig4J.parser.primitiv.CharParser;
import de.tinycodecrank.xmlConfig4J.parser.primitiv.DoubleParser;
import de.tinycodecrank.xmlConfig4J.parser.primitiv.FloatParser;
import de.tinycodecrank.xmlConfig4J.parser.primitiv.IntParser;
import de.tinycodecrank.xmlConfig4J.parser.primitiv.LongParser;
import de.tinycodecrank.xmlConfig4J.parser.primitiv.ShortParser;
import de.tinycodecrank.xmlConfig4J.parser.wrapping.BooleanObjectParser;
import de.tinycodecrank.xmlConfig4J.parser.wrapping.ByteObjectParser;
import de.tinycodecrank.xmlConfig4J.parser.wrapping.CharObjectParser;
import de.tinycodecrank.xmlConfig4J.parser.wrapping.DoubleObjectParser;
import de.tinycodecrank.xmlConfig4J.parser.wrapping.FloatObjectParser;
import de.tinycodecrank.xmlConfig4J.parser.wrapping.IntObjectParser;
import de.tinycodecrank.xmlConfig4J.parser.wrapping.LongObjectParser;
import de.tinycodecrank.xmlConfig4J.parser.wrapping.ShortObjectParser;

public class AConfig
{
	private static final String	UTF_8	= "UTF8";
	private static final Logger	log		= LogManager.getLogger(AConfig.class.getName());
	
	/**
	 * Register old class-paths in here and associate them with the current one to
	 * enable loading config-files saved in previous versions of a program!
	 */
	private HashMap<String, Class<?>> classNameHistory = new HashMap<>();
	
	private final HashMap<String, Parser> parserMap = new HashMap<>();
	
	private final LinkedList<ParserAssignable> assignableParser = new LinkedList<>();
	
	private boolean mapTypes = true;
	
	public AConfig()
	{
		addParser(new BooleanParser());
		addParser(new BooleanObjectParser());
		
		addParser(new ByteParser());
		addParser(new ByteObjectParser());
		
		addParser(new CharParser());
		addParser(new CharObjectParser());
		
		addParser(new ShortParser());
		addParser(new ShortObjectParser());
		
		addParser(new IntParser());
		addParser(new IntObjectParser());
		
		addParser(new FloatParser());
		addParser(new FloatObjectParser());
		
		addParser(new LongParser());
		addParser(new LongObjectParser());
		
		addParser(new DoubleParser());
		addParser(new DoubleObjectParser());
		
		addParser(new StringParser());
		addParser(new UUIDParser());
		
		addAssignableParser(new ListParser());
		addAssignableParser(new MapParser());
		addAssignableParser(new SetParser());
		addAssignableParser(new EnumParser());
		addAssignableParser(new ArrayParser());
	}
	
	protected void mapTypes(boolean doMap)
	{
		this.mapTypes = doMap;
	}
	
	/**
	 * 
	 * @param parser The parser to add
	 */
	protected final void addParser(Parser parser)
	{
		parserMap.put(parser.parsedType().getName(), parser);
	}
	
	protected final void addAssignableParser(ParserAssignable parser)
	{
		assignableParser.addFirst(parser);
	}
	
	private final ParserAssignable getAssignableParser(Class<?> type)
	{
		Optional<ParserAssignable> parserA = this.assignableParser.stream().filter(pa -> pa.canParse(type)).findAny();
		if (parserA.isPresent())
		{
			return parserA.get();
		}
		else
		{
			return null;
		}
	}
	
	final void loadPrepared(InputStream stream) throws FileNotFoundException, IOException
	{
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setIgnoringElementContentWhitespace(true);
			DocumentBuilder	parser		= factory.newDocumentBuilder();
			Document		document	= parser.parse(stream);
			
			NodeList rootNodeList = document.getElementsByTagName("root");
			if (rootNodeList.getLength() > 0)
			{
				Node		root			= rootNodeList.item(0);
				NodeList	nodes			= root.getChildNodes();
				String[]	typeMappings = new String[0];
				for (int i = 0; i < nodes.getLength(); i++)
				{
					Node node = nodes.item(i);
					if (node.getNodeName().equals("types"))
					{
						
						typeMappings = this.loadTypeMappings(node);
						break;
					}
				}
				LoadHelper loadHelper = new LoadHelper(
					parserMap,
					typeMappings,
					classNameHistory,
					this::getAssignableParser);
				for (int i = 0; i < nodes.getLength(); i++)
				{
					Node node = nodes.item(i);
					if (node.getNodeName().equals("config"))
					{
						loadConfigObject(loadHelper, node);
					}
				}
			}
		}
		catch (ParserConfigurationException | SAXException | ClassNotFoundException e)
		{
			log.error(e.getMessage(), e);
		}
	}
	
	private final String[] loadTypeMappings(Node mappings)
	{
		NodeList items = mappings.getChildNodes();
		String[] typeMappings = new String[items.getLength()];
		for (int i = 0; i < items.getLength(); i++)
		{
			try
			{
				Node	item		= items.item(i);
				Attr	idAttr		= (Attr) item.getAttributes().getNamedItem("id");
				Attr	typeAttr	= (Attr) item.getAttributes().getNamedItem("name");
				Integer	id			= Integer.valueOf(idAttr.getValue());
				String	type		= typeAttr.getValue();
				if (id != null && type != null)
				{
					typeMappings[id] = type;
				}
			}
			catch (NumberFormatException e)
			{
				log.error(e::getMessage, e);
			}
		}
		return typeMappings;
	}
	
	/**
	 * @param old
	 *            the old classPath
	 * @param current
	 * @return true if there wasn't yet an association and the given one was
	 *         successfully added. This method is used to ensure that configs can
	 *         still be loaded after changing a class or its classpath. Therefore
	 *         the classPath of the old Class and a Class<?> object of the new one
	 *         must be provided!
	 */
	protected final boolean addAssociation(String old, Class<?> current)
	{
		if (this.classNameHistory.containsKey(old))
		{
			return false;
		}
		this.classNameHistory.put(old, current);
		return this.classNameHistory.containsKey(old);
	}
	
	final InputStream prepareInput(InputStream origStream) throws IOException
	{
		return prepareInput(origStream, null);
	}
	
	final InputStream prepareInput(InputStream origStream, FileLock lock) throws IOException
	{
		StringBuilder		sb	= new StringBuilder();
		BufferedReader		br	= new BufferedReader(new InputStreamReader(origStream, UTF_8));
		br.lines().map(String::trim).forEach(sb::append);
		if (lock != null)
		{
			lock.release();
		}
		return new ByteArrayInputStream(sb.toString().getBytes(UTF_8));
	}
	
	private final void loadConfigObject(LoadHelper loadHelper, Node config) throws ClassNotFoundException
	{
		Class<?> oExtClass = this.getClass();
		do
		{
			for (Field field : oExtClass.getDeclaredFields())
			{
				if (field.isAnnotationPresent(Value.class))
				{
					NodeList nodeList = config.getChildNodes();
					for (int i = 0; i < nodeList.getLength(); i++)
					{
						Node	node	= nodeList.item(i);
						String	name	= node.getNodeName();
						if (field.getName().equals(name))
						{
							loadHelper.loadVar(field, this, node);
						}
					}
				}
			}
		}
		while ((oExtClass = oExtClass.getSuperclass()) != null && oExtClass != Object.class);
	}
	
	final void save(StreamResult output)
	{
		try
		{
			SaveHelper saveHelper = new SaveHelper(parserMap, this.mapTypes, this::getAssignableParser);
			
			DocumentBuilderFactory	factory		= DocumentBuilderFactory.newInstance();
			DocumentBuilder			parser		= factory.newDocumentBuilder();
			Document				document	= parser.newDocument();
			Element					rootElement	= document.createElement("root");
			
			Element		configElement	= saveHelper.saveObject("config", this, document);
			
			if (mapTypes)
			{
				rootElement.appendChild(saveHelper.saveTypeMappings(document));
			}
			rootElement.appendChild(configElement);
			
			document.appendChild(rootElement);
			document.normalizeDocument();
			
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			
			Source input = new DOMSource(document);
			transformer.transform(input, output);
		}
		catch (
			ParserConfigurationException
			| TransformerFactoryConfigurationError
			| TransformerException
			| IllegalArgumentException
			| DOMException
			| IllegalAccessException e)
		{
			log.error(e::getMessage, e);
		}
	}
}