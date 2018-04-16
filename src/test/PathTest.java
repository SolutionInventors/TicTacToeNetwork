package test;

import java.net.MalformedURLException;
import java.net.URL;

import application.App;

public class PathTest {

	public static void main(String[] args) throws MalformedURLException {
		URL finalLocation = new URL(App.class.getProtectionDomain().getCodeSource().getLocation(),
				"/GUI/fxmls/Welcome.fxml"); 
		
		System.out.println(System.getProperty("user.dir") );
		System.out.println(finalLocation.getFile());
	}

}
