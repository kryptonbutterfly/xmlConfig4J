package kryptonbutterfly.xmlConfig4J.persistence;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.function.Supplier;

import kryptonbutterfly.xmlConfig4J.XmlDataBinding;

final class PersistableFileData<Data> extends PersistableResource<Data>
{
	private final File file;
	
	PersistableFileData(
		XmlDataBinding binding,
		boolean persistNotDirty,
		File file,
		Class<Data> classOfT,
		boolean preserveComments)
	{
		super(binding, persistNotDirty, classOfT, preserveComments);
		this.file = file;
	}
	
	@Override
	public void load(Supplier<Data> fallback) throws IOException
	{
		Objects.requireNonNull(fallback);
		
		if (!file.exists())
		{
			data			= fallback.get();
			rawContent	= null;
			return;
		}
		
		if (persistNotDirty)
		{
			if (classOfT == null)
				data = binding.<Data>fromXml(comments, file);
			else
				data = binding.fromXml(comments, file, classOfT);
			return;
		}
		
		rawContent = Files.readString(file.toPath());
		if (classOfT == null)
			data = binding.<Data>fromXml(comments, rawContent);
		else
			data = binding.fromXml(comments, rawContent, classOfT);
	}
	
	@Override
	public void persist() throws Exception
	{
		final var output = binding.toXml(comments, data);
		
		if (!persistNotDirty && Objects.equals(rawContent, output) && file.exists())
			return;
		
		final var parent = file.getParentFile();
		if (!parent.exists())
			parent.mkdirs();
		
		Files.writeString(
			file.toPath(),
			output,
			StandardOpenOption.CREATE,
			StandardOpenOption.TRUNCATE_EXISTING,
			StandardOpenOption.WRITE);
		rawContent = output;
	}
}
