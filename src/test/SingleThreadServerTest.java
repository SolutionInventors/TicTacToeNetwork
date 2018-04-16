package test;

import application.server.SingleThreadServer;

public class SingleThreadServerTest {

	public static void main(String[] args) {
		SingleThreadServer server = new SingleThreadServer(3232); 
		server.start(); 

	}

}
