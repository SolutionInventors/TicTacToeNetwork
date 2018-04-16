package application;

import application.GUI.utils.Navigator;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
//		String url = "GUI/fxmls/WelcomeScreen.fxml"; 
		Navigator.loadScreen("WelcomeScreen");
		Navigator.getPrimaryStage().show();
	
	}

	public static void main(String[] args) {
		launch(args); 
	}

}
