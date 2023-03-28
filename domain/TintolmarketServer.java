package domain;

import controllers.FileReaderHandler;
import controllers.FileWriterHandler;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.stream.Collectors;

public class TintolmarketServer {

	private static FileReaderHandler fileReaderH; // Declare FileReaderHandler object
	private static FileWriterHandler fileWriterH; // Declare FileWriterHandler object
	private static UserCatalog userCatalog; // Declare UserCatalog object
	private static WineCatalog wineCatalog; // Declare WineCatalog object

	// Declare constants for default values of wine attributes
	private static final int DEFAULT_PRICE = 0;
	private static final int DEFAULT_QUANTITY = 0;
	private static final double DEFAULT_RATING = 0;
	private static final boolean DEFAULT_IS_FOR_SALE = false;

	/*
	 * This code is the main method of a TintolmarketServer class.
	 * It prints "Server is starting..." to the console, creates a new instance of
	 * the TintolmarketServer class,
	 * creates new instances of FileWriterHandler, FileReaderHandler and UserCatalog
	 * classes,
	 * and then calls the startServer() method on the TintolmarketServer instance.
	 */
	public static void main(String[] args) {
		System.out.println("Server is starting...");
		int socket;
		if (args.length == 1) {
			socket = Integer.parseInt(args[0]);
		} else {
			socket = 12345;
		}
		TintolmarketServer server = new TintolmarketServer();
		server.init();
		server.startServer(socket);
	}

	private void init() {
		fileWriterH = new FileWriterHandler();
		fileReaderH = new FileReaderHandler();
		userCatalog = new UserCatalog(fileReaderH.readUsers());
		wineCatalog = new WineCatalog(userCatalog);
	}

