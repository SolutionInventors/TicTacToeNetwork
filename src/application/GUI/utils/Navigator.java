package application.GUI.utils;

import java.io.File;
import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Navigator {
	private final static Stage PRIMARY_STAGE = 
			new Stage();
	private static GameData currentGameData = 
			new GameData(); 
	
	private final static String FXML_PATH = "resources/gui-resources/fxmls/"; 
	
	public static Stage getPrimaryStage() {
		return PRIMARY_STAGE;
	}

	

	public static void loadScreen(String fileName){
		
		Parent root;
		try {
			File file  = new File(FXML_PATH+ fileName+".fxml"); 
			root = FXMLLoader.load(file.toURI().toURL()); 
			Scene scene = new Scene(root); 
			getPrimaryStage().setScene(scene);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Wrong file name");
			
			e.printStackTrace();
		} 
		
		
	}

	public static GameData getCurrentGameData() {
		return currentGameData;
	}
	
	public static void resetGameData(){
		currentGameData = new GameData(); 
	}

	
}
