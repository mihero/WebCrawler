import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
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
	static final int IDLENGHT = 2;
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
		else if (crawlers.containsKey(worker.getId())){
			System.out.println("Has free url "+worker.getId()+" depth "+urlData.getDepth());
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
			//worker.setState(Crawler.States.WAITING);
			//worker.setCommand(Crawler.Commands.KILL);
			return null;
		}
		else if (crawlers.containsKey(worker.getId())){
			System.out.println("Getting new url");
			Crawler obj=crawlers.get(worker.getId());
			
			obj.setState(Crawler.States.SEARCHING);
			obj.setSite(urlData.getFreeURL());
			System.out.println("Getting new url "+obj.getSite().toString());
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
		if (crawlers.containsKey(worker.getId())&& crawlers.get(worker.getId()).getSite()!=null&& crawlers.get(worker.getId()).getState()==Crawler.States.SEARCHING){
			for (int i = 0;i<data.length;i++){
				urlData.addURL(data[i],worker.getSite());
			}
			Crawler obj=crawlers.get(worker.getId());
			
			obj.setState(Crawler.States.READY);
			obj.setSite(null);
			obj.setCommand(Crawler.Commands.SEARCH);
			
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
		System.out.println("Worker getting command "+worker.getId());
		try{
			return crawlers.get(worker.getId()).getCommand();
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
			while(crawlers.containsKey(newId)){newId=createRandomString(IDLENGHT);}
			worker.setId(newId);
			//set to default state
			worker.setCommand(Crawler.Commands.SEARCH);
			worker.setState(Crawler.States.READY);
			worker.setSite(null);
			crawlers.put(worker.getId(), new Crawler(worker));
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
		worker.setId(null);

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
		String[] arr= new String[sb.size()];
		sb.toArray(arr);
		return arr;
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
			if (crawlers.containsKey(worker.getId())){
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
			if (crawlers.containsKey(worker.getId())){
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
