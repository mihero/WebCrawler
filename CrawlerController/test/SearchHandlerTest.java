import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Mikko Rosten mikko.rosten@iki.fi
 * @author Teemu Miettinen tpjmie@utu.fi
 */


public class SearchHandlerTest {
	private SearchHandler SH;
	private Crawler C;
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
		SH = new SearchHandler();
		C = new Crawler();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		SH = null;
		C = null;
	}

	/**
	 * Test method for {@link SearchHandler#hasSearchable(Crawler)}.
	 * @throws MalformedURLException 
	 * @throws RemoteException 
	 */
	@Test
	public void testHasSearchable() throws MalformedURLException, RemoteException {
		registerCrawler();
		SH.setSeed(new URL("http://www.seed.tst/index.html"));
		assertTrue(SH.hasSearchable(C));
		assertEquals(Crawler.States.WAITING, C.getState());
		Crawler newC = new Crawler();
		assertFalse(SH.hasSearchable(newC));
		
		//fail("Not yet implemented"); // TODO
	}

	

	/**
	 * Test method for {@link SearchHandler#addSearchResult(java.net.URL[], Crawler)}.
	 * @throws RemoteException 
	 * @throws MalformedURLException 
	 */
	@Test
	public void testAddSearchResult() throws RemoteException, MalformedURLException {
		
		registerCrawler();
		URL[] data = new URL[3];
		data[0]=new URL("http://www.test.tst/index.html");
		data[1]=new URL("http://www.test2.tst/index.html");
		data[2]=new URL("http://www.test3.tst/index.html");
		
		SH.addSearchResult(data, C);
		assertEquals(0, SH.getFoundHits());
		
		SH.setSeed(new URL("http://www.seed.tst/index.html"));
		SH.getUrl(C);
		
//		SH.addSearchResult(data, C);
//		assertEquals(0, SH.getFoundHits());
//		C.setSite(new URL("http://www.seed.tst/index.html"));
		
		SH.addSearchResult(data, C);
		assertEquals(4, SH.getFoundHits());
		
		SH.addSearchResult(data, C);
		assertEquals(4, SH.getFoundHits());
		
		
		//fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link SearchHandler#getCommand(Crawler)}.
	 * @throws RemoteException 
	 */
	@Test
	public void testGetCommand() throws RemoteException {
		registerCrawler();
		int i = SH.getCommand(C);
		assertEquals(Crawler.Commands.SEARCH.ordinal(), i);
		assertEquals(Crawler.Commands.SEARCH, C.getCommand());
		
		//fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link SearchHandler#register(Crawler)}.
	 * @throws RemoteException 
	 */
	@Test
	public void testRegister() throws RemoteException {
		
		String test;
		registerCrawler();
		test = SH.register(C);
		assertNull(test);
		assertNotNull(C.getId());
		assertEquals(SH.getNumberOfCrawlers(),1);
		//fail("Not yet implemented"); // TODO
	}

	/**
	 * @throws RemoteException
	 */
	private void registerCrawler() throws RemoteException {
		String test = SH.register(C);
		assertNotNull(test);
		assertNotNull(C.getId());
	}

	/**
	 * Test method for {@link SearchHandler#unRegister(Crawler)}.
	 * @throws RemoteException 
	 */
	@Test
	public void testUnRegister() throws RemoteException {
		registerCrawler();
		int i = SH.getNumberOfCrawlers();
		SH.unRegister(C);
		assertEquals(i-1,SH.getNumberOfCrawlers());
		assertNull(C.getId());
		
		
	}

	/**
	 * Test method for {@link SearchHandler#killCrawler(java.lang.String)}.
	 * @throws RemoteException 
	 */
	@Test
	public void testKillCrawler() throws RemoteException {
		registerCrawler();
		SH.killCrawler(C.getId());
		assertEquals(Crawler.Commands.KILL, C.getCommand());
		
		//fail("Not yet implemented"); // TODO
	}
	
	@Test
	public void testRMI() throws RemoteException, MalformedURLException, NotBoundException{
		rmiStarter(SearchHandler.class);
		Naming.rebind("SearchHandler", SH);
		//System.setSecurityManager(new RMISecurityManager());
		SearchProvider SP = (SearchProvider)Naming.lookup(new String("rmi://localhost/SearchHandler"));
		
	}
	
	/**
    *
    * @param clazzToAddToServerCodebase a class that should be in the java.rmi.server.codebase property.
    */
   public void rmiStarter(Class clazzToAddToServerCodebase) {

       System.setProperty("java.rmi.server.codebase", clazzToAddToServerCodebase
           .getProtectionDomain().getCodeSource().getLocation().toString());

       System.setProperty("java.security.policy", getLocationOfPolicyFile());

       if(System.getSecurityManager() == null) {
           System.setSecurityManager(new SecurityManager());
       }

       //doCustomRmiHandling();
   }
   

   public static String getLocationOfPolicyFile() {
       try {
    	   String POLICY_FILE_NAME = "/allow_all.policy";
           File tempFile = File.createTempFile("SearchHandler", ".policy");
           InputStream is = SearchHandler.class.getResourceAsStream(POLICY_FILE_NAME);
           BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
           int read = 0;
           while((read = is.read()) != -1) {
               writer.write(read);
           }
           writer.close();
           tempFile.deleteOnExit();
           return tempFile.getAbsolutePath();
       }
       catch(IOException e) {
           throw new RuntimeException(e);
       }
   }

	

}
