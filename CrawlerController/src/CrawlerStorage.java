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
	
	
	
	public CrawlerStorage(){
		crawlers = new ConcurrentHashMap<String, Crawler>();
		
	}
	
	protected void finalize(){
		
		crawlers.clear();
		
	}
	
	public boolean isValid(Crawler worker){
		return crawlers.containsKey(worker.getId());
		
	}
	
	public Crawler get(Crawler worker){
		return crawlers.get(worker.getId());
		
		
	}
	public Crawler get(String id){
		return crawlers.get(id);
		
		
	}
	public boolean add(Crawler worker){
		
		crawlers.put(worker.getId(), worker);
		return true;
	}
	
	public Integer size(){
		return crawlers.size();
	}
	public boolean hasWorker(String id){
		return crawlers.containsKey(id);
	}
	public void remove(Crawler worker){
		
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
