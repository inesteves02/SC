package domain;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

public class Tintolmarket {
    
    private static ObjectInputStream in;
    private static ObjectOutputStream out;
    private static Scanner sc;
    private static final String HELP =    "Options available:\n" 
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
            if (args.length != 2 || args.length != 3){
                System.err.println("Usage: java Tintolmarket <server port> <username> <password>");
                System.exit(1);
            }

            String serverAddress = args[0];
            int port = 12345;
            if (serverAddress.contains(":")) {
                String[] parts = serverAddress.split(":");
                serverAddress = parts[0];
                port = Integer.parseInt(parts[1]);
            }

            String username = args[1];

            sc = new Scanner(System.in);

            String password = args[2];

            if (args.length == 2) {
                System.out.println("Insert password: ");
                password = sc.nextLine();
            }

            Socket clientSocket = new Socket(serverAddress, port);
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());

            if (clientLogin(username, password)) {
                System.out.println("Login successful\n");
                clientInteraction();
            } else {
                System.out.println("Login failed\n");
            }

            in.close();
            out.close();
            sc.close();
            clientSocket.close();
        } catch (Exception e) {
            System.err.println("Cannot connect to the server.");
        }

    }

    private static boolean clientLogin(String username, String password) {
        try {
            
            out.writeObject(username);
            out.writeObject(password);

            return (boolean) in.readObject();
            
        } catch (Exception e) {
            System.err.println("Error.\n");
            e.printStackTrace();
            return false;
        }
    }

    private static void clientInteraction() {
       try {
            System.out.println(HELP);
            
            String input;
            
            do {
                input = sc.nextLine();
                String[] inputArray = input.split(" ");
     
                processCommands(inputArray);                
                
            } while(!input.equalsIgnoreCase("exit"));

            
            out.writeObject(input);
            sc.close();
        } catch (Exception e) {
            System.err.println("Error. Disconnecting...");
        }
    }

    private static void processCommands(String[] inputArray) throws Exception {
                
        switch(inputArray[0].toLowerCase()) {
            case "add":
            case "a":
                out.writeObject(inputArray[0]);
                out.writeObject(inputArray[1]);
                out.writeObject(inputArray[2]);
                
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

                System.out.println((String) in.readObject());
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
                System.out.println("Saldo Disponivel: "+ balance);
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