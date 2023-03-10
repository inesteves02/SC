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


    public void addUser(String user, String pass) {
        try (FileWriter writer = new FileWriter(LOGIN_FILE, true)) {
            writer.write(user + ":" + pass + "\n");
        } catch (IOException e) {
            System.err.println("Error writing to login.txt: " + e.getMessage());
        }
    }
}
