package encrypt;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
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

    public static void encryptFile(SecretKey key, Cipher cipher, String file) throws Exception {

        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;

        try {
            // Create streams for reading the input file and writing the output file
            inputStream = new FileInputStream(file);
            outputStream = new FileOutputStream(file + ".enc");

            // Wrap the output stream with a CipherOutputStream to encrypt the data
            CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher);

            // Encrypt the input file and write the ciphertext to the output file
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                cipherOutputStream.write(buffer, 0, bytesRead);
            }

            // Flush and close the CipherOutputStream to write any remaining ciphertext
            cipherOutputStream.flush();
            cipherOutputStream.close();

        } catch (Exception e) {
            throw new Exception("Error encrypting file: " + e.getMessage(), e);

        } finally {
            // Close the input and output streams
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // Log a warning message but don't throw an exception
                    System.out.println("Warning: could not close input stream: " + e.getMessage());
                }
            }
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

    public static void decryptFile(SecretKey key, Cipher cipher, String inputFile, String outputFile) throws Exception {

        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        CipherInputStream cipherInputStream = null;
        try {
            // Create streams for reading the input file and writing the output file
            inputStream = new FileInputStream(inputFile);
            outputStream = new FileOutputStream(outputFile);

            // Wrap the input stream with a CipherInputStream to decrypt the data
            cipherInputStream = new CipherInputStream(inputStream, cipher);

            // Decrypt the input file and write the plaintext to the output file
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = cipherInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            // Flush and close the output stream to write any remaining plaintext
            outputStream.flush();
            outputStream.close();

        } catch (Exception e) {
            throw new Exception("Error decrypting file: " + e.getMessage(), e);

        } finally {
            // Close the input and output streams and the CipherInputStream
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // Log a warning message but don't throw an exception
                    System.out.println("Warning: could not close input stream: " + e.getMessage());
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    // Log a warning message but don't throw an exception
                    System.out.println("Warning: could not close output stream: " + e.getMessage());
                }
            }
            if (cipherInputStream != null) {
                try {
                    cipherInputStream.close();
                } catch (IOException e) {
                    // Log a warning message but don't throw an exception
                    System.out.println("Warning: could not close cipher input stream: " + e.getMessage());
                }
            }
        }
    }
}
