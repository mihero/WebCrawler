import java.net.URL;
import java.rmi.Remote;
import java.rmi.RemoteException;



/**
 * @author Mikko Rosten mikko.rosten@iki.fi
 * @author Teemu Miettinen tpjmie@utu.fi
 */

public interface SearchProvider extends Remote {
	
	/**
	 * Ask if there is something to search
	 * @return
	 * @throws RemoteException
	 */
	public boolean hasSearchable(Crawler worker) throws RemoteException;
	
	
	/**
	 * Get url to crawl
	 * @return url to search
	 * @throws RemoteException
	 */
	public URL getUrl(Crawler worker) throws RemoteException;
	
	
	/**
	 * Add new URL to controller what the search found
	 * @param data found urls
	 * @throws RemoteException
	 */
	public void addSearchResult(URL[] data,Crawler worker) throws RemoteException;
	
	/**
	 * get command to know what to do
	 * @return command what to do (search, kill, status)
	 * @throws RemoteException
	 */
	public int getCommand(Crawler worker) throws RemoteException;
	
	/**
	 * Register to provider
	 * @param worker TODO
	 * @return unique identifier
	 * @throws RemoteException
	 */
	public String register(Crawler worker) throws RemoteException;
	
	/**
	 * unregister identifier from provider
	 * @param id unique identifier
	 * @throws RemoteException
	 */
	public void unRegister(Crawler worker) throws RemoteException;
}
