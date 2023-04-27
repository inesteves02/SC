package domain;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignedObject;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.Arrays;
import java.util.Scanner;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.security.sasl.AuthenticationException;

public class Tintolmarket {

    private static ObjectInputStream in;
    private static ObjectOutputStream out;
    private static Scanner sc;
    private static final String HELP = "Options available:\n"
            + "\tadd <wine> <image> - add a new wine to the catalog\n "
            + "\tsell <wine> <value> <quantity> - sell a wine from the catalog\n "
            + "\tview <wine> - view the details of a wine\n "
            + "\tbuy <wine> <seller> <quantity> - buy a wine from the catalog\n "
            + "\twallet - view the current balance of the wallet\n "
            + "\tclassify <wine> <stars> - classify a wine\n "
            + "\ttalk <user> <message> - send a message to another user\n "
            + "\tread - read the messages received\n "
            + "\texit - exit the program\n";

    public static void main(String[] args) {
        try {
            if (args.length != 5) {
                System.err.println(
                        "Usage: java Tintolmarket <serverAdress> <truststore> <keyStore> <password-keyStore> <userID>");
                System.exit(1);
            }

            String serverAddress = args[0];
            int port = 12345;
            if (serverAddress.contains(":")) {
                String[] parts = serverAddress.split(":");
                serverAddress = parts[0];
                port = Integer.parseInt(parts[1]);
            }

            String trustStore = args[1];
            String keyStore = args[2];
            String passwordKeyStore = args[3];
            String userID = args[4];

            System.setProperty("javax.net.ssl.trustStore", trustStore);
            System.setProperty("javax.net.ssl.keyStore", keyStore);
            System.setProperty("javax.net.ssl.keyStorePassword", passwordKeyStore);
            // System.setProperty("javax.net.ssl.trustStorePassword", "servidor");

            // used to create a SSLSocket for secure socket communication over a network
            SocketFactory sf = SSLSocketFactory.getDefault();
            SSLSocket clientSocket = (SSLSocket) sf.createSocket(serverAddress, port);

            in = new ObjectInputStream(clientSocket.getInputStream());
            out = new ObjectOutputStream(clientSocket.getOutputStream());

            System.out.println("Authenticating user " + userID + "...\n");
            // sends the user id to the server
            out.writeObject((String) userID);

            Long nonce = (Long) in.readObject();

            // checks id the client is authenticated (readObject is a boolean wether it is
            // or not)
            boolean authenticated = (boolean) in.readObject();

            // creates an object and initializes it with the path of a keystore file
            FileInputStream fis = new FileInputStream(keyStore);
            // jks = java key store and creates an KeyStore that type
            KeyStore ks = KeyStore.getInstance("JKS");
            // loads the keyStoreFile into the ks object
            ks.load(fis, passwordKeyStore.toCharArray());

            // certificate of user
            Certificate cert = ks.getCertificate(userID);

            // privateKey from the keyStore
            PrivateKey privateKey = (PrivateKey) ks.getKey(userID, passwordKeyStore.toCharArray());

            // signs nonce
            SignedObject sig = new SignedObject(nonce, privateKey, Signature.getInstance("MD5withRSA"));

            if (authenticated) {
                // sends the nonce signed
                out.writeObject(sig);
            } else {
                // sends the nonce
                out.writeObject(nonce);
                // sends the certificate with the publicKey
                out.writeObject(cert.getEncoded());
                // sends the nonce signed
                out.writeObject(sig);
            }

            boolean authenticationSucessful = (boolean) in.readObject();
            if (authenticationSucessful) {
                System.out.println("User authenticated successfully.\n");
            } else {
                throw new AuthenticationException(userID + " authentication failed.");
            }

            clientInteraction();
            in.close();
            out.close();
            sc.close();
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Cannot connect to the server.");
        } catch (ClassNotFoundException e) {
            System.err.println("Cannot recieve data from the server.");
        } catch (CertificateEncodingException e) {
            System.err.println("Cannot encode certificate.");
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Cannot find algorithm.");
        } catch (Exception e) {
            System.err.println("Error.");
        }

    }

