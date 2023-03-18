package domain;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
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
            Socket clientSocket = new Socket("localhost", 12345);

            in = new ObjectInputStream(clientSocket.getInputStream());
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            sc = new Scanner(System.in);

            if (clientLogin()) {
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

    private static boolean clientLogin() {
        try {
            System.out.print("Insert username:");
            String user = sc.nextLine();

            System.out.print("Insert password:");
            String pass = sc.nextLine();

            out.writeObject(user);
            out.writeObject(pass);

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
                out.writeObject(inputArray[2]);
                System.out.println(in.readObject());
                break;

            case "read":
            case "r":
                out.writeObject(inputArray[0]);
                Object messages = in.readObject();
                if (messages == null) {
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