import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;



/**
 * @author Mikko Rosten mikko.rosten@iki.fi
 * @author Teemu Miettinen tpjmie@utu.fi
 */

public class UrlCollection {
	
	private HashMap<String, Integer> domains;
	private Vector<LinkedList<Integer>> relations;
	private Vector<URL> hits;
	private Vector<Boolean> usedHits;
	private int position;
	
	private boolean adding; //for synchronize
	private boolean using; //for synchronize
	
	public UrlCollection(){
		domains = new HashMap<String, Integer>();
		relations = new Vector<LinkedList<Integer>>();
		hits = new Vector<URL>();
		usedHits = new Vector<Boolean>();
		position = -1;
		adding = false;
		using = false;
	}
	
	/**
	 * Add new url to collection
	 * it is checked that the domain is new
	 * @param newUrl new found url
	 * @param parent parent were search is done
	 */
	public synchronized void addURL(URL newUrl, URL parent){
		try{
			while(using){
				wait();
			}
			/**
			 * @todo add error handling for invalid url
			 */
			if (domains.get(newUrl.getHost())!=null){
				return;
			}
			assert getIndex(parent) >= 0 : "Invalid parent";
			adding=true;
			hits.add(newUrl);
			usedHits.add(false);
			int index=hits.size()-1;
			domains.put(newUrl.getHost(), index);
			
			if (parent!= null){
				LinkedList<Integer> childs = relations.elementAt(getIndex(parent));
				childs.add(index);
				LinkedList<Integer> tmp = new LinkedList<Integer>();
				relations.addElement(tmp);
			}
			else{
				LinkedList<Integer> childs = new LinkedList<Integer>();
				//childs.add(index);
				relations.addElement(childs);
			}
			adding=false;
			notifyAll();
		}
		catch(InterruptedException e){e.printStackTrace();}
		assert hits.size()==usedHits.size() : "data structure incoherent";
		assert hits.size()==domains.size() : "data structure incoherent";
		assert relations.size()==domains.size() : "data structure incoherent";
	}
	
	/**
	 * Checks if there is new domains to be search
	 * @return true if there is something to search
	 */
	public synchronized boolean hasFreeURL(){
		try{
			while(using||adding){wait();}
			System.out.println("FreeUrl state "+ position+"/"+hits.size());
			return position<hits.size()-1;
		}
		catch(InterruptedException e){
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * gives next free url to search
	 * @return next free url to search
	 */
	public synchronized URL getFreeURL(){
		try{
			while(adding){wait();}
			using=true;
			position++;
			URL res=hits.elementAt(position);
			
			using=false;
			notifyAll();
			return res;
		}
		catch(InterruptedException e){
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * calculate depth of url searchs
	 * @return int value
	 */
	public int getDepth(){
		try{
			while(adding){wait();}
			if (relations.size()==0){
				return 0;
			}

			return relationDepth(0);
		}
		catch(InterruptedException e){
			e.printStackTrace();
			return 0;
		}
	}
	
	private int relationDepth(int index){
		try{
			while(adding){wait();}
			if (relations.elementAt(index).size()==0){
				return 0;
			}
			else{
				int maxDepth=0;
				for (int i=0;i<relations.elementAt(index).size();i++){
					int depth=relationDepth(relations.elementAt(index).get(i));
					if (depth>maxDepth){
						maxDepth=depth;
					}
					
				}
				return maxDepth+1;
			}
		}
		catch(InterruptedException e){
			e.printStackTrace();
			return 0;
		}
		
	}
	
	/**
	 * Amount of found urls
	 * @return int
	 */
	public int size(){
		try{
			while(adding){wait();}
			return hits.size();
		}
		catch(InterruptedException e){
			e.printStackTrace();
			return 0;
		}
	}
	
	public String getURLTree(){
		return treePrint(0, 0);
		
	}
	
	private String treePrint(int depth, int index){
		if(relations.size()<index+1){
			return null;
		}
		LinkedList<Integer> childs = relations.elementAt(index);
		String tmp = new String();
		int pad = depth+hits.elementAt(index).toString().length();
		String format="%1$#"+ pad+"s\n";
		tmp = String.format(format, hits.elementAt(index).toString());
		
		for (Iterator<Integer> i=childs.iterator();i.hasNext();){
			int next=i.next();
			tmp+=treePrint(depth+1, next);
		}
		return tmp;
	}
	
	protected int getIndex(URL url){
		
		if (domains.containsKey(url.getHost())){
			return domains.get(url.getHost());
		}
		else
			return -1;
		
		
	}
	
	
}
