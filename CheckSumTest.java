import static org.junit.Assert.*;

import org.junit.Test;


public class CheckSumTest {

	@Test
	public void testChecksum() {
		long cs = CheckSum.getChecksum("I like cake");
		long cs2 = CheckSum.getChecksum("I like cake2");
		System.out.println(cs+cs2);
	}

}
