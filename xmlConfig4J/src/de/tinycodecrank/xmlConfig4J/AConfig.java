package de.tinycodecrank.xmlConfig4J;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileLock;
import java.util.HashMap;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
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
	private static final String UTF_8 = "UTF8";
	
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
	 * @param parser
	 *            The parser to add
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
		return this.assignableParser.stream().filter(pa -> pa.canParse(type)).findAny().orElse(null);
	}
	
	final void loadPrepared(InputStream stream) throws FileNotFoundException, IOException
	{
		try
		{
			final var factory = DocumentBuilderFactory.newInstance();
			factory.setIgnoringElementContentWhitespace(true);
			final var	parser		= factory.newDocumentBuilder();
			final var	document	= parser.parse(stream);
			
			final var rootNodeList = document.getElementsByTagName("root");
			if (rootNodeList.getLength() > 0)
			{
				final var	root			= rootNodeList.item(0);
				final var	nodes			= root.getChildNodes();
				var			typeMappings	= new String[0];
				for (int i = 0; i < nodes.getLength(); i++)
				{
					final var node = nodes.item(i);
					if (node.getNodeName().equals("types"))
					{
						
						typeMappings = this.loadTypeMappings(node);
						break;
					}
				}
				final var loadHelper = new LoadHelper(
					parserMap,
					typeMappings,
					classNameHistory,
					this::getAssignableParser);
				for (int i = 0; i < nodes.getLength(); i++)
				{
					final var node = nodes.item(i);
					if (node.getNodeName().equals("config"))
					{
						loadConfigObject(loadHelper, node);
					}
				}
			}
		}
		catch (ParserConfigurationException | SAXException | ClassNotFoundException e)
		{
			e.printStackTrace();
		}
	}
	
	private final String[] loadTypeMappings(Node mappings)
	{
		final var	items			= mappings.getChildNodes();
		final var	typeMappings	= new String[items.getLength()];
		for (int i = 0; i < items.getLength(); i++)
		{
			try
			{
				final var	item		= items.item(i);
				final var	idAttr		= (Attr) item.getAttributes().getNamedItem("id");
				final var	typeAttr	= (Attr) item.getAttributes().getNamedItem("name");
				final var	id			= Integer.valueOf(idAttr.getValue());
				final var	type		= typeAttr.getValue();
				if (id != null && type != null)
				{
					typeMappings[id] = type;
				}
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
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
		final var	sb	= new StringBuilder();
		final var	br	= new BufferedReader(new InputStreamReader(origStream, UTF_8));
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
		
		while (oExtClass != null && oExtClass != Object.class)
		{
			for (final var field : oExtClass.getDeclaredFields())
			{
				if (field.isAnnotationPresent(Value.class))
				{
					final var nodeList = config.getChildNodes();
					for (int i = 0; i < nodeList.getLength(); i++)
					{
						final var	node	= nodeList.item(i);
						final var	name	= node.getNodeName();
						if (field.getName().equals(name))
						{
							loadHelper.loadVar(field, this, node);
						}
					}
				}
			}
			oExtClass = oExtClass.getSuperclass();
		}
	}
	
	final void save(StreamResult output)
	{
		try
		{
			final var	saveHelper	= new SaveHelper(parserMap, this.mapTypes, this::getAssignableParser);
			final var	factory		= DocumentBuilderFactory.newInstance();
			final var	parser		= factory.newDocumentBuilder();
			final var	document	= parser.newDocument();
			
			final var	rootElement		= document.createElement("root");
			final var	configElement	= saveHelper.saveObject("config", this, document);
			
			if (mapTypes)
			{
				rootElement.appendChild(saveHelper.saveTypeMappings(document));
			}
			rootElement.appendChild(configElement);
			
			document.appendChild(rootElement);
			document.normalizeDocument();
			
			final var transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			
			final var input = new DOMSource(document);
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
			e.printStackTrace();
		}
	}
}