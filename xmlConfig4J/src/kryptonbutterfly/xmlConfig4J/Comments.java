package kryptonbutterfly.xmlConfig4J;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.comments.Comment;
import kryptonbutterfly.xmlConfig4J.comments.path.CommentPath;
import kryptonbutterfly.xmlConfig4J.comments.path.PathRef;
import kryptonbutterfly.xmlConfig4J.comments.path.PathView;

public final class Comments
{
	private final ArrayList<Comment> allComments;
	
	public String toString()
	{
		var sb = new StringBuilder();
		for (var c : allComments)
			sb.append(c).append("\n");
		return sb.toString();
	}
	
	public static Comments create()
	{
		return new Comments(true);
	}
	
	Comments(boolean track)
	{
		this.allComments = track ? new ArrayList<>() : null;
	}
	
	private sealed abstract class AReader implements AutoCloseable permits DocReader, CommentReader
	{
		ArrayList<String> buffer = new ArrayList<>();
		
		boolean open = true;
		
		public final void addComment(org.w3c.dom.Comment comment)
		{
			assertOpen();
			if (allComments == null)
				return;
			
			buffer.add(comment.getData());
		}
		
		public final void close()
		{
			assertOpen();
			flushComments(buffer, false);
			open = false;
		}
		
		abstract PathView path();
		
		public CommentReader push(PathRef child)
		{
			assertOpen();
			final var reader = new CommentReader(path().child(child));
			reader.flushComments(buffer, true);
			return reader;
		}
		
		void flushComments(ArrayList<String> comments, boolean preceeding)
		{
			if (allComments == null)
				return;
			
			final var cPath = new CommentPath(path(), preceeding);
			for (final var c : comments)
				allComments.add(new Comment(cPath, c));
			comments.clear();
		}
		
		void assertOpen()
		{
			if (!open)
				throw new IllegalStateException("Reader has been closed!");
		}
	}
	
	final class DocReader extends AReader
	{
		@Override
		PathView path()
		{
			return PathView.ROOT;
		}
	}
	
	public final class CommentReader extends AReader
	{
		private final PathView path;
		
		CommentReader(PathView path)
		{
			this.path = path;
		}
		
		@Override
		PathView path()
		{
			return path;
		}
		
		public void clear()
		{
			buffer.clear();
		}
	}
	
	private sealed abstract class AWriter implements AutoCloseable permits DocWriter, CommentWriter
	{
		private final Node		parent;
		private final Document	document;
		
		private boolean open = true;
		
		private AWriter(Document document, Node parent)
		{
			this.parent		= parent;
			this.document	= document;
		}
		
		public void close()
		{
			assertOpen();
			writeComments(new CommentPath(path(), false));
			open = false;
		}
		
		void assertOpen()
		{
			if (!open)
				throw new IllegalStateException("Writer has been closed!");
		}
		
		abstract PathView path();
		
		public CommentWriter push(Node node, PathRef child)
		{
			final var childPath = path().child(child);
			writeComments(new CommentPath(childPath, true));
			final var writer = new CommentWriter(document, node, childPath);
			return writer;
		}
		
		private void writeComments(CommentPath path)
		{
			if (allComments != null)
				allComments.stream()
					.filter(c -> c.path().equals(path))
					.forEach(c -> parent.appendChild(document.createComment(c.comment())));
		}
	}
	
	final class DocWriter extends AWriter
	{
		DocWriter(Document document)
		{
			super(document, document);
		}
		
		@Override
		PathView path()
		{
			return PathView.ROOT;
		}
	}
	
	public final class CommentWriter extends AWriter
	{
		private final PathView path;
		
		private CommentWriter(Document document, Node parent, PathView path)
		{
			super(document, parent);
			this.path = path;
		}
		
		@Override
		PathView path()
		{
			return path;
		}
	}
}