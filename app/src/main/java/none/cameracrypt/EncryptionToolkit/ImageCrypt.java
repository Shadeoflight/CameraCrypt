package none.cameracrypt.EncryptionToolkit;

import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.security.AlgorithmParameters;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by joshua.wu on 7/31/17.
 */

public class ImageCrypt {

    private Cipher dcipher;

    private static byte[] salt = new String("12345678").getBytes();
    private static int iterationCount = 1024;
    private static int keyStrength = 256;
    private SecretKey key;
    private byte[] iv;

    public ImageCrypt(String passPhrase) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec spec = new PBEKeySpec(passPhrase.toCharArray(), salt, iterationCount, keyStrength);
        SecretKey tmp = factory.generateSecret(spec);
        key = new SecretKeySpec(tmp.getEncoded(), "AES");
        dcipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    }

    public byte[] encryptImage(byte[] dataToEncrypt) throws Exception {

        // Generate iv
//        AlgorithmParameters params = dcipher.getParameters();
//        iv = params.getParameterSpec(IvParameterSpec.class).getIV();
        SecureRandom randomSecureRandom = SecureRandom.getInstance("SHA1PRNG");
        byte[] iv = new byte[16];
        randomSecureRandom.nextBytes(iv);

        // Initialize copy of cipher parameters
        IvParameterSpec initVector = new IvParameterSpec(iv);

        dcipher.init(Cipher.ENCRYPT_MODE, key, initVector);

        // Encrypt
        byte[] utf8EncryptedData = dcipher.doFinal(dataToEncrypt);//check

        // Append Initialization Vector to end of byte array
        byte[] encryptedDataAndIv = appendIvToEncryptedData(utf8EncryptedData, iv);

        // Encode byte array
        byte[] encodedByteArray = Base64.encode(encryptedDataAndIv,Base64.DEFAULT);

        // Test
        String base64EncryptedData = Base64.encodeToString(encryptedDataAndIv, Base64.DEFAULT);
        String base64EncryptedDataOrig = Base64.encodeToString(dataToEncrypt, Base64.DEFAULT);
        System.out.println("Original data: " + base64EncryptedDataOrig);
        System.out.println("IV: " + Base64.encodeToString(iv, Base64.DEFAULT));
        System.out.println("Encrypted Data: " + base64EncryptedData);

        return encodedByteArray;
    }

    public byte[] decryptImage(byte[] base64EncryptedData) throws Exception {

        //dcipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));

        byte[] decryptedData = Base64.decode(base64EncryptedData, Base64.DEFAULT);

        int ivIndex = decryptedData.length - 16;

        dcipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(decryptedData,ivIndex,16));
        //byte[] decryptedData = new java.util.Base64.decodeBuffer(base64EncryptedData);
        //byte[] decryptedData = Base64.decode(base64EncryptedData, Base64.DEFAULT);
        byte[] decryptedByteArray = dcipher.doFinal(decryptedData, 0, ivIndex);

        // Test output
        String base64EncryptedDataOrigC = Base64.encodeToString(decryptedByteArray, Base64.DEFAULT);
        System.out.println("Decrypted Data: " + base64EncryptedDataOrigC);

        return decryptedByteArray;
    }

    //
    //  Helper methods
    //

    private static byte[] appendIvToEncryptedData(byte[] encryptedData, byte[] iv) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        os.write(encryptedData);
        os.write(iv);
        return os.toByteArray();
    }
}
