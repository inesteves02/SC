package domain;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.IntStream;

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
	private static List<Block> blockchain; // Declare blockchain object

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
				current.writeToLog(0 + "\n"); // Escreve o nr de transações
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

			// Verify the integrity of the blockchain
			if (current.getBlockNumber() == 1) {
				System.out.println("Blockchain is new.");
			} else if (verifyBlockchain()) {
				System.out.println("Blockchain is valid.");
			} else {
				System.out.println("Blockchain is corrupted.");
				throw new Exception("Blockchain is corrupted.");
			}

			while (true) {
				new ServerThread(serverSocket.accept(), userCatalog, wineCatalog, fileReaderH, fileWriterH, privKey,
						current, blockchain)
						.start();
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
		blockchain = loadBlockchain();
	}

	private static List<Block> loadBlockchain() throws IOException, ClassNotFoundException {

		// Check if blockchain file exists and create it if it doesn't
		// If it exists, load the blockchain

		File f = new File("blockchain.txt");
		if (!f.exists()) {
			System.out.println("Blockchain file does not exist.");
			return new ArrayList<Block>();
		}

		FileInputStream inputFile = new FileInputStream("blockchain.txt");
		ObjectInputStream inputObj = new ObjectInputStream(inputFile);
		List<Block> blockchain = (List<Block>) inputObj.readObject();
		inputObj.close();
		inputFile.close();

		return blockchain;
	}

	private static boolean verifyBlockchain() {
		try {
			File f = new File("logs.txt");
			if (!f.exists()) {
				System.out.println("Blockchain file does not exist.");
				return false;
			}

			FileInputStream inputFileLog = new FileInputStream("logs.txt");
			ObjectInputStream inputObjLog = new ObjectInputStream(inputFileLog);
			Log current = (Log) inputObjLog.readObject();
			inputObjLog.close();
			inputFileLog.close();

			String prevHash = current.getPrevHash();
			long blockNumber = current.getBlockNumber();

			for (long i = blockNumber - 1; i >= 1; i--) {
				String filename = "block_" + i + ".blk";
				File blockFile = new File(filename);
				if (!blockFile.exists()) {
					System.out.println("Block file " + filename + " does not exist.");
					return false;
				}

				BufferedReader reader = new BufferedReader(new FileReader(blockFile));
				String line;
				String hash = reader.readLine().trim();
				int currentBlockNumber = Integer.parseInt(reader.readLine().trim());
				long expectedTransactions = Long.parseLong(reader.readLine().trim());
				int transactions = 0;

				if (currentBlockNumber != i) {
					System.out.println("Block number mismatch in block " + i);
					reader.close();
					return false;
				}

				while ((line = reader.readLine()) != null) {

					line = line.trim();

					if (line.startsWith("add") || line.startsWith("sell")) {
						transactions++;
					}
				}

				if (transactions != expectedTransactions) {
					System.out.println("Transaction count mismatch in block " + i);
					reader.close();
					return false;
				}

				reader.close();

				byte[] logBytes = Files.readAllBytes(Paths.get(filename));
				String fileBytesString = Base64.getEncoder().encodeToString(logBytes);

				String calculatedHash = calculateBlockHash(fileBytesString);
				if (!calculatedHash.equals(prevHash)) {
					System.out.println("Hash mismatch in block " + i);
					return false;
				}

				prevHash = hash;

			}

			System.out.println("Blockchain verification complete.");
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static String calculateBlockHash(String data) {
		String dataToHash = data;
		MessageDigest digest = null;
		StringBuffer buffer = new StringBuffer();
		try {
			// Create SHA-256 hash
			digest = MessageDigest.getInstance("SHA-256");
			final byte[] bytes = digest.digest(dataToHash.getBytes());

			IntStream.range(0, bytes.length)
					.mapToObj(i -> bytes[i])
					.forEach(b -> buffer.append(String.format("%02x", b)));

		} catch (NoSuchAlgorithmException ex) {
			// Handle exception
			ex.printStackTrace();
		}
		return buffer.toString();
	}
}
