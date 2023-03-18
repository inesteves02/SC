package domain;

import java.util.Scanner;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Tintolmarket {
    
    private static ObjectInputStream in;
    private static ObjectOutputStream out;
    private static Scanner sc;
    private static final String HELP =    "Options available:\n" 
                                        + "add <wine> <image> - add a new wine to the catalog\n "
                                        + "sell <wine> <value> <quantity> - sell a wine from the catalog\n "
                                        + "view <wine> - view the details of a wine\n "
                                        + "buy <wine> <seller> <quantity> - buy a wine from the catalog\n "
                                        + "wallet - view the current balance of the wallet\n "
                                        + "classify <wine> <stars> - classify a wine\n "
                                        + "talk <user> <message> - send a message to another user\n "
                                        + "read - read the messages received\n "
                                        + "exit - exit the program";

    /*
     * This code creates a socket connection to a server at localhost on port 12345, and then creates input and output streams for communication. 
     * It then calls the clientLogin() method, and finally closes the input and output streams.
     */
    public static void main(String[] args) {
        try {
            // iniciar socket
            Socket clientSocket = new Socket("localhost", 12345);

            // iniciar streams
            in = new ObjectInputStream(clientSocket.getInputStream());
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            sc = new Scanner(System.in);

            if(clientLogin()) {
                System.out.println("Login successful\n");
                clientInteraction();
            } else {
                System.out.println("Login failed\n");
            }

            // fechar streams
            in.close();
            out.close();
            sc.close();
            clientSocket.close();
        } catch (Exception e) {
            System.err.println("Erro");
        }

    }

    /*
     * This code is used to log in a client. 
     * It creates a Scanner object to get the username and password from the user. 
     * It then writes the username and password to an output stream, and reads a boolean value from an input stream. 
     * If an exception is thrown, it prints the stack trace.
     */
    private static boolean clientLogin() {
        try {
            System.out.print("Insert username:");
            String user = sc.nextLine();

            System.out.print("Insert password:");
            String pass = sc.nextLine();

            out.writeObject(user);
            out.writeObject(pass);

            boolean login = (boolean) in.readObject();
            
            return login;
            
        } catch (Exception e) {
            System.err.println("Error.\n");
            e.printStackTrace();
            return false;
        }
    }

    private static void clientInteraction() {
       try {
            System.out.println(HELP);
            
            String input = sc.nextLine();
            String[] inputArray = input.split(" ");
            
            while(!inputArray[0].equals("exit")) {
                
                if (inputArray[0].equals("add") || inputArray[0].equals("a")){
                    out.writeObject(inputArray[0]);
                    out.writeObject(inputArray[1]);
                    out.writeObject(inputArray[2]);
                    if ((boolean) in.readObject()){
                        System.out.println("Wine added successfully");
                    }
                    else{
                        System.out.println("Wine already exists");
                    }
                }
                else if (inputArray[0].equals("sell") || inputArray[0].equals("s")){

                    out.writeObject(inputArray[0]);
                    out.writeObject(inputArray[1]);
                    out.writeObject(inputArray[2]);
                    out.writeObject(inputArray[3]);

                    if ((boolean) in.readObject()){
                        System.out.println("Wine to sell added successfully");
                    }
                    else{
                        System.out.println("Wine not added to sell");
                    }
                }
                else if (inputArray[0].equals("view") || inputArray[0].equals("v")){

                    out.writeObject(inputArray[0]);
                    out.writeObject(inputArray[1]);

                    System.out.println((String) in.readObject());
                }
                else if (inputArray[0].equals("buy") || inputArray[0].equals("b")){

                    out.writeObject(inputArray[0]);
                    out.writeObject(inputArray[1]);
                    out.writeObject(inputArray[2]);
                    out.writeObject(inputArray[3]);

                    if ((boolean) in.readObject()){
                        System.out.println("Wine bought successfully");
                    }
                    else{
                        System.out.println("Wine not bought");
                    }
                }
                else if (inputArray[0].equals("wallet") || inputArray[0].equals("w")){

                    out.writeObject(inputArray[0]);

                    Double balance = (Double) in.readObject();
                    System.out.println("Saldo Disponivel: "+ balance);
                }
                else if (inputArray[0].equals("classify") || inputArray[0].equals("c")){

                    out.writeObject(inputArray[0]);
                    out.writeObject(inputArray[1]);
                    out.writeObject(inputArray[2]);

                    if ((boolean) in.readObject()){
                        System.out.println("Wine classified successfully");
                    }
                    else{
                        System.out.println("Wine not classified");
                    }
                }
                else if (inputArray[0].equals("talk") || inputArray[0].equals("t")){
                    out.writeObject(inputArray[0]);
                    out.writeObject(inputArray[1]);
                    out.writeObject(inputArray[2]);
                    System.out.println(in.readObject());
                }
                else if (inputArray[0].equals("read") || inputArray[0].equals("r")){
                    out.writeObject(inputArray[0]);
                    System.out.println(in.readObject());
                }
                else if (inputArray[0].equals("help") || inputArray[0].equals("h")){
                    System.out.println(HELP);
                }
                else {
                    System.out.println("Invalid command. Type 'help' for a list of commands.");
                }
                input = sc.nextLine();
                inputArray = input.split(" ");
            }
            out.writeObject(inputArray[0]);
            sc.close();
        } catch (Exception e) {
            System.err.println("Error. Disconnecting...");
        }
    }
}
