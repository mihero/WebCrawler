import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.Time;

/**
 * @author mihero
 *
 */
public class WebCrawler extends Crawler {
	
	private SearchProvider SP;
	private long executionTime;
	private static final long WAITTIME = 1000;
	/**
	 * @throws NotBoundException 
	 * @throws RemoteException 
	 * @throws MalformedURLException 
	 * 
	 */
	public WebCrawler(String url) throws MalformedURLException, RemoteException, NotBoundException {
		// TODO Auto-generated constructor stub
		super();
		SP = (SearchProvider)Naming.lookup(url+"/SearchHandler");
		String id = SP.register(this);
		setId(id);
		System.out.println("New crawler registered with id "+id);
		setState(States.READY);
		
		System.out.println(getId());
		
	}
	protected void finalize(){
		try {
			SP.unRegister(this);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void checkCommand() {
		
		try{
			if ( executionTime!=0 && getState()==States.WAITING && executionTime < System.currentTimeMillis()-WAITTIME){
				System.out.println("Wait timeout exceeded");
				setCommand(Commands.KILL);
				debugPrint();
				return;
			}
			if (executionTime==0){
				executionTime=System.currentTimeMillis();
			}
			Crawler.Commands cmd = SP.getCommand(this);
			if(cmd!=null){
				System.out.println("New command "+cmd.toString());
				debugPrint();
				if (cmd == Crawler.Commands.SEARCH){
					if (getState()!=States.WAITING){
						executionTime=System.currentTimeMillis();
					}
					setState(States.WAITING);
					if(SP.hasSearchable(this)){
						
						URL url = SP.getUrl(this);
						if (url!=null){
							setSite(url);
							doSiteSearch();
						}
					}
				}
				else if (cmd == Crawler.Commands.KILL ){
					executionTime=System.currentTimeMillis();
					setCommand(cmd);
				}
			}
			else{
				setState(States.WAITING);
				setCommand(Commands.SEARCH);
			}
		}
		catch(RemoteException e){
			System.err.println("No response from Server");
			setState(States.WAITING);
			executionTime=System.currentTimeMillis();
		}
		catch (NullPointerException e){
			System.err.println("No response from Server");
			setState(States.WAITING);
			executionTime=System.currentTimeMillis();
		}
	}
	private void doSiteSearch() {
		// TODO Auto-generated method stub
		setState(States.SEARCHING);
		
	}
	
	private void debugPrint(){
		//System.out.println("object "+getId().toString()+":"+getState().toString()+":"+getSite().toString()+":"+getCommand().toString());
		System.out.println(toString());
	}
	/* (non-Javadoc)
	 * @see Crawler#setCommand(Crawler.Commands)
	 */
	@Override
	public void setCommand(Commands cmd) {
		// TODO Auto-generated method stub
		Commands oldCmd = getCommand();
		super.setCommand(cmd);
		try {
			SP.setCommand(this);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			setCommand(oldCmd);
			//e.printStackTrace();
		}
		catch (NullPointerException e){
			super.setCommand(oldCmd);
			System.err.println("Error setting command");
		}
	}
	/* (non-Javadoc)
	 * @see Crawler#setState(Crawler.States)
	 */
	@Override
	public void setState(States state) {
		// TODO Auto-generated method stub
		super.setState(state);
		try {
			SP.setState(this);
		} 
		catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	

}
