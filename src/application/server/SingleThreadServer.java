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
import java.util.Scanner;
import java.util.logging.Logger;

import application.utils.GameStatus;

public class SingleThreadServer {

	private final int port; 

	private Scanner input; 
	private final static Logger errorLogger = Logger.getLogger("errors");
	

	public SingleThreadServer(int port){
		this.port = port; 
	}

	public void start(){
		System.out.println("Listening on port " + port + " .........");


		try(ServerSocket server = new ServerSocket(port); ){
			input = new Scanner(System.in); 
			while(true){
				try(Socket connection = server.accept()){
					TicTactToeProcessor process = new TicTactToeProcessor(connection); 
					process.playGame();
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
		}finally{
			if(input != null ) input.close();
		}
	}

	private class TicTactToeProcessor{

		private final Socket connection;

		private  String serverType;
		private GameStatus gameStatus; 
		private  String[][] board; 

		public TicTactToeProcessor(Socket connection){
			this.connection = connection; 
			serverType = "X";  

		}

		private  void playGame(){
			try(
					OutputStream raw = connection.getOutputStream(); 
					BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(raw, "ASCII")); 
					InputStream rawIn = connection.getInputStream(); 
					BufferedReader reader = new BufferedReader(new InputStreamReader(rawIn, "ASCII")); 
					)
			{

				gameStatus = GameStatus.X_TO_MOVE; 
				String resp = "yes"; 

				while(resp.toLowerCase().matches("yes") ){
					initialiseBoard(); 
					writer.flush();

					String message = String.format("%s\r\nThe server is %s \r\n"
							+ "Wait for %s to play. \r\n", getBoardString(), serverType, gameStatus.currentPlayerMove()); 

					writer.write(message); 
					writer.flush(); 
					
					System.out.println(message);
					

					while(gameStatus == GameStatus.X_TO_MOVE || gameStatus== GameStatus.O_TO_MOVE){

						message = "\r\nCurrent  state of board: \r\n "; 
						message += getBoardString(); 
						message+= "Wait for " + gameStatus.currentPlayerMove() + " to move....\r\n"; 

						if(!gameStatus.currentPlayerMove().matches(serverType)){
							resp = getMove(writer, reader); //gets the move from the client
						}else{
							resp = getMove(writer, input);
						}

						makeMove(Integer.parseInt(resp), gameStatus.currentPlayerMove()); 


						message = String.format("%s played %s \r\nCurrent board state: \r\n%s",
								gameStatus.currentPlayerMove(), resp, getBoardString()); 

						writer.write(message); 
						System.out.println(message);
						writer.flush();
						updateStatus(); 
						if(gameStatus == GameStatus.O_TO_MOVE){
							message = String.format("%s to move!", gameStatus.currentPlayerMove()); 

							writer.flush();

							if(!gameStatus.currentPlayerMove().matches(serverType)){
								resp = getMove(writer, reader); //gets the move from the client
							}else{
								
								resp = getMove(writer, input);
							}
							
							makeMove(Integer.parseInt(resp), gameStatus.currentPlayerMove()); 
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

						writer.write(message); 
						writer .flush();
						System.out.println(message);

					}

					writer.write("Would you like to play again: \r\n" );
					writer.write("Type yes or no: " );
					writer.flush(); 

					resp = reader.readLine(); 
					
					if(resp!= null && resp.toLowerCase().matches("yes")){
						gameStatus = GameStatus.X_TO_MOVE; 
						//toggle serverType
						serverType = serverType.toUpperCase().matches("O") ? "X" : "O"; 	
						message = "\r\nRestarting game.......\r\n"; 
						System.out.print(message);
						writer.write(message); 
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

		
		private String getMove(BufferedWriter writer, Scanner input) throws IOException {
			writer.write("\r\nWait for server to move.....\r\n");
			writer.flush();
			
			System.out.print("Your turn to move: ");
			String resp = input.nextLine().replaceAll("\\s{1,}", ""); 
			while(!validateMove(resp)){
				System.out.println("The move you made was invalid");
				System.out.print("Try again: " );
				resp = input.nextLine().replaceAll("\\s{1,}", "");  
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
				gameStatus = GameStatus.DRAW; 
				return; 
			}
			

			//toggle gameStatus
			if(gameStatus == GameStatus.O_TO_MOVE){
				gameStatus = GameStatus.X_TO_MOVE; 
			}else{
				gameStatus = GameStatus.O_TO_MOVE; 
			}
		}

		private void updateWinnerHelper(String string) {
			if(string.toLowerCase().equals("x")){
				gameStatus = GameStatus.X_WON; 
			}else{
				gameStatus = GameStatus.O_WON; 
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

	}
}
