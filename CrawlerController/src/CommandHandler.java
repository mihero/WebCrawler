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
		SITES,
		HELP,
		QUIT
		
	}
	private Socket client;
	private SearchController SC;
	private PrintWriter out;
	private BufferedReader in;
	private boolean endThread = false;

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
			doGreeting();
			String cmd;
	        while(!endThread && (cmd=in.readLine()) != null){
	        	System.out.println(cmd);
	        	handleCommand(cmd);
	        }
        
        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	private void handleCommand(String cmd) throws IOException{
		
		String[] args = cmd.split(" ");
		try{
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
			case HELP:
				doHelp();
				break;
			case QUIT:
				client.close();
				endThread=true;
				break;
			default:
				doInvalid();
				break;
			}
		}
		catch(IllegalArgumentException e){
			e.printStackTrace();
			doInvalid();
		}
		
	}
	private void doList(){
		String[] crawlers=SC.getCrawlerList();
		/**
		 * print to output list of crawlers
		 */
		out.println("WebCrawler workers "+crawlers.length);
		out.print(crawlers);
	}
	private void doKill(String id){
		SC.killCrawler(id);
		
		
	}
	private void doCount(){
		out.println("Active workers currently"+SC.getNumberOfCrawlers());
		
	}
	private void doSiteCount(){
		out.println("Found sites so far \n" + SC.getFoundHits());
	}
	private void doInvalid(){
		out.println("Command was invalid use help");
		doHelp();
		
	}
	private void doHelp(){
		out.println("Valid commands are these");
		Commands[] tmp = Commands.values();
 		for (int i=0; i<tmp.length; i++){
 			out.println(tmp[i].toString());
 		}
		
		
	}
	private void doGreeting(){
		out.println("Welcome to WebCrawler system");
		out.println("Created by Mikko Rosten");
	}
}
