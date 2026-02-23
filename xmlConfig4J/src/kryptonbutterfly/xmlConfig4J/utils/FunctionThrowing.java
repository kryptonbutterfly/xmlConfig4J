package kryptonbutterfly.xmlConfig4J.utils;

@FunctionalInterface
public interface FunctionThrowing<T, A, E extends Throwable>
{
	public T apply(A arg1) throws E;
}
