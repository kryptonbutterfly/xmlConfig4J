package kryptonbutterfly.xmlConfig4J;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;

import javax.xml.transform.stream.StreamResult;

public class StreamConfig extends AConfig
{
	public StreamConfig(Class<? extends Annotation> includeFieldMarker)
	{
		super(includeFieldMarker);
	}
	
	public StreamConfig()
	{}
	
	public void load(InputStream iStream) throws IOException
	{
		loadPrepared(prepareInput(iStream));
	}
	
	public void save(OutputStream oStream)
	{
		save(new StreamResult(oStream));
	}
}