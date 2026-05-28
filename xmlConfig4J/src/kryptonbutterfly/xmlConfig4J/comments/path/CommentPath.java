package kryptonbutterfly.xmlConfig4J.comments.path;

public record CommentPath(PathView path, boolean preceeding)
{
	@Override
	public String toString()
	{
		return "%s — %s".formatted(path, preceeding ? "preceeding" : "inner");
	}
}