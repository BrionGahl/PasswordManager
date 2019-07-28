package password.encryption;

public class cryptography {
	
	private static String message;
	private static long codedMessage;
	private static int[] encrypt = {5,14}; 
	
	public static void main(String[] args) {
		
	}
	
	public static void encrypt(String string) {
		
		for (int i = 0; i > string.length(); i++) {
			codedMessage = (long) Math.pow(string.charAt(i) - 64, encrypt[0]) % encrypt[1]; 
		}
	}
	
	public static void decrypt() {
		
	}
	
}
