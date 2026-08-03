package kryptonbutterfly.xmlConfig4J.persistence;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.function.Supplier;

import kryptonbutterfly.xmlConfig4J.Comments;
import kryptonbutterfly.xmlConfig4J.XmlDataBinding;

final class PersistableFileData<Data> implements PersistableResource<Data>
{
	private String					rawFileContent	= null;
	private final XmlDataBinding	binding;
	private final File				file;
	private Data					data			= null;
	private final Class<Data>		classOfT;
	private final boolean			persistNotDirty;
	private Comments				comments;
	
	PersistableFileData(
		XmlDataBinding binding,
		boolean persistNotDirty,
		File file,
		Class<Data> classOfT,
		boolean preserveComments)
	{
		this.binding			= binding;
		this.file				= file;
		this.classOfT			= classOfT;
		this.persistNotDirty	= persistNotDirty;
		this.comments			= preserveComments ? Comments.create() : null;
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
				data = binding.<Data>fromXml(comments, file);
			else
				data = binding.fromXml(comments, file, classOfT);
			return;
		}
		
		rawFileContent = Files.readString(file.toPath());
		if (classOfT == null)
			data = binding.<Data>fromXml(comments, rawFileContent);
		else
			data = binding.fromXml(comments, rawFileContent, classOfT);
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
		final var output = binding.toXml(comments, data);
		
		if (!persistNotDirty && Objects.equals(rawFileContent, output) && file.exists())
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
