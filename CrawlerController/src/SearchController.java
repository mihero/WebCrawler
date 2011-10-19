/**
 * @author Mikko Rosten mikko.rosten@iki.fi
 * @author Teemu Miettinen tpjmie@utu.fi
 */
public interface SearchController {
	
	public void killCrawler(int id);
	
	public String[] getCrawlerList();
	
	public int getNumberOfCrawlers();
	
	public int getFoundHits();
	
	
}
