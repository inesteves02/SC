package domain;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.SignedObject;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import controllers.FileReaderHandler;
import controllers.FileWriterHandler;

// Threads used to communicate with the clients
public class ServerThread extends Thread {

    private Socket socket = null;
    private UserCatalog userCatalog;
    private WineCatalog wineCatalog;
    private FileReaderHandler fileReaderH;
    private FileWriterHandler fileWriterH;
    private ObjectOutputStream outStream;
    private ObjectInputStream inStream;
    private PublicKey public_key;
    private User user;
    private long nonce;
    private static PrivateKey privKey;
    private Log current;
    private static List<Block> blockchain = new ArrayList<Block>();

    // Declare constants for default values of wine attributes
    private static final int DEFAULT_PRICE = 0;
    private static final int DEFAULT_QUANTITY = 0;
    private static final double DEFAULT_RATING = 0;
    private static final boolean DEFAULT_IS_FOR_SALE = false;

    ServerThread(Socket inSoc, UserCatalog userCatalog, WineCatalog wineCatalog, FileReaderHandler fileReaderH,
            FileWriterHandler fileWriterH, PrivateKey privKey, Log current) throws IOException {
        this.socket = inSoc;
        nonce = new Random().nextLong();
        outStream = new ObjectOutputStream(socket.getOutputStream());
        inStream = new ObjectInputStream(socket.getInputStream());
        this.userCatalog = userCatalog;
        this.wineCatalog = wineCatalog;
        this.fileReaderH = fileReaderH;
        this.fileWriterH = fileWriterH;
        this.privKey = privKey;
        this.current = current;

    }

    /*
     * This code creates an ObjectOutputStream object and an ObjectInputStream
     * object from the port's input and output streams.
     * It then reads the username and password from the client and calls the
     * clientLogin() method of the FileReaderHandler object.
     * If the login is successful, it prints "Login successful!" to the console.
     * If the password is wrong, it prints "Wrong password!" to the console.
     * If the user is not found, it prints "User not found! Creating new user..." to
     * the console and calls the addUser() method of the FileWriterHandler object.
     */
    public void run() {
        try {
            System.out.println("Client connected! Waiting for authentication...");
            String userID = ((String) inStream.readObject()).toLowerCase();

            outStream.writeObject(nonce);
            outStream.flush();

            boolean existUser = userCatalog.existUser(userID);
            // server anwsers with the nonce (being an unknown user or not)
            outStream.writeObject(existUser);
            boolean verify = false;

            if (!existUser) {

                long nonce = (long) inStream.readObject();

                if (nonce != this.nonce) {
                    outStream.writeObject(false);
                }

                byte[] ceritficado_byte = (byte[]) inStream.readObject();
                Certificate certificado = CertificateFactory.getInstance("X.509")
                        .generateCertificate(new ByteArrayInputStream(ceritficado_byte));

                this.public_key = certificado.getPublicKey();

                SignedObject signedObject = (SignedObject) inStream.readObject();
                Signature signature = Signature.getInstance("MD5withRSA");

                verify = signedObject.verify(public_key, signature);

                // caso 2) a)
                if (verify) {
                    fileWriterH.addUser(userID, userID + ".cer");
                    // agora o ficheiro vai ter que ser cifrado pelo server
                    // chave publica = nome do ficheiro que contém o certificado
                    File certificate_file = new File("certificates/" + userID + ".cer");
                    certificate_file.createNewFile();
                    FileOutputStream fos = new FileOutputStream(certificate_file);
                    fos.write(certificado.getEncoded());
                    fos.close();
                    outStream.writeObject(true);
                    System.out.println("Login successful!");
                    userCatalog.addUser(userID);
                    user = userCatalog.getUser(userID);
                    fileWriterH.createUserFolderAndFiles(user);

                } else {
                    outStream.writeObject(false);
                    System.out.println("Login failed!");
                }

            } else { // caso 2) b)
                FileInputStream fis = new FileInputStream(fileReaderH.getCertificateName(userID));
                Certificate certificado = CertificateFactory.getInstance("X.509").generateCertificate(fis);

                this.public_key = certificado.getPublicKey();

                SignedObject signedObject = (SignedObject) inStream.readObject();
                Signature signature = Signature.getInstance("MD5withRSA");

                verify = signedObject.verify(public_key, signature);
                outStream.writeObject(verify);
                if (verify) {
                    System.out.println("Login successful!");
                    user = userCatalog.getUser(userID);
                } else {
                    System.out.println("Login failed!");
                }
            }

            if (verify) {
                userInteraction();
            }
            inStream.close();
            outStream.close();
            socket.close();

        } catch (IOException | ClassNotFoundException | CertificateException | NoSuchAlgorithmException
                | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }
    }

