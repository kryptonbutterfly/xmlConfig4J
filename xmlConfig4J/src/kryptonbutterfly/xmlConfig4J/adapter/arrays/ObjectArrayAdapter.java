package kryptonbutterfly.xmlConfig4J.adapter.arrays;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;

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

public final class ObjectArrayAdapter implements TypeAdapter<Object[]>
{
	@Override
	public Class<Object[]> getType()
	{
		return Object[].class;
	}
	
	@Override
	public void write(CommentWriter cw, XmlWriter writer, Element elem, Object[] value) throws IllegalAccessException
	{
		if (value == null)
			writer.writeNull(elem);
		else
			for (final int i : new IntRange(value.length))
			{
				final var	e		= value[i];
				final var	item	= writer.doc.createElement(writer.getTags().itemTag());
				try (var w = cw.push(item, new PathIndexRef(i)))
				{
					elem.appendChild(item);
					if (e != null)
						writer.writeType(item, e.getClass());
					writer.write(w, item, e);
				}
			}
	}
	
	@Override
	public Object[] read(CommentReader cr, XmlReader reader, Node node, Class<?> classOfT)
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
		
		final var		nodes	= new Nodes(node.getChildNodes());
		final Object[]	result	= (Object[]) Array
			.newInstance(classOfT.componentType(), (int) nodes.stream().filter(n -> isItemNode(reader, n)).count());
		reader.registerInstance(node, result);
		
		int index = 0;
		for (final var n : nodes)
			if (n instanceof org.w3c.dom.Comment comment)
				cr.addComment(comment);
			else if (n.getNodeName().equals(reader.getTags().itemTag()))
				try (var r = cr.push(new PathIndexRef(index)))
				{
					result[index++] = reader.read(r, n);
				}
			else
			{
				System.err.printf("Unexpected element '%s'\n", n.getNodeName());
				cr.clear();
			}
		
		return result;
	}
}
