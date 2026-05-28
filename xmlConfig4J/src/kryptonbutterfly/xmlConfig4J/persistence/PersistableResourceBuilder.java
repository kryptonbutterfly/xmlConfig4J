package kryptonbutterfly.xmlConfig4J.persistence;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Supplier;

import kryptonbutterfly.xmlConfig4J.XmlDataBinding;

public record PersistableResourceBuilder<Data>(XmlDataBinding binding, boolean persistNotDirty, Class<Data> classOfData)
{
	public PersistableResource<Data> fromFile(File file, Supplier<Data> fallback, boolean preserveComments)
		throws FileNotFoundException,
		IOException
	{
		Objects.requireNonNull(binding);
		Objects.requireNonNull(file);
		Objects.requireNonNull(fallback);
		
		final var resource = new PersistableFileData<Data>(
			binding,
			persistNotDirty,
			file,
			classOfData,
			preserveComments);
		resource.load(fallback);
		return resource;
	}
	
	public PersistableResource<Data> create(File file, Data data, Supplier<Data> fallback, boolean preserveComments)
	{
		Objects.requireNonNull(binding);
		Objects.requireNonNull(file);
		Objects.requireNonNull(fallback);
		
		final var resource = new PersistableFileData<Data>(
			binding,
			persistNotDirty,
			file,
			classOfData,
			preserveComments);
		resource.data(data);
		return resource;
	}
}
