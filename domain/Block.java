package domain;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.stream.IntStream;

public class Block implements Serializable {

    private String hash;
    private String previousHash;
    private String data;

    public Block(String data, String previousHash) {
        this.data = data;
        this.previousHash = previousHash;
        this.hash = calculateBlockHash();
    }

    public String getData() {
        return this.data;
    }

    public String getPrevHash() {
        return this.previousHash;
    }

    public String calculateBlockHash() {
        String dataToHash = data;
        MessageDigest digest = null;
        try {
            // Create SHA-256 hash
            digest = MessageDigest.getInstance("SHA-256");
            final byte[] bytes = digest.digest(dataToHash.getBytes());

            StringBuffer buffer = new StringBuffer();
            IntStream.range(0, bytes.length)
                    .mapToObj(i -> bytes[i])
                    .forEach(b -> buffer.append(String.format("%02x", b)));

            return buffer.toString();
        } catch (NoSuchAlgorithmException ex) {
            // Handle exception
            ex.printStackTrace();
        }
        return "";
    }

    public String getHash() {
        return this.hash;
    }
}
