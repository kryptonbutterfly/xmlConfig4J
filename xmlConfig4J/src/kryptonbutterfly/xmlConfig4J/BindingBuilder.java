package kryptonbutterfly.xmlConfig4J;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

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
import kryptonbutterfly.xmlConfig4J.annotations.InfoProperty;
import kryptonbutterfly.xmlConfig4J.annotations.Value;
import kryptonbutterfly.xmlConfig4J.annotations.handlers.Handler;
import kryptonbutterfly.xmlConfig4J.utils.ArrayUtils;
import kryptonbutterfly.xmlConfig4J.utils.InternalConstants;

public final class BindingBuilder
{
	private final HashMap<String, Class<?>>												classNameHistory		= new HashMap<>();
	private final HashSet<Class<? extends Annotation>>									includeFieldAnnotations	= new HashSet<>();
	private final ArrayList<TypeAdapter<?>>												adapterMap				= new ArrayList<>();
	private final HashMap<XmlTags, String>												tags					= XmlTags
		.createTagsMap();
	private final HashMap<Class<? extends Annotation>, Handler<? extends Annotation>>	annotationHandler		= new HashMap<>();
	
	private boolean	mapTypes		= true;
	private boolean	indent			= true;
	private int		indentAmount	= 4;
	private boolean	declaredOnly	= true;
	
	public BindingBuilder()
	{
		addIncludeAnnotation(Value.class);
		
		addTypeAdapter(
			new BoolAdapter(),
			new ByteAdapter(),
			new CharAdapter(),
			new ShortAdapter(),
			new IntAdapter(),
			new FloatAdapter(),
			new LongAdapter(),
			new DoubleAdapter());
		
		addTypeAdapter(
			new BoolArrayAdapter(),
			new ByteArrayAdapter(),
			new CharArrayAdapter(),
			new ShortArrayAdapter(),
			new IntArrayAdapter(),
			new FloatArrayAdapter(),
			new LongArrayAdapter(),
			new DoubleArrayAdapter(),
			new ObjectArrayAdapter());
		
		addTypeAdapter(
			new BoolObjAdapter(),
			new ByteObjAdapter(),
			new CharObjAdapter(),
			new ShortObjAdapter(),
			new IntObjAdapter(),
			new LongObjAdapter(),
			new FloatObjAdapter(),
			new DoubleObjAdapter());
		
		addTypeAdapter(
			new ListAdapter(),
			new MapAdapter(),
			new SetAdapter());
		
		addTypeAdapter(
			new StringAdapter(),
			new UuidAdapter());
	}
	
	public BindingBuilder addClassNameMapping(String oldName, Class<?> newType)
	{
		Objects.requireNonNull(oldName);
		Objects.requireNonNull(newType);
		classNameHistory.put(oldName, newType);
		return this;
	}
	
	public BindingBuilder addTypeAdapter(TypeAdapter<?> adapter)
	{
		Objects.requireNonNull(adapter);
		adapterMap.add(adapter);
		return this;
	}
	
	public BindingBuilder addTypeAdapter(TypeAdapter<?>... adapter)
	{
		Objects.requireNonNull(adapter);
		for (final var a : adapter)
			addTypeAdapter(a);
		return this;
	}
	
	public BindingBuilder addIncludeAnnotation(Class<? extends Annotation> annotation)
	{
		Objects.requireNonNull(annotation);
		final Retention retention = annotation.getDeclaredAnnotation(Retention.class);
		if (retention == null || retention.value() != RetentionPolicy.RUNTIME)
			throw new IllegalStateException(
				"\n\tat %s.<cinit>(%s.java:-1) must be annotated with %s"
					.formatted(
						annotation.getName(),
						annotation.getSimpleName(),
						"@Retention(RUNTIME)"));
		
		final Target target = annotation.getDeclaredAnnotation(Target.class);
		if (target == null || !ArrayUtils.containsAll(target.value(), ElementType.FIELD, ElementType.RECORD_COMPONENT))
			throw new IllegalStateException(
				"\n\tat %s.<clinit>(%s.java:-1) must be annotated with %s"
					.formatted(
						annotation.getName(),
						annotation.getSimpleName(),
						"@Target({ FIELD, RECORD_COMPONENT })"));
		
		long infoCount = 0;
		for (var m : annotation.getDeclaredMethods())
		{
			final var info = m.getDeclaredAnnotation(InfoProperty.class);
			if (info != null)
			{
				infoCount++;
				if (!CharSequence.class.isAssignableFrom(m.getReturnType()))
					throw new IllegalStateException(
						"@%s is only allowed to be applied to a property of type or subtype of CharSequence, but was applied to \n\tat %s.%5$s(%s.java:-1) { %s %s }"
							.formatted(
								InfoProperty.class.getSimpleName(),
								annotation.getName(),
								annotation.getSimpleName(),
								m.getReturnType().getSimpleName(),
								m.getName()));
			}
		}
		if (infoCount > 1)
			throw new IllegalStateException(
				"At most one annotation property per annotation may be annotated with @%s, but \n\tat %s.<cinit>(%s.java:-1) has %d properties annotated with @%1$s."
					.formatted(
						InfoProperty.class.getSimpleName(),
						annotation.getName(),
						annotation.getSimpleName(),
						infoCount));
		
		includeFieldAnnotations.add(annotation);
		return this;
	}
	
	public BindingBuilder addAnnotationHandler(Handler<? extends Annotation> handler)
	{
		Objects.requireNonNull(handler);
		addIncludeAnnotation(handler.getType());
		annotationHandler.put(handler.getType(), handler);
		return this;
	}
	
	public BindingBuilder mapTypes(boolean mapTypes)
	{
		this.mapTypes = mapTypes;
		return this;
	}
	
	public BindingBuilder setTag(XmlTags tag, String tagValue)
	{
		Objects.requireNonNull(tag);
		Objects.requireNonNull(tagValue);
		// TODO add unit test
		if (!tagValue.matches(InternalConstants.XML_IDENTIFIER_MATCHER))
			throw new IllegalArgumentException("'%s' is not a valid xml identifier!".formatted(tagValue));
		
		this.tags.put(tag, tagValue);
		return this;
	}
	
	public BindingBuilder indent(boolean indent)
	{
		this.indent = indent;
		return this;
	}
	
	public BindingBuilder indent(int indentAmount)
	{
		// TODO add unit test
		if (indentAmount < 1)
			throw new IllegalArgumentException("The indent amount must be > 0, but was %d.".formatted(indentAmount));
		
		this.indentAmount = indentAmount;
		return this;
	}
	
	public BindingBuilder declaredOnly(boolean declaredOnly)
	{
		this.declaredOnly = declaredOnly;
		return this;
	}
	
	public XmlDataBinding build()
	{
		return new XmlDataBinding(
			classNameHistory,
			adapterMap,
			mapTypes,
			XmlTags.fromMap(tags),
			includeFieldAnnotations,
			annotationHandler,
			indent,
			indentAmount,
			declaredOnly);
	}
}