	public void startServer(int socket) {
		ServerSocket serverSocket = null;

		try {
			serverSocket = new ServerSocket(socket);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}

		while (true) {
			try {
				Socket inSoc = serverSocket.accept();
				ServerThread newServerThread = new ServerThread(inSoc);
				newServerThread.start();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		// sSoc.close();

	}

	// Threads used to communicate with the clients
	class ServerThread extends Thread {

		private Socket socket = null;
		private ObjectOutputStream outStream;
		private ObjectInputStream inStream;
		private User user;

		ServerThread(Socket inSoc) throws IOException {
			this.socket = inSoc;
			outStream = new ObjectOutputStream(socket.getOutputStream());
			inStream = new ObjectInputStream(socket.getInputStream());
			System.out.println("Client connected!");
		}

		/*
		 * This code creates an ObjectOutputStream object and an ObjectInputStream
		 * object from the socket's input and output streams.
		 * It then reads the username and password from the client and calls the
		 * clientLogin() method of the FileReaderHandler object.
		 * If the login is successful, it prints "Login successful!" to the console.
		 * If the password is wrong, it prints "Wrong password!" to the console.
		 * If the user is not found, it prints "User not found! Creating new user..." to
		 * the console and calls the addUser() method of the FileWriterHandler object.
		 */
		public synchronized void run() {
			try {
				String username = (String) inStream.readObject();
				String password = (String) inStream.readObject();

				synchronized (this) {
					int login = fileReaderH.clientLogin(username, password);
					if (login == 0) {
						System.out.println("Client " + user + " tried to login with wrong password! Disconnecting...");
						outStream.writeBoolean(false);
						outStream.close();
						inStream.close();
						socket.close();
						return;
					} else if (login == -1) {
						System.out.println("User not found! Creating new user...");
						fileWriterH.addUser(username, password);
						userCatalog.addUser(username);
						user = userCatalog.getUser(username);
					} else {
						user = userCatalog.getUser(username);
					}
				}

				System.out.println("Login successful!");
				outStream.writeObject(true);

				// create user folder and its files with their default values
				synchronized (this) {
					fileWriterH.createUserFolderAndFiles(user);
				}
				userInteraction();
				inStream.close();
				outStream.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		private synchronized void userInteraction() throws IOException {

			String userInput = "";

			try {
				userInput = (String) inStream.readObject();
			} catch (Exception e) {
				System.err.println("Error reading user input! Disconnecting...");
				return;
			}

			switch (userInput) {
				case "add":
				case "a":
					addWine();
					break;

				case "sell":
				case "s":
					sellWine();
					break;

				case "view":
				case "v":
					viewWines();
					break;

				case "buy":
				case "b":
					buyWine();
					break;

				case "wallet":
				case "w":
					outStream.writeObject(user.getBalance());
					break;

				case "classify":
				case "c":
					classifyWine();
					break;

				case "talk":
				case "t":
					talk();
					break;

				case "read":
				case "r":
					read();
					break;

				case "exit":
					return;

				default:
					System.out.println("Invalid command!");
					break;
			}

			userInteraction();
		}

		private void addWine() {
			try {

				String wineName = (String) inStream.readObject();
				String imageFormat = (String) inStream.readObject();

				// if the wine doesn't already exists, then we add it to the catalog
				if (!user.haveWine(wineName)) {

					// recieve the image from the client
					byte[] image = (byte[]) inStream.readObject();

					// save the image in the user folder
					fileWriterH.saveImage(image, wineName, imageFormat, user);
					
					Wine wine = new Wine(wineName, DEFAULT_PRICE, DEFAULT_QUANTITY, DEFAULT_RATING,
							user.getName(), DEFAULT_IS_FOR_SALE, imageFormat);
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

		private void sellWine() {
			try {
				String wineName = (String) inStream.readObject();
				double value = Double.parseDouble((String) inStream.readObject());
				int quantity = Integer.parseInt((String) inStream.readObject());

				// to sell a wine, it has to be present in the user catalog
				if (user.haveWine(wineName)) {

					Wine wine = userCatalog.getUser(user.getName()).getWine(wineName);
					wine.setPrice(value);
					wine.setQuantity(quantity);
					wine.setIsForSale(true);

					fileWriterH.updateWine(user, wine);
					// acho que aqui tem q haver um if(ñ existir ja no wineCatalog)
					wineCatalog.addWine(wine);

					outStream.writeObject(true);
				} else {
					outStream.writeObject(false);
				}
			} catch (ClassNotFoundException | IOException e) {
				System.err.println("Error selling wine!");
				e.printStackTrace();
			}
		}

		private String buildViewResponse(List<Wine> wines) {
			StringBuilder sb = new StringBuilder();

			for (Wine wine : wines) {
				sb.append("Nome: " + wine.getName() + ", " + wine.getPrice() + " Euros, Quantidade: "
						+ wine.getQuantity() + ", Classificação: " + wine.getRating() + ", Vendedor: "
						+ wine.getSellerName() + "\n");
			}

			return sb.toString();
		}

		private void viewWines() {
			try {
				String wineName = (String) inStream.readObject();
				List<Wine> wines = wineCatalog.getWines(wineName);
				if (wines.isEmpty()) {
					outStream.writeObject("No wines found!");
					return;
				}
				String response = buildViewResponse(wines);
				outStream.writeObject(response);

				// Send all the images of the wines
				outStream.writeObject(wines.size());
				for (Wine wine : wines) {
					sendImage(wine);
				}

				
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
		}
		
		private void sendImage(Wine wine) throws IOException{
			// Read image
			File file = new File("user_data/" + wine.getSellerName() + "/images/" + wine.getName() + "." + wine.getImageFormat());
			// Send the name of the image with the seller name and the format
			outStream.writeObject(wine.getName() + " FROM SELLER " + wine.getSellerName() + " ." + wine.getImageFormat());
			// Send the image
			byte[] image = new byte[(int) file.length()];
			DataInputStream dis = new DataInputStream(new FileInputStream(file));
			dis.readFully(image);
			outStream.writeObject(image);
			dis.close();
		}

		private void buyWine() {
			try {
				String wineName = (String) inStream.readObject();
				String sellerName = (String) inStream.readObject();
				int quantity = Integer.parseInt((String) inStream.readObject());

				if (isValidBuyInput(wineName, sellerName, quantity)) {

					Wine wine = userCatalog.getUser(sellerName).getWine(wineName);

					wine.setQuantity(wine.getQuantity() - quantity); // update quantity
					wineCatalog.updateWine(wine); // update wine catalog
					userCatalog.getUser(sellerName)
							.setBalance(userCatalog.getUser(sellerName).getBalance() + wine.getPrice() * quantity); // update
																													// seller
																													// balance
					user.setBalance(user.getBalance() - wine.getPrice() * quantity); // update buyer balance

					fileWriterH.updateWine(userCatalog.getUser(sellerName), wine); // update seller wine file
					fileWriterH.updateUserBalance(userCatalog.getUser(sellerName)); // update seller balance file
					fileWriterH.updateUserBalance(user); // update buyer balance file

					outStream.writeObject(true);

					outStream.writeObject("Wish to rate the wine? (y/n)");

					String answer = (String) inStream.readObject();

					if (answer.equals("y")) {
						classifyWine();
					}
				} else {
					outStream.writeObject(false);
				}
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
		}

		private boolean isValidBuyInput(String wineName, String sellerName, int quantity) {
			try {
				if (!userCatalog.getUser(sellerName).haveWine(wineName)) {
					outStream.writeObject(false);
					return false;
				}

				Wine wine = userCatalog.getUser(sellerName).getWine(wineName);

				if (wine.getQuantity() < quantity) {
					outStream.writeObject(false);
					return false;
				}

				if (user.getBalance() < wine.getPrice() * quantity) {
					outStream.writeObject(false);
					return false;
				}

				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}

		private void classifyWine() {
			try {
				String wineName = (String) inStream.readObject();
				int rating = Integer.parseInt((String) inStream.readObject());

				if (isValidRating(rating)) {

					List<Wine> wines = wineCatalog.getWines(wineName);

					wines.forEach(wine -> wine.setRating(rating));

					wines.forEach(wine -> {
						try {
							fileWriterH.updateWine(userCatalog.getUser(wine.getSellerName()), wine);
						} catch (IOException e) {
							e.printStackTrace();
						}
					});

					outStream.writeObject(true);
				} else {
					outStream.writeObject(false);
				}
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
		}

		private boolean isValidRating(int rating) {
			try {
				if (rating < 1 || rating > 5) {
					outStream.writeObject(false);
					return false;
				}

				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}

		private void talk() {
			try {

				String receiverName = (String) inStream.readObject();
				String message = (String) inStream.readObject();

				if (userCatalog.getUser(receiverName) != null) {

					Message msg = new Message(user.getName(), receiverName, message);

					User receiver = userCatalog.getUser(receiverName);

					receiver.addMessage(msg);
					fileWriterH.addMessage(msg);

					outStream.writeObject(true);

				} else {
					outStream.writeObject(false);
				}
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
		}

		private void read() {
			try {

				List<Message> messages = user.getMessages();

				String result = messages.stream().map(Message::toString).collect(Collectors.joining("\n"));

				outStream.writeObject(result);
				user.clearMessages();
				fileWriterH.clearMessages(user);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
