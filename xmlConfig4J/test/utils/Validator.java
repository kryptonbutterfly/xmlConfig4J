package utils;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import kryptonbutterfly.xmlConfig4J.BindingBuilder;
import kryptonbutterfly.xmlConfig4J.XmlDataBinding;
import kryptonbutterfly.xmlConfig4J.adapter.misc.AwtColorAdapter;

public interface Validator
{
	public static final XmlDataBinding c4j = new BindingBuilder()
		.addTypeAdapter(new AwtColorAdapter())
		.build();
	
	default void validate()
	{
		validate(this);
	}
	
	static <T> void validate(T data)
	{
		validate(data, false);
	}
	
	static <T> void validate2(T data)
	{
		try
		{
			final String	xml1	= c4j.toXml(data);
			final var		data2	= c4j.fromXml(xml1);
			final String	xml2	= c4j.toXml(data2);
			assertEquals(xml1, xml2);
		}
		catch (Exception e)
		{
			try
			{
				Thread.sleep(100);
			}
			catch (InterruptedException e1)
			{}
			throw new RuntimeException(e);
		}
	}
	
	static <T> void validate(T data, boolean printXml)
	{
		try
		{
			final var xml = c4j.toXml(data);
			try
			{
				final var result = c4j.fromXml(xml);
				if (data instanceof boolean[] b)
					assertArrayEquals(b, (boolean[]) result, "xml was:\n" + xml);
				else if (data instanceof byte[] b)
					assertArrayEquals(b, (byte[]) result, "xml was:\n" + xml);
				else if (data instanceof char[] c)
					assertArrayEquals(c, (char[]) result, "xml was:\n" + xml);
				else if (data instanceof short[] s)
					assertArrayEquals(s, (short[]) result, "xml was:\n" + xml);
				else if (data instanceof int[] i)
					assertArrayEquals(i, (int[]) result, "xml was:\n" + xml);
				else if (data instanceof float[] f)
					assertArrayEquals(f, (float[]) result, "xml was:\n" + xml);
				else if (data instanceof long[] l)
					assertArrayEquals(l, (long[]) result, "xml was:\n" + xml);
				else if (data instanceof double[] d)
					assertArrayEquals(d, (double[]) result, "xml was:\n" + xml);
				else if (data instanceof Object[] o)
					assertArrayEquals(o, (Object[]) result, "xml was:\n" + xml);
				else
					assertEquals(data, result, "xml was:\n" + xml);
				if (printXml)
					System.out.printf("xml:%s\n", xml);
			}
			catch (Throwable e)
			{
				System.out.printf("""
						```xml
						%s```
						
						%s
						""", xml, e.getMessage());
				throw e;
			}
		}
		catch (Throwable e)
		{
			try
			{
				Thread.sleep(Duration.of(100, ChronoUnit.MILLIS));
			}
			catch (InterruptedException e1)
			{}
			throw new RuntimeException(e);
		}
	}
}
