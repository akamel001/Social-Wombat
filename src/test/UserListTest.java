package test;
//import static org.junit.Assert.*;
//
//import org.junit.After;
//import org.junit.AfterClass;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//
//public class UserListTest {
//
//	@test
//	public final void testValidateUser() {
//		UserList test_list = new UserList();
//		String user1 = "user_1";
//		
//		test_list.addUser(user1);
//		
//	}
//
//	@test
//	public final void testAddUser() {
//		UserList test_list = new UserList();
//		String user1 = "user_a";
//		
//		// add user
//		boolean t = test_list.addUser(user1);
//		assertEquals(true, t);
//		
//		// add duplicate user
//		t = test_list.addUser(user1);
//		assertEquals(false, t);
//		
//		
//		for(int i=0; i<500; i++){
//			t = test_list.addUser("user_"+i);
//			assertEquals(true, t);
//		}
//	}
//
//	@test
//	public final void testRemoveUser() {
//		UserList test_list = new UserList();
//		boolean t;
//		for(int i=0; i<500; i++){
//			t = test_list.addUser("user_"+i);
//			assertEquals(true, t);
//		}
//		
//		for(int i=0; i<500; i++){
//			t = test_list.removeUser("user_"+i);
//			assertEquals(true, t);
//		}
//	}
//}
