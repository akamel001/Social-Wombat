import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ClientTest {


	/**
	 * Test method for {@link Client#handleLogin(java.lang.String)}.
	 */
	@Test
	public void testHandleLogin() {
		Client client = new Client();
		
		if(client.handleLogin("Mike"))
			System.out.println("Pass");
		else 
			System.out.println("Fail");
	}

	/**
	 * Test method for {@link Client#createClassroom(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testCreateClassroom() {
		Client client = new Client();
		
		client.createClassroom("Dogs", "Mike");
	}

	/**
	 * Test method for {@link Client#requestToJoinClassroom(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testRequestToJoinClassroom() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link Client#getClassroomMapForUser(java.lang.String)}.
	 */
	@Test
	public void testGetClassroomMapForUser() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link Client#main(java.lang.String[])}.
	 */
	@Test
	public void testMain() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link Client#createThread(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testCreateThread() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link Client#deleteClassroom(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testDeleteClassroom() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link Client#disjoinClassroom(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testDisjoinClassroom() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link Client#getThreadMapForClassroom(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testGetThreadMapForClassroom() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link Client#createComment(java.lang.String, int, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testCreateComment() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link Client#getMemberMapForClassroom(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testGetMemberMapForClassroom() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link Client#removeMember(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testRemoveMember() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link Client#changeStatus(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testChangeStatus() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link Client#getRequestListForClassroom(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testGetRequestListForClassroom() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link Client#confirmAsMemberOfClassroom(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testConfirmAsMemberOfClassroom() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link Client#denyMembershipToClassroom(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testDenyMembershipToClassroom() {
		fail("Not yet implemented"); // TODO
	}

}
