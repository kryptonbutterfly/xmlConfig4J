package kryptonbutterfly.xmlConfig4J;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import kryptonbutterfly.xmlConfig4J.exceptions.AttributeNotFoundException;
import kryptonbutterfly.xmlConfig4J.exceptions.BrokenReferenceException;

public final class XmlDataBinding
{
	public static final String TRUE = Boolean.TRUE.toString();
	
	final Map<String, Class<?>>			classNameHistory;
	private final List<TypeAdapter<?>>	adapterMap;
	
	final boolean			mapTypes;
	private final boolean	indent;
	private final int		indentAmount;
	private final boolean	declaredOnly;
	
	final Tags tags;
	
	private final Function<Field, ? extends Annotation> includeFieldAnnotation;
	
	public Annotation includeFieldAnnotation(Field field)
	{
		return includeFieldAnnotation.apply(field);
	}
	
	public TypeAdapter<?> getAdapter(Class<?> cls)
	{
		for (final var adapter : adapterMap)
			if (adapter.getType().isAssignableFrom(cls))
				return adapter;
		return null;
	}
	
	XmlDataBinding(
		Map<String, Class<?>> classNameHistory,
		List<TypeAdapter<?>> adapterMap,
		boolean mapTypes,
		Tags tags,
		Function<Field, ? extends Annotation> includeFieldAnnotation,
		boolean indent,
		int indentAmount,
		boolean declaredOnly)
	{
		this.classNameHistory		= classNameHistory;
		this.adapterMap				= adapterMap;
		this.mapTypes				= mapTypes;
		this.includeFieldAnnotation	= includeFieldAnnotation;
		
		this.tags = tags;
		
		this.indent			= indent;
		this.indentAmount	= indentAmount;
		this.declaredOnly	= declaredOnly;
	}
	
	private DocumentBuilder docBuilder() throws ParserConfigurationException
	{
		final var factory = DocumentBuilderFactory.newInstance();
		
		factory.setIgnoringElementContentWhitespace(true);
		return factory.newDocumentBuilder();
	}
	
	private HashMap<Integer, String> readTypeMappings(Node mappings)
	{
		final var typeMappings = new HashMap<Integer, String>();
		if (mappings == null)
			return typeMappings;
		
		for (var item : new Nodes(mappings.getChildNodes()))
		{
			try
			{
				final var	idAttr		= (Attr) item.getAttributes().getNamedItem(tags.idTag());
				final var	typeAttr	= (Attr) item.getAttributes().getNamedItem(tags.nameTag());
				final var	id			= Integer.valueOf(idAttr.getValue());
				final var	type		= typeAttr.getValue();
				if (type != null)
					typeMappings.put(id, type);
			}
			catch (NumberFormatException e)
			{
				throw e; // TODO reevaluate!
			}
		}
		return typeMappings;
	}
	
	private InputStream prepareInput(InputStream origStream)
	{
		final var	isr			= new InputStreamReader(origStream, StandardCharsets.UTF_8);
		final var	shortened	= new BufferedReader(isr)
			.lines()
			.map(String::trim)
			.collect(Collectors.joining());
		return new ByteArrayInputStream(shortened.getBytes(StandardCharsets.UTF_8));
	}
	
	public <T> T fromXml(String xml)
	{
		try (final var iStream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)))
		{
			return fromXml(iStream);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public <T> T fromXml(InputStream iStream)
	{
		try
		{
			return fromDoc(docBuilder().parse(prepareInput(iStream)));
		}
		catch (
			ParserConfigurationException
			| SAXException
			| IOException
			| ClassNotFoundException
			| AttributeNotFoundException
			| InvocationTargetException
			| InstantiationException
			| IllegalAccessException
			| NoSuchMethodException
			| NoSuchFieldException
			| BrokenReferenceException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public <T> T fromXml(File inputFile) throws FileNotFoundException, IOException
	{
		try (final var iStream = new FileInputStream(inputFile))
		{
			return fromXml(iStream);
		}
	}
	
	private <T> T fromDoc(Document doc)
		throws ClassNotFoundException,
		AttributeNotFoundException,
		InvocationTargetException,
		InstantiationException,
		IllegalAccessException,
		NoSuchMethodException,
		NoSuchFieldException,
		BrokenReferenceException
	{
		final var rootNodeList = doc.getElementsByTagName(tags.rootTag());
		if (rootNodeList.getLength() == 0)
			return null;
		
		final var	root	= rootNodeList.item(0);
		final var	nodes	= root.getChildNodes();
		
		Node	typesNode	= null;
		Node	dataNode	= null;
		for (final var node : new Nodes(nodes))
		{
			final var nodeName = node.getNodeName();
			if (nodeName.equals(tags.typesTag()))
				typesNode = node;
			else if (nodeName.equals(tags.dataTag()))
				dataNode = node;
			else
				System.err.printf("Ignoring unexpected node: '%s'\n", nodeName);
		}
		
		final var reader = new XmlReader(this, readTypeMappings(typesNode), declaredOnly);
		return reader.read(dataNode);
	}
	
	public <T> void toXml(T data, StreamResult output)
		throws ParserConfigurationException,
		TransformerException,
		IllegalAccessException
	{
		final var	factory		= DocumentBuilderFactory.newInstance();
		final var	parser		= factory.newDocumentBuilder();
		final var	document	= parser.newDocument();
		final var	writer		= new XmlWriter(this, document, declaredOnly);
		
		final var	rootElement	= document.createElement(tags.rootTag());
		final var	dataElement	= document.createElement(tags.dataTag());
		writer.writeType(dataElement, data.getClass());
		writer.write(dataElement, data);
		
		if (mapTypes)
			rootElement.appendChild(writeTypeMappings(writer));
		rootElement.appendChild(dataElement);
		
		document.appendChild(rootElement);
		document.normalizeDocument();
		
		final var transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, indent ? "yes" : "no");
		if (indent)
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", Integer.toString(indentAmount));
		
		final var input = new DOMSource(document);
		transformer.transform(input, output);
	}
	
	public <T> String toXml(T data)
		throws ParserConfigurationException,
		TransformerException,
		IllegalAccessException
	{
		final var writer = new StringWriter();
		toXml(data, new StreamResult(writer));
		return writer.toString();
	}
	
	public <T> void toXml(T data, OutputStream oStream)
		throws ParserConfigurationException,
		TransformerException,
		IllegalAccessException
	{
		toXml(data, new StreamResult(oStream));
	}
	
	public <T> void toXml(T data, File outputFile)
		throws IllegalAccessException,
		ParserConfigurationException,
		TransformerException,
		FileNotFoundException,
		IOException
	{
		try (final var iStream = new FileOutputStream(outputFile))
		{
			toXml(data, iStream);
		}
	}
	
	private Element writeTypeMappings(XmlWriter writer)
	{
		final var mapping = writer.doc.createElement(tags.typesTag());
		writer.types.forEach((typeName, i) -> {
			final var type = writer.doc.createElement(tags.itemTag());
			type.setAttribute(tags.idTag(), i.toString());
			type.setAttribute(tags.nameTag(), typeName);
			mapping.appendChild(type);
		});
		return mapping;
	}
}
