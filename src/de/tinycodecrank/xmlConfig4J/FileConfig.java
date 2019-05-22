package de.tinycodecrank.xmlConfig4J;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileLock;

import javax.xml.transform.stream.StreamResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class FileConfig extends AConfig
{
	private static final Logger log = LogManager.getLogger(FileConfig.class);
	
	private File configFile;

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
		FileInputStream iStream = new FileInputStream(configFile);
		FileLock lock = iStream.getChannel().lock(0, Long.MAX_VALUE, true);
		InputStream prepIStream = prepareInput(iStream, lock);
		loadPrepared(prepIStream);
		iStream.close();
	}
	
	public void save()
	{
		if (!configFile.getParentFile().exists())
		{
			if (!configFile.getParentFile().mkdirs())
			{
				log.warn(String.format("Failed to create config Folder: %s", configFile.getAbsoluteFile()));
			}
		}

		try(FileOutputStream fOStream = new FileOutputStream(configFile))
		{
			FileLock lock = fOStream.getChannel().lock();
			StreamResult output = new StreamResult(fOStream);

			save(output);
	
			fOStream.flush();
			lock.release();
			fOStream.close();
		}
		catch (IOException e)
		{
			log.error(e.getMessage(), e);
		}
	}
}