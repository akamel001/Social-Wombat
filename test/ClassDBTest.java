package test;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import security.AES;
import storage.ClassDB;


/**
 * 
 */

/**
 * @author chris d
 *
 */
public class ClassDBTest {

	AES aes; //dummy aes to stop errors
	/**
	 * test method for {@link ClassDB#addClassRoom(java.lang.String)}.
	 */
	@Test
	public final void testAddClassRoom() {
		ClassDB db = new ClassDB();
		
		// add 1 class
		int t = db.addClassRoom("CLASS1", aes);
		assertEquals("Result", 1, t);
		
		// add a duplicate class
		t = db.addClassRoom("CLASS1", aes);
		assertEquals("Result", -1, t);
		
		// Add 1000 classes
		for (int i=0; i<1000; i++){
			t = db.addClassRoom("CLASS_" + i, aes);
			assertEquals(1, t);
		}
		
	}

	/**
	 * test method for {@link ClassDB#removeClassRoom(java.lang.String)}.
	 */
	@Test
	public final void testRemoveClassRoom() {
		ClassDB db = new ClassDB();
		int t = db.addClassRoom("CLASS1", aes);
		
		// remove class
		t = db.removeClassRoom("CLASS1");
		assertEquals("Result", 1, t);
		
		// remove nonexistent class
		t = db.removeClassRoom("CLASSX");
		assertEquals("Result", -1, t);
		
		// add and remove 1000 classes
		for (int i=0; i<1000; i++){
			t = db.addClassRoom("CLASS_" + i, aes);
			assertEquals(1, t);
		}
		for (int i=0; i<1000; i++){
			t = db.removeClassRoom("CLASS_" + i);
			assertEquals(1, t);
		}
	}

	/**
	 * test method for {@link ClassDB#addPost(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public final void testAddPost() {
		ClassDB db = new ClassDB();
		String class_name = "TEST_CLASS_1";
		db.addClassRoom(class_name, aes);
		
		// Add a post.
		int t = db.addPost(class_name, "test post", "BODY OF TEST POST", aes);
		assertEquals(1, t);
		
		// Add 1000 posts
		for (int i=0; i<1000; i++){
			 t = db.addPost(class_name, "test post", "BODY OF TEST POST", aes);
			assertEquals(1, t);
		}
		
		// Add 1000 classes and 1000 posts to each
		for (int i=0; i<1000; i++){
			t = db.addClassRoom("CLASS_" + i, aes);
			assertEquals(1, t);
			for (int j=0; j<1000; j++){
				t = db.addPost("CLASS_" + i, "test post", "BODY OF TEST POST", aes);
				assertEquals(1, t);
			}
		}
		
		// Delete the 1000 classrooms
		for (int i=0; i<1000; i++){
			t = db.removeClassRoom("CLASS_" + i);
			assertEquals(1, t);
		}
	}

	/**
	 * test method for {@link ClassDB#removePost(java.lang.String, int)}.
	 */
	@Test
	public final void testRemovePost() {
		ClassDB db = new ClassDB();
		String class_name = "TEST_CLASS_1";
		db.addClassRoom(class_name, aes);
		
		int t;
		
		// Add and remove a post
		t = db.addPost(class_name, "test post", "BODY OF TEST POST", aes);
		assertEquals(1, t);
		t = db.removePost(class_name, 1, aes);
		assertEquals(1, t);
		
		// Add 1000 classes and 1000 posts to each
		for (int i=0; i<1000; i++){
			t = db.addClassRoom("CLASS_" + i, aes);
			assertEquals(1, t);
			for (int j=0; j<1000; j++){
				t = db.addPost("CLASS_" + i, "test post", "BODY OF TEST POST", aes);
				assertEquals(1, t);
			}
		}
		
		// Delete all 1000 x 1000 posts (not classrooms)
		for (int i=0; i<1000; i++){
			for (int j=1; j<2; j++){
				t = db.removePost("CLASS_" + i, j, aes);
				assertEquals(1, t);
			}
		}
		
		// Then, delete the classrooms
		for (int i=0; i<1000; i++){
			t = db.removeClassRoom("CLASS_" + i);
			assertEquals(1, t);
		}
	}

	/**
	 * test method for {@link ClassDB#addComment(java.lang.String, int, java.lang.String)}.
	 */
	@Test
	public final void testAddComment() {
		ClassDB db = new ClassDB();
		String class_name = "TEST_CLASS_1";
		db.addClassRoom(class_name, aes);
		db.addPost(class_name, "POST 1", "POST 1 BODY", aes);
		
		int t;
		
		// add a comment
		t = db.addComment(class_name, 1, "COMMENT!", aes);
		assertEquals(1, t);
		
		// add 100 comments to 100 threads in 100 classrooms
		for (int i=0; i<100; i++){
			t = db.addClassRoom("CLASS_" + i, aes);
			assertEquals(1, t);
			for (int j=0; j<100; j++){
				t = db.addPost("CLASS_" + i, "test post", "BODY OF TEST POST", aes);
				assertEquals(1, t);
				for(int k=0; k<100; k++){
					t = db.addComment("CLASS_" + i, j+1, "THIS IS THE COMMENT", aes);
					assertEquals(1, t);
				}
			}
		}
		
	}

	/**
	 * test method for {@link ClassDB#removeComment(java.lang.String, int, int)}.
	 */
	@Test
	public final void testRemoveComment() {
		ClassDB db = new ClassDB();
		String class_name = "TEST_CLASS_1";
		db.addClassRoom(class_name, aes);
		db.addPost(class_name, "POST 1", "POST 1 BODY", aes);
		
		int t;
		
		// add a comment
		t = db.addComment(class_name, 1, "COMMENT!", aes);
		assertEquals(1, t);
		
		// Try to remove comment 1 or 2
		t = db.removeComment(class_name, 1, 1, aes);
		assertEquals(-1, t);
		t = db.removeComment(class_name, 1, 2, aes);
		assertEquals(-1, t);
		
		// add 464^3 comments to 464^2 threads in 464 classrooms
		for (int i=0; i<100; i++){
			t = db.addClassRoom("CLASS_" + i, aes);
			assertEquals(1, t);
			for (int j=0; j<100; j++){
				t = db.addPost("CLASS_" + i, "test post", "BODY OF TEST POST", aes);
				assertEquals(1, t);
				for(int k=0; k<100; k++){
					t = db.addComment("CLASS_" + i, j+1, "THIS IS THE COMMENT", aes);
					assertEquals(1, t);
				}
			}
		}
		
		// delete the comments
		for (int i=0; i<100; i++){
			for (int j=0; j<100; j++){
				for(int k=3; k<103; k++){
					t = db.removeComment("CLASS_" + i, j+1, k, aes);
					assertEquals(1, t);
				}
			}
		}
		
		// then, delete the posts
		for (int i=0; i<100; i++){
			for (int j=0; j<100; j++){
				t = db.removePost("CLASS_" + i, j+1, aes);
				assertEquals(1, t);
			}
		}
	}

	/**
	 * test method for {@link ClassDB#getThreadList(java.lang.String)}.
	 */
	@Test
	public final void testGetThreadList() {
		//fail("Not yet implemented"); // TODO
	}

	/**
	 * test method for {@link ClassDB#getThread(java.lang.String, int)}.
	 */
	@Test
	public final void testGetThread() {
		//fail("Not yet implemented"); // TODO
	}
}
