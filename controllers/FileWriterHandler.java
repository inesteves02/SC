package controllers;

import domain.User;
import domain.Wine;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

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
            FileWriter writer = new FileWriter(wineFile);
            writer.write(wine.getName() + ":" + wine.getImage() + ":" + wine.getPrice() + ":" + wine.getQuantity() + ":" + wine.getRating() + ":" + wine.isForSale() + "\n");
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

            String path = USER_DATA_FOLDER + "/" + user + "/";

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

                bw.write("Balance: " + user.getBalance());
                bw.newLine();
                bw.close();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
