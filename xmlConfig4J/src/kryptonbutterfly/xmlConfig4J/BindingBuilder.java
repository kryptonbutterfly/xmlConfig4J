package kryptonbutterfly.xmlConfig4J;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import kryptonbutterfly.xmlConfig4J.adapter.arrays.BoolArrayAdapter;
import kryptonbutterfly.xmlConfig4J.adapter.arrays.ByteArrayAdapter;
import kryptonbutterfly.xmlConfig4J.adapter.arrays.CharArrayAdapter;
import kryptonbutterfly.xmlConfig4J.adapter.arrays.DoubleArrayAdapter;
import kryptonbutterfly.xmlConfig4J.adapter.arrays.FloatArrayAdapter;
import kryptonbutterfly.xmlConfig4J.adapter.arrays.IntArrayAdapter;
import kryptonbutterfly.xmlConfig4J.adapter.arrays.LongArrayAdapter;
import kryptonbutterfly.xmlConfig4J.adapter.arrays.ObjectArrayAdapter;
import kryptonbutterfly.xmlConfig4J.adapter.arrays.ShortArrayAdapter;
import kryptonbutterfly.xmlConfig4J.adapter.boxed.BoolObjAdapter;
import kryptonbutterfly.xmlConfig4J.adapter.boxed.ByteObjAdapter;
import kryptonbutterfly.xmlConfig4J.adapter.boxed.CharObjAdapter;
import kryptonbutterfly.xmlConfig4J.adapter.boxed.DoubleObjAdapter;
import kryptonbutterfly.xmlConfig4J.adapter.boxed.FloatObjAdapter;
import kryptonbutterfly.xmlConfig4J.adapter.boxed.IntObjAdapter;
import kryptonbutterfly.xmlConfig4J.adapter.boxed.LongObjAdapter;
import kryptonbutterfly.xmlConfig4J.adapter.boxed.ShortObjAdapter;
import kryptonbutterfly.xmlConfig4J.adapter.collections.ListAdapter;
import kryptonbutterfly.xmlConfig4J.adapter.collections.MapAdapter;
import kryptonbutterfly.xmlConfig4J.adapter.collections.SetAdapter;
import kryptonbutterfly.xmlConfig4J.adapter.misc.StringAdapter;
import kryptonbutterfly.xmlConfig4J.adapter.misc.UuidAdapter;
import kryptonbutterfly.xmlConfig4J.adapter.primitive.BoolAdapter;
import kryptonbutterfly.xmlConfig4J.adapter.primitive.ByteAdapter;
import kryptonbutterfly.xmlConfig4J.adapter.primitive.CharAdapter;
import kryptonbutterfly.xmlConfig4J.adapter.primitive.DoubleAdapter;
import kryptonbutterfly.xmlConfig4J.adapter.primitive.FloatAdapter;
import kryptonbutterfly.xmlConfig4J.adapter.primitive.IntAdapter;
import kryptonbutterfly.xmlConfig4J.adapter.primitive.LongAdapter;
import kryptonbutterfly.xmlConfig4J.adapter.primitive.ShortAdapter;
import kryptonbutterfly.xmlConfig4J.annotations.Value;

public class BindingBuilder
{
	private final HashMap<String, Class<?>>				classNameHistory		= new HashMap<>();
	private final HashSet<Class<? extends Annotation>>	includeFieldAnnotations	= new HashSet<>(
		Set.of(Value.class));
	
	private final ArrayList<TypeAdapter<?>> adapterMap = new ArrayList<>(
		List.of(
			new BoolAdapter(),
			new ByteAdapter(),
			new CharAdapter(),
			new ShortAdapter(),
			new IntAdapter(),
			new FloatAdapter(),
			new LongAdapter(),
			new DoubleAdapter(),
			
			new BoolArrayAdapter(),
			new ByteArrayAdapter(),
			new CharArrayAdapter(),
			new ShortArrayAdapter(),
			new IntArrayAdapter(),
			new FloatArrayAdapter(),
			new LongArrayAdapter(),
			new DoubleArrayAdapter(),
			new ObjectArrayAdapter(),
			
			new BoolObjAdapter(),
			new ByteObjAdapter(),
			new CharObjAdapter(),
			new ShortObjAdapter(),
			new IntObjAdapter(),
			new LongObjAdapter(),
			new FloatObjAdapter(),
			new DoubleObjAdapter(),
			
			new ListAdapter(),
			new MapAdapter(),
			new SetAdapter(),
			
			new StringAdapter(),
			new UuidAdapter()));
	
	private boolean	mapTypes		= true;
	private String	rootTag			= "root";
	private String	typesTag		= "types";
	private String	dataTag			= "data";
	private boolean	indent			= true;
	private int		indentAmount	= 4;
	
	public BindingBuilder addClassNameMapping(String oldName, Class<?> newType)
	{
		classNameHistory.put(oldName, newType);
		return this;
	}
	
	public BindingBuilder addTypeAdapter(TypeAdapter<?> adapter)
	{
		adapterMap.add(adapter);
		return this;
	}
	
	public BindingBuilder addIncludeAnnotation(Class<? extends Annotation> annotation)
	{
		includeFieldAnnotations.add(annotation);
		return this;
	}
	
	public BindingBuilder mapTypes(boolean mapTypes)
	{
		this.mapTypes = mapTypes;
		return this;
	}
	
	public BindingBuilder rootTag(String rootTag)
	{
		this.rootTag = rootTag;
		return this;
	}
	
	public BindingBuilder typesTag(String typesTag)
	{
		this.typesTag = typesTag;
		return this;
	}
	
	public BindingBuilder dataTag(String dataTag)
	{
		this.dataTag = dataTag;
		return this;
	}
	
	public BindingBuilder indent(boolean indent)
	{
		this.indent = indent;
		return this;
	}
	
	public BindingBuilder indent(int indentAmount)
	{
		this.indentAmount = indentAmount;
		return this;
	}
	
	public XmlDataBinding build()
	{
		return new XmlDataBinding(
			classNameHistory,
			adapterMap,
			mapTypes,
			rootTag,
			typesTag,
			dataTag,
			includeAnnotations(),
			indent,
			indentAmount);
	}
	
	private Function<Field, ? extends Annotation> includeAnnotations()
	{
		final var annotations = Collections.unmodifiableSet(includeFieldAnnotations);
		return field -> annotations.stream()
			.map(field::getAnnotation)
			.filter(Objects::nonNull)
			.findAny()
			.orElse(null);
	}
}