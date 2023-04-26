package controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import domain.Message;
import domain.User;
import domain.Wine;

public class FileReaderHandler {

    private final String LOGIN_FILE = "login.txt";
    private final String CERTIFICATES_FOLDER = "certificates";
    private final String USER_DATA_FOLDER = "user_data";
    private final String USER_FILE = "User.txt";
    private final String WINE_FILE = "Wine.txt";
    private final String MESSAGE_FILE = "Message.txt";

    private SecretKey key;
    private Cipher cipher;

    public ConcurrentHashMap<String, User> readUsers() {
        ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();

        // get all folders from user_data directory
        File folder = new File(USER_DATA_FOLDER);

        if (folder.isDirectory()) {
            File[] folders = folder.listFiles();

            // loop through all user folders and read user data
            for (File f : folders) {
                if (f.isDirectory()) {
                    String balance = readBalanceFromFile(f.getAbsolutePath() + "/" + USER_FILE);

                    HashMap<String, Wine> wines = readWinesFromFile(f.getAbsolutePath() + "/" + WINE_FILE);

                    List<Message> messages = readMessagesFromFile(f.getAbsolutePath() + "/" + MESSAGE_FILE);

                    // create user object and add to HashMap
                    User user = new User(f.getName(), Double.parseDouble(balance), wines, messages);
                    users.put(f.getName(), user);
                }
            }

            return users;
        } else {
            System.out.println("A pasta user_data não existe!");
            return null;
        }
    }

    private String readBalanceFromFile(String filepath) {
        String balance = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                balance = line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return balance;
    }

    /*
     * Read wine data from file
     */
    private HashMap<String, Wine> readWinesFromFile(String filepath) {
        HashMap<String, Wine> wines = new HashMap<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filepath));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                Wine wine = new Wine(parts[0], Double.parseDouble(parts[1]), Integer.parseInt(parts[2]),
                        Double.parseDouble(parts[3]), parts[4], Boolean.parseBoolean(parts[5]), parts[6]);
                wines.put(parts[0], wine);
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wines;
    }

    private List<Message> readMessagesFromFile(String filename) {
        // List of messages to return
        List<Message> messages = new ArrayList<>();

        try {
            // Open the file
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            // Read each line of the file
            while ((line = reader.readLine()) != null) {
                // Split each line of the file into parts
                String[] parts = line.split(":");
                // Create a new message from each line
                Message msg = new Message(parts[0], parts[1], parts[2]);
                // Add the new message to the list
                messages.add(msg);
            }
            // Close the file
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return messages;
    }

    /*
     * Check if the username and password match by reading the LOGIN_FILE file
     */
    public int clientLogin(String clientID, String public_key) {
        File file = new File(LOGIN_FILE);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts[0].equals(clientID) && parts[1].equals(public_key)) {
                    return 1;
                } else if (parts[0].equals(clientID) && !parts[1].equals(public_key)) {
                    return 0;
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo login.txt: " + e.getMessage());
        }
        return -1;
    }

    public boolean userExists(String userID) {
        File folder = new File(USER_DATA_FOLDER);
        if (folder.isDirectory()) {
            File[] folders = folder.listFiles();
            for (File f : folders) {
                if (f.isDirectory()) {
                    if (f.getName().equals(userID)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public String getCertificateName(String userID) {
        return null;
    }

    public void setKey(SecretKey key) {
        this.key = key;
    }

    public void setCipher(Cipher cipher) {
        this.cipher = cipher;
    }
}