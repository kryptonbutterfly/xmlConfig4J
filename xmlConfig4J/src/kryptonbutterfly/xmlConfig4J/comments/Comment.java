package kryptonbutterfly.xmlConfig4J.comments;

import kryptonbutterfly.xmlConfig4J.comments.path.CommentPath;

public record Comment(CommentPath path, String comment)
{
	@Override
	public String toString()
	{
		return "%s\n\t%s\n".formatted(path, comment);
	}
}