    private static void clientInteraction() {
        try {
            System.out.println(HELP);

            String input;
            sc = new Scanner(System.in);

            do {
                input = sc.nextLine();
                String[] inputArray = input.split(" ");

                processCommands(inputArray);

            } while (!input.equalsIgnoreCase("exit"));

            out.writeObject(input);
            sc.close();
        } catch (Exception e) {
            System.err.println("Error. Disconnecting...");
        }
    }

    private static void processCommands(String[] inputArray) throws Exception {

        switch (inputArray[0].toLowerCase()) {
            case "add":
            case "a":

                out.writeObject(inputArray[0]);
                out.writeObject(inputArray[1]);

                String imageFormat = inputArray[2].substring(inputArray[2].lastIndexOf(".") + 1);
                out.writeObject(imageFormat);

                // Read image
                File file = new File(inputArray[2]);
                byte[] imageSent = new byte[(int) file.length()];
                DataInputStream dis = new DataInputStream(new FileInputStream(file));
                dis.readFully(imageSent);
                out.writeObject(imageSent);
                dis.close();

                if ((boolean) in.readObject()) {
                    System.out.println("Wine added successfully");
                } else {
                    System.out.println("Wine already exists");
                }
                break;

            case "sell":
            case "s":
                out.writeObject(inputArray[0]);
                out.writeObject(inputArray[1]);
                out.writeObject(inputArray[2]);
                out.writeObject(inputArray[3]);

                if ((boolean) in.readObject()) {
                    System.out.println("Wine to sell added successfully");
                } else {
                    System.out.println("Wine not added to sell");
                }
                break;

            case "view":
            case "v":
                out.writeObject(inputArray[0]);
                out.writeObject(inputArray[1]);

                String result = (String) in.readObject();

                if (result.equals("No wines found!")) {
                    System.out.println(result);
                    return;
                }

                // loop to recieve the images from the server
                int numberOfImages = (int) in.readObject();

                for (int i = 0; i < numberOfImages; i++) {
                    // recieve the image name from the client
                    String imageNameRecieved = (String) in.readObject();

                    // recieve the image from the client
                    byte[] imageRecieved = (byte[]) in.readObject();

                    // write the image to a file
                    File fileRecieved = new File(imageNameRecieved);
                    FileOutputStream fos = new FileOutputStream(fileRecieved);
                    fos.write(imageRecieved);
                    fos.close();
                }

                System.out.println("Wine images saved in the current directory");

                break;

            case "buy":
            case "b":
                out.writeObject(inputArray[0]);
                out.writeObject(inputArray[1]);
                out.writeObject(inputArray[2]);
                out.writeObject(inputArray[3]);

                if ((boolean) in.readObject()) {
                    System.out.println("Wine bought successfully");

                } else {
                    System.out.println("Wine not bought");
                }
                break;

            case "wallet":
            case "w":
                out.writeObject(inputArray[0]);

                Double balance = (Double) in.readObject();
                System.out.println("Saldo Disponivel: " + balance);
                break;

            case "classify":
            case "c":
                out.writeObject(inputArray[0]);
                out.writeObject(inputArray[1]);
                out.writeObject(inputArray[2]);

                if ((boolean) in.readObject()) {

                    System.out.println("Wine classified successfully");

                } else {
                    System.out.println("Wine not classified");
                }
                break;

            case "talk":
            case "t":
                out.writeObject(inputArray[0]);
                out.writeObject(inputArray[1]);
                out.writeObject(String.join(" ", Arrays.copyOfRange(inputArray, 2, inputArray.length)));
                System.out.println(in.readObject());
                break;

            case "read":
            case "r":

                out.writeObject(inputArray[0]);
                String messages = (String) in.readObject();

                if (messages.isEmpty()) {
                    System.out.println("No messages");
                } else {
                    System.out.println(messages);
                }
                break;

            case "help":
            case "h":
                System.out.println(HELP);
                break;

            case "exit":
                out.writeObject(inputArray[0]);
                break;

            default:
                System.out.println("Invalid command. Type 'help' for a list of commands.");
        }
    }
}