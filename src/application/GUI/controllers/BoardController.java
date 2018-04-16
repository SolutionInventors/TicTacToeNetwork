package application.GUI.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import application.GUI.utils.GameData;
import application.GUI.utils.Navigator;
import application.utils.GameProcessor;
import application.utils.GameProcessor;
import application.utils.GameStatus;
import javafx.beans.binding.StringExpression;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class BoardController  implements Initializable{

	@FXML private Label cell3;

	@FXML private Label cell5;

	@FXML private Label cell4;

	@FXML private Label cell7;

	@FXML private Label cell8;

	@FXML private Label cell9;

	@FXML private Label cell6;

	@FXML private Label cell2;

	@FXML private Label cell1;

	@FXML private Label statusLabel;

	@FXML private Label playerOneStatus;

	@FXML private Label playerTwoStatus;
	@FXML private Button playAgainButton; 
	

	private Label[][] board;
	private GameProcessor process; 

	@FXML
	public void makeMove(MouseEvent event) {
		String id = ((Label ) event.getSource()).getId(); 
		int position = Integer.parseInt(id.substring(id.length() -1 ));

		process.makeMove(position);
		
		updateStatus();
		

	}



	@FXML
	public void resetGame(ActionEvent event) {
		process.moveToNextRound();
		updateStatus();
		playAgainButton.setVisible(false);
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		GameData gameData = Navigator.getCurrentGameData(); 
		process = new GameProcessor(gameData.getPlayerOneName(), gameData.getPlayerTwoName());
		resetBoard(); 
		
		StringExpression playerOneStatExp = 
				process.playerOneNameProperty()
				.concat(": ")
				.concat(process.playerOneScoreProperty()
						.asString());
		StringExpression playerTwoStatExp = 
				process.playerTwoNameProperty()
				.concat(": ")
				.concat(process.playerTwoScoreProperty()
						.asString());
		
		playerOneStatus.textProperty()
			.bind(playerOneStatExp);
		playerTwoStatus.textProperty()
			.bind(playerTwoStatExp);

		playAgainButton.setVisible(false);
	}

	/**
	 * Resets the board and gets it ready for a new game
	 */
	public void resetBoard() {
		Label[][] temp  = {
				{cell1, cell2, cell3}, 
				{cell4, cell5, cell6}, 
				{cell7, cell8 ,cell9}, 

		}; 
		board = temp; 

		for(int i = 0; i< board.length; i++)
			for(int j =0; j<board.length; j++){
				Label cell = board[i][j]; 
				cell.setText("");
				cell.getStyleClass().removeAll("o", "x", "win"); 
				cell.getStyleClass().add("x");
				cell.textProperty().addListener((obs, oldValue, newValue)-> {
					if(newValue.matches("(?i:x)") ) cell.getStyleClass().add("x"); 
					else if(newValue.matches("(?i:o)") ) cell.getStyleClass().add("o"); 
					else{
						cell.getStyleClass().removeAll("o", "x", "win"); 
					}
				});
				cell.textProperty().bind(process.boardProperty()[i][j]);
			}

	}

	public void updateStatus(){
		GameStatus status = process.getGameStatus(); 
		String msg; 
		switch(status ){
		case DRAW:
			msg = String.format("Game ended in a draw") ; 
			break; 
		case O_TO_MOVE:
			msg = String.format("%s to move" , process.getOName()); 
			break;
		case O_WON:
			msg = String.format("O won the game. Congratulations %s !", 
					process.getOName()); 
			break; 
		case X_TO_MOVE:
			msg = String.format("%s to move" , process.getXName()); 
			break;
		case X_WON:
			msg = String.format("X won the game. Congratulations %s !", 
					process.getXName()); 
			break; 
		default:
			msg = "Something unusual happended"; 
		}
		if(status.isGameOver() ){
			playAgainButton.setVisible(true);
			if(!status.isDraw()){
				for(int pos : process.getWinningPositions()){
					int row = (pos-1)/3; 
					int col = (pos-1)%3; 
					board[row][col].getStyleClass().add("win"); 
				}
			}
			
		}
		statusLabel.setText(msg);

	}


}
