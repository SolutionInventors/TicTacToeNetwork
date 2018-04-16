package test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import application.utils.GameProcessor;

public class MultithreadedProcessTest {

	public static void main(String[] args) {
		try(ServerSocket server = new ServerSocket(4000)){
			
			Socket connection = server.accept(); 
			Socket socket = new Socket("localhost", 4000); 
			
			GameProcessor process = new GameProcessor("A", "B"); 
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
