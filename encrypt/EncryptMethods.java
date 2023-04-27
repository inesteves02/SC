package encrypt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.AlgorithmParameters;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class EncryptMethods {

    // returns a secretKey generated with the password using SecretKeyFactory
    public static SecretKey generateSecretKey(String password) {
        byte[] salt = { (byte) 0xc9, (byte) 0x36, (byte) 0x78, (byte) 0x99, (byte) 0x52, (byte) 0x3e, (byte) 0xea,
                (byte) 0xf2 };
        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, 20); // pass, salt, iterations
        SecretKeyFactory kf;
        try {
            kf = SecretKeyFactory.getInstance("PBEWithHmacSHA256AndAES_128");
            return kf.generateSecret(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void encryptFile(SecretKey key, Cipher cipher, List<String> data, String outputFileName)
            throws Exception {

        FileOutputStream outputStream = null;

        try {
            // Initialize the cipher for encryption
            cipher.init(Cipher.ENCRYPT_MODE, key);

            // Open the output file stream
            outputStream = new FileOutputStream(outputFileName);

            // Write each line of data to the output file stream
            for (String line : data) {
                byte[] encryptedLine = cipher.doFinal(line.getBytes());
                outputStream.write(encryptedLine);
                outputStream.write('\n');
            }

        } catch (Exception e) {
            throw new Exception("Error encrypting file: " + e.getMessage(), e);

        } finally {
            // Close the output stream
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    // Log a warning message but don't throw an exception
                    System.out.println("Warning: could not close output stream: " + e.getMessage());
                }
            }
        }
    }

    public static List<String> decryptFile(SecretKey key, Cipher cipher, String inputFileName) throws Exception {

        FileInputStream inputStream = null;
        List<String> decryptedData = new ArrayList<>();

        try {
            AlgorithmParameters algorithm = AlgorithmParameters.getInstance("PBEWithHmacSHA256AndAES_128");
            algorithm.init();
            // Initialize the cipher for decryption
            cipher.init(Cipher.DECRYPT_MODE, key);
            // Open the input file stream
            inputStream = new FileInputStream(inputFileName);

            // Read each line of ciphertext from the input file stream and decrypt it
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                byte[] encryptedLine = line.getBytes();
                byte[] decryptedLine = cipher.doFinal(encryptedLine);
                decryptedData.add(new String(decryptedLine));
            }

        } catch (Exception e) {
            throw new Exception("Error decrypting file: " + e.getMessage(), e);

        } finally {
            // Close the input stream
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // Log a warning message but don't throw an exception
                    System.out.println("Warning: could not close input stream: " + e.getMessage());
                }
            }
        }

        return decryptedData;
    }

    private static byte[] readParams(String fileName) throws IOException {
        File f = new File(fileName);

        // work only for 2GB file, because array index can only up to Integer.MAX

        byte[] buffer = new byte[(int) f.length()];

        FileInputStream is = new FileInputStream(fileName);

        is.read(buffer);

        is.close();

        return buffer;
    }
}
