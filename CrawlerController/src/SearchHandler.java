import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;






/**
 * Implements control and provider functionality
 * Collects data into UrlCollection
 * @author Mikko Rosten mikko.rosten@iki.fi
 * @author Teemu Miettinen tpjmie@utu.fi
 */


public class SearchHandler extends UnicastRemoteObject implements
		SearchController, SearchProvider {

	
	private UrlCollection urlData;
	private int crawlerMax;
	static final int IDLENGHT = 2;
	private int dataDepthMax;
	private CrawlerStorage crawlers;

	private String createRandomString(int length) {
		Random random = new Random();
		StringBuilder sb = new StringBuilder();
		
		while (sb.length() < length) {
			
			sb.append(Integer.toHexString(random.nextInt(16)));
		}
		return sb.toString();
	}

	/**
	 * @throws RemoteException
	 */
	public SearchHandler() throws RemoteException {
		
		crawlers = new CrawlerStorage();
		urlData = new UrlCollection();
		crawlerMax = 10; // default value
		dataDepthMax = 5; //default value
	}
	
	public SearchHandler(String seed)throws RemoteException {
		this();
		try {
			setSeed(new URL(seed));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		else if (crawlers.isValid(worker)){
			//System.out.println("Has free url "+worker.getId()+" depth "+urlData.getDepth());
			if(urlData.getDepth()>=dataDepthMax){
				//worker.setState(Crawler.States.WAITING);
				//worker.setCommand(Crawler.Commands.KILL);
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
			//worker.setState(Crawler.States.WAITING);
			//worker.setCommand(Crawler.Commands.KILL);
			return null;
		}
		else if (crawlers.isValid(worker)){
			//System.out.println("Getting new url");
			Crawler obj=crawlers.get(worker);
			
			obj.setState(Crawler.States.SEARCHING);
			obj.setSite(urlData.getFreeURL());
			System.out.println("Getting new url "+obj.getSite().toString()+ " for "+worker.getId());
			return obj.getSite();
		}
		else{
			System.err.println("GetUrl Illegal worker id:" + worker.getId());
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
		//System.out.println(Arrays.toString(getCrawlerList()));
		if (crawlers.isValid(worker)){
			Crawler obj=crawlers.get(worker);
			if (obj.getSite()!=null && obj.getState()==Crawler.States.SEARCHING){
			
		
		
				for (int i = 0;i<data.length;i++){
					urlData.addURL(data[i],worker.getSite());
				}
				
				
				obj.setState(Crawler.States.READY);
				obj.setSite(null);
				obj.setCommand(Crawler.Commands.SEARCH);
			}
			
		}
		else{
			System.err.println(Arrays.toString(getCrawlerList()));
			System.err.println(worker.toString());
			System.err.println("AddSearchResult Illegal worker id:" + worker.getId());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see SearchProvider#getCommand(Crawler)
	 */
	@Override
	public Crawler.Commands getCommand(Crawler worker) throws RemoteException {
		// TODO Auto-generated method stub
		//System.out.println("Worker getting command "+worker.getId());
		try{
			//if (getFoundHits()>50){
			//	System.gc();
			//}
			return crawlers.get(worker).getCommand();
		}
		catch(NullPointerException e){
			System.err.println("No worker for " +worker.getId());
			return null;
		}
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
			while(crawlers.hasWorker(newId)){newId=createRandomString(IDLENGHT);}
			worker.setId(newId);
			//set to default state
			worker.setCommand(Crawler.Commands.SEARCH);
			worker.setState(Crawler.States.READY);
			worker.setSite(null);
			//Crawler tmp = new Crawler(worker);
			//add copy of worker into list
			crawlers.add( new Crawler(worker));
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
		
		crawlers.remove(worker);
		worker.setId(null);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see SearchController#killCrawler(int)
	 */
	@Override
	public void killCrawler(String id) {
	    System.out.println("Kill crawler "+id);
		crawlers.get(id).setCommand(Crawler.Commands.KILL);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see SearchController#getCrawlerList()
	 */
	@Override
	public String[] getCrawlerList() {
		
		return crawlers.getCrawlerList();
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

	@Override
	public void setSeed(URL seed) {
		if (urlData.size()==0){
			urlData.addURL(seed, null);
		}
		else{
			System.err.println("Setting seed to non-empty");
		}
		
	}

	@Override
	public void setState(Crawler worker) throws RemoteException {
		// TODO Auto-generated method stub
		try{
			if (crawlers.isValid(worker)){
				crawlers.get(worker.getId()).setState(worker.getState());
			}
		}
		catch(NullPointerException e){
			System.err.print(worker.toString());
		}
		
	}

	@Override
	public void setCommand(Crawler worker) throws RemoteException {
		// TODO Auto-generated method stub
		try{
			if (crawlers.isValid(worker)){
				crawlers.get(worker.getId()).setCommand(worker.getCommand());
			}
		}
		catch(NullPointerException e){
			System.err.print(worker.toString());
		}
	}

	@Override
	public int getDepth() {
		// TODO Auto-generated method stub
		return urlData.getDepth();
	}

	@Override
	public String getSearchTree() {
		// TODO Auto-generated method stub
		return urlData.getURLTree();
	}

}
