import java.security.Key;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESAlgorithm {

	private static final String ALGO = "AES";
	private byte[] keyValue;  //secret key
	
	public AESAlgorithm(String key) {
		keyValue = key.getBytes();
	}


	public String Encrypt(String Data) throws Exception {
		Key key = generateKey();
		Cipher c = Cipher.getInstance(ALGO);
		c.init(Cipher.ENCRYPT_MODE, key);
		byte[] encVal = c.doFinal(Data.getBytes());
		String encodedString =  Base64.getEncoder().encodeToString(encVal);
		return encodedString;
		
	}
	
	public String Decrypt(String encryptedData) throws Exception {
		Key key = generateKey();
		Cipher c = Cipher.getInstance(ALGO);
		c.init(Cipher.DECRYPT_MODE, key);
		byte[] decodedValue = Base64.getDecoder().decode(encryptedData);
		byte[] decVal = c.doFinal(decodedValue);
		String decryptedString =  new String(decVal);
		return decryptedString;
	}
	
	private Key generateKey() throws Exception {
		Key key = new SecretKeySpec(keyValue, ALGO);
		return key;
	}
	
}
