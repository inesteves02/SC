package controllers;

import domain.Message;
import domain.User;
import domain.Wine;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileWriterHandler {

    private final String LOGIN_FILE = "login.txt";
    private final String USER_DATA_FOLDER = "user_data";
    private final String USER_FILE = "User.txt";
    private final String WINE_FILE = "Wine.txt";
    private final String MESSAGE_FILE = "Message.txt";
    private final String COLON_DELIMITER = ":";

    public FileWriterHandler() {
        createVerifyLoginTxt();
        createUserFolder();
    }

    private void createVerifyLoginTxt() {
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

    public synchronized void addUser(String user, String pass) {
        try (FileWriter writer = new FileWriter(LOGIN_FILE, true)) {
            writer.write(user + COLON_DELIMITER + pass + "\n");
        } catch (IOException e) {
            System.err.println("Error writing to login.txt: " + e.getMessage());
        }
    }

    public void createUserFolder() {
        Path path = Paths.get(USER_DATA_FOLDER);
        try {
            if (!Files.exists(path)) {
                Files.createDirectory(path);
            }
        } catch (IOException e) {
            System.out.println("Error creating user data folder: " + e.getMessage());
        }
    }

    public void createUserFolderAndFiles(User user) {
        Path path = Paths.get(USER_DATA_FOLDER, user.getName());
        try {
            if (!Files.exists(path)) {
                Files.createDirectory(path);

                File userFile = new File(path.toString(), USER_FILE);
                File wineFile = new File(path.toString(), WINE_FILE);
                File messageFile = new File(path.toString(), MESSAGE_FILE);

                userFile.createNewFile();
                wineFile.createNewFile();
                messageFile.createNewFile();

                writeUserBalance(user, path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeUserBalance(User user, Path path) throws IOException {
        FileWriter fw = new FileWriter(path + "/" + USER_FILE);
        BufferedWriter bw = new BufferedWriter(fw);

        bw.write(Double.toString(user.getBalance()));
        bw.newLine();

        bw.close();
        fw.close();
    }

    public synchronized void addWineToUser(User user, Wine wine) {
        Path path = Paths.get(USER_DATA_FOLDER, user.getName());
        File wineFile = new File(path.toString(), WINE_FILE);

        try {
            FileWriter writer = new FileWriter(wineFile, true);
            writer.write(wine.getName() + COLON_DELIMITER + wine.getImage() + COLON_DELIMITER + wine.getPrice() + COLON_DELIMITER + wine.getQuantity() + COLON_DELIMITER + wine.getRating() + COLON_DELIMITER + user.getName() + COLON_DELIMITER + wine.isForSale() + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void updateWine(User user, Wine wine) throws IOException {
        Path path = Paths.get(USER_DATA_FOLDER, user.getName());
        File wineFile = new File(path.toString(), WINE_FILE);

        List<String> wines = new ArrayList<>();

        try (Stream<String> stream = Files.lines(wineFile.toPath())) {
            wines = stream.collect(Collectors.toList());
        }

        boolean guard = false;
        for (int i = 0; i < wines.size() && !guard; i++) {
            String[] wineData = wines.get(i).split(COLON_DELIMITER);
            if (wineData[0].equals(wine.getName())) {
                wines.set(i, wine.getName() + COLON_DELIMITER + wine.getImage() + COLON_DELIMITER + wine.getPrice() + COLON_DELIMITER + wine.getQuantity() + COLON_DELIMITER + wine.getRating() + COLON_DELIMITER + user.getName() + COLON_DELIMITER + wine.isForSale());
                guard = true;
            }
        }

        FileWriter writer = new FileWriter(wineFile);
        for (String wineString : wines) {
            writer.write(wineString + "\n");
        }
        writer.close();
    }

    public synchronized void updateUserBalance(User user) throws IOException {
        Path path = Paths.get(USER_DATA_FOLDER, user.getName(), USER_FILE);
        FileWriter writer = new FileWriter(path.toFile());
        writer.write(Double.toString(user.getBalance()));
        writer.close();
    }

    public synchronized void addMessage(Message message) {

        Path path = Paths.get(USER_DATA_FOLDER, message.getReceiver(), MESSAGE_FILE);

        try {
            FileWriter writer = new FileWriter(path.toFile(), true);
            BufferedWriter bw = new BufferedWriter(writer);
            bw.write(message.getSender() + COLON_DELIMITER + message.getReceiver() + COLON_DELIMITER + message.getMessage() + "\n");
            bw.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public synchronized void clearMessages(User user) {

        Path path = Paths.get(USER_DATA_FOLDER, user.getName(), MESSAGE_FILE);

        FileWriter fw;
        try {
            fw = new FileWriter(path.toFile(), false);
            PrintWriter pw = new PrintWriter(fw, false);
            pw.flush();
            pw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}