package controllers;

import java.io.FileWriter;
import java.io.File;
import java.io.IOException;

public class FileWriterHandler {

    private File file;
    private final String LOGIN_FILE = "login.txt";
    
    public FileWriterHandler () {
        create_verify_logintxt();
    }

    /*
     * This code creates a file called login.txt if it does not already exist. If the file is created, a message is printed. If the file already exists, a message is printed.
     * If an IOException is thrown, an error message is printed.
     */
    private void create_verify_logintxt () {
        File file = new File("login.txt");
        try {
            if (file.createNewFile()) {
                System.out.println("The file login.txt was created.");
            } else {
                System.out.println("The file login.txt already exists.");
            }
        } catch (IOException e) {
            System.out.println("Error creating login.txt: " + e.getMessage());
        }
    }

    /*
     * This code adds a user to the LOGIN_FILE file. It takes two parameters: a username (String user) and a password (String pass). 
     * It uses a FileWriter to write the username and password to the file, separated by a colon (":"). If an IOException is thrown, an error message is printed.
     */
    public void addUser(String user, String pass) {
        try (FileWriter writer = new FileWriter(LOGIN_FILE, true)) {
            writer.write(user + ":" + pass + "\n");
        } catch (IOException e) {
            System.err.println("Error writing to login.txt: " + e.getMessage());
        }
    }
}
