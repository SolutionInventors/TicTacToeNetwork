package application.GUI.controllers;

import application.GUI.utils.Navigator;
import application.utils.GameType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class WelcomeScreenController {

	@FXML public void beginMultiplayerGame(ActionEvent event) {
		Navigator.getCurrentGameData().setGameType(GameType.MULTI_PLAYER);
		Navigator.loadScreen("Board");
		
		
	}

	@FXML public void beginSingleGame(ActionEvent event) {
		Navigator.getCurrentGameData().setGameType(GameType.SINGLE_PLAYER);
		
	}

	@FXML public void beginTwoPlayerGame(ActionEvent event) {
		Navigator.getCurrentGameData().setGameType(GameType.TWO_PLAYER);
		Navigator.loadScreen("PromptPlayerName");
	}

	@FXML
	public void closeProgram(ActionEvent event) {
		
		Stage stage = Navigator.getPrimaryStage(); 
		stage.close(); 
	}

}
