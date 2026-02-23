package kryptonbutterfly.xmlConfig4J.exceptions;

@SuppressWarnings("serial")
public class BindingException extends Exception
{
	public BindingException()
	{
		super();
	}
	
	public BindingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
	public BindingException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public BindingException(String message)
	{
		super(message);
	}
	
	public BindingException(Throwable cause)
	{
		super(cause);
	}
}
