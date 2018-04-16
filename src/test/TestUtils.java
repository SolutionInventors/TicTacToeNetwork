package test;

import java.util.Scanner;

public class TestUtils {
	public static String getFromConsole(String msg){
		System.out.println(msg);
		Scanner input = new Scanner(System.in);
		return input.nextLine(); 
		
	}
	
	public static int getNumberFromConsole(String msg){
		return Integer.parseInt(getFromConsole(msg)); 
	}
}
