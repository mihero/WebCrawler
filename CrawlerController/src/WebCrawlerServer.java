import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;

/**
 * @author Mikko Rosten mikko.rosten@iki.fi
 * @author Teemu Miettinen tpjmie@utu.fi
 */
public class WebCrawlerServer {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws AlreadyBoundException 
	 */
	public static void main(String[] args) throws IOException, AlreadyBoundException {
		// TODO Auto-generated method stub
		/**
		 * Create serversocket that listens controller clients joining in and creates Commandhandler for every one
		 * also starts the search controller/provider object for handling server functionality
		 * it is passed to commandhandler
		 */
		try {
			rmiStarter(SearchHandler.class);
			SearchHandler SH;
			if (args[0]!=null)
				SH = new SearchHandler(args[0]);
			else
				SH = new SearchHandler();
			Naming.rebind("SearchHandler", SH);
		
			ServerSocket SS = new ServerSocket(1026);
			System.out.println("WebCrawler Command socket at:"+SS.getLocalPort());
			while (true){
				Socket CS = SS.accept();
				CommandHandler CH = new CommandHandler(SH, CS);
				System.out.println("Creating new CommandHandler");
				CH.start();
			}
		} catch (RemoteException e) {
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
