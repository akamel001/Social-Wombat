import java.io.Serializable;
import java.util.Collections;
import java.util.TreeMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;


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
		classRoomList = Collections.synchronizedMap(new TreeMap<String, ClassRoom>());
	}

	/**
	 * Adds a classroom to the DB.
	 * @param n The name of the new classroom -- must be unique!
	 * @return Returns 1 if the class was added, -1 if it already exists.
	 */
	public int addClassRoom(String n){
		if (n==null)
			return -1;
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
		if (c==null)
			return -1;
		ClassRoom t = classRoomList.remove(c);
		if (t==null)
			return -1;
		else
			return 1;
	}

	/**
	 * Adds a post to a classroom.
	 * @param className The className to which the post is to be added.
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
		if (className==null)
			return -1;
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
		if (className==null)
			return -1;
		ClassRoom c = classRoomList.get(className);
		if (c==null)
			return -1;
		int out = c.removeComment(postId, commentId);
		return out;
	}

	/**
	 * Returns a list of all the threads in a class.
	 * @param The name of the classroom which contains the threads.
	 * @return Returns a list of Integers mapped to Strings 
	 * where the Integer is the post id and the String is the post title.
	 */
	public Map<Integer, String> getThreadList(String className){
		if (className==null)
			return null; 
		ClassRoom c = classRoomList.get(className);
		if (c==null)
			return null;
		Map<Integer, String> out = c.getThreadList();
		return out;
	}

	/**
	 * Returns the entire thread from a specific classRoom.
	 * @param className The name of the class containing the thread.
	 * @param threadId The id number of the thread to be returned
	 * @return Returns a map containing Integer keys mapped to Strings. 
	 */
	public Map<Integer, String> getThread(String className, int threadId){
		if (className==null)
			return null;
		ClassRoom c = classRoomList.get(className);
		if (c==null)
			return null;
		return c.getThread(threadId);		
	}

	/**
	 * Deletes every class in a list.
	 * @param toBeDeleted A List<String> of classes to be deleted. 
	 * @return Returns 1 if any classes have been deleted.
	 */
	public int deleteClasses(List<String> toBeDeleted){
		if (toBeDeleted.isEmpty())
			return -1;
		else{
			Iterator<String> i = toBeDeleted.iterator(); 
			while (i.hasNext())
				classRoomList.remove(i.next());
		}
		return 1;
	}


}
