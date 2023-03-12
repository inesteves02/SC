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
	private static final int DEFAULT_PRICE = 0;
	private static final int DEFAULT_BALANCE = 200;
	private static final int DEFAULT_QUANTITY = 0;
	private static final double DEFAULT_RATING = 0;
	private static final boolean DEFAULT_IS_FOR_SALE = false;
    
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

		userCatalog = new UserCatalog(fileReaderH.readUsers()); 

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
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}

		while(true) {
			try {
				Socket inSoc = sSoc.accept();
				ServerThread newServerThread = new ServerThread(inSoc);
				newServerThread.start();
		    }
		    catch (IOException e) {
		        e.printStackTrace();
		    }

		}
		//sSoc.close();

    }

	//Threads used to communicate with the clients
	class ServerThread extends Thread {

		private Socket socket = null;
		private ObjectOutputStream outStream;
		private ObjectInputStream inStream;
		private User user;

		ServerThread(Socket inSoc) {
			this.socket = inSoc;
			try {
				outStream = new ObjectOutputStream(socket.getOutputStream());
				inStream = new ObjectInputStream(socket.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("Client connected!");
		}
		
		/*
		 * This code creates an ObjectOutputStream object and an ObjectInputStream object from the socket's input and output streams.
		 * It then reads the username and password from the client and calls the clientLogin() method of the FileReaderHandler object.
		 * If the login is successful, it prints "Login successful!" to the console.
		 * If the password is wrong, it prints "Wrong password!" to the console.
		 * If the user is not found, it prints "User not found! Creating new user..." to the console and calls the addUser() method of the FileWriterHandler object.
		 */
		public void run(){
			try {

				String username = (String) inStream.readObject();
				String password = (String) inStream.readObject();

				int login = fileReaderH.clientLogin(username, password);

				if (login == 0){
					System.out.println("Client " + user + " tried to login with wrong password! Disconnecting...");
					outStream.writeBoolean(false);
					outStream.close();
					inStream.close();
					socket.close();
					return;

				} else if (login == -1){
					System.out.println("User not found! Creating new user...");
					fileWriterH.addUser(username, password);
					userCatalog.addUser(username);
				}

				System.out.println("Login successful!");
				outStream.writeObject(true);

				user = new User(username);

				//create user folder and its files with their default values
				fileWriterH.createUserFolderAndFiles(user);

				userInteraction();

			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		private void userInteraction() {

			String userInput = "";

			try {
				userInput = (String) inStream.readObject();
			} catch (Exception e) {
				e.printStackTrace();
			}

			String userInputArray [] = userInput.split(" ");

			if (userInputArray[0].equals("add") || userInputArray[0].equals("a") && userInputArray.length == 3){

				try {
					String wineName = (String) inStream.readObject();
					String wineImage = (String) inStream.readObject();

					if (!user.haveWine(wineName)){
	
						Wine wine = new Wine(wineName, wineImage, DEFAULT_PRICE, DEFAULT_QUANTITY, DEFAULT_RATING, DEFAULT_IS_FOR_SALE);
						fileWriterH.addWineToUser(user, wine);
						userCatalog.getUser(user.getName()).addWine(wine);
						outStream.writeObject(true);
						
					} else {
						outStream.writeObject(false);
					}

				} catch (ClassNotFoundException | IOException e) {
					e.printStackTrace();
				}

			}
		}
	}

}
