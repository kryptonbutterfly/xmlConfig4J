package de.tinycodecrank.xmlConfig4J;

import java.security.AccessController;
import java.security.PrivilegedAction;

final class PrivAction implements PrivilegedAction<Void>
{
	private final Runnable action;
	
	PrivAction(Runnable action)
	{
		this.action = action;
	}
	
	@Override
	public Void run()
	{
		action.run();
		return null;
	}
	
	static void doPrivileged(Runnable runnable)
	{
		AccessController.doPrivileged(new PrivAction(runnable));
	}
}