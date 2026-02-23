package kryptonbutterfly.xmlConfig4J;

import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

public enum XmlTags
{
	ROOT("root"),
	TYPES("types"),
	ITEM("item"),
	DATA("data"),
	ID("id"),
	NAME("name"),
	TYPE("type"),
	NULL("null"),
	VALUE("value"),
	INFO("info"),
	INST_ID("inst-id"),
	REF_ID("ref-id");
	
	String defaultTag;
	
	XmlTags(String defaultTag)
	{
		this.defaultTag = defaultTag;
	}
	
	static HashMap<XmlTags, String> createTagsMap()
	{
		return Arrays.stream(values())
			.collect(
				Collectors.toMap(
					t -> t,
					t -> t.defaultTag,
					(e, r) -> e,
					HashMap::new));
	}
	
	static Tags fromMap(HashMap<XmlTags, String> tags)
	{
		validateMap(tags);
		return new Tags(
			tags.get(ROOT),
			tags.get(TYPES),
			tags.get(ITEM),
			tags.get(DATA),
			tags.get(ID),
			tags.get(NAME),
			tags.get(TYPE),
			tags.get(NULL),
			tags.get(VALUE),
			tags.get(INFO),
			tags.get(INST_ID),
			tags.get(REF_ID));
	}
	
	private static void validateMap(HashMap<XmlTags, String> tags)
	{
		final var map = new HashMap<String, XmlTags>();
		tags.forEach((tag, value) -> {
			if (map.containsKey(value))
				throw new IllegalArgumentException(
					"%s and %s have been assigned the same value '%s'."
						.formatted(tag, map.get(value), value));
			map.put(value, tag);
		});
	}
}
