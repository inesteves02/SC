package encrypt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class EncryptMethods {

    // returns a secretKey generated with the password using SecretKeyFactory
    public static SecretKey generateSecretKey(String password) {
        byte[] salt = { (byte) 0xc9, (byte) 0x36, (byte) 0x78, (byte) 0x99, (byte) 0x52, (byte) 0x3e, (byte) 0xea,
                (byte) 0xf2 };
        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, 20); 
        SecretKeyFactory kf;
        try {
            kf = SecretKeyFactory.getInstance("PBEWithHmacSHA256AndAES_128");
            return kf.generateSecret(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void encrypt(SecretKey key, Cipher c, String fileName, List<String> data) {
        stringToFile(data, fileName);
        encryptation(key, c, fileName);
    }
    
    
    public static void encryptation(SecretKey key, Cipher c, String fileName) {
        FileInputStream fis;
        FileOutputStream fos;
        CipherOutputStream cos;
    
        try {
            c.init(Cipher.ENCRYPT_MODE, key);
            fis = new FileInputStream(fileName);
            String[] aux = fileName.split("\\.");
            deleteFile(aux[0] + ".cif");
            fos = new FileOutputStream(aux[0] + ".cif");
            cos = new CipherOutputStream(fos, c);
            byte[] b = new byte[16];
            int i;
            while ((i = fis.read(b)) != -1) {
                cos.write(b, 0, i);
            }
            cos.close();
            fis.close();
            fos.close();
            deleteFile(fileName);
            File f = new File(aux[0] + "Params.enc");
            deleteFile(aux[0] + "Params.enc");
            FileOutputStream out = new FileOutputStream(f);
            out.write(c.getParameters().getEncoded());
            out.close();
            f.createNewFile();
        } catch (IOException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }
    
    public static List<String> decryptation(SecretKey key, Cipher c, String fileName) {
        FileInputStream fis;
        FileOutputStream fos;
        CipherOutputStream cos;
        try {
            String[] aux = fileName.split("\\.");
            byte[] params = readFile(aux[0] + "Params.enc");
            AlgorithmParameters p = AlgorithmParameters.getInstance("PBEWithHmacSHA256AndAES_128");
            p.init(params);
            c.init(Cipher.DECRYPT_MODE, key, p);
            fis = new FileInputStream(fileName);
            fos = new FileOutputStream("temp.txt");
            cos = new CipherOutputStream(fos, c);
            byte[] b = new byte[16];
            int i;
            while ((i = fis.read(b)) != -1) {
                cos.write(b, 0, i);
            }
            List<String> res = fileToString();
            if (!res.isEmpty()) {
                res.remove(res.size() - 1);
            }
            cos.close();
            fis.close();
            fos.close();
            deleteFile("temp.txt");
            return res;
        } catch (IOException | InvalidKeyException | NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private static void deleteFile(String file) {
        File f = new File(file);
        if (f.exists()) {
            f.delete();
        }
    }
    
    private static byte[] readFile(String fileName) throws IOException {
        File f = new File(fileName);
        byte[] buffer = new byte[(int) f.length()]; // works only for 2GB file, because array index can only up to Integer.MAX
        FileInputStream is = new FileInputStream(fileName);
        is.read(buffer);
        is.close();
        return buffer;
    }
    
    
    private static List<String> fileToString() {
        List<String> res = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("temp.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                res.add(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    private static void stringToFile(List<String> data, String fileName) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileName))) {
            for (String line : data) {
                writer.write(line);
                writer.newLine();
            }
            writer.write("EOF");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}