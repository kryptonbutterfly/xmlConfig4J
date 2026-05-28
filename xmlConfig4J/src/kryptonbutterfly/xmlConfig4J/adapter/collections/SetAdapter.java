package kryptonbutterfly.xmlConfig4J.adapter.collections;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import org.w3c.dom.Comment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.Comments.CommentReader;
import kryptonbutterfly.xmlConfig4J.Comments.CommentWriter;
import kryptonbutterfly.xmlConfig4J.Nodes;
import kryptonbutterfly.xmlConfig4J.TypeAdapter;
import kryptonbutterfly.xmlConfig4J.XmlReader;
import kryptonbutterfly.xmlConfig4J.XmlWriter;
import kryptonbutterfly.xmlConfig4J.comments.path.PathHashRef;
import kryptonbutterfly.xmlConfig4J.exceptions.AttributeNotFoundException;
import kryptonbutterfly.xmlConfig4J.exceptions.BrokenReferenceException;

@SuppressWarnings("rawtypes")
public final class SetAdapter implements TypeAdapter<Set>
{
	@Override
	public Class<Set> getType()
	{
		return Set.class;
	}
	
	@Override
	public void write(CommentWriter cw, XmlWriter writer, Element elem, Set value) throws IllegalAccessException
	{
		if (value == null)
			writer.writeNull(elem);
		else
			for (var child : value)
			{
				final var childElem = writer.doc.createElement(writer.getTags().itemTag());
				try (var wItem = cw.push(childElem, new PathHashRef(child)))
				{
					elem.appendChild(childElem);
					if (child == null)
						writer.writeNull(childElem);
					else
					{
						writer.write(wItem, childElem, child, child.getClass());
						writer.writeType(childElem, child.getClass());
					}
				}
			}
	}
	
	@Override
	public Set<?> read(CommentReader cr, XmlReader reader, Node node, Class<?> classOfT)
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
		final var set = (Set<?>) classOfT.getConstructor().newInstance();
		reader.registerInstance(node, set);
		
		for (final var n : new Nodes(node.getChildNodes()))
			if (n instanceof Comment comment)
				cr.addComment(comment);
			else if (n.getNodeName().equals(reader.getTags().itemTag()))
			{
				var ref = new PathHashRef();
				try (var rItem = cr.push(ref))
				{
					set.add(ref.init(reader.read(rItem, n)));
				}
			}
			else
			{
				System.err.printf("Unexpected element '%s'\n", n.getNodeName());
				cr.clear();
			}
		
		return set;
	}
}
