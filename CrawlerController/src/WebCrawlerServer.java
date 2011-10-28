import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;

/**
 * @author Mikko Rosten mikko.rosten@iki.fi
 * @author Teemu Miettinen tpjmie@utu.fi
 */
public class WebCrawlerServer {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		/**
		 * Create serversocket that listens controller clients joining in and creates Commandhandler for every one
		 * also starts the search controller/provider object for handling server functionality
		 * it is passed to commandhandler
		 */
		try {
			SearchHandler SH = new SearchHandler();
		
			ServerSocket SS = new ServerSocket();
			while (true){
				Socket CS = SS.accept();
				CommandHandler CH = new CommandHandler(SH, CS);
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
