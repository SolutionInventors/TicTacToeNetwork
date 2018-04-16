package test;

public class StringMatchTest {

	public static void main(String[] args) {
		String x = "O"; 
		System.out.println(x.matches("X"));
		System.out.println(x.matches("(?i:x|o)"));

	}

}
