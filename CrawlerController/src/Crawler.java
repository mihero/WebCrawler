import java.io.Serializable;
import java.net.URL;



/**
 * @author Mikko Rosten mikko.rosten@iki.fi
 * @author Teemu Miettinen tpjmie@utu.fi
 */

public class Crawler implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum Commands {
		KILL,
		SEARCH
	}
	public enum States {
		READY,
		WAITING,
		SEARCHING
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Crawler other = (Crawler) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	private String id;
	private States state;
	private Commands command;
	private URL site;
	
	public Crawler(){
		id = null;
		state = States.READY;
		command = Commands.SEARCH;
		site = null;
	}
	
	public Crawler(Crawler worker){
		id=worker.id;
		state=worker.state;
		command=worker.command;
		site=worker.site;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Crawler [id=" + id + ", state=" + state + ", command="
				+ command + ", site=" + site + "]";
	}

	/**
	 * @return the site
	 */
	public URL getSite() {
		return site;
	}

	/**
	 * @param site the site to set
	 */
	public void setSite(URL site) {
		this.site = site;
	}

	/**
	 * @return the state
	 */
	public States getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(States state) {
		this.state = state;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	public void setCommand(Commands cmd) {
	    System.out.println("Set command "+cmd);
	    //if already in kill mode don't change
		if (command!=Commands.KILL){
		    command=cmd;
		}
		
	}

	/**
	 * @return the command
	 */
	public Commands getCommand() {
		return command;
	}
	
	
	
}
