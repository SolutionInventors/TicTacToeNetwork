package application.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class GameProcessorServer extends GameProcessor implements Runnable{
	
	private BufferedWriter writer; 
	private BufferedReader reader; 
	
	private static final String BOARD_COMMAND ="board"; 
	private static final String MOVE_COMMAND ="move"; 
	private static final String WAIT_COMMAND ="wait"; 
	private static final String STATUS_COMMAND ="status"; 
	
	public GameProcessorServer(Socket connection, 
			String p1Name, String p2Name) throws IOException {
		super(p1Name, p2Name);
		
		OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(),  "ASCII"); 
		InputStreamReader reader = new InputStreamReader(connection.getInputStream(),  "ASCII"); 
		
		this.writer = new BufferedWriter(writer); 
		this.reader = new BufferedReader(reader); 
		this.writer.flush(); 
	}
	
	
	@Override
	public void run() {
		while(true){
			try {
				
				String command = reader.readLine();
				switch(getCommandType(command)){
					case BOARD_COMMAND: 
						String[][] board = getBoard(); 
						writeHelper(String.format(
								"%s %s %s \r\n"
								+ "%s %s %s\r\n"
								+ "%s %s %s",
								board[0][0],board[0][1],board[0][2],
								board[1][0],board[1][1],board[1][2],
								board[2][0],board[2][1],board[2][2]));
						break; 
					case MOVE_COMMAND: 
						if(isPlayerTwoMove()){
							String position = command.replaceAll("\\s{2,}", " " );
							int pos = Integer.parseInt(position); 
							if(makeMove(pos))
								writeHelper("true"); 
							else
								writeHelper("Illegal Move"); 
							
						}else{
							writeHelper("false"); 
						}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
		}
	}

	
	private String getCommandType(String command) {
		if(command.trim().matches(BOARD_COMMAND)){
			return BOARD_COMMAND; 
		}else if(command.matches(String.format("(?i:%s[1-9]{1})", MOVE_COMMAND))){
			return MOVE_COMMAND; 
		}else if(command.startsWith(STATUS_COMMAND)){
			return STATUS_COMMAND; 
		}
		return null;
	}


	private void writeHelper(String data) throws IOException{
		writer.write(data + "\r\n");
		writer.flush();
	}
}
