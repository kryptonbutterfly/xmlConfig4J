package test.comments;

import static org.junit.jupiter.api.Assertions.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.jupiter.api.Test;

import kryptonbutterfly.xmlConfig4J.Comments;
import kryptonbutterfly.xmlConfig4J.annotations.Value;
import utils.GenericUtils;
import utils.Validator;

public final class TestCommentBoxed implements Validator, GenericUtils
{
	@Value
	public Boolean z = null;
	
	@Value
	public Byte b = null;
	
	@Value
	public Character c = null;
	
	@Value
	public Short s = null;
	
	@Value
	public Integer i = null;
	
	@Value
	public Long l = null;
	
	@Value
	public Float f = null;
	
	@Value
	public Double d = null;
	
	private static final String input = """
			<?xml version="1.0" encoding="UTF-8" standalone="no"?>
			<root>
			    <types>
			        <item id="0" name="test.comments.TestCommentBoxed"/>
			    </types>
			    <data type="0">
			        <z value="true"/>
			        <b value="#7b"/>
			        <c value="Æ"/>
			        <s value="1233"/>
			        <i value="-432134"/>
			        <l value="12312312313"/>
			        <f value="0.1"/>
			        <d value="756365.123"/>
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
