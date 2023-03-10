package domain;

import java.util.Scanner;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Tintolmarket {
    
    private static ObjectInputStream in;
    private static ObjectOutputStream out;

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

            clientLogin();

            // fechar streams
            in.close();
            out.close();
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
    private static void clientLogin() {
        try {
            Scanner sc = new Scanner(System.in);

            System.out.print("Insert username:");
            String user = sc.nextLine();

            System.out.print("Insert password:");
            String pass = sc.nextLine();

            out.writeObject(user);
            out.writeObject(pass);

            String answer = (String) in.readObject();
            System.out.println(answer);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void clientInteraction() {
       // TODO
    } 
}
