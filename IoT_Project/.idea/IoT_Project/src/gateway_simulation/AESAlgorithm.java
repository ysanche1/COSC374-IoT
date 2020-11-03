package gateway_simulation;
import java.security.Key;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

//Initialized with a string which is converted into bytes and used to generate the secret key

public class AESAlgorithm {
	private static final String ALGO = "AES";
	private byte[] keyValue1;  //secret key
	private String keyValue2;  //key for encrypting tickets and authnenticators
	private String authKeyValue;

	public AESAlgorithm(String key) {
		keyValue1 = key.getBytes();
	} //For client and ticket decryption

	public AESAlgorithm(String key, String authOrTicketKey) { //Serverside encryption
		keyValue1 = key.getBytes();
		keyValue2 = authOrTicketKey;
	}


	public void encryptMessage(Message m) throws Exception {
		AESAlgorithm aesForTicket = new AESAlgorithm(keyValue2);
		m.ticket = aesForTicket.encryptTicket(m.ticket);    //send ticket to ticket encryption method

		switch (m.mNum) {                        //mNum indicates which message this is
			case 2:
				m.key = encrypt(m.key);
				m.serverID = encrypt(m.serverID);
				m.timestamp = encrypt(m.timestamp);  //encrypt the relevant variables
				m.lifetime = encrypt(m.lifetime);
				m.ticketRetrieval = encrypt(m.ticketRetrieval);break;
			case 4:
			default:m.displayContents();

		}
	}

	//Both tickets have the same structure so no switch statement here
	private Ticket encryptTicket(Ticket t) throws Exception {
		t.key = encrypt(t.key);
		t.clientID = encrypt(t.clientID);
		t.clientAD = encrypt(t.clientAD);
		t.serverID = encrypt(t.serverID);
		t.timestamp = encrypt(t.timestamp);
		t.lifetime = encrypt(t.lifetime);

		return t;
	}

	public String encrypt(String Data) throws Exception {
		Key key = generateKey();
		Cipher c = Cipher.getInstance(ALGO);
		c.init(Cipher.ENCRYPT_MODE, key);
		byte[] encVal = c.doFinal(Data.getBytes());
		String encodedString = Base64.getEncoder().encodeToString(encVal);
		return encodedString;
	}

	public String decrypt(String encryptedData) throws Exception {
		Key key = generateKey();
		Cipher c = Cipher.getInstance(ALGO);
		c.init(Cipher.DECRYPT_MODE, key);
		byte[] decodedValue = Base64.getDecoder().decode(encryptedData);
		byte[] decVal = c.doFinal(decodedValue);
		String decryptedString = new String(decVal);
		return decryptedString;
	}

	//key is generated from object parameter
	private Key generateKey() throws Exception {
		Key key = new SecretKeySpec(keyValue1, ALGO);
		return key;
	}

	public Message decryptMessage(Message m) throws Exception {

		switch (m.mNum) {
			case 2:
				try {
					m.lifetime = decrypt(m.lifetime);
					m.key = decrypt(m.key);
					m.serverID = decrypt(m.serverID);
					m.timestamp = decrypt(m.timestamp);
					m.ticketRetrieval = decrypt(m.ticketRetrieval);
				} catch (Exception e) {
				}break;
			case 3:
				m.ticket = decryptTicket(m.ticket);
				AESAlgorithm aesAT = new AESAlgorithm(m.ticket.key);
				m.auth = aesAT.decryptAuthenticator(m.auth);
		}
		return m;
	}

	public Ticket decryptTicket(Ticket t) throws Exception {
		t.key = decrypt(t.key);
		t.clientID = decrypt(t.clientID);
		t.clientAD = decrypt(t.clientAD);
		t.serverID = decrypt(t.serverID);
		t.timestamp = decrypt(t.timestamp);
		t.lifetime = decrypt(t.lifetime);

		return t;
	}

	public void encryptAuthenticator(Authenticator a) {

		try {
			a.clientID = encrypt(a.clientID);
			a.clientAddress = encrypt(a.clientAddress);
			a.timestamp = encrypt(a.timestamp);
		} catch (Exception e) {
		}
	}

	private Authenticator decryptAuthenticator(Authenticator a) throws Exception {
		a.clientID = decrypt(a.clientID);
		a.clientAddress = decrypt(a.clientAddress);
		a.timestamp = decrypt(a.timestamp);
		return a;
	}
}