import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Mikko Rosten mikko.rosten@iki.fi
 * @author Teemu Miettinen tpjmie@utu.fi
 */

/**
 * @author mihero
 *
 */
public class CrawlerStorage {
	
	private ConcurrentHashMap<String,Crawler> crawlers;
	private Vector<Crawler> data;
	private Vector<String> keys;
	//private static CrawlerStorage CrawlerStorageInstance;
	
//	public static synchronized CrawlerStorage getSingletonObject() {
//		if (CrawlerStorageInstance == null) {
//			CrawlerStorageInstance = new CrawlerStorage();
//		}
//		return CrawlerStorageInstance;
//	}
	
	public CrawlerStorage(){
		crawlers = new ConcurrentHashMap<String, Crawler>();
		keys = new Vector<String>();
		data = new Vector<Crawler>();
	}
	
	protected void finalize(){
		data.clear();
		crawlers.clear();
		keys.clear();
	}
	
	public boolean isValid(Crawler worker){
		return crawlers.containsKey(worker.getId());
		//return keys.contains(worker.getId());
	}
	
	public Crawler get(Crawler worker){
		return crawlers.get(worker.getId());
		//return data.elementAt(keys.indexOf(worker.getId()));
		
	}
	public Crawler get(String id){
		return crawlers.get(id);
		//return data.elementAt( keys.indexOf(id));
		
	}
	public boolean add(Crawler worker){
		//keys.add(worker.getId());
		crawlers.put(worker.getId(), worker);
		
		//data.add(worker);
		return true;
	}
	
	public Integer size(){
		return crawlers.size();
	}
	public boolean hasWorker(String id){
		return crawlers.contains(id);
	}
	public void remove(Crawler worker){
		//data.remove(get(worker));
		//keys.remove(worker.getId());
		crawlers.remove(worker.getId());
	}
	
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

}
