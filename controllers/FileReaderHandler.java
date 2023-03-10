package controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileReaderHandler {

    private final String LOGIN_FILE = "login.txt";

    public int clientLogin(String user, String pass) {
        File file = new File("login.txt");
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts[0].equals(user) && parts[1].equals(pass)) {
                    return 1;
                } else if (parts[0].equals(user) && !parts[1].equals(pass)) {
                    return 0;
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo login.txt: " + e.getMessage());
        }
        return -1;
    }
}
