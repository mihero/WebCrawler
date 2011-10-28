import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.Vector;






/**
 * @author Mikko Rosten mikko.rosten@iki.fi
 * @author Teemu Miettinen tpjmie@utu.fi
 */

/**
 * @author mihero
 * 
 */
public class SearchHandler extends UnicastRemoteObject implements
		SearchController, SearchProvider {

	private HashMap<String,Crawler> crawlers;
	private UrlCollection urlData;
	private int crawlerMax;
	static final int IDLENGHT = 8;
	private int dataDepthMax;

	private String createRandomString(int length) {
		Random random = new Random();
		StringBuilder sb = new StringBuilder();
		while (sb.length() < length) {
			sb.append(Integer.toHexString(random.nextInt()));
		}
		return sb.toString();
	}

	/**
	 * @throws RemoteException
	 */
	public SearchHandler() throws RemoteException {
		// TODO Auto-generated constructor stub
		crawlers = new HashMap<String, Crawler>();
		urlData = new UrlCollection();
		crawlerMax = 10; // default value
		dataDepthMax = 5; //default value
	}

	/**
	 * @param arg0
	 * @throws RemoteException
	 */
	public SearchHandler(int arg0) throws RemoteException {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see SearchProvider#hasSearchable(Crawler)
	 */
	@Override
	public boolean hasSearchable(Crawler worker) throws RemoteException {
		if (worker.getState() == Crawler.States.SEARCHING){
			System.err.println("worker allready searching id:"+worker.getId());
			return false;
		}
		else if (crawlers.containsKey(worker.getId())){
			if(urlData.getDepth()>=dataDepthMax){
				worker.setState(Crawler.States.WAITING);
				worker.setCommand(Crawler.Commands.KILL);
				return false;
			}
			else{
				worker.setState(Crawler.States.WAITING);
			
				return urlData.hasFreeURL();
			}
		}
		else{
			System.err.println("invalid worker id:"+worker.getId());
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see SearchProvider#getUrl(Crawler)
	 */
	@Override
	public URL getUrl(Crawler worker) throws RemoteException {
		// TODO Auto-generated method stub
		if (!urlData.hasFreeURL() || urlData.getDepth()>=dataDepthMax){
			//we are in the end
			worker.setState(Crawler.States.WAITING);
			worker.setCommand(Crawler.Commands.KILL);
			return null;
		}
		else if (crawlers.containsKey(worker.getId())){
			worker.setState(Crawler.States.SEARCHING);
			worker.setSite(urlData.getFreeURL());
			return worker.getSite();
		}
		else{
			System.err.println("Illegal worker id:" + worker.getId());
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see SearchProvider#addSearchResult(java.net.URL[], Crawler)
	 */
	@Override
	public void addSearchResult(URL[] data, Crawler worker)
			throws RemoteException {
		if (crawlers.containsKey(worker.getId())){
			for (int i = 0;i<data.length;i++){
				urlData.addURL(data[i],worker.getSite());
			}
			worker.setSite(null);
			worker.setState(Crawler.States.READY);
		}
		else{
			System.err.println("Illegal worker id:" + worker.getId());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see SearchProvider#getCommand(Crawler)
	 */
	@Override
	public int getCommand(Crawler worker) throws RemoteException {
		// TODO Auto-generated method stub
		return crawlers.get(worker.getId()).getCommand().ordinal();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see SearchProvider#register()
	 */
	@Override
	public String register(Crawler worker) throws RemoteException {
		// TODO Auto-generated method stub
		if(crawlers.size()==crawlerMax){
			return null;
		}
		if (worker.getId()==null){
			String newId=createRandomString(IDLENGHT);
			while(crawlers.containsKey(newId)){newId=createRandomString(IDLENGHT);}
			worker.setId(newId);
			//set to default state
			worker.setState(Crawler.States.READY);
			worker.setSite(null);
			crawlers.put(worker.getId(), worker);
			return worker.getId();
		}
		else{
			System.err.println("Illegal worker id:" + worker.getId());
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see SearchProvider#unRegister(Crawler)
	 */
	@Override
	public void unRegister(Crawler worker) throws RemoteException {
		
		crawlers.remove(worker.getId());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see SearchController#killCrawler(int)
	 */
	@Override
	public void killCrawler(String id) {
		
		crawlers.get(id).setCommand(Crawler.Commands.KILL);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see SearchController#getCrawlerList()
	 */
	@Override
	public String[] getCrawlerList() {
		Vector<String> sb = new Vector<String>();
		
		Iterator<String> i= crawlers.keySet().iterator();
		while(i.hasNext()){
			sb.add(new String(i.next()));
		}
		
		return (String[])sb.toArray(new String[0]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see SearchController#getNumberOfCrawlers()
	 */
	@Override
	public int getNumberOfCrawlers() {
		
		return crawlers.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see SearchController#getFoundHits()
	 */
	@Override
	public int getFoundHits() {
		// TODO Auto-generated method stub
		return urlData.size();
	}

	public int getDataDepthMax() {
		return dataDepthMax;
	}

	public void setDataDepthMax(int dataDepthMax) {
		this.dataDepthMax = dataDepthMax;
	}

}
