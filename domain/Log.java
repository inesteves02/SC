package domain;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.security.SignedObject;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class Log implements Serializable {

    private List<SignedObject> transactions = new ArrayList<>();
    private long nrTransactions = 5;
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
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

        // Check if the log has reached its maximum size (e.g. 5 transactions)
        if (transactions.size() == nrTransactions ) {
            // If so, seal the block and create a new one
            sealBlock();
            createNewBlock();
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

    public synchronized void sealBlock() {
        // create a new block file
        addBlockNumber();
        createNewFile();
        // write the hash of the previous block to the new block file
        setPrevHash(prevHash);
        writeToLog("Previous block hash: " + prevHash + "\n");
        // write the transactions in the current block to the new block file
        writeToLog("Transactions:\n");
        for (SignedObject transaction : transactions) {
            String signatureString = Base64.getEncoder().encodeToString(transaction.getSignature());
            try {
                writeToLog((String) transaction.getObject() + ":" + signatureString + "\n");
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
        }
        // write the current block number to the new block file
        writeToLog("Block number: " + blockNumber + "\n");
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



    public synchronized void createNewBlock() {
        // Create a new file for the next block
        File blockFile = new File("block_" + blockNumber + ".blk");
        try {
            blockFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        return this.nrTransactions;
    }
}
