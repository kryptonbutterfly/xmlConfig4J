package de.tinycodecrank.xmlConfig4J;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

public final class Accessor<E extends AccessibleObject & Member>
{
	private final Object	parent;
	private final E			element;
	
	public Accessor(Object parent, E element)
	{
		this.parent		= parent;
		this.element	= element;
	}
	
	public <Value> void perform(AccessConsumer<E, Object, Value> action, Value value)
		throws IllegalArgumentException,
		IllegalAccessException
	{
		final boolean isAccessible = makeAccessible();
		try
		{
			action.accept(element, parent, value);
		}
		finally
		{
			revertAccess(isAccessible);
		}
	}
	
	public <Return> Return perform(AccessSupplier<E, Object, Return> action)
		throws IllegalArgumentException,
		IllegalAccessException
	{
		final boolean isAccessible = makeAccessible();
		try
		{
			return action.get(element, parent);
		}
		finally
		{
			revertAccess(isAccessible);
		}
	}
	
	private boolean makeAccessible()
	{
		final boolean isAccessible;
		if (Modifier.isStatic(element.getModifiers()))
			isAccessible = element.canAccess(null);
		else
			isAccessible = element.canAccess(parent);
		element.setAccessible(true);
		return isAccessible;
	}
	
	private void revertAccess(boolean previous)
	{
		element.setAccessible(previous);
	}
	
	@FunctionalInterface
	public static interface AccessConsumer<Parent, AccObj, Value>
	{
		void accept(Parent a, AccObj b, Value v) throws IllegalArgumentException, IllegalAccessException;
	}
	
	@FunctionalInterface
	public static interface AccessSupplier<Parent, AccObj, Return>
	{
		Return get(Parent a, AccObj b) throws IllegalArgumentException, IllegalAccessException;
	}
}