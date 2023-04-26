package domain;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import controllers.FileReaderHandler;
import controllers.FileWriterHandler;
import encrypt.EncryptMethods;

public class TintolmarketServer {

	private static FileReaderHandler fileReaderH; // Declare FileReaderHandler object
	private static FileWriterHandler fileWriterH; // Declare FileReaderHandler object
	private static UserCatalog userCatalog; // Declare UserCatalog object
	private static WineCatalog wineCatalog; // Declare WineCatalog object

	/*
	 * This code is the main method of a TintolmarketServer class.
	 * It prints "Server is starting..." to the console, creates a new instance of
	 * the TintolmarketServer class,
	 * creates new instances of FileWriterHandler, FileReaderHandler and UserCatalog
	 * classes,
	 * and then calls the startServer() method on the TintolmarketServer instance.
	 */
	public static void main(String[] args) {

		try {
			String keyStoreName;
			String keyStorePassword;
			String cifraPassword;

			System.out.println("Server is starting...");

			int port;

			if (args.length == 4) {
				port = Integer.parseInt(args[0]);
				cifraPassword = args[1];
				keyStoreName = args[2];
				keyStorePassword = args[3];

			} else {
				port = 12345;
				cifraPassword = args[0];
				keyStoreName = args[1];
				keyStorePassword = args[2];
			}

			// specifies the path and name of the keystore file that contains the server's
			// private key and its corresponding certificate chain
			System.setProperty("javax.net.ssl.keyStore", keyStoreName);
			// specifies the password used to protect the keystore file
			System.setProperty("javax.net.ssl.keyStorePassword", keyStorePassword);

			TintolmarketServer server = new TintolmarketServer();
			server.init();

			// FAZER OS LOGSSS.txt

			SecretKey key = EncryptMethods.generateSecretKey(cifraPassword);
			Cipher cipher = Cipher.getInstance("PBEWithHmacSHA256AndAES_128");

			fileReaderH.setKey(key);
			fileReaderH.setCipher(cipher);

			fileWriterH.setKey(key);
			fileWriterH.setCipher(cipher);

			// Sockets SSL
			ServerSocketFactory ssf = SSLServerSocketFactory.getDefault();
			SSLServerSocket serverSocket = (SSLServerSocket) ssf.createServerSocket(12345);

			// Vai buscar o keystore do servidor
			FileInputStream keyFile = new FileInputStream(keyStoreName);
			KeyStore keyStore = KeyStore.getInstance("JKS");
			keyStore.load(keyFile, keyStorePassword.toCharArray());

			// Vai buscar a chave privada do servidor
			PrivateKey privKey = (PrivateKey) keyStore.getKey("TintolmarketServer",
					keyStorePassword.toCharArray());

			while (true) {
				new ServerThread(serverSocket.accept(), userCatalog, wineCatalog, fileReaderH, fileWriterH).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void init() {
		fileReaderH = new FileReaderHandler();
		fileWriterH = new FileWriterHandler();
		userCatalog = new UserCatalog(fileReaderH.readUsers());
		wineCatalog = new WineCatalog(userCatalog);
	}
}
