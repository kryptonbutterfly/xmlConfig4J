package kryptonbutterfly.xmlConfig4J;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Iterator;

public record Nodes(NodeList list) implements Iterable<Node>
{
    public Iterator<Node> iterator() {
        return new Iterator<>(){
            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < list.getLength();
            }

            @Override
            public Node next() {
                return list.item(i++);
            }
        };
    }
}
