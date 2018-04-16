package application.GUI.utils;

import java.net.Socket;

import application.utils.GameType;
import javafx.beans.property.SimpleStringProperty;

public class GameData {
	private GameType gameType;
	private SimpleStringProperty playerOneName; 
	private SimpleStringProperty playerTwoName; 
	private Socket playerOneConnection; 
	private Socket playerTwoConnection; 
	public GameData(){
		playerOneName = new SimpleStringProperty(this, "playerOneName", ""); 
		playerTwoName = new SimpleStringProperty(this, "playerTwoName", ""); 
		
	}
	
	public GameData(Socket p1Connection, Socket p2Connection){
		this(); 
		playerOneConnection = p1Connection; 
		playerTwoConnection =p2Connection; 
		
	}
	public GameType getGameType() {
		return gameType;
	}

	public void setGameType(GameType gameType) {
		this.gameType = gameType;
	}
	public SimpleStringProperty playerTwoNameProperty() {
		return playerTwoName;
	}
	
	public void setPlayerTwoName(String playerTwoName) {
		this.playerTwoName.set( playerTwoName);
	} 
	
	public String getPlayerTwoName() {
		return this.playerTwoName.get();
	} 
	
	public SimpleStringProperty playerOneNameProperty() {
		return playerOneName;
	}
	
	public void setPlayerOneName(String playerOneName) {
		this.playerOneName.set( playerOneName);
	} 
	
	public String getPlayerOneName() {
		return playerOneName.get();
	} 
	
}
