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
    
    public static void main(String[] args) {
        
        System.out.println("Server is starting...");
        TintolmarketServer server = new TintolmarketServer();
		fileWriterH = new FileWriterHandler();
		fileReaderH = new FileReaderHandler();
		userCatalog = new UserCatalog();
        server.startServer();
    }

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
