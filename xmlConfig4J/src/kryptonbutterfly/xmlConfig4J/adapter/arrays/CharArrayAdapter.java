package kryptonbutterfly.xmlConfig4J.adapter.arrays;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.Comments.CommentReader;
import kryptonbutterfly.xmlConfig4J.Comments.CommentWriter;
import kryptonbutterfly.xmlConfig4J.TypeAdapter;
import kryptonbutterfly.xmlConfig4J.XmlReader;
import kryptonbutterfly.xmlConfig4J.XmlWriter;
import kryptonbutterfly.xmlConfig4J.adapter.primitive.CharAdapter;
import kryptonbutterfly.xmlConfig4J.comments.path.PathIndexRef;
import kryptonbutterfly.xmlConfig4J.utils.IntRange;
import kryptonbutterfly.xmlConfig4J.utils.Nodes;

public final class CharArrayAdapter implements TypeAdapter<char[]>
{
	@Override
	public Class<char[]> getType()
	{
		return char[].class;
	}
	
	@Override
	public void write(CommentWriter cw, XmlWriter writer, Element elem, char[] value)
	{
		if (value == null)
			writer.writeNull(elem);
		else
			for (int i : new IntRange(value.length))
			{
				final char	e		= value[i];
				final var	item	= writer.doc.createElement(writer.getTags().itemTag());
				try (var _w = cw.push(item, new PathIndexRef(i)))
				{
					CharAdapter.write(writer.getTags().valueTag(), item, e);
					elem.appendChild(item);
				}
			}
	}
	
	@Override
	public char[] read(CommentReader cr, XmlReader reader, Node node, Class<?> classOfT)
	{
		if (reader.isNull(node))
			return null;
		
		final var nodes = new Nodes(node.getChildNodes());
		
		final var result = new char[(int) nodes.stream().filter(n -> isItemNode(reader, n)).count()];
		reader.registerInstance(node, result);
		
		int index = 0;
		for (final var n : nodes)
			if (n instanceof org.w3c.dom.Comment comment)
				cr.addComment(comment);
			else if (n.getNodeName().equals(reader.getTags().itemTag()))
				try (var _r = cr.push(new PathIndexRef(index)))
				{
					result[index++] = CharAdapter.read(reader.getTags().valueTag(), n);
				}
			else
			{
				System.err.printf("Unexpected element '%s'\n", n.getNodeName());
				cr.clear();
			}
		
		return result;
	}
}