    private void userInteraction()
            throws IOException, InvalidKeyException, SignatureException, NoSuchAlgorithmException {

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

            case "list":
            case "l":
                outStream.writeObject(saveBlockchainLog(blockchain));
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

    private void sellWine() throws InvalidKeyException, SignatureException, NoSuchAlgorithmException {
        try {
            String wineName = (String) inStream.readObject();
            double value = Double.parseDouble((String) inStream.readObject());
            int quantity = Integer.parseInt((String) inStream.readObject());

            SignedObject signedObject = (SignedObject) inStream.readObject();
            Signature signature = Signature.getInstance("MD5withRSA");
            boolean verify = signedObject.verify(public_key, signature);
            if (!verify) {
                outStream.writeObject(false);
                return;
            }

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

                current.addToLog(signedObject);
                logFull(current);

            } else {
                outStream.writeObject(false);
            }
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return;
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
            return;
        }
    }

    private void sendImage(Wine wine) throws IOException {
        // Read image
        File file = new File(
                "user_data/" + wine.getSellerName() + "/images/" + wine.getName() + "." + wine.getImageFormat());
        // Send the name of the image with the seller name and the format
        outStream.writeObject(
                wine.getName() + " FROM SELLER " + wine.getSellerName() + " ." + wine.getImageFormat());
        // Send the image
        byte[] image = new byte[(int) file.length()];
        DataInputStream dis = new DataInputStream(new FileInputStream(file));
        dis.readFully(image);
        outStream.writeObject(image);
        dis.close();
    }

    private void buyWine() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        try {

            String wineName = ((String) inStream.readObject()).toLowerCase();
            String sellerName = ((String) inStream.readObject()).toLowerCase();
            int quantity = Integer.parseInt((String) inStream.readObject());

            SignedObject signedObject = (SignedObject) inStream.readObject();
            Signature signature = Signature.getInstance("MD5withRSA");
            boolean verify = signedObject.verify(public_key, signature);
            if (!verify) {
                outStream.writeObject(false);
                return;
            }

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
                outStream.flush();

                current.addToLog(signedObject);
                logFull(current);

            } else {
                outStream.writeObject(false);
            }
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return;
        }
    }

    public void logFull(Log currentLog)
            throws InvalidKeyException, SignatureException, NoSuchAlgorithmException, IOException {
        // Check if log has reached its maximum size
        if (currentLog.getLogSize() == 5) {
            // Create initial hash of all zeros and convert to string
            byte[] firstBytes = new byte[32];
            String string = Base64.getEncoder().encodeToString(firstBytes);
            Block newBlock = loadingLogFile(currentLog, string);
            nextLog(currentLog, newBlock, currentLog.getBlockNumber());
        }
    }

    // returns the newBlock
    private static Block loadingLogFile(Log currentLog, String string)
            throws IOException, InvalidKeyException, SignatureException, NoSuchAlgorithmException {
        // Carrega o ficheiro do log
        byte[] logBytes = Files.readAllBytes(Paths.get(currentLog.getLogFile()));

        // Assina tudo o que esta no log
        SignedObject signedFile = new SignedObject(logBytes, privKey, Signature.getInstance("MD5withRSA"));
        String sigString = Base64.getEncoder().encodeToString(signedFile.getSignature());
        // Adiciona assinatura
        currentLog.writeToLog(sigString);

        logBytes = Files.readAllBytes(Paths.get(currentLog.getLogFile()));
        String fileBytesString = Base64.getEncoder().encodeToString(logBytes);

        // Entao cria um novo block e adiciona o à blockchain
        Block newBlock = new Block(fileBytesString, string);
        blockchain.add(newBlock);
        return newBlock;
    }

    public void nextLog(Log currentLog, Block block, long blockNum) {

        if (blockNum == 1) {
            // Passa ao proximo ficheiro de log
            currentLog.addBlockNumber();
            currentLog.createNewFile();
            currentLog.clearTransactions();
            // Escreve no prox log o hash do bloco anterior, associa o log ao hash anterior
            currentLog.writeToLog(block.getHash() + "\n");
            currentLog.setPrevHash(block.getHash());
            // Escreve o numero do bloco e o nr de transações
            currentLog.writeToLog(currentLog.getBlockNumber() + "\n");
            currentLog.writeToLog(currentLog.getNrTrans() + "\n");
        } else {
            String prevHash = blockchain.get(blockchain.size() - 1).getHash();

            // Passa ao proximo ficheiro de log
            currentLog.addBlockNumber();
            currentLog.createNewFile();
            currentLog.clearTransactions();
            // Escreve no prox log o hash do bloco anterior, associa o log ao hash anterior
            currentLog.writeToLog(prevHash + "\n");
            currentLog.setPrevHash(prevHash);
            // Escreve o numero do bloco e o nr de transações
            currentLog.writeToLog(currentLog.getBlockNumber() + "\n");
            currentLog.writeToLog(currentLog.getNrTrans() + "\n");
        }
    }

    private boolean isValidBuyInput(String wineName, String sellerName, int quantity) {
        try {
            if (userCatalog.getUser(sellerName) == null) {
                outStream.writeObject(false);
                return false;
            }
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
                        return;
                    }
                });

                outStream.writeObject(true);
            } else {
                outStream.writeObject(false);
            }
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return;
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
            return;
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
            return;
        }
    }

    public synchronized String saveBlockchainLog(List<Block> blockchain) throws IOException {

        StringBuilder sb = new StringBuilder();
        for (Block b : blockchain) {
            sb.append(b.getData() + "\n");
        }

        return sb.toString();
    }

}