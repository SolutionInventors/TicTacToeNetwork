package application.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class MultithreadedServer {
	private final int port; 
	private final ExecutorService service; 
	
	private final static Logger errorLogger = Logger.getLogger("errors");
	private enum Status{
		X_WON, O_WON, DRAW, 
		X_TO_MOVE, O_TO_MOVE;


		public String playerToMove(){
			switch(this){

			case O_TO_MOVE:
				return "O"; 
			case X_TO_MOVE:
				return "X"; 
			default:
				return ""; 
			}
		}

	}


	public MultithreadedServer(int port, int poolSize){
		this.port = port; 
		service = Executors.newFixedThreadPool(poolSize); 
	}

	public void start(){
		System.out.println("Listening on port " + port + " .........");

		
		try(ServerSocket server = new ServerSocket(port); ){
			
			while(true){
				try{
					System.out.println("Waiting for player 1.... ");
					@SuppressWarnings("resource")
					Socket player1 = server.accept(); 
					System.out.println("Waiting for player 2.... ");
					@SuppressWarnings("resource")
					Socket player2  = server.accept(); 
					TicTactToeProcessor process = new TicTactToeProcessor(player1, player2); 
					service.execute(process);
					
					
				} catch (IOException e) {
					// TODO Auto-generated catch block

					e.printStackTrace();
				}catch(RuntimeException e){
					e.printStackTrace();
					errorLogger.severe("A runtime exception occured while processing client "+ e);
				}
			}


		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}catch(RuntimeException e){
			e.printStackTrace();
			errorLogger.severe("A runtime exception occured while starting up server "+ e);
		}
	}

	private class TicTactToeProcessor implements Runnable{

		private final Socket connection1;
		private final Socket connection2; 
		
		private  String connOneType;
		private Status gameStatus; 
		private  String[][] board; 

		public TicTactToeProcessor(Socket connection1, Socket connection2){
			this.connection1 = connection1; 
			this.connection2 = connection2; 
			connOneType = "X";  

		}

		private  void playGame(){
			try(
					OutputStream raw1 = connection1.getOutputStream(); 
					BufferedWriter writer1 = new BufferedWriter(new OutputStreamWriter(raw1, "ASCII")); 
					InputStream rawIn1 = connection1.getInputStream(); 
					BufferedReader reader1 = new BufferedReader(new InputStreamReader(rawIn1, "ASCII")); 
					
					OutputStream raw2 = connection2.getOutputStream(); 
					BufferedWriter writer2 = new BufferedWriter(new OutputStreamWriter(raw2, "ASCII")); 
					InputStream rawIn2 = connection2.getInputStream(); 
					BufferedReader reader2 = new BufferedReader(new InputStreamReader(rawIn2, "ASCII")); 
					)
			{

				gameStatus = Status.X_TO_MOVE; 
				String resp = "yes"; 

				while(resp.toLowerCase().matches("yes") ){
					initialiseBoard(); 
					
					String message = String.format("%s\r\nThe server is %s \r\n"
							+ "Wait for %s to play. \r\n", getBoardString(), connOneType, gameStatus.playerToMove()); 

					writeToPlayers(writer1, writer2, message); 
					

					while(gameStatus == Status.X_TO_MOVE || gameStatus== Status.O_TO_MOVE){

						message = "\r\nCurrent  state of board: \r\n "; 
						message += getBoardString(); 
						message+= "Wait for " + gameStatus.playerToMove() + " to move....\r\n"; 

						if(!gameStatus.playerToMove().matches(connOneType)){
							resp = getMove(writer1, reader1); //gets the move from the client
						}else{
							resp = getMove(writer2, reader2);
						}

						makeMove(Integer.parseInt(resp), gameStatus.playerToMove()); 


						message = String.format("%s played %s \r\nCurrent board state: \r\n%s",
								gameStatus.playerToMove(), resp, getBoardString()); 

						writeToPlayers(writer1, writer2, message); 
						updateStatus(); 
						if(gameStatus == Status.O_TO_MOVE){
							message = String.format("%s to move!", gameStatus.playerToMove()); 

							writer1.flush();

							if(!gameStatus.playerToMove().matches(connOneType)){
								resp = getMove(writer1, reader1); //gets the move from the client
							}else{

								resp = getMove(writer2, reader2);
							}

							makeMove(Integer.parseInt(resp), gameStatus.playerToMove()); 
							updateStatus(); 
						}
						message = "\r\nCurrent State of the board:\r\n " + getBoardString() + "\r\n"; 
						switch(gameStatus){
						case DRAW:
							message +="The game ended in a draw\r\n"; 
							break; 
						case O_WON:
							message += "O won the game\r\n"; 
							break; 
						case X_WON:
							message += "X won the game\r\n"; 
							break; 
						default:
							message += ""; 

						}

						writeToPlayers(writer1, writer2, message);

					}

					message = "Would you like to play again: \r\nType yes or no:"; 
					
					writeToPlayers(writer1, writer2, message); 
					resp = reader1.readLine(); 
					
					if(resp!= null && 
							resp.toLowerCase().matches(reader2.readLine().toLowerCase()) &&
							resp.toLowerCase().matches("yes")){
						gameStatus = Status.X_TO_MOVE; 
						//toggle connOneType
						connOneType = connOneType.toUpperCase().matches("O") ? "X" : "O"; 	
						message = "\r\nRestarting game.......\r\n"; 
						writeToPlayers(writer1, writer2, message); 
						
					}else{
						message = "\r\nOne of the players refused to continue\r\n"; 
						writeToPlayers(writer1, writer2, message); 
						
					}
				}



			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch(NullPointerException e ){
				e.printStackTrace(); 
				errorLogger.warning("A NULL error was caught while processing client\n " + e);
			}
		}

		/**
		 * @param writer1
		 * @param writer2
		 * @param message
		 * @throws IOException
		 */
		public void writeToPlayers(BufferedWriter writer1, BufferedWriter writer2, String message) throws IOException {
			writer1.write(message);
			writer1.flush(); 
			writer2.write(message);
			writer2.flush();
		}



		private String getMove(BufferedWriter writer, BufferedReader reader) throws IOException {

			writer.write("Your turn to move: ");
			writer.flush();
			String resp = reader.readLine();  

			while(!validateMove(resp)){
				writer.write("\r\nThe move you made was invalid\r\n");
				writer.write("\r\nTry again: " );
				writer.flush();
				resp = reader.readLine(); 
			}

			return resp; 
		}


		

		private void initialiseBoard() {
			String[][] initBoard =  {
					{"1", "2", "3"}, 
					{"4", "5", "6"},
					{"7", "8", "9"}, 
			}; 
			board =  initBoard; 

		}

		private void updateStatus() {

			//horizontal check
			String valToCheck = board[0][0]; 
			if(board[0][1].matches(valToCheck) && board[0][2].matches(valToCheck)){
				updateWinnerHelper(valToCheck); 
				return; 
			}
			valToCheck = board[1][0]; 
			if(board[1][1].matches(valToCheck) && board[1][2].matches(valToCheck)){
				updateWinnerHelper(valToCheck); 
				return; 
			}
			valToCheck = board[2][0]; 
			if(board[2][1].matches(valToCheck) && board[2][2].matches(valToCheck)){
				updateWinnerHelper(valToCheck); 
				return; 
			}

			//vertical check
			valToCheck = board[0][0]; 
			if(board[1][0].matches(valToCheck) && board[2][0].matches(valToCheck)){
				updateWinnerHelper(valToCheck); 
				return; 
			}
			valToCheck = board[0][1]; 
			if(board[1][1].matches(valToCheck) && board[2][1].matches(valToCheck)){
				updateWinnerHelper(valToCheck); 
				return; 
			}
			valToCheck = board[0][2]; 
			if(board[1][2].matches(valToCheck) && board[2][2].matches(valToCheck)){
				updateWinnerHelper(valToCheck); 
				return; 
			}

			//diagonal checks
			valToCheck = board[0][0];
			if(board[1][1].matches(valToCheck) && board[2][2].matches(valToCheck)){
				updateWinnerHelper(valToCheck); 
				return; 
			}
			valToCheck = board[2][0]; 
			if(board[1][1].matches(valToCheck) && board[0][2].matches(valToCheck)){
				updateWinnerHelper(valToCheck); 
				return; 
			}

			//check for draw
			if(Arrays.stream(board)
					.allMatch(arr -> Arrays.stream(arr).allMatch(sq-> sq.toLowerCase().matches("x|o")))){
				gameStatus = Status.DRAW; 
				return; 
			}


			//toggle gameStatus
			if(gameStatus == Status.O_TO_MOVE){
				gameStatus = Status.X_TO_MOVE; 
			}else{
				gameStatus = Status.O_TO_MOVE; 
			}
		}

		private void updateWinnerHelper(String string) {
			if(string.toLowerCase().equals("x")){
				gameStatus = Status.X_WON; 
			}else{
				gameStatus = Status.O_WON; 
			}

		}

		private String getBoardString() {
			// TODO Auto-generated method stub
			return String.format(
					"%s %s %s\r\n"
							+ "%s %s %s\r\n"
							+ "%s %s %s\r\n", 
							board[0][0], board[0][1], board[0][2], 
							board[1][0], board[1][1], board[1][2],
							board[2][0], board[2][1], board[2][2]);
		}


		private boolean validateMove(String serverMove) {
			if(serverMove.matches("\\d") ){
				int square = Integer.parseInt(serverMove); 
				return board[(square -1)/3][(square -1) %3].matches("\\d"); 
			}
			return false;
		}

		private void makeMove(int square, String value) {
			board[(square -1)/3][(square -1) %3] = value; 
		}

		@Override
		public void run() {
			playGame();
		}

	}
}
