package kryptonbutterfly.xmlConfig4J.adapter.collections;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.Comments.CommentReader;
import kryptonbutterfly.xmlConfig4J.Comments.CommentWriter;
import kryptonbutterfly.xmlConfig4J.Nodes;
import kryptonbutterfly.xmlConfig4J.TypeAdapter;
import kryptonbutterfly.xmlConfig4J.XmlReader;
import kryptonbutterfly.xmlConfig4J.XmlWriter;
import kryptonbutterfly.xmlConfig4J.comments.path.PathIndexRef;
import kryptonbutterfly.xmlConfig4J.exceptions.AttributeNotFoundException;
import kryptonbutterfly.xmlConfig4J.exceptions.BrokenReferenceException;
import kryptonbutterfly.xmlConfig4J.utils.IntRange;

@SuppressWarnings("rawtypes")
public final class ListAdapter implements TypeAdapter<List>
{
	@Override
	public Class<List> getType()
	{
		return List.class;
	}
	
	@Override
	public void write(CommentWriter cw, XmlWriter writer, Element elem, List value) throws IllegalAccessException
	{
		if (value == null)
			writer.writeNull(elem);
		else
			for (int i : new IntRange(value.size()))
			{
				final var	child		= value.get(i);
				final var	childElem	= writer.doc.createElement(writer.getTags().itemTag());
				try (var iw = cw.push(childElem, new PathIndexRef(i)))
				{
					elem.appendChild(childElem);
					if (child == null)
						writer.writeNull(childElem);
					else
					{
						writer.write(iw, childElem, child, child.getClass());
						writer.writeType(childElem, child.getClass());
					}
				}
			}
	}
	
	@Override
	public List<?> read(CommentReader cr, XmlReader reader, Node node, Class<?> classOfT)
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
		
		final var list = (List<?>) classOfT.getConstructor().newInstance();
		reader.registerInstance(node, list);
		
		int index = 0;
		for (final var n : new Nodes(node.getChildNodes()))
			if (n instanceof org.w3c.dom.Comment comment)
				cr.addComment(comment);
			else if (n.getNodeName().equals(reader.getTags().itemTag()))
				try (var r = cr.push(new PathIndexRef(index++)))
				{
					list.add(reader.read(r, n));
				}
			else
				System.err.printf("Unexpected element '%s'\n", n.getNodeName());
			
		return list;
	}
}
