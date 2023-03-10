package domain;

import controllers.FileReaderHandler;
import controllers.FileWriterHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TintolmarketServer {

	private static FileReaderHandler fileReaderH;
	private static FileWriterHandler fileWriterH;
	private static UserCatalog userCatalog;
    
	/*
	 * This code is the main method of a TintolmarketServer class. 
	 * It prints "Server is starting..." to the console, creates a new instance of the TintolmarketServer class, 
	 * creates new instances of FileWriterHandler, FileReaderHandler and UserCatalog classes, 
	 * and then calls the startServer() method on the TintolmarketServer instance.
	 */
    public static void main(String[] args) {
        
        System.out.println("Server is starting...");

        TintolmarketServer server = new TintolmarketServer();

		server.init(); // Initialize the required objects

        server.startServer(); // Start the server and wait for clients
    }

	private void init() { // Initialize the required objects 

		fileWriterH = new FileWriterHandler(); 

		fileReaderH = new FileReaderHandler(); 

		userCatalog = new UserCatalog(); 

    } 

	/*
	 * This code creates a ServerSocket object with port number 12345 and then enters an infinite loop. 
	 * In each iteration of the loop, it calls the accept() method of the ServerSocket object to wait for a client connection. 
	 * When a client connects, it creates a new ServerThread object and starts it. 
	 * The loop continues until the program is terminated. The commented out line at the end closes the ServerSocket object, 
	 * but this line is never reached due to the infinite loop.
	 */
    public void startServer (){
		ServerSocket sSoc = null;
        
		try {
			sSoc = new ServerSocket(12345);

            while(true) {
                try {
                    Socket inSoc = sSoc.accept();
                    ServerThread newServerThread = new ServerThread(inSoc);
                    newServerThread.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }   
            }

        } catch (IOException e) { //catch IOException outside of while loop to ensure that the server socket is closed when an error occurs 
            System.err.println(e.getMessage());  //print out the error message 

            try { //attempt to close the server socket 
                sSoc.close();   //close the server socket 

            } catch (IOException ex) { //catch any errors that occur when closing the server socket 

                System.err.println(ex.getMessage()); //print out the error message 

            } finally { //finally block to ensure that the program exits with an error code if an exception is thrown  

                System.exit(-1); //exit with an error code of -1 

            }   

        }    

    }

	//Threads used to communicate with the clients
	class ServerThread extends Thread {

		private Socket socket = null;

		ServerThread(Socket inSoc) {
			this.socket = inSoc;
			System.out.println("Client connected!");
		}
 
		public void run(){
			try {

				ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());

				String user = (String) inStream.readObject();
				String pass = (String) inStream.readObject();

				int login = fileReaderH.clientLogin(user, pass);
				if (login == 1) {
					System.out.println("Login successful!");
				} else if (login == 0) {
					System.out.println("Wrong password!");
				} else {
					System.out.println("User not found! Creating new user...");
					fileWriterH.addUser(user, pass);
					userCatalog.addUser(user);
				}

			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

}
