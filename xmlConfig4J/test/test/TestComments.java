package test;

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

public final class TestComments implements Validator, GenericUtils
{
	@Value
	public boolean bool = false;
	
	@Value
	public boolean[] bools = { false, true };
	
	@Value
	public Boolean[] bools2 = { Boolean.FALSE, Boolean.TRUE };
	
	@Value
	public String str = "Hello, Comments!";
	
	@Value
	public HashMap<String, String> map = new HashMap<>();
	
	@Value
	public HashSet<String> set = new HashSet<>();
	
	@Value
	public ArrayList<String> list = new ArrayList<>();
	
	private static final String input = """
			<?xml version="1.0" encoding="UTF-8" standalone="no"?>
			<!-- pre root comment 1! --><root>
			    <!-- pre types comment 1! -->
			    <!-- pre types comment 2! -->
			    <types>
			        <item id="0" name="test.TestComments"/>
			        <!-- type: java.lang.Boolean -->
			        <item id="1" name="java.lang.Boolean"/>
			        <!-- type: java.lang.String -->
			        <item id="2" name="java.lang.String"/>
			        <!-- types - inner 1 -->
			        <!-- types - inner 2 -->
			    </types>
			    <!-- pre data comment 1! -->
			    <data type="0">
			        <!-- /root/data/bool – pre1 -->
			        <!-- /root/data/bool – pre2 -->
			        <bool value="false"/>
			        <!-- /root/data/bools – pre1 -->
			        <bools>
			            <!-- /root/data/bools/0 – pre1 -->
			            <item value="false"/>
			            <!-- /root/data/bools/1 – pre2 -->
			            <item value="true"/>
			            <!-- /root/data/bools – inner1 -->
			        </bools>
			        <!-- /root/data/bools2 – pre1 -->
			        <bools2>
			            <item type="1" value="false"/>
			            <item type="1" value="true"/>
			        </bools2>
			        <str value="Hello, Comments!"/>
			        <!-- /root/data/map – pre1 -->
			        <map>
			            <!-- /root/data/map/e0 – pre -->
			            <item>
			                <!-- /root/data/map/e0/key – pre1 -->
			                <key type="2" value="k1"/>
			                <!-- /root/data/map/e0/val – pre1 -->
			                <value type="2" value="v1"/>
			                <!-- /root/data/map/e0 – inner1 -->
			            </item>
			            <!-- /root/data/map/e1 – pre -->
			            <item>
			                <key type="2" value="k2"/>
			                <value type="2" value="v2"/>
			            </item>
			            <!-- /root/data/map/e2 – pre -->
			            <item>
			                <key type="2" value="k3"/>
			                <value type="2" value="v3"/>
			            </item>
			            <!-- /root/data/map/e3 – pre -->
			            <item>
			                <key type="2" value="k4"/>
			                <value type="2" value="v4"/>
			            </item>
			            <!-- /root/data/map – inner -->
			        </map>
			        <!-- /root/data/set – pre -->
			        <set>
			            <!-- /root/data/set/e0 – pre -->
			            <item type="2" value="v1"/>
			            <!-- /root/data/set/e1 – pre -->
			            <item type="2" value="v2"/>
			            <!-- /root/data/set/e2 – pre -->
			            <item type="2" value="v3"/>
			            <!-- /root/data/set/e3 – pre -->
			            <item type="2" value="v4"/>
			        </set>
			        <!-- /root/data/list – pre -->
			        <list>
			            <!-- /root/data/list/0 – pre -->
			            <item type="2" value="1"/>
			            <!-- /root/data/list/1 – pre -->
			            <item type="2" value="2"/>
			            <item type="2" value="3"/>
			            <item type="2" value="4"/>
			            <item type="2" value="5"/>
			        </list>
			        <!-- /root/data – inner -->
			    </data>
			    <!-- /root – inner -->
			</root><!-- / – inner -->
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
