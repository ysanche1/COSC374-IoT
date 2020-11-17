package gateway_simulation;
import java.security.Key;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

// Initialized with a string which is converted into bytes and used to generate the secret key
public class AESAlgorithm {
    private static final String ALGO = "AES";
    private byte[] keyValue;  //secret key
    private String keyValue2;  //key for encrypting tickets and authenticators
    private String keyValue1;

    public AESAlgorithm(String key) { //For client and ticket decryption
        keyValue1 = key;
        keyValue = key.getBytes();
    }

    public AESAlgorithm(String key, String authOrTicketKey) { //Serverside encryption
        keyValue1 = key;
        keyValue = key.getBytes();
        keyValue2 = authOrTicketKey; //This is used to generate a real key later
    }

    // Encrypts contents of a message and creates a new AESAlgorithm for the function below
    public void encryptMessage(Message m) throws Exception {
       if(keyValue2 != null) {
           AESAlgorithm aesForTicket = new AESAlgorithm(keyValue2);
           m.ticket = aesForTicket.encryptTicket(m.ticket);    //send ticket to ticket encryption method
       }
       if(m.mNum == 7){
           m.command = encrypt(m.command);
           m.custom = encrypt(m.custom);
       }
       else{
        m.key = encrypt(m.key);
        m.serverID = encrypt(m.serverID);
        m.timestamp = encrypt(m.timestamp);
        if(m.mNum == 2)m.lifetime = encrypt(m.lifetime);}
       // m.ticketRetrieval = encrypt(m.ticketRetrieval);
    }

    // ticket encryption method
    public Ticket encryptTicket(Ticket t) throws Exception {
        t.key = encrypt(t.key);
        t.clientID = encrypt(t.clientID);
        t.clientAD = encrypt(t.clientAD);
        t.serverID = encrypt(t.serverID);
        t.timestamp = encrypt(t.timestamp);
        t.lifetime = encrypt(t.lifetime);

        return t;
    }

    // encryption method called for individual elements
    public String encrypt(String Data) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(Data.getBytes());
        String encodedString = Base64.getEncoder().encodeToString(encVal);
        return encodedString;
    }


    // decryption method called for individual elements
    public String decrypt(String encryptedData) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedValue = Base64.getDecoder().decode(encryptedData);
        byte[] decVal = c.doFinal(decodedValue);
        String decryptedString = new String(decVal);
        return decryptedString;
    }

    // key is generated from object parameter
    private Key generateKey() throws Exception {
        Key key = new SecretKeySpec(keyValue, ALGO);
        return key;
    }

    // Decrypts message components based on message number variable
    public Message decryptMessage(Message m) throws Exception {

        switch (m.mNum) {
            case 2:
                m.lifetime = decrypt(m.lifetime);
                m.key = decrypt(m.key);
                m.serverID = decrypt(m.serverID);
                m.timestamp = decrypt(m.timestamp);
                //m.ticketRetrieval = decrypt(m.ticketRetrieval);
                break;
            case 3:
                decryptTicket(m.ticket);
                AESAlgorithm aesAT = new AESAlgorithm(m.ticket.key);
                aesAT.decryptAuthenticator(m.auth);break;
            case 4:
                m.key = decrypt(m.key);
                m.serverID = decrypt(m.serverID);
                m.timestamp = decrypt(m.timestamp);
              //  m.ticketRetrieval = decrypt(m.ticketRetrieval);
                break;
            case 5:
                decryptTicket(m.ticket);
                aesAT = new AESAlgorithm(m.ticket.key);
                aesAT.decryptAuthenticator(m.auth);
                m.pub_key = aesAT.decrypt(m.pub_key);break;
            case 7:
                m.command = decrypt(m.command);
                m.custom = decrypt(m.custom);
        }
        return m;
    }


    public void decryptTicket(Ticket t) throws Exception {
        t.key = decrypt(t.key);
        t.clientID = decrypt(t.clientID);
        t.clientAD = decrypt(t.clientAD);
        t.serverID = decrypt(t.serverID);
        t.timestamp = decrypt(t.timestamp);
        t.lifetime = decrypt(t.lifetime);
    }


    public void encryptAuthenticator(Authenticator a) throws Exception {
            a.clientID = encrypt(a.clientID);
            a.clientAddress = encrypt(a.clientAddress);
            a.timestamp = encrypt(a.timestamp);
    }


    public void decryptAuthenticator(Authenticator a) throws Exception {
        System.out.println(a.clientID);
        a.clientID = decrypt(a.clientID);
        a.clientAddress = decrypt(a.clientAddress);
        a.timestamp = decrypt(a.timestamp);
    }
}