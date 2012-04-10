package storage;
import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import security.AES;



/**
 * Class to store and access ClassRoom objects.
 * @author chris d
 *
 */
public final class ClassDB implements Serializable{

	private static final long serialVersionUID = -808645813216407780L;
	private Map <String, byte[]> classRoomList; 
	private int nextClass = 1;

	/**
	 * ClassDB holds a list of ClassRoom objects.
	 */
	public ClassDB(){
		classRoomList = Collections.synchronizedMap(new TreeMap<String, byte[]>());
	}

	/**
	 * Adds a classroom to the DB.
	 * @param n The name of the new classroom -- must be unique!
	 * @param encryptor The AES object used to encrypt the classroom
	 * @return Returns 1 if the class was added, -1 if it already exists.
	 */
	public int addClassRoom(String class_name, AES encryptor){
		// sanity check
		if (class_name==null || encryptor==null)
			return -1;
		
		synchronized(classRoomList){
			if(classRoomList.containsKey(class_name))
				return -1;
			else{
				// Create new class with given name
				ClassRoom class_in = new ClassRoom(class_name, nextClass++);
				
				// Encrypt and add ClassRoom
				return encryptAndAddClassRoom(class_in, encryptor);
			}
		}
	}

	/**
	 * Removes a class from the DB.
	 * @param c The name of the class.
	 * @return Returns 1 if the class was removed, -1 if the class did not exist.
	 */
	public int removeClassRoom(String class_name){
		if (class_name==null)
			return -1;
		if (classRoomList.remove(class_name)==null)
			return -1;
		else
			return 1;
	}

	/**
	 * Adds a post to a classroom.
	 * @param className The className to which the post is to be added.
	 * @param postTitle The title of the post to be added.
	 * @param post_body The body of the post to be added.
	 * @param encryptor The AES object used to encrypt the classroom
	 * @return Returns 1 if the post was added, -1 if it was not.
	 */
	public int addPost(String class_name, String post_title, String post_body, AES encryptor){
		
		// Sanity checks
		if (class_name==null || post_title==null || post_body==null || encryptor==null)
			return -1;
		if (post_title.length()<1 || post_title.length()> Post.maxTitleLength)
			return -1;
		if(post_body.length()<1 || post_body.length()>Post.maxCommentLength)
			return -1;

		synchronized(classRoomList){
			// (a): decrypt the ClassRoom object
			ClassRoom class_room = this.getAndDecryptClassRoom(class_name, encryptor);
			if (class_room==null)
				return -1;
	
			// (b) add post to ClassRoom		
			if (class_room.addNewPost(post_title, post_body)==-1) 
				return -1;
	
			// (c) reencrypt the ClassRoom object, and reinsert the cipher into the classRoomList
			return this.encryptAndAddClassRoom(class_room, encryptor);
		}
	}

	/**
	 * Removes a post from a class.
	 * @param class_name The name of the class that removes the post.
	 * @param post_id The name of the post to be removed.
	 * @param encryptor The AES object to be encrypted.
	 * @return Returns 1 if the post was removed, -1 if was not contained in the classroom
	 * or on error.
	 */
	public int removePost(String class_name, int post_id, AES encryptor){
		// sanity checks
		if (class_name==null || encryptor==null)
			return -1;

		synchronized(classRoomList){
			// (a): decrypt the ClassRoom object,
			ClassRoom class_room = this.getAndDecryptClassRoom(class_name, encryptor);
			if (class_room==null)
				return -1;
	
			// (b): remove the post from the ClassRoom object,
			if (class_room.removePost(post_id)==-1)
				return -1;
	
			//  (c) reencrypt the ClassRoom object, and reinsert the ClassRoom 
			//      cipher into the classRoomList (replacing the old ClassRoom)
			return this.encryptAndAddClassRoom(class_room, encryptor);
		}
	}

	/**
	 * Adds s comment to a post in a classroom
	 * @param class_name The name of the classroom that contains the post.
	 * @param post_id The name of the post to which to add the comment.
	 * @param comment The comment to be added.
	 * @param encryptor The AES object to be encrypted.
	 * @return Returns 1 if the comment had been added, -1 otherwise. 
	 */
	public int addComment(String class_name, int post_id, String comment, AES encryptor){
		//sanity checks
		if (class_name==null || comment==null || encryptor==null)
			return -1;

		synchronized(classRoomList){
			// (a): decrypt the ClassRoom object,
			ClassRoom class_room = this.getAndDecryptClassRoom(class_name, encryptor);
			if (class_room==null)
				return -1;
			
			// (b) add a comment to the ClassRoom object
			if (class_room.addComment(post_id, comment)==-1)
				return -1;
	
			//  (c) reencrypt the ClassRoom object, and reinsert the ClassRoom 
			//      cipher into the classRoomList (replacing the old ClassRoom)
			return this.encryptAndAddClassRoom(class_room, encryptor);
		}
	}

