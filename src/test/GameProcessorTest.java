package test;

import application.utils.GameProcessor;
import application.utils.GameStatus;

public class GameProcessorTest {

	public static void main(String[] args) {
		GameProcessor game1 = new GameProcessor("Chidiebere", "Fred"); 
		
		printBoardState(game1);
		while(!game1.getGameStatus().isGameOver()){
			int pos = TestUtils.getNumberFromConsole("Enter board position: " ); 
			while(!game1.makeMove(pos)){
				System.out.println("The position you chose was invalid. Try again");
				printBoardState(game1); 
				pos = TestUtils.getNumberFromConsole("Enter board position: " ); 
				
			}
			printBoardState(game1); 
			if(!game1.getGameStatus().isGameOver()){
				pos = TestUtils.getNumberFromConsole("Enter board position: " ); 
				while(!game1.makeMove(pos)){
					System.out.println("The position you chose was invalid. Try again");
					printBoardState(game1); 
					pos = TestUtils.getNumberFromConsole("Enter board position: " ); 
					
				}
			}
			printBoardState(game1); 
		}
	}


	private static void  printBoardState(GameProcessor process) {
		// TODO Auto-generated method stub
		String[][] board = process.getBoard(); 
		String temp = String.format(
				"%s %s %s\r\n"
						+ "%s %s %s\r\n"
						+ "%s %s %s\r\n", 
						board[0][0], board[0][1], board[0][2], 
						board[1][0], board[1][1], board[1][2],
						board[2][0], board[2][1], board[2][2]);

		System.out.println("-------CURRENT BOARD STATE------");
		System.out.println(temp);
		GameStatus status = process.getGameStatus(); 
		if(!status.isGameOver())
			System.out.printf("%s to move\n",status.currentPlayerMove() );
		else if(status.isDraw())
			System.out.println("The game ended in a draw" );
		else{
			System.out.printf("The game was won by %s\n" , 
					status ==GameStatus.O_WON ? "O" : "X");
		}
			
	}

}
