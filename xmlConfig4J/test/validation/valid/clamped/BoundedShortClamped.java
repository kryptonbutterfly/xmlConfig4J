package validation.valid.clamped;

import static org.junit.jupiter.api.Assertions.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.jupiter.api.Test;

import kryptonbutterfly.xmlConfig4J.annotations.Bounded;
import utils.GenericUtils;
import utils.Validator;

public final class BoundedShortClamped implements GenericUtils
{
	@Bounded(minInclusive = 0, maxInclusive = 5, clamp = true)
	public short valid = 10;
	
	@Test
	public void test() throws IllegalAccessException, ParserConfigurationException, TransformerException
	{
		var	xml	= Validator.c4j.toXml(this);
		var	res	= Validator.c4j.fromXml(xml);
		this.valid = 5; // pretend to clamp value
		
		assertEquals(this, res);
	}
	
	@Override
	public int hashCode()
	{
		return hashCode(this);
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
}
