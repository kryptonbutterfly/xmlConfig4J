package test.comments;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.jupiter.api.Test;

import kryptonbutterfly.xmlConfig4J.Comments;
import kryptonbutterfly.xmlConfig4J.annotations.Value;
import utils.GenericUtils;
import utils.Validator;

public final class TestCommentCollection implements Validator, GenericUtils
{
	@Value
	public ArrayList<String> list = null;
	
	@Value
	public HashSet<String> set = null;
	
	@Value
	public HashMap<String, String> map = null;
	
	private static final String input = """
			<?xml version="1.0" encoding="UTF-8" standalone="no"?>
			<root>
			    <types>
			        <item id="0" name="test.comments.TestCommentCollection"/>
			        <item id="1" name="java.lang.String"/>
			    </types>
			    <data type="0">
			        <list>
			            <item type="1" value="Hello, "/>
			            <item type="1" value="World!"/>
			            <item null="true"/>
			        </list>
			        <set>
			            <item null="true"/>
			            <item type="1" value="test"/>
			        </set>
			        <map>
			            <item>
			                <key type="1" value="key1"/>
			                <value type="1" value="value1"/>
			            </item>
			            <item>
			                <key null="true"/>
			                <value type="1" value="null"/>
			            </item>
			        </map>
			    </data>
			</root>
			""";
	
	@Test
	public void test() throws IllegalAccessException, ParserConfigurationException, TransformerException
	{
		var	comments	= Comments.create();
		var	value		= Validator.c4j.fromXml(comments, input);
		var	xml			= Validator.c4j.toXml(comments, value);
		
		assertEquals(input, xml);
	}
	
	@Override
	public String toString()
	{
		return toString(this);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		return equals(this, obj);
	}
	
	@Override
	public int hashCode()
	{
		return hashCode(this);
	}
}
