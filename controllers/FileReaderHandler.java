package controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import domain.User;
import domain.Wine;

public class FileReaderHandler {

    private final String LOGIN_FILE = "login.txt";
    private final String USER_DATA_FOLDER = "user_data";
    private final String USER_FILE = "User.txt";
    private final String WINE_FILE = "Wine.txt";
    private final String MESSAGE_FILE = "Message.txt";

    //Ler todos os usuarios e os seus respetivos vinhos e criar um hashmap com os mesmos
    public HashMap<String, User> readUsers() {

        HashMap<String, User> users = new HashMap<>();

        File folder = new File(USER_DATA_FOLDER);

        if (folder.isDirectory()) { // Verifica a existência da pasta user_data
            File[] folders = folder.listFiles(); // Lista todos as pastas da pasta user_data
            for (File f : folders) {
                if (f.isDirectory()) {

                    File[] files2 = f.listFiles(); // Lista todos os arquivos da pasta do usuário
                    String balance = null;
                    HashMap<String, Wine> wines = new HashMap<String, Wine>();

                    for (File file : files2) {

                        try {
                            // Verifica se o arquivo é o User.txt
                            if (file.getName().equals(USER_FILE)) {

                                // Lê o balance do user no arquivo User.txt    
                                BufferedReader reader = new BufferedReader(new FileReader(file));
                                String line;
                                    
                                while ((line = reader.readLine()) != null) {
                                    balance = line;
                                }

                                reader.close();
                            }
                            
                            if (file.getName().equals(WINE_FILE)){

                                //Cria um objeto Wine com o nome do vinho
                                BufferedReader reader = new BufferedReader(new FileReader(file));
                                String line;

                                while ((line = reader.readLine()) != null) {
                                    String[] parts = line.split(":");
                                    Wine wine = new Wine(parts[0], parts[1], Double.parseDouble(parts[2]), Integer.parseInt(parts[3]), Double.parseDouble(parts[4]), file.getName(), Boolean.parseBoolean(parts[5]));
                                    wines.put(parts[0], wine);
                                }

                                reader.close();
                            }
                            
                            if (file.getName().equals(MESSAGE_FILE)){
                                //TODO
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    
                    // Cria um objeto User com o nome do usuário
                    User user = new User(f.getName(), Double.parseDouble(balance), wines);
                    users.put(f.getName(), user);
                }
            }
            return users;
        } else {
            System.out.println("A pasta user_data não existe!");
            return null;
        }
    }
    /*
     * This code reads the LOGIN_FILE file and checks if the username and password match. 
     * It takes two parameters: a username (String user) and a password (String pass).
     */
    public int clientLogin(String user, String pass) {
        File file = new File(LOGIN_FILE);
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
