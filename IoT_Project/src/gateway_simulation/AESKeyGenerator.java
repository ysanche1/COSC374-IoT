package gateway_simulation;

import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;

//This is not actually implemented, passing actual keys to encryption causes problems
public class AESKeyGenerator {

    private Key key;


    public AESKeyGenerator() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        this.key = keyGen.generateKey();
    }


    public Key generateKeyFromString(String key) throws Exception {
        this.key = new SecretKeySpec(key.getBytes(), "AES");
        return this.key;
    }


    public Key getKey() {
        return this.key;
    }


}



