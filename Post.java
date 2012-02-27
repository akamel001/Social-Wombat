import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;


/**
 * Used to hold a thread.<br>
 * <br>
 * The thread consists of a map that maps Integers to Strings. The first two entries are the post 
 * title (mapped to 1), and the post body (mapped to 2). Comments are mapped starting from 3 on. 
 * 
 * @author chris d
 *
 */
public class Post implements Serializable{
	
	/** Serializable version number */
	private static final long serialVersionUID = -3423294746856380398L;
	
	/** Max length for a post title.*/
	protected static final int maxTitleLength = 60;
	
	/** Max length for a comment.*/ 
	protected static final int maxCommentLength = 2000;
	
	/** The id number for the next comment. */
	private int nextComment = 1;
	
	/** Post id number. Should be unique -- value for id is set by ClassRoom on Post creation.  */
	private final int id;

	/** Holds a list of comments mapped to Integer identifiers. */
	private Map<Integer, String> commentList;
	
	/**
	 * Default constructor disallowed. Use Post(String, String)
	 */
	@SuppressWarnings("unused")
	private Post(){id=-1;}
	
	/**
	 * Create a new post with the given title and body for the first post.
	 * @param t Post title; max length is maxTitleLength
	 * @param b Post body; max length is maxCommentLength
	 */
	public Post(String t, String b, int i){
		commentList = Collections.synchronizedMap(new HashMap<Integer, String>());
		// truncate title if it's too long
		if (t.length()<=60)
			t= t.substring(0, maxTitleLength-1);
		// if the title is empty
		else if (t.length()==0)
			t = "[no title]"; 			// this issue should be caught at ClassRoom 
		if (b.length()>maxCommentLength)
			b = b.substring(0,maxCommentLength-1);
		commentList.put(nextComment, t);		// add title
		nextComment++;
		commentList.put(nextComment, b);		// add body
		nextComment++;
		id = i;
	}
	
	/**
	 * Adds a comment to the post.
	 * @param c Strings of length 0 are ignored; strings linger than maxCommentLength are truncated.
	 * @return Returns 1 if comment is added, -1 otherwise.
	 */
	public int addComment(String c){
		if (c.length()<1 || c==null)
			return -1;
		if (c.length()>maxCommentLength)
			c = c.substring(0,maxCommentLength-1); 
		synchronized (this) {
			commentList.put(nextComment, c);
			nextComment++;
		}
		return 1;
	}
	
	/**
	 * Removes a comment from the post.<br>
	 * NOTE: you cannot remove the title of a post (index 0), or the post's body (index 1).
	 * @param i The index of the comment.
	 * @return Returns 1 if the comment was removed, -1 if it did not exist in the post.
	 */
	public int removeComment(int i){
		String s = commentList.remove(i);
		if (s==null)
			return -1;
		else
			return 1;
	}
	
	/**
	 * Returns the entire post as a Map(Integer,Strings). <br>
	 * NOTE: Map(0) contains the title of the post. Map(1)1 contains the body. Values 
	 * greater than 1 are comments.
	 * @return Returns a Map(Integer, String) containing the post.
	 */
	public Map<Integer, String> getPosts(){
		return commentList;
	}
	
	/**
	 * Returns the post's id. 
	 * @return 
	 */
	public int getId(){
		return id;
	}
}
