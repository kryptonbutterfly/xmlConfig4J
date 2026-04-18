package kryptonbutterfly.xmlConfig4J.persistence;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.function.Supplier;

import kryptonbutterfly.xmlConfig4J.XmlDataBinding;

final class PersistableFileData<Data> implements PersistableResource<Data>
{
	private String					rawFileContent	= null;
	private final XmlDataBinding	binding;
	private final File				file;
	private Data						data			= null;
	private final Class<Data>			classOfT;
	private final boolean			persistNotDirty;
	
	PersistableFileData(XmlDataBinding binding, boolean persistNotDirty, File file, Class<Data> classOfT)
	{
		this.binding			= binding;
		this.file				= file;
		this.classOfT			= classOfT;
		this.persistNotDirty	= persistNotDirty;
	}
	
	@Override
	public void load(Supplier<Data> fallback) throws IOException
	{
		Objects.requireNonNull(fallback);
		
		if (!file.exists())
		{
			data			= fallback.get();
			rawFileContent	= null;
			return;
		}
		
		if (persistNotDirty)
		{
			if (classOfT == null)
				data = binding.<Data>fromXml(file);
			else
				data = binding.fromXml(file, classOfT);
			return;
		}
		
		rawFileContent = Files.readString(file.toPath());
		if (classOfT == null)
			data = binding.<Data>fromXml(rawFileContent);
		else
			data = binding.fromXml(rawFileContent, classOfT);
	}
	
	@Override
	public Data data()
	{
		return data;
	}
	
	@Override
	public void data(Data data)
	{
		this.data = data;
	}
	
	@Override
	public void persist() throws Exception
	{
		final var output = binding.toXml(data);
		
		if (!persistNotDirty && rawFileContent.equals(output) && file.exists())
			return;
		Files.writeString(
			file.toPath(),
			output,
			StandardOpenOption.CREATE,
			StandardOpenOption.TRUNCATE_EXISTING,
			StandardOpenOption.WRITE);
		rawFileContent = output;
	}
}
