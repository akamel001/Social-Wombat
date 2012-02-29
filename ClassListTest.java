import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class ClassListTest {

	
	@Test
	public final void testAddClass() {
		String iName = "DR. G";
		ClassData cd = new ClassData(iName);
		cd.setClassName("CLASS_A");
		cd.setClassServer(1, 1000);
		
	}

	@Test
	public final void testRemoveClass() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testGetUserPermissions() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testSetUserPermissions() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testGetInstructor() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testGetClassAll() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testGetClassEnrolled() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testGetClassPending() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testGetUserEnrollment() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testGetClassServer() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testGetClassPort() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testDeleteUser() {
		fail("Not yet implemented"); // TODO
	}

}
