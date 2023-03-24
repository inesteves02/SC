package domain;

public class Message {
    
    private String sender;
    private String receiver;
    private String message;

    public Message(String sender, String receiver, String message) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }

    // returns the sender of the message
    public String getSender() {
        return sender;
    }

    //returns the receiver of the message
    public String getReceiver() {
        return receiver;
    }

    // returns the message sent
    public String getMessage() {
        return message;
    }


    public String toString() {
        return sender + " -> " + receiver + ": " + message;
    }

}
