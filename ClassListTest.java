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
		int t;
		ClassData cd = new ClassData(iName);
		cd.setClassName("CLASS_A");
		cd.setClassServer(1, 1000);
		ClassList cl = new ClassList();
		t = cl.addClass(cd);
		assertEquals(1, t);
	}

	@Test
	public final void testRemoveClass() {
		String iName = "DR. G";
		int t;
		ClassData cd = new ClassData(iName);
		cd.setClassName("CLASS_A");
		cd.setClassServer(1, 1000);
		ClassList cl = new ClassList();
		t = cl.addClass(cd);
		assertEquals(1, t);
		t = cl.removeClass("CLASS_A");
		assertEquals(1, t);
	}



}
