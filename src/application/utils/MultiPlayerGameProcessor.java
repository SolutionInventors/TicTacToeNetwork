package application.utils;

import java.net.Socket;

public class MultiPlayerGameProcessor extends GameProcessor implements Runnable {

	private Socket playerOneConnection; 
	private Socket playerTwoConnection; 
	
	private static final String BOARD_COMMAND ="board"; 
	private static final String MOVE_COMMAND ="move"; 
	private static final String WAIT_COMMAND ="wait"; 
	private static final String STATUS_COMMAND ="status"; 
	
	public MultiPlayerGameProcessor(Socket p1Connection, Socket p2Connection, 
			String p1Name, String p2Name) {
		super(p1Name, p2Name);
		playerOneConnection= p1Connection; 
		playerTwoConnection= p2Connection; 
		
		
	}
	
	public boolean makeMove(){
		if(isPlayerOneMove()){
			
		}
	}
	
	private boolean sendMessage(String command){
		
	}

	@Override
	public void run() {
		while(true){
			
		}
	}

}
