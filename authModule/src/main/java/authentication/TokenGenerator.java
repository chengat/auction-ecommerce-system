package authentication;

import java.security.SecureRandom;

public class TokenGenerator {
    private static final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    
    public static String generateToken(int length) {
    	SecureRandom rand = new SecureRandom();
    	StringBuilder builder = new StringBuilder(length);
    	for(int x = 0; x < length; x++) {
    		int charIndex = rand.nextInt(characters.length());
    		builder.append(characters.charAt(charIndex));
    	}
    	return builder.toString();
    }
}
