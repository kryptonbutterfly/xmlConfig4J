package kryptonbutterfly.xmlConfig4J.adapter.collections;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.Nodes;
import kryptonbutterfly.xmlConfig4J.TypeAdapter;
import kryptonbutterfly.xmlConfig4J.XmlReader;
import kryptonbutterfly.xmlConfig4J.XmlWriter;
import kryptonbutterfly.xmlConfig4J.exceptions.AttributeNotFoundException;
import kryptonbutterfly.xmlConfig4J.exceptions.BrokenReferenceException;

@SuppressWarnings("rawtypes")
public final class MapAdapter implements TypeAdapter<Map>
{
	public static final String KEY = "key";
	
	@Override
	public Class<Map> getType()
	{
		return Map.class;
	}
	
	@Override
	public void write(XmlWriter writer, Element elem, Map value) throws IllegalAccessException
	{
		if (value == null)
			writer.writeNull(elem);
		else
		{
			for (final var e : value.entrySet())
			{
				final var	entry		= (Entry<?, ?>) e;
				final var	entryElem	= writer.doc.createElement(writer.getTags().itemTag());
				elem.appendChild(entryElem);
				
				final var keyElem = writer.doc.createElement(KEY);
				entryElem.appendChild(keyElem);
				final var key = entry.getKey();
				if (key == null)
					writer.writeNull(keyElem);
				else
				{
					writer.writeType(keyElem, key.getClass());
					writer.write(keyElem, key, key.getClass());
				}
				
				var valElem = writer.doc.createElement(writer.getTags().valueTag());
				entryElem.appendChild(valElem);
				final var val = entry.getValue();
				if (val == null)
					writer.writeNull(valElem);
				else
				{
					writer.writeType(valElem, val.getClass());
					writer.write(valElem, val, val.getClass());
				}
			}
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Map read(XmlReader reader, Node node, Class<?> classOfT)
		throws ClassNotFoundException,
		AttributeNotFoundException,
		NoSuchFieldException,
		InvocationTargetException,
		InstantiationException,
		IllegalAccessException,
		NoSuchMethodException,
		BrokenReferenceException
	{
		if (reader.isNull(node))
			return null;
		final Map<Object, Object> map = (Map<Object, Object>) classOfT.getConstructor().newInstance();
		reader.registerInstance(node, map);
		
		for (final var n : new Nodes(node.getChildNodes()))
			if (n.getNodeName().equals(reader.getTags().itemTag()))
			{
				Object key = null, value = null;
				for (var e : new Nodes(n.getChildNodes()))
				{
					final var nodeName = e.getNodeName();
					if (Objects.equals(nodeName, KEY))
						key = reader.read(e);
					else if (Objects.equals(nodeName, reader.getTags().valueTag()))
						value = reader.read(e);
					else
						System.err.printf("Unexpected element '%s'\n", e.getNodeName());
				}
				map.put(key, value);
			}
			else
				System.err.printf("Unexpected element '%s'\n", n.getNodeName());
		return map;
	}
}
