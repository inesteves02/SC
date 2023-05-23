package domain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SignedObject;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class Log implements Serializable {

    private List<SignedObject> transactions = new ArrayList<>();
    private long nmr_transactions = 0;
    private long blockNumber = 1;
    private String prevHash;

    public Log() {
        File f = new File("block_" + blockNumber + ".blk");
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void addToLog(SignedObject transaction) {
        // Add the transaction to the list of transactions
        this.transactions.add(transaction);

        // Write the transaction and its signature to the log file
        String signatureString = Base64.getEncoder().encodeToString(transaction.getSignature());
        try {
            writeToLog((String) transaction.getObject() + ":" + signatureString + "\n");

            // Increment the number of transactions
            nmr_transactions++;

            // Increment on the 3 line of block file
            String blockFile = "block_" + blockNumber + ".blk";
            List<String> lines = Files.readAllLines(Paths.get(blockFile));
            lines.set(2, String.valueOf(nmr_transactions));
            Files.write(Paths.get(blockFile), lines);

        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

        // Check if the log has reached its maximum size (e.g. 5 transactions)
        if (transactions.size() == 5) {

            System.out.println("Sealing block " + blockNumber + "...");
        }
    }

    public synchronized void writeToLog(String toWrite) {
        try {
            FileWriter myWriter = new FileWriter("block_" + blockNumber + ".blk", true);
            myWriter.write(toWrite);
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized List<SignedObject> getTransactions() {
        return transactions;
    }

    public synchronized int getLogSize() {
        return transactions.size();
    }

    public synchronized void clearTransactions() {
        transactions.clear();
    }

    public synchronized long getBlockNumber() {
        return blockNumber;
    }

    public synchronized void addBlockNumber() {
        blockNumber++;
    }

    public synchronized void setPrevHash(String prevHash) {
        this.prevHash = prevHash;
    }

    public synchronized String getPrevHash() {
        return prevHash;
    }

    public String getLogFile() {
        return "block_" + blockNumber + ".blk";
    }

    public synchronized void createNewFile() {
        try {
            File f = new File("block_" + blockNumber + ".blk");
            boolean success = f.createNewFile();
            if (success) {
                System.out.println("New block file created: " + f.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while creating the block file.");
            e.printStackTrace();
        }
    }

    public synchronized long getNrTrans() {
        return this.nmr_transactions;
    }

    public void setNmrTransactions(int i) {
        this.nmr_transactions = i;
    }
}
