package kryptonbutterfly.xmlConfig4J.persistence;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Supplier;

import kryptonbutterfly.xmlConfig4J.Comments;
import kryptonbutterfly.xmlConfig4J.XmlDataBinding;

public abstract class PersistableResource<Data> implements AutoCloseable
{
	final XmlDataBinding	binding;
	final Class<Data>		classOfT;
	final boolean			persistNotDirty;
	
	String		rawContent	= null;
	Data		data			= null;
	Comments	comments;
	
	PersistableResource(
		XmlDataBinding binding,
		boolean persistNotDirty,
		Class<Data> classOfT,
		boolean preserveComments)
	{
		this.binding			= binding;
		this.classOfT			= classOfT;
		this.persistNotDirty	= persistNotDirty;
		this.comments			= preserveComments ? Comments.create() : null;
	}
	
	public final Data data()
	{
		return data;
	}
	
	public final void data(Data data)
	{
		this.data = data;
	}
	
	@Override
	public final void close() throws Exception
	{
		persist();
	}
	
	public final boolean isDirty() throws Exception
	{
		final var output = binding.toXml(comments, data);
		return !Objects.equals(rawContent, output);
	}
	
	public abstract void load(Supplier<Data> fallback) throws IOException;
	
	public abstract void persist() throws Exception;
}
