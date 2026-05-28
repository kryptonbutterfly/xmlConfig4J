package test.comments;

import static org.junit.jupiter.api.Assertions.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.jupiter.api.Test;

import kryptonbutterfly.xmlConfig4J.Comments;
import kryptonbutterfly.xmlConfig4J.annotations.Value;
import utils.GenericUtils;
import utils.Validator;

public final class TestCommentArrays implements Validator, GenericUtils
{
	@Value
	public boolean[] bool = null;
	
	@Value
	public byte[] bytes = null;
	
	@Value
	public char[] chars = null;
	
	@Value
	public short[] shorts = null;
	
	@Value
	public int[] ints = null;
	
	@Value
	public long[] longs = null;
	
	@Value
	public float[] floats = null;
	
	@Value
	public double[] doubles = null;
	
	@Value
	public Object[] objs = null;
	
	private static final String input = """
			<?xml version="1.0" encoding="UTF-8" standalone="no"?>
			<root>
			    <types>
			        <item id="1" name="java.lang.String"/>
			        <item id="0" name="test.comments.TestCommentArrays"/>
			    </types>
			    <data type="0">
			        <bool>
			            <!-- bool true -->
			            <item value="true"/>
			            <!-- bool false -->
			            <item value="false"/>
			            <!-- bool inner -->
			        </bool>
			        <bytes>
			            <!-- bytes 0 -->
			            <item value="#0"/>
			            <!-- bytes 1 -->
			            <item value="#1"/>
			            <!-- bytes 2 -->
			            <item value="#2"/>
			            <!-- bytes 3 -->
			        </bytes>
			        <chars>
			            <!-- chars 1 -->
			            <item value="#67"/>
			            <!-- chars 2 -->
			            <item value="#54"/>
			            <!-- chars 3 -->
			            <item value="#5f"/>
			            <!-- chars inner -->
			        </chars>
			        <shorts>
			            <!-- shorts 1 -->
			            <item value="234"/>
			            <!-- shorts 2 -->
			            <item value="-19891"/>
			            <!-- shorts inner -->
			        </shorts>
			        <ints>
			            <!-- ints 1 -->
			            <item value="4657567"/>
			            <!-- ints 2 -->
			            <item value="-3564456"/>
			            <!-- ints inner -->
			        </ints>
			        <longs>
			            <!-- longs 1 -->
			            <item value="-4567456745675467"/>
			            <!-- longs 2 -->
			            <item value="7897683765474567856"/>
			            <!-- longs inner -->
			        </longs>
			        <floats>
			            <!-- floats 1 -->
			            <item value="123.43"/>
			            <!-- floats 2 -->
			            <item value="-456.123"/>
			            <!-- floats inner -->
			        </floats>
			        <doubles>
			            <!-- doubles 1 -->
			            <item value="1234512.12"/>
			            <!-- doubles 2 -->
			            <item value="-12341.1"/>
			            <!-- doubles inner -->
			        </doubles>
			        <objs>
			            <!-- objs 1 -->
			            <item type="1" value="Hello, "/>
			            <!-- objs 2 -->
			            <item type="1" value="Comments!"/>
			            <!-- objs inner -->
			        </objs>
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
