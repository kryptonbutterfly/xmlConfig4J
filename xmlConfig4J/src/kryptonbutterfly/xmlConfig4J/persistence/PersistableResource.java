package kryptonbutterfly.xmlConfig4J.persistence;

import java.io.IOException;
import java.util.function.Supplier;

public interface PersistableResource<T> extends AutoCloseable
{
	public T data();
	
	public void data(T data);
	
	void load(Supplier<T> fallback) throws IOException;
	
	public void persist() throws Exception;
	
	@Override
	default void close() throws Exception
	{
		persist();
	}
}
