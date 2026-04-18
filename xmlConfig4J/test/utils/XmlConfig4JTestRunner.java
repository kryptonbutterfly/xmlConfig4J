package utils;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import test.TestArray;
import test.TestBoxed;
import test.TestColor;
import test.TestCyclicData;
import test.TestEnum;
import test.TestList;
import test.TestMap;
import test.TestNull;
import test.TestPrimitive;
import test.TestRecord;
import test.TestSet;
import test.TestUuid;
import test.TestXml;

public final class XmlConfig4JTestRunner
{
	public static void main(String[] args)
		throws IllegalAccessException,
		ParserConfigurationException,
		TransformerException
	{
		final var tA = new TestArray();
		tA.testBoolean();
		tA.testByte();
		tA.testShort();
		tA.testChar();
		tA.testInt();
		tA.testFloat();
		tA.testLong();
		tA.testDouble();
		tA.testNested();
		tA.testNested2();
		
		new TestBoxed().test();
		new TestColor().test();
		new TestCyclicData().test();
		new TestEnum().testEnumValues();
		new TestEnum().test();
		new TestList().test();
		new TestMap().test();
		new TestNull().test();
		new TestPrimitive().test();
		new TestRecord().test();
		new TestRecord().testRepeating();
		new TestSet().test();
		new TestUuid().test();
		new TestXml().test();
	}
}
