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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import kryptonbutterfly.xmlConfig4J.Comments.CommentReader;
import kryptonbutterfly.xmlConfig4J.Comments.CommentWriter;
import kryptonbutterfly.xmlConfig4J.comments.path.GeneralPath;
import kryptonbutterfly.xmlConfig4J.comments.path.PathTypeRef;
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
	
	private HashMap<Integer, String> readTypeMappings(CommentReader rootInner, Node mappings)
	{
		Objects.requireNonNull(mappings);
		final var typeMappings = new HashMap<Integer, String>();
		try (var r = rootInner.push(GeneralPath.create(tags, mappings)))
		{
			for (var item : new Nodes(mappings.getChildNodes()))
				if (item instanceof org.w3c.dom.Comment comment)
					r.addComment(comment);
				else
				{
					final var	idAttr		= (Attr) item.getAttributes().getNamedItem(tags.idTag());
					final var	typeAttr	= (Attr) item.getAttributes().getNamedItem(tags.nameTag());
					final var	id			= Integer.valueOf(idAttr.getValue());
					final var	type		= typeAttr.getValue();
					if (type != null)
					{
						typeMappings.put(id, type);
						try (var itemR = r.push(new PathTypeRef(type)))
						{}
					}
				}
		}
		catch (NumberFormatException e)
		{
			throw e; // TODO reevaluate!
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
		return fromXml(null, xml);
	}
	
	public <T> T fromXml(Comments comments, String xml)
	{
		try (final var iStream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)))
		{
			return fromXml(comments, iStream);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public <T> T fromXml(String xml, Class<T> classOfT)
	{
		return fromXml(null, xml, classOfT);
	}
	
	public <T> T fromXml(Comments comments, String xml, Class<T> classOfT)
	{
		try (final var iStream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)))
		{
			return fromXml(comments, iStream, classOfT);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public <T> T fromXml(InputStream iStream)
	{
		return fromXml(null, iStream);
	}
	
	public <T> T fromXml(Comments comments, InputStream iStream)
	{
		try
		{
			return fromDoc(comments, docBuilder().parse(prepareInput(iStream)));
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
	
	public <T> T fromXml(InputStream iStream, Class<T> classOfT)
	{
		return fromXml(null, iStream, classOfT);
	}
	
	public <T> T fromXml(Comments comments, InputStream iStream, Class<T> classOfT)
	{
		try
		{
			return fromDoc(comments, docBuilder().parse(prepareInput(iStream)), classOfT);
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
		return fromXml(null, inputFile);
	}
	
	public <T> T fromXml(Comments comments, File inputFile) throws FileNotFoundException, IOException
	{
		try (final var iStream = new FileInputStream(inputFile))
		{
			return fromXml(comments, iStream);
		}
	}
	
	public <T> T fromXml(File inputFile, Class<T> classOfT) throws FileNotFoundException, IOException
	{
		return fromXml(null, inputFile, classOfT);
	}
	
	public <T> T fromXml(Comments comments, File inputFile, Class<T> classOfT) throws FileNotFoundException, IOException
	{
		try (final var iStream = new FileInputStream(inputFile))
		{
			return fromXml(comments, iStream, classOfT);
		}
	}
	
	private <T> T fromDoc(Comments comments, Document doc)
		throws ClassNotFoundException,
		AttributeNotFoundException,
		InvocationTargetException,
		InstantiationException,
		IllegalAccessException,
		NoSuchMethodException,
		NoSuchFieldException,
		BrokenReferenceException
	{
		return fromDoc(comments, doc, null);
	}
	
	private <T> T fromDoc(Comments comments, Document doc, Class<T> classOfT)
		throws ClassNotFoundException,
		AttributeNotFoundException,
		InvocationTargetException,
		InstantiationException,
		IllegalAccessException,
		NoSuchMethodException,
		NoSuchFieldException,
		BrokenReferenceException
	{
		if (comments == null)
			comments = new Comments(false);
		
		T result = null;
		
		try (var rootPreReader = comments.new DocReader())
		{
			for (var n : new Nodes(doc.getChildNodes()))
				if (n instanceof org.w3c.dom.Comment c)
					rootPreReader.addComment(c);
				else if (n.getNodeName().equals(tags.rootTag()))
				{
					final var root = n;
					try (var rootInner = rootPreReader.push(GeneralPath.create(tags, root)))
					{
						var		typesMapping	= new HashMap<Integer, String>();
						Node	dataNode		= null;
						
						var					commentBuffer		= new ArrayList<String>();
						ArrayList<String>	dataCommentBuffer	= null;
						
						for (final var node : new Nodes(root.getChildNodes()))
						{
							final var nodeName = node.getNodeName();
							if (node instanceof org.w3c.dom.Comment c)
								commentBuffer.add(c.getData());
							else if (nodeName.equals(tags.typesTag()))
							{
								rootInner.buffer.addAll(commentBuffer);
								commentBuffer.clear();
								typesMapping = readTypeMappings(rootInner, node);
								
							}
							else if (nodeName.equals(tags.dataTag()))
							{
								dataCommentBuffer	= commentBuffer;
								commentBuffer		= new ArrayList<String>();
								dataNode			= node;
							}
							else
								System.err.printf("Ignoring unexpected node: '%s'\n", nodeName);
						}
						
						final var reader = new XmlReader(this, typesMapping, declaredOnly);
						
						rootInner.buffer = dataCommentBuffer;
						try (var rData = rootInner.push(GeneralPath.create(tags, dataNode)))
						{
							result = classOfT != null
								? reader.read(rData, dataNode, classOfT)
									: reader.read(rData, dataNode);
						}
						rootInner.buffer = commentBuffer;
					}
				}
				else
					System.err.printf("Ignoring unexpected node: '%s'\n", n.getNodeName());
			// EVAL if throwing an exception is more appropriate.
		}
		return result;
	}
	
	public <T> void toXml(T data, StreamResult output)
		throws IllegalAccessException,
		ParserConfigurationException,
		TransformerException
	{
		toXml(null, data, output);
	}
	
	public <T> void toXml(Comments comments, T data, StreamResult output)
		throws ParserConfigurationException,
		TransformerException,
		IllegalAccessException
	{
		if (comments == null)
			comments = new Comments(false);
		
		final var	factory	= DocumentBuilderFactory.newInstance();
		final var	parser	= factory.newDocumentBuilder();
		final var	doc		= parser.newDocument();
		final var	writer	= new XmlWriter(this, doc, declaredOnly);
		
		try (var dw = comments.new DocWriter(doc))
		{
			final var rootElement = doc.createElement(tags.rootTag());
			try (var rw = dw.push(rootElement, GeneralPath.create(tags, tags.rootTag())))
			{
				final var		typesElem	= doc.createElement(tags.typesTag());
				CommentWriter	typesWriter	= null;
				if (mapTypes)
				{
					typesWriter = rw.push(typesElem, GeneralPath.create(tags, tags.typesTag()));
					rootElement.appendChild(typesElem);
				}
				
				final var dataElement = doc.createElement(tags.dataTag());
				try (var dataWriter = rw.push(dataElement, GeneralPath.create(tags, dataElement)))
				{
					writer.writeType(dataElement, data.getClass());
					writer.write(dataWriter, dataElement, data);
					rootElement.appendChild(dataElement);
				}
				
				if (mapTypes)
					writeTypeMappings(typesWriter, writer, typesElem);
				
				doc.appendChild(rootElement);
				doc.normalizeDocument();
			}
		}
		
		final var transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, indent ? "yes" : "no");
		if (indent)
			transformer
				.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", Integer.toString(indentAmount));
		final var input = new DOMSource(doc);
		transformer.transform(input, output);
	}
	
	public <T> String toXml(T data) throws IllegalAccessException, ParserConfigurationException, TransformerException
	{
		return toXml(null, data);
	}
	
	public <T> String toXml(Comments comments, T data)
		throws ParserConfigurationException,
		TransformerException,
		IllegalAccessException
	{
		final var writer = new StringWriter();
		toXml(comments, data, new StreamResult(writer));
		return writer.toString();
	}
	
	public <T> void toXml(T data, OutputStream oStream)
		throws IllegalAccessException,
		ParserConfigurationException,
		TransformerException
	{
		toXml(null, data, oStream);
	}
	
	public <T> void toXml(Comments comments, T data, OutputStream oStream)
		throws ParserConfigurationException,
		TransformerException,
		IllegalAccessException
	{
		toXml(comments, data, new StreamResult(oStream));
	}
	
	public <T> void toXml(T data, File outputFile)
		throws IllegalAccessException,
		FileNotFoundException,
		ParserConfigurationException,
		TransformerException,
		IOException
	{
		toXml(null, data, outputFile);
	}
	
	public <T> void toXml(Comments comments, T data, File outputFile)
		throws IllegalAccessException,
		ParserConfigurationException,
		TransformerException,
		FileNotFoundException,
		IOException
	{
		try (final var iStream = new FileOutputStream(outputFile))
		{
			toXml(comments, data, iStream);
		}
	}
	
	private void writeTypeMappings(CommentWriter cw, XmlWriter writer, Node mapping)
	{
		writer.types.forEach((typeName, i) -> {
			final var type = writer.doc.createElement(tags.itemTag());
			type.setAttribute(tags.idTag(), i.toString());
			type.setAttribute(tags.nameTag(), typeName);
			try (var itemW = cw.push(type, new PathTypeRef(typeName)))
			{
				mapping.appendChild(type);
			}
		});
		cw.close();
	}
}
