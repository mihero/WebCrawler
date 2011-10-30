import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * 
 */
public class CrawlerClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			rmiStarter(WebCrawler.class);
			WebCrawler WC = new WebCrawler("rmi://localhost");
			while(WC.getCommand()!=Crawler.Commands.KILL){
				WC.checkCommand();
				System.out.println("Sleeping...");
				Thread.sleep(500);
				
			}
			System.out.println("Closing Crawler client, bye.");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	
	/**
    *
    * @param clazzToAddToServerCodebase a class that should be in the java.rmi.server.codebase property.
    */
   public static void rmiStarter(Class clazzToAddToServerCodebase) {

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
