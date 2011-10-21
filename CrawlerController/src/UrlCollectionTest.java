import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Mikko Rosten mikko.rosten@iki.fi
 * @author Teemu Miettinen tpjmie@utu.fi
 */

public class UrlCollectionTest {
	
	private UrlCollection data;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		data=new UrlCollection();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link UrlCollection#addURL(java.net.URL, java.net.URL)}.
	 * @throws MalformedURLException 
	 */
	@Test
	public void testAddURL() throws MalformedURLException {
		int oldSize = data.size();
		URL test=new URL("http://yle.fi/index.html");
		data.addURL(test, null);
		assertEquals(oldSize+1, data.size());
		assertTrue(data.hasFreeURL());
		
		URL tmp=data.getFreeURL();
		assertEquals(tmp,test);
		URL test2 =new URL("http://svt.fi/index.html");
		data.addURL(test2, test);
		assertEquals(oldSize+2, data.size());
		assertTrue(data.hasFreeURL());
		assertEquals(test2,data.getFreeURL());
//		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link UrlCollection#getFreeURL()}.
	 * @throws MalformedURLException 
	 */
	@Test
	public void testGetFreeURL() throws MalformedURLException {
		
		insertData();
		assertTrue(data.hasFreeURL());
		assertEquals(new URL("http://get.free/index.html"), data.getFreeURL());
//		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link UrlCollection#getDepth()}.
	 * @throws MalformedURLException 
	 */
	@Test
	public void testGetDepth() throws MalformedURLException {
		insertData();
		assertEquals(1, data.getDepth());
//		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link UrlCollection#size()}.
	 * @throws MalformedURLException 
	 */
	@Test
	public void testSize() throws MalformedURLException {
		insertData();
		assertEquals(2, data.size());
//		fail("Not yet implemented");
	}
	
	public void insertData() throws MalformedURLException{
		
		assertEquals(0,data.size());
		data.addURL(new URL("http://yle.fi/index.html"),null);
		assertEquals(1,data.size());
		assertEquals(new URL("http://yle.fi/index.html"),data.getFreeURL());
		URL test = new URL("http://get.free/index.html");
		data.addURL(test, new URL("http://yle.fi/index.html"));
		
	}

}
