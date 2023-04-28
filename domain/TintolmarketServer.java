package domain;

import java.io.*;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.Base64;

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

			System.setProperty("javax.net.ssl.keyStore", keyStoreName);
			System.setProperty("javax.net.ssl.keyStorePassword", keyStorePassword);
			System.setProperty("javax.net.ssl.keyStoreType", "JCEKS");

			Log current = null;
			File f = new File("logs.txt");

			if (f.exists()) {
				FileInputStream inputFileLog = new FileInputStream("logs.txt");
				ObjectInputStream inputObjLog = new ObjectInputStream(inputFileLog);
				current = (Log) inputObjLog.readObject();
				inputObjLog.close();
				inputFileLog.close();
			} else {
				byte[] startBytes = new byte[32];
				String startString = Base64.getEncoder().encodeToString(startBytes);
				current = new Log();
				current.writeToLog(startString + "\n"); // Escreve os 32 bytes a zero
				current.writeToLog(current.getBlockNumber() + "\n"); // Escreve o nr do bloco
				current.writeToLog(current.getNrTrans() + "\n"); // Escreve o nr de transações
				current.setPrevHash(startString); // associa hash do bloco anterior a este log

				FileOutputStream outputFileLog = new FileOutputStream("logs.txt");
				ObjectOutputStream outputObjLog = new ObjectOutputStream(outputFileLog);
				outputObjLog.writeObject(current);
				outputFileLog.close();
				outputObjLog.close();

			}

			SecretKey key = EncryptMethods.generateSecretKey(cifraPassword);
			Cipher cipher = Cipher.getInstance("PBEWithHmacSHA256AndAES_128");

			TintolmarketServer server = new TintolmarketServer();
			server.init(cipher, key);

			// Sockets SSL
			ServerSocketFactory ssf = SSLServerSocketFactory.getDefault();
			SSLServerSocket serverSocket = (SSLServerSocket) ssf.createServerSocket(port);

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

	private void init(Cipher cipher, SecretKey key) throws Exception {
		fileReaderH = new FileReaderHandler(cipher, key);
		fileWriterH = new FileWriterHandler(cipher, key);
		userCatalog = new UserCatalog(fileReaderH.readUsers());
		wineCatalog = new WineCatalog(userCatalog);
	}
}
