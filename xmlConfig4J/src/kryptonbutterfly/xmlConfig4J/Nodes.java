package kryptonbutterfly.xmlConfig4J;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public record Nodes(NodeList list) implements Iterable<Node>
{
	public Iterator<Node> iterator()
	{
		return new Iterator<>()
		{
			private int i = 0;
			
			@Override
			public boolean hasNext()
			{
				return i < list.getLength();
			}
			
			@Override
			public Node next()
			{
				return list.item(i++);
			}
		};
	}
	
	public Stream<Node> stream()
	{
		return StreamSupport.stream(spliterator(), false);
	}
}
