import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class CrawlerTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	
	@Test
	public void testEqualsObject() {
		
		Crawler c= new Crawler();
		Crawler newC= new Crawler();
		
		assertTrue(c.equals(newC));
		c.setId("1111");
		newC.setId("1111");
		assertTrue(c.equals(newC));
		newC.setId("2222");
		assertFalse(c.equals(newC));
		
		
	}

}
