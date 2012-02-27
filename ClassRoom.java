import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;

public class ClassRoom implements Serializable{

	static final long serialVersionUID = 2158685748414139162L;
	private static final int maxNameLength = 30;
	private int nextPost = 1;
	private int idNum;
	private String name;
	private Map <Integer, Post> postList;

	
	/**
	 * Default constructor disallowed.
	 */
	@SuppressWarnings("unused")
	private ClassRoom(){}
	
	/**
	 * Creates a new classroom with the given name.
	 * @param n 
	 */
	protected ClassRoom(String n, int i){
		if (n.length()>maxNameLength)
			n = n.substring(0, maxNameLength-1);
		name = n;
		if (n.length()==0)
			n = "[no class title]"; // Should be caught at ClassDB
		idNum = i;
		postList = Collections.synchronizedMap(new HashMap<Integer, Post>());
	}
	
	/**
	 * Creates a new post and adds it to the post list.
	 * @param postTitle The title of the post. The length must be 1<=t<=Post.maxTitleLength.
	 * @param postBody  The body of the post. The length must be 1<=b<=Post.maxBodyLength.
	 * @return
	 */
	protected int addNewPost(String postTitle, String postBody){
		if (postTitle == null || postBody == null)
			return -1;
		if (postTitle.length() > Post.maxTitleLength || postTitle.length() <1)
			return -1;
		if (postBody.length() > Post.maxCommentLength || postBody.length() <1)
			return -1;
		Post p = new Post(postTitle, postBody, nextPost);
		synchronized (this) {
			postList.put(nextPost, p);
			nextPost++;
		}
		return 1;
	}
	
	/**
	 * Removes a post from the post and all its comments from the post list.
	 * @param p The id of the post to be removed.
	 * @return Returns 1 if the post was successfully removed, -1 otherwise.
	 */
	protected int removePost(int p){
		postList.remove(p);
		if (postList==null)
			return -1;
		else
			return 1;
	}
	
	/**
	 * Adds a comment to a post.
	 * @param postId The id number of the post to which the comment will be added.
	 * @param comment The comment to be added.
	 * @return
	 */
	protected int addComment(int postId, String comment){
		Post p = postList.get(postId);
		int out = p.addComment(comment);
		return out;
	}
	
	/**
	 * Removes a comment from a post.
	 * @param postId The id number of the post to which to add a comment. 
	 * @param commentId The id number of the comment to be removed.
	 * @return Returns 1 if the comment is removed, -1 if the comment was not in the post.
	 */
	protected int removeComment(int postId, int commentId){
		Post p = postList.get(postId);
		int out = p.removeComment(commentId);
		return out;
	}
	
	/**
	 * Returns the post list for a class.
	 * @return Returns a map (Integer,String) containing all the posts for the class.
	 */
	protected Map<Integer, Post> getClassRoom(){
		return postList;
	}

	/**
	 * Returns a list of all the posts in a class.
	 * @return Returns a list of Integers mapped to Strings w
	 * here the Integer is the post id and the String is the post title.
	 */
	protected Map<Integer, String> getTitleList(){
		Map<Integer, String> titleList = Collections.synchronizedMap(new HashMap<Integer, String>());

		Set<Integer> s = postList.keySet();
		synchronized(postList) {  
			Iterator<Integer> i = s.iterator(); 
			while (i.hasNext()){
				int postId = i.next();
				Post tempPost = postList.get(postId);
				String postTitle = tempPost.getTitle();
				titleList.put(postId, postTitle);
			}
		}
		return titleList;
	}

		
	
	/**
	 * Returns the id number of the class.
	 * @return Returns the value of the idNum field.
	 */
	protected int getId(){
		return idNum;
	}
	
	/**
	 * returns the name of the class.
	 * @return
	 */
	protected String getName(){
		return name;
	}
}
