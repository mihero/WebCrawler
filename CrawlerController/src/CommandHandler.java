import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author Mikko Rosten mikko.rosten@iki.fi
 * @author Teemu Miettinen tpjmie@utu.fi
 */
public class CommandHandler extends Thread {
	
	private enum Commands {
		LIST,
		KILL,
		COUNT,
		SITES
		
	}
	private Socket client;
	private SearchController SC;
	private PrintWriter out;
	private BufferedReader in;

	public CommandHandler(SearchController controller, Socket soc){
		client=soc;
		SC=controller;
	}
	public void run(){
		/**
		 * listen for socket and handle commands received from there
		 */
		try {
			out = new PrintWriter(client.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		
			String cmd;
	        while((cmd=in.readLine()) != null){
	        	handleCommand(cmd);
	        }
        
        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	private void handleCommand(String cmd){
		
		String[] args = cmd.split(" ");
		Commands command = Commands.valueOf(args[0].toUpperCase());
		
		switch (command){
		case LIST:
			doList();
			
			break;
		case KILL:
			if (args.length<2){
				doInvalid();
			}
			else{
				try{
					
					doKill(args[1]);
				}
				catch(NumberFormatException e){
					System.err.println(e.toString());
					doInvalid();
				}
				
			}
						
			break;
		case COUNT:
			doCount();
			break;
		case SITES:
			doSiteCount();
			break;
		default:
			doInvalid();
			break;
		}
		
	}
	private void doList(){
		String[] crawlers=SC.getCrawlerList();
		/**
		 * print to output list of crawlers
		 */
		out.print(crawlers);
	}
	private void doKill(String id){
		SC.killCrawler(id);
		
		
	}
	private void doCount(){
		SC.getNumberOfCrawlers();
		
	}
	private void doSiteCount(){
		SC.getFoundHits();
	}
	private void doInvalid(){
		
	}
}
