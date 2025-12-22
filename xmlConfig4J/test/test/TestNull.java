package test;

import java.awt.Color;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import components.Comp;
import kryptonbutterfly.xmlConfig4J.annotations.Value;
import utils.GenericUtils;
import utils.Validator;

public class TestNull implements Validator, GenericUtils
{
	@Value
	public String a = null;
	
	@Value
	public Comp b = null;
	
	@Value
	public List<String> c = null;
	
	@Value
	public Set<String> d = null;
	
	@Value
	public Map<String, String> e = null;
	
	@Value
	public boolean[] f = null;
	
	@Value
	public byte[] g = null;
	
	@Value
	public char[] h = null;
	
	@Value
	public short[] i = null;
	
	@Value
	public int[] j = null;
	
	@Value
	public float[] k = null;
	
	@Value
	public long[] l = null;
	
	@Value
	public double[] m = null;
	
	@Value
	public Boolean n = null;
	
	@Value
	public Byte o = null;
	
	@Value
	public Character p = null;
	
	@Value
	public Short q = null;
	
	@Value
	public Integer r = null;
	
	@Value
	public Float s = null;
	
	@Value
	public Long t = null;
	
	@Value
	public Double u = null;
	
	@Value
	public String[] v = null;
	
	@Value
	public Object[][] w = null;
	
	@Value
	public UUID x = null;
	
	@Value
	public Color y = null;
	
	@Test
	public void test()
	{
		validate();
	}
	
	@Override
	public String toString()
	{
		return toString(this);
		// return """
		// TestNull{
		// a='%s'
		// b=%s
		// c=%s
		// d=%s
		// e=%s
		// f=%s
		// g=%s
		// h=%s
		// i=%s
		// j=%s
		// k=%s
		// l=%s
		// m=%s
		// n=%s
		// o=%s
		// }
		// """.formatted(
		// a,
		// b,
		// c,
		// d,
		// e,
		// Arrays.toString(f),
		// Arrays.toString(g),
		// Arrays.toString(h),
		// Arrays.toString(i),
		// Arrays.toString(j),
		// Arrays.toString(k),
		// Arrays.toString(l),
		// Arrays.toString(m),
		// Arrays.toString(v),
		// Arrays.toString(w));
	}
	
	@Override
	public boolean equals(Object o)
	{
		return equals(this, o);
		// if (!(o instanceof TestNull testNull))
		// return false;
		// return Objects.equals(a, testNull.a)
		// && b == testNull.b
		// && Objects.equals(c, testNull.c)
		// && Objects.equals(d, testNull.d)
		// && Objects.equals(e, testNull.e)
		// && Objects.deepEquals(f, testNull.f)
		// && Objects.deepEquals(g, testNull.g)
		// && Objects.deepEquals(h, testNull.h)
		// && Objects.deepEquals(i, testNull.i)
		// && Objects.deepEquals(j, testNull.j)
		// && Objects.deepEquals(k, testNull.k)
		// && Objects.deepEquals(l, testNull.l)
		// && Objects.deepEquals(m, testNull.m)
		// && Objects.deepEquals(v, testNull.v);
	}
	
	@Override
	public int hashCode()
	{
		return hashCode(this);
		// return Objects.hash(
		// a,
		// b,
		// c,
		// d,
		// e,
		// Arrays.hashCode(f),
		// Arrays.hashCode(g),
		// Arrays.hashCode(h),
		// Arrays.hashCode(i),
		// Arrays.hashCode(j),
		// Arrays.hashCode(k),
		// Arrays.hashCode(l),
		// Arrays.hashCode(m),
		// Arrays.hashCode(v));
	}
}