	/**
	 * Removes a comment from a post in a classroom.
	 * @param class_name The name of the classroom which contains the post. 
	 * @param post_id The id number of the post which contains the comment.
	 * @param comment_id The id number of the comment which is to be removed.
	 * @param encryptor The AES object to be encrypted.
	 * @return Returns 1 if the comment is removed, -1 if the id did not exist in the post.
	 */
	public int removeComment(String class_name, int post_id, int comment_id, AES encryptor){
		if (class_name==null || encryptor==null)
			return -1;
		
		synchronized(classRoomList){
			// (a): decrypt the ClassRoom object,
			ClassRoom class_room = this.getAndDecryptClassRoom(class_name, encryptor);
			if (class_room==null)
				return -1;
			
			// (b) Remove comment from ClassRoom 
			if( class_room.removeComment(post_id, comment_id) == -1)
				return -1;
			
			//  (c) reencrypt the ClassRoom object, and reinsert the ClassRoom 
			//      cipher into the classRoomList (replacing the old ClassRoom)
			return this.encryptAndAddClassRoom(class_room, encryptor);
		}
	}

	/**
	 * Returns a list of all the threads in a class.
	 * @param The name of the classroom which contains the threads.
	 * @return Returns a list of Integers mapped to Strings 
	 * where the Integer is the post id and the String is the post title.
	 */
	public Map<Integer, String> getThreadList(String class_name, AES encryptor){
		if (class_name==null || encryptor==null)
			return null; 
		
		// (a): decrypt the ClassRoom object,
		ClassRoom class_room = this.getAndDecryptClassRoom(class_name, encryptor);
		if (class_room==null)
			return null;
		
		// (b) Extract thread list and return
		Map<Integer, String> out = class_room.getThreadList();
		return out;
	}

	/**
	 * Returns the entire thread from a specific classRoom.
	 * @param className The name of the class containing the thread.
	 * @param threadId The id number of the thread to be returned
	 * @return Returns a map containing Integer keys mapped to Strings. 
	 */
	public Map<Integer, String> getThread(String class_name, int thread_id, AES encryptor){
		if (class_name==null || encryptor==null)
			return null;
	
		// (a): decrypt the ClassRoom object,
		ClassRoom class_room = this.getAndDecryptClassRoom(class_name, encryptor);
		if (class_room==null)
			return null;
		
		// (b) Extract thread and return
		return class_room.getThread(thread_id);		
	}

	/**
	 * Deletes every class in a list.
	 * @param toBeDeleted A List<String> of classes to be deleted. 
	 * @return Returns 1 if any classes have been deleted.
	 */
	public int deleteClasses(List<String> toBeDeleted){
		if (toBeDeleted.isEmpty() || toBeDeleted==null)
			return -1;
		else{
			Iterator<String> i = toBeDeleted.iterator(); 
			while (i.hasNext())
				classRoomList.remove(i.next());
		}
		return 1;
	}

	/**
	 * Returns a decrypted CLassRoom object from the classDB.
	 * @param class_name The name of the ClassRoom to be decrypted.
	 * @param encryptor The AES object to decrypt the ClassRoom
	 * @return Returns the classroom. Returns null on error or if the ClassRomm is not present in the class list.
	 */
	private ClassRoom getAndDecryptClassRoom(String class_name, AES encryptor){
		// sanity checks
		if (class_name==null || encryptor==null)
			return null;
		
		// (a) get the ClassRoom cipher
		byte[] cipher = classRoomList.get(class_name);
		
		// (b) decrypt cipher and return ClassRoom
		try{
			return (ClassRoom)encryptor.decryptObject(cipher);
		}catch(ClassCastException e){
			return null;
		}
	}

	/**
	 * Encrypts a given ClassRoom and adds it to the class list
	 * @param class_room The ClassRoom to be encrypted
	 * @param encryptor The AES object used to encrypt the ClassRoom
	 * @return 1 if the ClassRoom has been added correctly, or -1 on error. 
	 */
	private int encryptAndAddClassRoom(ClassRoom class_room, AES encryptor){
		// sanity checks
		if (class_room==null || encryptor==null)
			return -1;

		byte[] cipher;

		try{
			// encrypt the ClassRoom
			cipher = encryptor.encrypt((Object)class_room);
			
			// Add encrypted ClassRoom to class list
			if(cipher==null)
				return -1;
			else{
				classRoomList.put(class_room.getName(), cipher);
				return 1;
			}
		}catch(ClassCastException e){ 
			return -1; 
		}
	}
}
