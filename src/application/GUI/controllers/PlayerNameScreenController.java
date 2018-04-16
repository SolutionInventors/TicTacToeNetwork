package application.GUI.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import application.GUI.utils.GameData;
import application.GUI.utils.Navigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

public class PlayerNameScreenController implements Initializable {

	
    @FXML private TextField playerOneNameTxtbox;

    @FXML private TextField playerTwoNameTxtbox;

    @FXML 
    public void beginGame(ActionEvent event) {
    	switch(Navigator.getCurrentGameData().getGameType()){
		case MULTI_PLAYER:
			break; 
		case SINGLE_PLAYER:
		case TWO_PLAYER:
			Navigator.loadScreen("Board");
			break;
		default:
			break;
    	}
    }
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		switch(Navigator.getCurrentGameData().getGameType()){
		case MULTI_PLAYER:
		case SINGLE_PLAYER:
			playerTwoNameTxtbox.setVisible(false);
		default:
			break;
		
		}

		GameData gameData =  Navigator.getCurrentGameData(); 
		gameData.playerOneNameProperty()
			.bind(playerOneNameTxtbox.textProperty()); 
		gameData.playerTwoNameProperty()
			.bind(playerTwoNameTxtbox.textProperty()); 
	}

}
