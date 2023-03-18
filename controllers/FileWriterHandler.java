package controllers;

import domain.User;
import domain.Wine;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileWriterHandler {

    private final String LOGIN_FILE = "login.txt";
    private final String USER_DATA_FOLDER = "user_data";
    private final String USER_FILE = "User.txt";
    private final String WINE_FILE = "Wine.txt";
    private final String MESSAGE_FILE = "Message.txt";
    
    public FileWriterHandler () {
        create_verify_logintxt();
        create_user_data();
    }

    private void create_user_data() {
        File file = new File(USER_DATA_FOLDER);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    public void addWineToUser(User user, Wine wine) {
        // cria um objeto File com o caminho da pasta do usuário
        File userDirectory = new File(USER_DATA_FOLDER, user.getName());

        // cria um objeto File com o caminho do arquivo do vinho
        File wineFile = new File(userDirectory, "Wine.txt");

        // escreve as informações do vinho no arquivo
        try {
            FileWriter writer = new FileWriter(wineFile,true);
            writer.write(wine.getName() + ":" + wine.getImage() + ":" + wine.getPrice() + ":" + wine.getQuantity() + ":" + wine.getRating() + ":" + user.getName() + ":" + wine.isForSale() + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * This code creates a file called login.txt if it does not already exist. If the file is created, a message is printed. If the file already exists, a message is printed.
     * If an IOException is thrown, an error message is printed.
     */
    private void create_verify_logintxt () {
        File file = new File(LOGIN_FILE);
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

    public void createUserFolderAndFiles(User user) {
        File file = new File(USER_DATA_FOLDER, user.getName());
        if (!file.exists()) {
            file.mkdir();

            String path = USER_DATA_FOLDER + "/" + user.getName() + "/";

            File userFile = new File(path, USER_FILE);
            File wineFile = new File(path, WINE_FILE);
            File messageFile = new File(path, MESSAGE_FILE);

            try {
                userFile.createNewFile();
                wineFile.createNewFile();
                messageFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // escreve as informações do usuário no arquivo
            try {
                FileWriter fw = new FileWriter(path + USER_FILE);
                BufferedWriter bw = new BufferedWriter(fw);

                bw.write(Double.toString(user.getBalance()));
                bw.newLine();
                bw.close();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Enters the user's wine data and replaces the old wine data with the new one
    public void updateWine(User user, Wine wine) throws IOException {

        File file = new File(USER_DATA_FOLDER + "/" + user.getName() + "/" + WINE_FILE);

        BufferedReader reader = new BufferedReader(new FileReader(file));
        List<String> wines = new ArrayList<>();
        String line;

        while ((line = reader.readLine()) != null) {
            wines.add(line);
        }

        boolean guard = false;
        for (int i = 0; i < wines.size() && !guard; i++) {
            String[] wineData = wines.get(i).split(":");
            if (wineData[0].equals(wine.getName())) {
                wines.set(i, wine.getName() + ":" + wine.getImage() + ":" + wine.getPrice() + ":" + wine.getQuantity() + ":" + wine.getRating() + ":" + user.getName() + ":" + wine.isForSale());
                guard = true;
            }
        }
        reader.close();

        FileWriter writer = new FileWriter(file);
        for (String wineString : wines) {
            writer.write(wineString + "\n");
        }
        writer.close();
    }

    public void updateUserBalance (User user) throws IOException {

        File file = new File(USER_DATA_FOLDER + "/" + user.getName() + "/" + USER_FILE);

        FileWriter writer = new FileWriter(file);
        writer.write(Double.toString(user.getBalance()));
        writer.close();
    }
}
