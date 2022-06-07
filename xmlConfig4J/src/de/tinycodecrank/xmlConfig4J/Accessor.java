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
	
	public void perform(AccessBoolConsumer<E, Object> action, boolean value)
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
	
	public void perform(AccessByteConsumer<E, Object> action, byte value)
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
	
	public void perform(AccessShortConsumer<E, Object> action, short value)
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
	
	public void perform(AccessCharConsumer<E, Object> action, char value)
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
	
	public void perform(AccessIntConsumer<E, Object> action, int value)
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
	
	public void perform(AccessLongConsumer<E, Object> action, long value)
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
	
	public void perform(AccessFloatConsumer<E, Object> action, float value)
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
	
	public void perform(AccessDoubleConsumer<E, Object> action, double value)
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
	public static interface AccessConsumer<AccObj, Parent, Value>
	{
		void accept(AccObj obj, Parent parent, Value value) throws IllegalArgumentException, IllegalAccessException;
	}
	
	@FunctionalInterface
	public static interface AccessBoolConsumer<AccObj, Parent>
	{
		void accept(AccObj obj, Parent parent, boolean value) throws IllegalArgumentException, IllegalAccessException;
	}
	
	@FunctionalInterface
	public static interface AccessByteConsumer<AccObj, Parent>
	{
		void accept(AccObj obj, Parent parent, byte value) throws IllegalArgumentException, IllegalAccessException;
	}
	
	@FunctionalInterface
	public static interface AccessShortConsumer<AccObj, Parent>
	{
		void accept(AccObj obj, Parent parent, short value) throws IllegalArgumentException, IllegalAccessException;
	}
	
	@FunctionalInterface
	public static interface AccessCharConsumer<AccObj, Parent>
	{
		void accept(AccObj obj, Parent parent, char value) throws IllegalArgumentException, IllegalAccessException;
	}
	
	@FunctionalInterface
	public static interface AccessIntConsumer<AccObj, Parent>
	{
		void accept(AccObj obj, Parent parent, int value) throws IllegalArgumentException, IllegalAccessException;
	}
	
	@FunctionalInterface
	public static interface AccessLongConsumer<AccObj, Parent>
	{
		void accept(AccObj obj, Parent parent, long value) throws IllegalArgumentException, IllegalAccessException;
	}
	
	@FunctionalInterface
	public static interface AccessFloatConsumer<AccObj, Parent>
	{
		void accept(AccObj obj, Parent parent, float value) throws IllegalArgumentException, IllegalAccessException;
	}
	
	@FunctionalInterface
	public static interface AccessDoubleConsumer<AccObj, Parent>
	{
		void accept(AccObj obj, Parent parent, double value) throws IllegalArgumentException, IllegalAccessException;
	}
	
	@FunctionalInterface
	public static interface AccessSupplier<AccObj, Parent, Return>
	{
		Return get(AccObj obj, Parent parent) throws IllegalArgumentException, IllegalAccessException;
	}
}