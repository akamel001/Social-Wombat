import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * Used to hold a thread.
 * The title field is the title of the thread.
 * The first item in the list is the body of the first post.
 * All additional list items are comments to that post.
 * 
 * @author chris d
 *
 */
public class Post {
	private final int maxTitleLength = 60;
	private final int maxCommentLength = 2000;
	private String title;
	private int lastValue = 1;
	private Map <Integer, String> commentList;
	
	/**
	 * Default constructor disallowed. Use Post(String, String)
	 */
	private Post(){}
	
	/**
	 * Create a new post with the given title and body for the first post.
	 * @param t Post title; max length is maxTitleLength
	 * @param b Post body; max length is maxCommentLength
	 */
	public Post(String t, String b){
		commentList = Collections.synchronizedMap(new HashMap<Integer, String>());
		title = "";
		if (t.length()<=60)
			title = t.substring(0, maxTitleLength-1);
		else if (t.length()==0)
			title += lastValue;
		else
			title = t;
		if (b.length()>maxCommentLength)
			b = b.substring(0,maxCommentLength-1);
		commentList.put(lastValue, b);
		lastValue++;
	}
	
	/**
	 * Adds a comment to the post.
	 * @param c Strings of length 0 are ignored; strings linger than maxCommentLength are truncated.
	 * @return Returns 1 if comment is added, -1 otherwise.
	 */
	public int addComment(String c){
		if (c.length()<1)
			return -1;
		if (c.length()>maxCommentLength)
			c = c.substring(0,maxCommentLength-1); 
		commentList.put(lastValue, c);
		lastValue++;
		return 1;
	}
	
	/**
	 * Removes a comment from the post.
	 * @param i The index of the comment.
	 * @return Returns 1 if the comment was removed, -1 if it did not exist in the post.
	 */
	public int removeComment(int i){
		if (!commentList.containsKey(i))
			return -1;
		else {
			commentList.remove(i);
			return 1;
		}
	}
}
