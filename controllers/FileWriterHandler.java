package controllers;

import domain.Message;
import domain.User;
import domain.Wine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
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

// All file writing operations
public class FileWriterHandler {

    private final String LOGIN_FILE = "login.txt";
    private final String USER_DATA_FOLDER = "user_data";
    private final String IMAGE_FOLDER = "images";
    private final String USER_FILE = "User.txt";
    private final String WINE_FILE = "Wine.txt";
    private final String MESSAGE_FILE = "Message.txt";
    private final String COLON_DELIMITER = ":";

    // Singleton
    public FileWriterHandler() {
        createVerifyLoginTxt();
        createUserFolder();
    }

    // Create login.txt if it doesn't exist
    private void createVerifyLoginTxt() {
        try {
            File file = new File(LOGIN_FILE);
            if (file.createNewFile()) {
                System.out.println("The file login.txt was created.");
            } else {
                System.out.println("The file login.txt already exists.");
            }
        } catch (IOException e) {
            System.out.println("Error creating login.txt: " + e.getMessage());
        }
    }

    // Create user_data folder if it doesn't exist
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

    // Create user folder and files if it doesn't exist
    public void createUserFolderAndFiles(User user) {
        try {
            Path path = Paths.get(USER_DATA_FOLDER, user.getName());
            if (Files.exists(path)) {
                return;
            }

            Files.createDirectory(path);
            // Create image folder
            File imageFolder = new File(path.toString(), IMAGE_FOLDER);
            // Create user, wine and message paths
            File userFile = new File(path.toString(), USER_FILE);
            File wineFile = new File(path.toString(), WINE_FILE);
            File messageFile = new File(path.toString(), MESSAGE_FILE);

            // Create files
            imageFolder.mkdir();
            userFile.createNewFile();
            wineFile.createNewFile();
            messageFile.createNewFile();

            writeUserBalance(user, path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Write user balance to file
    public void writeUserBalance(User user, Path path) {
        try {
            FileWriter fw = new FileWriter(path + "/" + USER_FILE);
            BufferedWriter bw = new BufferedWriter(fw);

            bw.write(Double.toString(user.getBalance()));
            bw.newLine();

            bw.close();
            fw.close();
        } catch (IOException e) {
            System.err.println("Error writing to user file: " + e.getMessage());
        }
    }

    // Add user to login.txt
    public synchronized void addUser(String user, String pass) {
        try (FileWriter writer = new FileWriter(LOGIN_FILE, true)) {
            writer.write(user + COLON_DELIMITER + pass + "\n");
        } catch (IOException e) {
            System.err.println("Error writing to login.txt: " + e.getMessage());
        }
    }

    // Add wine to user's wine file
    public synchronized void addWineToUser(User user, Wine wine) {
        try {
            Path path = Paths.get(USER_DATA_FOLDER, user.getName());
            File wineFile = new File(path.toString(), WINE_FILE);

            FileWriter writer = new FileWriter(wineFile, true);
            writer.write(wine.getName() + COLON_DELIMITER + wine.getPrice() + COLON_DELIMITER + wine.getQuantity() + COLON_DELIMITER + wine.getRating() + COLON_DELIMITER + user.getName() + COLON_DELIMITER + wine.isForSale() + COLON_DELIMITER + wine.getImageFormat() + "\n");
            writer.close();

        } catch (IOException e) {
            System.err.println("Error writing to wine file: " + e.getMessage());
        }
    }

    // Update wine in user's wine file
    public synchronized void updateWine(User user, Wine wine) {
        try {
            Path path = Paths.get(USER_DATA_FOLDER, user.getName());
            File wineFile = new File(path.toString(), WINE_FILE);

            List<String> wines = new ArrayList<>();

            // Read the wine file and put all of the lines in a list
            try (Stream<String> stream = Files.lines(wineFile.toPath())) {
                wines = stream.collect(Collectors.toList());
            }

            // Loop through the list until we find the wine we want to update
            boolean guard = false;
            for (int i = 0; i < wines.size() && !guard; i++) {
                String[] wineData = wines.get(i).split(COLON_DELIMITER);
                if (wineData[0].equals(wine.getName())) {
                    // Update the wine data
                    wines.set(i, wine.getName() + COLON_DELIMITER + wine.getPrice() + COLON_DELIMITER + wine.getQuantity() + COLON_DELIMITER + wine.getRating() + COLON_DELIMITER + user.getName() + COLON_DELIMITER + wine.isForSale() + COLON_DELIMITER + wine.getImageFormat());
                    guard = true;
                }
            }

            // Write the updated list back to the wine file
            FileWriter writer = new FileWriter(wineFile);
            for (String wineString : wines) {
                writer.write(wineString + "\n");
            }
            writer.close();
        } catch (IOException e) {
            System.err.println("Error updating wine: " + e.getMessage());
        }
    }

    // Update user balance
    public synchronized void updateUserBalance(User user) {
        try {
            Path path = Paths.get(USER_DATA_FOLDER, user.getName(), USER_FILE);
            FileWriter writer = new FileWriter(path.toFile());
            writer.write(Double.toString(user.getBalance()));
            writer.close();

        } catch (IOException e) {
            System.err.println("Error updating user balance: " + e.getMessage());
        }
    }

    // Add message to user's message file
    public synchronized void addMessage(Message message) {
        try {
            Path path = Paths.get(USER_DATA_FOLDER, message.getReceiver(), MESSAGE_FILE);
            FileWriter writer = new FileWriter(path.toFile(), true);
            BufferedWriter bw = new BufferedWriter(writer);
            bw.write(message.getSender() + COLON_DELIMITER + message.getReceiver() + COLON_DELIMITER + message.getMessage() + "\n");
            bw.close();
            writer.close();

        } catch (IOException e) {
            System.err.println("Error writing message: " + e.getMessage());
        }

    }

    // Clear all messages from user's message file
    public synchronized void clearMessages(User user) {
        try {
            Path path = Paths.get(USER_DATA_FOLDER, user.getName(), MESSAGE_FILE);
            FileWriter fw = new FileWriter(path.toFile(), false);
            try (PrintWriter pw = new PrintWriter(fw, false)) {
                pw.flush();
            }
        } catch (IOException e) {
            System.err.println("Error clearing messages: " + e.getMessage());
        }
    }

    // Save the images to the user's image folder
    public void saveImage(byte[] image, String wineName, String imageFormat, User user) {
        try {
            // Create the path to the image folder
            Path path = Paths.get(USER_DATA_FOLDER, user.getName(), IMAGE_FOLDER);
    
            // Create the image file
            File imageFile = new File(path.toString(), wineName + "." + imageFormat);

            // Write the image to the file
            FileOutputStream fos = new FileOutputStream(imageFile);
            fos.write(image);
            fos.close();

        } catch (IOException e) {
            System.err.println("Error saving image: " + e.getMessage());
        }
    }
}