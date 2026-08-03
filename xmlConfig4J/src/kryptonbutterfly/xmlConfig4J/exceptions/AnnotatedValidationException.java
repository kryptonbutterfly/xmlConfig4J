package kryptonbutterfly.xmlConfig4J.exceptions;

import java.io.Serial;

public final class AnnotatedValidationException extends RuntimeException
{
	@Serial
	private static final long serialVersionUID = 7577888958072451447L;
	
	public AnnotatedValidationException(String message, Object... args)
	{
		super(message.formatted(args));
	}
}