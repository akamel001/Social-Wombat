package storage;
import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * ClassRoom stores threads and their associated comments.
 * @author chris d
 *
 */
public final class ClassRoom implements Serializable{

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
	 * @param n The name of the new class
	 * @param i The identifier for the class.
	 */
	protected ClassRoom(String n, int i){
		if (n==null)
			n = "Generic class name " + i; // Should be caught at ClassDB
		if (n.length()>maxNameLength)
			n = n.substring(0, maxNameLength-1);
		name = n;
		idNum = i;
		postList = Collections.synchronizedMap(new TreeMap<Integer, Post>());
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
		synchronized (postList) {
			postList.put(nextPost++, p);
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
	 * @return Returns 1 on success, -1 otherwise.
	 */
	protected int addComment(int postId, String comment){
		if (comment==null || comment.length()==0)
			return -1;
		int out;
		synchronized(postList){
			Post p = postList.get(postId);
			if (p==null)
				return -1;
			out = p.addComment(comment);
		}
		return out;
	}
	
	/**
	 * Removes a comment from a post.
	 * @param postId The id number of the post to which to add a comment. 
	 * @param commentId The id number of the comment to be removed.
	 * @return Returns 1 if the comment is removed, -1 if the comment was not in the post.
	 */
	protected int removeComment(int postId, int commentId){
		if(postId<0 || commentId < 3)
			return -1;
		int out;
		synchronized(postList){
			Post p = postList.get(postId);
			if (p==null)
				out = -1;
			else
				out = p.removeComment(commentId);
		}
		return out;
	}

	/**
	 * Returns a thread.
	 * @param The id of the thread to be returned.
	 * @return Returns a map with an Integer key and a String value. The Integer is the id of the comment,
	 * the String is the content.
	 */
	protected Map<Integer, String> getThread(int threadId){
		Post p = postList.get(threadId);
		if (p==null)
			return null;
		else
			return p.getThread();
	}
	
	/**
	 * Returns a list of the titles of all the threads in a class.
	 * @return Returns a list of Integers mapped to Strings w
	 * here the Integer is the post id and the String is the thread title.
	 */
	protected Map<Integer, String> getThreadList(){
		Map<Integer, String> titleList = Collections.synchronizedMap(new TreeMap<Integer, String>());
		
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
