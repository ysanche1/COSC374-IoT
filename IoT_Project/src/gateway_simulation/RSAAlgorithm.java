package gateway_simulation;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSAAlgorithm {
    private static final String ALGO = "RSA";
    private PublicKey pub;  //secret key
    private PrivateKey priv;

    public RSAAlgorithm(PublicKey p) { //For client and ticket decryption
        pub = p;
    }
    public RSAAlgorithm(PrivateKey p) {
        priv = p;
    }


    public String encrypt(String Data) throws Exception {
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, pub);
        byte[] encVal = c.doFinal(Data.getBytes());
        String encodedString = Base64.getEncoder().encodeToString(encVal);
        return encodedString;
    }

    public String decrypt(String encryptedData) throws Exception {
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE, priv);
        byte[] decodedValue = Base64.getDecoder().decode(encryptedData);
        byte[] decVal = c.doFinal(decodedValue);
        String decryptedString = new String(decVal);
        return decryptedString;
    }

    public String keyToStr(PublicKey key){
        byte[] byte_pubkey = key.getEncoded();
        String str_key = Base64.getEncoder().encodeToString(byte_pubkey);
// String str_key = new String(byte_pubkey,Charset.);
        return str_key;
    }

    public PublicKey strToKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] byte_pubkey  = Base64.getDecoder().decode(key);
        KeyFactory factory = KeyFactory.getInstance(ALGO);
        PublicKey pk =  factory.generatePublic(new X509EncodedKeySpec(byte_pubkey));
        return pk;
    }


    public void encryptMessage(Message m) throws Exception {
        m.custom = encrypt(m.custom);
        m.command = encrypt(m.command);
    }

    public void decryptMessage(Message m) throws Exception {
        switch (m.mNum) {
            case 1:
                m.custom = decrypt(m.custom);
                m.command = decrypt(m.command);
                break;
            case 2:
                m.update = decrypt(m.update);
        }
    }
}
