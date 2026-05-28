package kryptonbutterfly.xmlConfig4J.adapter.collections;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.w3c.dom.Comment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.Comments.CommentReader;
import kryptonbutterfly.xmlConfig4J.Comments.CommentWriter;
import kryptonbutterfly.xmlConfig4J.Nodes;
import kryptonbutterfly.xmlConfig4J.TypeAdapter;
import kryptonbutterfly.xmlConfig4J.XmlReader;
import kryptonbutterfly.xmlConfig4J.XmlWriter;
import kryptonbutterfly.xmlConfig4J.comments.path.GeneralPath;
import kryptonbutterfly.xmlConfig4J.comments.path.PathHashRef;
import kryptonbutterfly.xmlConfig4J.comments.path.PathName;
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
	public void write(CommentWriter cw, XmlWriter writer, Element elem, Map value) throws IllegalAccessException
	{
		if (value == null)
			writer.writeNull(elem);
		else
		{
			for (final var e : value.entrySet())
			{
				final var	entry		= (Entry<?, ?>) e;
				final var	entryElem	= writer.doc.createElement(writer.getTags().itemTag());
				
				final var key = entry.getKey();
				
				final var hashRef = new PathHashRef(key);
				try (var wItem = cw.push(entryElem, hashRef))
				{
					elem.appendChild(entryElem);
					final var keyElem = writer.doc.createElement(KEY);
					try (var kw = wItem.push(keyElem, new PathName(KEY)))
					{
						entryElem.appendChild(keyElem);
						if (key == null)
							writer.writeNull(keyElem);
						else
						{
							writer.writeType(keyElem, key.getClass());
							writer.write(kw, keyElem, key, key.getClass());
						}
					}
					
					var valElem = writer.doc.createElement(writer.getTags().valueTag());
					try (var vw = wItem.push(valElem, GeneralPath.create(writer.getTags(), valElem)))
					{
						entryElem.appendChild(valElem);
						final var val = entry.getValue();
						if (val == null)
							writer.writeNull(valElem);
						else
						{
							writer.writeType(valElem, val.getClass());
							writer.write(vw, valElem, val, val.getClass());
						}
					}
				}
			}
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Map read(CommentReader cr, XmlReader reader, Node node, Class<?> classOfT)
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
		{
			if (n instanceof Comment comment)
				cr.addComment(comment);
			else if (n.getNodeName().equals(reader.getTags().itemTag()))
			{
				var hashRef = new PathHashRef();
				try (var rItem = cr.push(hashRef))
				{
					Object key = null, value = null;
					for (var e : new Nodes(n.getChildNodes()))
					{
						final var nodeName = e.getNodeName();
						if (e instanceof Comment comment)
							rItem.addComment(comment);
						else if (Objects.equals(nodeName, KEY))
							try (var rKey = rItem.push(new PathName(KEY)))
							{
								key = reader.read(rKey, e);
							}
						else if (Objects.equals(nodeName, reader.getTags().valueTag()))
							try (var rValue = rItem.push(GeneralPath.create(reader.getTags(), nodeName)))
							{
								value = reader.read(rValue, e);
							}
						else
						{
							System.err.printf("Unexpected element '%s'\n", e.getNodeName());
							rItem.clear();
						}
					}
					hashRef.init(key);
					map.put(key, value);
				}
			}
			else
			{
				System.err.printf("Unexpected element '%s'\n", n.getNodeName());
				cr.clear();
			}
		}
		return map;
	}
}
