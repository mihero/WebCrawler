import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.Time;
import java.util.Vector;
import java.net.*;
import java.io.*;

/**
 * @author mihero
 *
 */
public class WebCrawler extends Crawler {
	
	private SearchProvider SP;
	private long executionTime;
	private static final long WAITTIME = 30000;
	static final int HTTP=80; // http port
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
						System.out.println("site to search "+url.toString());
						if (url!=null){
							setSite(url);
							try {
								doSiteSearch();
							} catch (UnknownHostException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
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
	private void doSiteSearch() throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		setState(States.SEARCHING);
		
		// Create socket connection to a server (hostname: args[0])
	    Socket s = new Socket(getSite().getHost() , HTTP);

	    // Open write channel to the server
	    BufferedWriter sout = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));

	    // Make the GET-command
	    sout.write("GET "+getSite().getPath() + " HTTP/1.0");
	    sout.newLine();
	    sout.write("Accept: text/plain, text/html, text/*");
	    sout.newLine();

	    // Make HOST-command
	    sout.write("Host: "+getSite().getHost());
	    sout.newLine();

	    // HTTP-protocol requires one empty line
	    sout.newLine();

	    // Send everything over the network
	    sout.flush();

	    // Open a connection for reading the response
	    BufferedReader sin = new BufferedReader(new InputStreamReader(s.getInputStream()));

	    // Go through the response
	    String line = " ";
	    Vector<URL> urls = new Vector<URL>();
	    while ((line=sin.readLine()) != null){
		// Print only lines containing the mark '@'
		//	if (line.indexOf('@') != -1)
	    	System.out.println(line);
	    	int hrefpos=line.indexOf("href=");
	    	if(hrefpos>0){
		    	int startpos=line.indexOf('"',hrefpos+4)+1;
		    	int stoppos=line.indexOf('"',startpos);
		    	if(startpos<stoppos && startpos>0 && stoppos>0){
		    		String url=line.substring(startpos,stoppos);
		    		System.out.println(url);
		    		try{
		    			urls.add(new URL(url));
		    		}
		    		catch(MalformedURLException e){
		    			System.out.println(url);
		    		}
		    	}
	    	}
		    
	    }
	    // Close connections (after a timeout)
	    sout.close();
	    sin.close();
	    s.close();
		SP.addSearchResult((URL[]) urls.toArray(new URL[0]), this);
		setState(States.READY);
		
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
