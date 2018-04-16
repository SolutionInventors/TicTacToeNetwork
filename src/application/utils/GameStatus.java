package application.utils;

public enum GameStatus {
	X_WON, O_WON, DRAW, 
	X_TO_MOVE, O_TO_MOVE;


	public String currentPlayerMove(){
		switch(this){

		case O_TO_MOVE:
			return "O"; 
		case X_TO_MOVE:
			return "X"; 
		default:
			return ""; 
		}
	}


	public boolean isDraw(){
		return this == GameStatus.DRAW; 
	}
	public boolean isGameOver(){
		return !(this == X_TO_MOVE || this==O_TO_MOVE); 
	}
}
