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

public final class PersistableStreamData<Data> extends PersistableResource<Data>
{
	private final StreamHandler<InputStream, IOException>	input;
	private final StreamHandler<OutputStream, IOException>	output;
	
	PersistableStreamData(
		XmlDataBinding binding,
		boolean persistNotDirty,
		Class<Data> classOfT,
		StreamHandler<InputStream, IOException> input,
		StreamHandler<OutputStream, IOException> output,
		boolean preserveComments)
	{
		super(binding, persistNotDirty, classOfT, preserveComments);
		this.input	= input;
		this.output	= output;
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
			this.data = binding.fromXml(comments, data, classOfT);
		});
		
	}
	
	@Override
	public void persist() throws Exception
	{
		final var output = binding.toXml(comments, data);
		
		if (!persistNotDirty && Objects.equals(rawContent, output))
			return;
		this.output.useStream(oStream -> {
			
			final var sw = new OutputStreamWriter(oStream, StandardCharsets.UTF_8);
			sw.write(output);
			sw.flush();
		});
	}
}
