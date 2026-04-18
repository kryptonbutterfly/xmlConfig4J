package kryptonbutterfly.xmlConfig4J.persistence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import kryptonbutterfly.xmlConfig4J.XmlDataBinding;

public final class PersistableStreamData<Data> implements PersistableResource<Data>
{
	private String											rawContent	= null;
	private final XmlDataBinding							binding;
	private final StreamHandler<InputStream, IOException>	input;
	private final StreamHandler<OutputStream, IOException>	output;
	private Data											data		= null;
	private final Class<Data>								classOfT;
	private final boolean									persistNotDirty;
	
	PersistableStreamData(
		XmlDataBinding binding,
		boolean persistNotDirty,
		Class<Data> classOfT,
		StreamHandler<InputStream, IOException> input,
		StreamHandler<OutputStream, IOException> output)
	{
		this.binding			= binding;
		this.classOfT			= classOfT;
		this.persistNotDirty	= persistNotDirty;
		this.input				= input;
		this.output				= output;
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
	public void load(Supplier<Data> fallback) throws IOException
	{
		Objects.requireNonNull(fallback);
		
		this.input.useStream(iStream -> {
			final var data = new BufferedReader(new InputStreamReader(iStream, StandardCharsets.UTF_8))
				.lines()
				.collect(Collectors.joining("\n"));
			if (!persistNotDirty)
				rawContent = data;
			this.data = binding.fromXml(data);
		});
		
	}
	
	@Override
	public void persist() throws Exception
	{
		final var output = binding.toXml(data);
		
		if (!persistNotDirty && rawContent.equals(output))
			return;
		this.output.useStream(oStream -> {
			
			final var sw = new OutputStreamWriter(oStream, StandardCharsets.UTF_8);
			sw.write(output);
			sw.flush();
		});
	}
}
