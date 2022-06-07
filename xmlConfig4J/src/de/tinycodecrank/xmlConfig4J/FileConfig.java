package de.tinycodecrank.xmlConfig4J;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.transform.stream.StreamResult;

public class FileConfig extends AConfig
{
	protected File configFile;
	
	public FileConfig(File config)
	{
		configFile = config;
	}
	
	public boolean exists()
	{
		return this.configFile.exists();
	}
	
	public void load() throws IOException
	{
		try (final var iStream = new FileInputStream(configFile))
		{
			final var	lock		= iStream.getChannel().lock(0, Long.MAX_VALUE, true);
			final var	prepIStream	= prepareInput(iStream, lock);
			loadPrepared(prepIStream);
		}
	}
	
	public void save()
	{
		if (!configFile.getParentFile().exists())
		{
			if (!configFile.getParentFile().mkdirs())
			{
				System.err.println("Failed to create config Folder: %s".formatted(configFile.getAbsoluteFile()));
			}
		}
		
		try (final var fOStream = new FileOutputStream(configFile))
		{
			final var	lock	= fOStream.getChannel().lock();
			final var	output	= new StreamResult(fOStream);
			
			save(output);
			
			fOStream.flush();
			lock.release();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}