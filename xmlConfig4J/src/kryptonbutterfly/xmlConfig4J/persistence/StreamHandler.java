package kryptonbutterfly.xmlConfig4J.persistence;

public interface StreamHandler<S, E extends Exception>
{
	public void useStream(ConsumerThrowing<S, E> streamUser);
	
	public static interface ConsumerThrowing<S, E extends Exception>
	{
		public void use(S stream) throws E;
	}
}
