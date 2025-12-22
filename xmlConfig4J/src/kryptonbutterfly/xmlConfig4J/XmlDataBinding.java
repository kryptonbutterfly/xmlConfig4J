package kryptonbutterfly.xmlConfig4J;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
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

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import kryptonbutterfly.xmlConfig4J.exceptions.AttributeNotFoundException;

public final class XmlDataBinding
{
	public static final String	ID		= "id";
	public static final String	NAME	= "name";
	public static final String	TYPE	= "type";
	public static final String	NULL	= "null";
	public static final String	ITEM	= "item";
	public static final String	VALUE	= "value";
	public static final String	INFO	= "info";
	public static final String	TRUE	= Boolean.TRUE.toString();
	
	final Map<String, Class<?>>			classNameHistory;
	private final List<TypeAdapter<?>>	adapterMap;
	
	final boolean			mapTypes;
	private final boolean	indent;
	private final int		indentAmount;
	
	private final String rootTag, typesTag, dataTag;
	
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
		String rootTag,
		String typesTag,
		String dataTag,
		Function<Field, ? extends Annotation> includeFieldAnnotation,
		boolean indent,
		int indentAmount)
	{
		this.classNameHistory		= classNameHistory;
		this.adapterMap				= adapterMap;
		this.mapTypes				= mapTypes;
		this.includeFieldAnnotation	= includeFieldAnnotation;
		
		this.rootTag	= rootTag;
		this.typesTag	= typesTag;
		this.dataTag	= dataTag;
		
		this.indent			= indent;
		this.indentAmount	= indentAmount;
	}
	
	private DocumentBuilder docBuilder() throws ParserConfigurationException
	{
		final var factory = DocumentBuilderFactory.newInstance();
		
		factory.setIgnoringElementContentWhitespace(true);
		return factory.newDocumentBuilder();
	}
	
	private Validator createValidator(String schema) throws SAXException
	{
		var schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		return schemaFactory.newSchema(new StreamSource(new StringReader(schema))).newValidator();
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
				final var	idAttr		= (Attr) item.getAttributes().getNamedItem(ID);
				final var	typeAttr	= (Attr) item.getAttributes().getNamedItem(NAME);
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
	
	public <T> T fromXML(String xml)
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
			| NoSuchFieldException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	private <T> T fromDoc(Document doc)
		throws ClassNotFoundException,
		AttributeNotFoundException,
		InvocationTargetException,
		InstantiationException,
		IllegalAccessException,
		NoSuchMethodException,
		NoSuchFieldException
	{
		final var rootNodeList = doc.getElementsByTagName(rootTag);
		if (rootNodeList.getLength() == 0)
			return null;
		
		final var	root	= rootNodeList.item(0);
		final var	nodes	= root.getChildNodes();
		
		Node	typesNode	= null;
		Node	dataNode	= null;
		for (final var node : new Nodes(nodes))
		{
			final var nodeName = node.getNodeName();
			if (nodeName.equals(typesTag))
				typesNode = node;
			else if (nodeName.equals(dataTag))
				dataNode = node;
			else
				System.err.printf("Ignoring unexpected node: '%s'\n", nodeName);
		}
		
		final var reader = new XmlReader(this, readTypeMappings(typesNode));
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
		final var	writer		= new XmlWriter(this, document);
		
		final var	rootElement	= document.createElement(rootTag);
		final var	dataElement	= document.createElement(dataTag);
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
	
	private Element writeTypeMappings(XmlWriter writer)
	{
		final var mapping = writer.doc.createElement(typesTag);
		writer.types.forEach((typeName, i) -> {
			final var type = writer.doc.createElement(ITEM);
			type.setAttribute(ID, i.toString());
			type.setAttribute(NAME, typeName);
			mapping.appendChild(type);
		});
		return mapping;
	}
}
