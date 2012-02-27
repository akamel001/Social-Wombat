import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/*
 * 
 */
public class ClassDB implements Serializable{

	private static final long serialVersionUID = -808645813216407780L;
	private Map <String, ClassRoom> classRoomList; 
	private int nextClass = 1;
	
	/**
	 * ClassDB holds a list of ClassRoom objects.
	 */
	public ClassDB(){
		classRoomList = Collections.synchronizedMap(new HashMap<String, ClassRoom>());
	}
	
	/**
	 * Adds a classroom to the DB.
	 * @param n The name of the new classroom -- must be unique!
	 * @return Returns 1 if the class was added, -1 if it already exists.
	 */
	public int addClassRoom(String n){
		if (classRoomList.containsKey(n))
			return -1;
		else{
			ClassRoom c = new ClassRoom(n, nextClass);
			classRoomList.put(n, c);
			return 1;
		}
	}
	
	/**
	 * Removes a class from the DB.
	 * @param c The name of the class.
	 * @return Returns 1 if the class was removed, -1 if the class did not exist.
	 */
	public int removeClassRoom(String c){
		ClassRoom t = classRoomList.remove(c);
		if (t==null)
			return -1;
		else
			return 1;
	}
	
	/**
	 * Adds a post to a classroom.
	 * @param className The classname to which the post is to be added.
	 * @param postTitle The title of the post to be added.
	 * @param postBody The body of the post to be added.
	 * @return Returns 1 if the post was added, -1 if it was not.
	 */
	public int addPost(String className, String postTitle, String postBody){
		if (className==null || postTitle==null || postBody==null)
			return -1;
		ClassRoom c = classRoomList.get(className);
		if (c==null)
			return -1;
		if (postTitle.length()<1 || postTitle.length()> Post.maxTitleLength)
			return -1;
		if(postBody.length()<1 || postBody.length()>Post.maxCommentLength)
			return -1;
		int out = c.addNewPost(postTitle, postBody);
		return out;
	}
	
	/**
	 * Removes a post from a class.
	 * @param className The name of the class that removes the post.
	 * @param postId The name of the post to be removed.
	 * @return Returns 1 if the post was removed, -1 if was not contained in the classroom.
	 */
	public int removePost(String className, int postId){
		ClassRoom c = classRoomList.get(className);
		if (c==null)
			return -1;
		int out = c.removePost(postId);
		return out;
	}
	
	/**
	 * Adds s comment to a post in a classroom
	 * @param className The name of the classroom that contains the post.
	 * @param postId The name of the post to which to add the comment.
	 * @param comment The comment to be added.
	 * @return Returns 1 if the comment had been added, -1 otherwise. 
	 */
	public int addComment(String className, int postId, String comment){
		// Added conversion because postID and comment are stored in a string array
		if (className==null || comment==null)
			return -1;
		ClassRoom c = classRoomList.get(className);
		int out = c.addComment(postId, comment);
		return out;
	}
	
	/**
	 * Removes a comment from a post in a classroom.
	 * @param className The name of the classroom which contains the post. 
	 * @param postId The id number of the post which contains the comment.
	 * @param commentId The id number of the comment which is to be removed.
	 * @return Returns 1 if the comment is removed, -1 if the id did not exist in the post.
	 */
	public int removeComment(String className, int postId, int commentId){
		ClassRoom c = classRoomList.get(className);
		int out = c.removeComment(postId, commentId);
		return out;
	}
	
	/**
	 * Returns a list of all the posts in a class.
	 * @return Returns a list of Integers mapped to Strings 
	 * where the Integer is the post id and the String is the post title.
	 */
	public Map<Integer, String> getTitleList(String className){
		ClassRoom c = classRoomList.get(className);
		Map<Integer, String> out = c.getTitleList();
		return out;
	}
}
