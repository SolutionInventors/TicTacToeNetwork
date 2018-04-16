package application.utils;

import java.util.Arrays;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class GameProcessor {
	private  SimpleStringProperty[][] board; 

	private GameStatus gameStatus; 

	private int[] winningPosition; 

	private SimpleIntegerProperty playerOneScore; 
	private SimpleIntegerProperty  playerTwoScore; 

	private SimpleStringProperty playerOneName; 
	private SimpleStringProperty playerTwoName;
	private SimpleStringProperty playerOneLetter; 

	public GameProcessor(String p1Name, String p2Name){
		SimpleStringProperty[][] initBoard =  {
				{new SimpleStringProperty("1"), new SimpleStringProperty("2"), new SimpleStringProperty("3")}, 
				{new SimpleStringProperty("4"),new SimpleStringProperty("5"), new SimpleStringProperty("6")},
				{new SimpleStringProperty("7"), new SimpleStringProperty("8"), new SimpleStringProperty("9")}, 
		}; 
		board =  initBoard;
		resetBoard(); 
		gameStatus = GameStatus.X_TO_MOVE; 
		playerOneName = new SimpleStringProperty(this, "playerOneName", p1Name); 
		playerTwoName = new SimpleStringProperty(this, "playerTwoName", p2Name); 
		playerOneLetter = new SimpleStringProperty(this, "playerOneLetter", "X"); 
		playerOneScore = new SimpleIntegerProperty(this, "playerOneScore" , 0);
		playerTwoScore = new SimpleIntegerProperty(this, "playerTwoScore" ,0); 
	}

	private void resetBoard() {
		for(int row =0; row< board.length; row++)
			for(int col=0; col<board[row].length; col++){
				String pos = String.valueOf(3*row + (col+1)); 
				board[row][col].set(pos);
			}

	}

	public void moveToNextRound(){
		if(gameStatus.isGameOver()){
			Arrays.stream(board)
			.forEach(arr-> 
			Arrays.stream(arr)
			.forEach(elem -> elem.set("") )   );
			gameStatus = GameStatus.X_TO_MOVE; 
			playerOneLetter.set(getPlayerTwoLetter());
			resetBoard();
		}

	}

	public SimpleStringProperty[][] boardProperty(){
		return board; 
	}
	public String[][] getBoard(){
		String[][] stringBoard =  {
				{board[0][0].get(),board[0][1].get(), board[0][2].get()},  
				{board[1][0].get(),board[1][1].get(), board[1][2].get()},
				{board[2][0].get(),board[2][1].get(), board[2][2].get()},
		}; 

		return stringBoard; 

	}

	public SimpleStringProperty playerOneLetterProperty(){
		return playerOneLetter; 
	}

	public String getPlayerTwoLetter(){
		return playerOneLetter.get().matches("(?i:x)") ? "X": "O"; 
	}

	public String getPlayerOneLetter(){
		return playerOneLetter.get(); 
	}

	public GameStatus getGameStatus() {
		return gameStatus;
	}
	public void setGameStatus(GameStatus gameStatus) {
		this.gameStatus = gameStatus;
	}
	public String getPlayerOneName() {
		return playerOneName.get();
	}
	public void setPlayerOneName(String playerOneName) {
		this.playerOneName.set(playerOneName);
	}
	public String getPlayerTwoName() {
		return playerTwoName.get();
	}
	public void setPlayerTwoName(String playerTwoName) {
		this.playerTwoName.set(playerTwoName);
	}

	public SimpleStringProperty playerOneNameProperty(){
		return playerOneName; 
	}
	public SimpleStringProperty playerTwoNameProperty(){
		return playerTwoName; 
	}
	public SimpleIntegerProperty playerOneScoreProperty(){
		return playerOneScore; 
	}
	public SimpleIntegerProperty playerTwoScoreProperty(){
		return playerTwoScore; 
	}
	public boolean makeMove(int position){
		int row = (position-1) /3; 
		int col = (position-1)%3; 

		if( validateMove(position)){
			board[row][col].set(gameStatus.currentPlayerMove()); 
			updateGameStatus(); 
			return true; 
		}
		return false; 

	}

	public boolean validateMove(int position){
		String[][] board = getBoard(); 
		int row = (position-1) /3; 
		int col = (position-1)%3; 
		return board[row][col].matches("[1-9]")&&  !gameStatus.isGameOver(); 

	}

	private void updateGameStatus() {
		String[][] board = getBoard(); 
		int[][] winSequence = {
				{1,2,3}, {4,5,6}, {7,8,9}, 
				{1,4,7}, {2,5,8}, {3,6,9}, 
				{1,5,9}, {7,5,3}, 
		}; 

		for(int[] sequence : winSequence){
			final String square = board[(sequence[0]-1)/3][(sequence[0]-1)%3]; 

			boolean gameWon =
					Arrays.stream(sequence)
					.allMatch(pos-> board[(pos-1)/3][(pos-1)%3]
							.matches(square)); 
			if(gameWon){
				winningPosition = sequence; 
				updateWinnerHelper(square);
				System.out.println("------Winnning score added------");
				return; 
			}

		}
		if(Arrays.stream(board)
				.allMatch(arr -> 
				Arrays.stream(arr)
				.allMatch(sq-> sq.matches("(?i:x|o)")))){
			gameStatus = GameStatus.DRAW; 
			playerOneScore.set(getPlayerOneScore()+1); 
			playerTwoScore.set(getPlayerOneScore()+1); 
			System.out.println("------Draw score added------");
		}else if(gameStatus == GameStatus.O_TO_MOVE){
			gameStatus = GameStatus.X_TO_MOVE; 
		}else{
			gameStatus = GameStatus.O_TO_MOVE; 
		}
	}

	private void updateWinnerHelper(String string) {
		if(string.toLowerCase().equals("x")){
			gameStatus = GameStatus.X_WON;
			if(getPlayerOneLetter().matches("(?i:x)")) playerOneScore.set(getPlayerOneScore()  +2); 
			else{
				playerTwoScore.set(getPlayerTwoScore()  +2) ; 
				System.out.println("------Winnning score added to P2------");
			}
		}else{
			gameStatus = GameStatus.O_WON;
			if(getPlayerOneLetter().matches("(?i:o)")) playerOneScore.set(getPlayerOneScore() +1); 
			else {
				playerTwoScore.set(getPlayerTwoScore() +2); 
				System.out.println("------Winnning score added to P2------");
			}
		}

	}

	public int getPlayerOneScore(){
		return playerOneScore.get();
	}

	public int getPlayerTwoScore(){
		return playerTwoScore.get(); 
	}

	public String getXName() {
		return playerOneLetter.get()
				.matches("(?i:x)") ? getPlayerOneName() : 
					getPlayerTwoName(); 
	}

	public String getOName() {
		return playerOneLetter.get()
				.matches("(?i:o)") ? getPlayerOneName() : 
					getPlayerTwoName(); 
	}

	public boolean isPlayerOneMove(){
		return (gameStatus == GameStatus.O_TO_MOVE && getPlayerOneLetter().matches("(?i:o)"))  ||
				(gameStatus == GameStatus.X_TO_MOVE && getPlayerOneLetter().matches("(?i:x)")); 
	}

	public boolean isPlayerTwoMove(){
		return !gameStatus.isGameOver() && !isPlayerOneMove(); 
	}

	public int[] getWinningPositions() {
		System.out.println(winningPosition);
		return winningPosition;
	}	
}
