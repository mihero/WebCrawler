import java.net.URL;

/**
 * @author Mikko Rosten mikko.rosten@iki.fi
 * @author Teemu Miettinen tpjmie@utu.fi
 */
public interface SearchController {
	
	public void killCrawler(String id);
	
	public String[] getCrawlerList();
	
	public int getNumberOfCrawlers();
	
	public int getFoundHits();
	
	public void setSeed(URL seed);
}
